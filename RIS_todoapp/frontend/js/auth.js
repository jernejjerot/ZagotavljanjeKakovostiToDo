// Register a new user
document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const userData = {
        name: document.getElementById('name').value,
        surname: document.getElementById('surname').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
    };

    try {
        const response = await fetch('/users/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData),
        });

        if (response.ok) {
            alert('Registration successful! Please log in.');
            window.location.href = '/login.html'; // Redirect to login
        } else {
            const errorText = await response.text();
            alert(`Registration failed: ${errorText}`);
        }
    } catch (error) {
        alert('Error registering user.');
    }
});

// Log in a user
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const loginData = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
    };

    try {
        const response = await fetch('/users/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginData),
        });

        if (response.ok) {
            const user = await response.json();

            // Debugging: Log the user object to verify the response
            console.log("User object from login response:", user);
            
            localStorage.setItem('userId', user.id); // Save user ID for session
            localStorage.setItem('userName', user.name); // Save user name
            // Check and save admin status
            if (user.admin !== undefined) {
                localStorage.setItem('isAdmin', user.admin); // Save admin status
            } else {
                console.warn("Admin status not provided in response.");
                localStorage.removeItem('isAdmin'); // Ensure consistency
            }

            // Debugging admin status
            console.log("Admin Status in localStorage:", localStorage.getItem('isAdmin'));

            window.location.href = '/'; // Redirect to index.html
        } else {
            const errorText = await response.text();
            alert(`Login failed: ${errorText}`);
        }
    } catch (error) {
        alert('Error logging in.');
    }
});

// Update user profile
document.getElementById('editUserForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const userId = localStorage.getItem('userId');
    if (!userId) {
        alert('You must be logged in to edit your profile.');
        return;
    }

    const userData = {
        name: document.getElementById('name').value,
        surname: document.getElementById('surname').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
    };

    try {
        // Use the correct backend API URL
        const response = await fetch(`http://localhost:8080/users/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        if (response.ok) {
            const updatedUser = await response.json();
            localStorage.setItem('userName', updatedUser.name); // Update username in localStorage
            alert('Profile updated successfully!');
            window.location.href = '/'; // Redirect to the home page
        } else {
            const errorText = await response.text();
            alert(`Failed to update profile: ${errorText}`);
        }
    } catch (error) {
        console.error('Error updating profile:', error);
        alert('An error occurred while updating the profile.');
    }
});

