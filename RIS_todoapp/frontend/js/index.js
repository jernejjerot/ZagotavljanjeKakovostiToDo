import { geocodeAddress } from './locationUtils.js';

document.addEventListener('DOMContentLoaded', () => {
    const userId = localStorage.getItem("userId");
    const userName = localStorage.getItem("userName");
    const isAdmin = localStorage.getItem("isAdmin") === "true"; // Check admin status
    const navLinks = document.getElementById("navLinks");
    const userNameElement = document.getElementById("userName");
    const userActions = document.getElementById("userActions");
    const taskGrid = document.getElementById("taskGrid");
    const taskForm = document.getElementById("taskForm");
    const taskTypeSelect = document.getElementById("taskType");
    const tasksSection = document.getElementById("tasksSection");
    const createUserSection = document.getElementById("createUserSection"); // Forma za ustvarjanje uporabnikov

    // Privzeto skrij formo za ustvarjanje uporabnikov
    if (createUserSection) {
        createUserSection.style.display = 'none';
    }

    // prva izvedba funkcije
    checkUserProximity();

    // periodično preverjanje lokacije
    setInterval(() => {
        console.log("Running periodic proximity check...");
        checkUserProximity();
    }, 60000); // Preverjanje vsakih 60 sekund (60000 ms)

    // Prikaz forme za ustvarjanje uporabnikov in admin navigacijskega elementa
    if (isAdmin && userId) {
        // Prikaži formo za admina
        if (createUserSection) {
            createUserSection.style.display = 'block';
        }

        // Dodaj povezavo do admin strani
        const adminLink = document.createElement("li");
        adminLink.innerHTML = `<a href="/html/admin.html">Admin Page</a>`;
        navLinks.appendChild(adminLink);

        // Poslušalec dogodkov za formo za ustvarjanje uporabnikov
        document.getElementById('createUserForm')?.addEventListener('submit', async (e) => {
            e.preventDefault();

            const userData = {
                name: document.getElementById('name').value,
                surname: document.getElementById('surname').value,
                email: document.getElementById('email').value,
                password: document.getElementById('password').value,
                admin: document.getElementById('admin').checked,
            };

            try {
                const response = await fetch('/users/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(userData),
                });

                if (response.ok) {
                    alert('User created successfully!');
                    document.getElementById('createUserForm').reset();
                } else {
                    const errorText = await response.text();
                    alert(`Failed to create user: ${errorText}`);
                }
            } catch (error) {
                console.error('Error creating user:', error);
            }
        });
    }

    // Login/logout funkcionalnost
    if (!userId) {
        userActions.innerHTML = `
            <form id="loginForm">
                <input type="email" id="email" placeholder="Email" required>
                <input type="password" id="password" placeholder="Password" required>
                <button type="submit">Login</button>
            </form>
        `;
        document.getElementById('loginForm').addEventListener('submit', handleLogin);
    } else {
        userActions.innerHTML = `
            <div class="logout-container">
                <p>Logged in as: ${userName}</p>
                <button id="logoutButton" class="logout-button">Logout</button>
            </div>
        `;
        document.getElementById('logoutButton').addEventListener('click', handleLogout);
        userNameElement.textContent = userName;
        tasksSection.style.display = 'block';
        loadTaskTypes();
        fetchTasks();
    }
    
    cancelEditButton.id = 'cancelEditButton';
    cancelEditButton.textContent = 'Cancel Edit';
    cancelEditButton.style.display = 'none';
    taskForm.appendChild(cancelEditButton);

    let editingTaskId = null;


    async function moveToDoneTasks(task) {
        console.log("Task received by moveToDoneTasks:", task); // Debugging input task
    
        // Validate taskName
        if (!task.taskName || task.taskName.trim() === "") {
            console.error("Task name is missing or empty.");
            return;
        }
        if (!task.taskType || !task.taskType.id) {
            console.error("Task type is missing or invalid.");
            return;
        }
    
        const payload = {
            id: task.id,
            taskName: task.taskName,
            taskType: {
                id: task.taskType.id,
                type: task.taskType.type, // Optional but helpful for clarity
            },
            description: task.description,
            dueDateTime: task.dueDateTime,
            user: {
                id: task.user.id,
            },
            isCompleted: task.isCompleted,
        };
    
        console.log("Payload sent to /tasks/done:", JSON.stringify(payload, null, 2)); // Debugging payload
    
        try {
            const response = await fetch('/tasks/done', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json', 
                    'user-id': localStorage.getItem('userId') 
                },
                body: JSON.stringify({
                    id: task.id,
                    taskName: task.taskName,
                    taskType: task.taskType, // Ensure taskType is an object with `id` and `type`
                    description: task.description,
                    dueDateTime: task.dueDateTime,
                    user: task.user, // Ensure this is an object with `id`
                    isCompleted: true, // Mark as done
                }),
            });
    
            if (!response.ok) {
                const errorText = await response.text();
                console.error("Error from server:", errorText);
                alert(`Failed to mark task as done: ${errorText}`);
                return;
            }
    
            console.log(`Task "${task.taskName}" marked as completed.`);
            alert(`Task "${task.taskName}" moved to completed.`);
        } catch (error) {
            console.error("Error in moveToDoneTasks:", error.message);
            alert("Failed to mark task as done.");
        }
    }

    async function fetchTasks() {
        const response = await fetch('/tasks', { headers: { 'user-id': localStorage.getItem('userId') } });
        const tasks = await response.json();
        console.log("Fetched tasks:", tasks); // Debugging
    
        // Počistimo mrežo nalog
        const taskGrid = document.getElementById('taskGrid');
        taskGrid.innerHTML = '';
    
        const now = new Date();
    
        for (const task of tasks) {
            console.log("Processing task:", task);
    
            const dueTime = new Date(task.dueDateTime);
            const timeLeft = dueTime - now;
    
            // Premaknemo nalogo v "done", če je rok potekel
            if (timeLeft <= 0 && !task.isCompleted) {
                await moveToDoneTasks(task);
            } else if (!task.isCompleted) {
                // Prikazujemo le nepopolne naloge
                renderTask(task, taskGrid, now);
            }
        }
    }
    

    // // Display login/logout functionality
    // if (!userId) {
    //     userActions.innerHTML = `
    //         <form id="loginForm">
    //             <input type="email" id="email" placeholder="Email" required>
    //             <input type="password" id="password" placeholder="Password" required>
    //             <button type="submit">Login</button>
    //         </form>
    //     `;
    //     document.getElementById('loginForm').addEventListener('submit', handleLogin);
    // } else {
    //     userActions.innerHTML = `
    //         <div class="logout-container">
    //             <p>Logged in as: ${userName}</p>
    //             <button id="logoutButton" class="logout-button">Logout</button>
    //         </div>
    //     `;
    //     document.getElementById('logoutButton').addEventListener('click', handleLogout);
    //     userNameElement.textContent = userName;
    //     tasksSection.style.display = 'block';
    //     loadTaskTypes();
    //     fetchTasks();
    // }

    // Login functionality
    async function handleLogin(event) {
        event.preventDefault();
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
                localStorage.setItem('userId', user.id);
                localStorage.setItem('userName', user.name);
                localStorage.setItem('isAdmin', user.admin); // Shranimo status admina
                location.reload(); // Osveži stran po prijavi
            } else {
                alert('Login failed.');
            }
        } catch (error) {
            console.error('Error logging in:', error);
        }
    }

    // Logout functionality
    function handleLogout() {
        localStorage.removeItem('userId');
        localStorage.removeItem('userName');
        localStorage.removeItem('isAdmin');
        alert('You have been logged out.');
        location.reload();
    }

    // Load task types from the backend
    async function loadTaskTypes() {
        try {
            const response = await fetch('/task-types');
            if (!response.ok) throw new Error('Failed to load task types.');

            const taskTypes = await response.json();
            taskTypeSelect.innerHTML = taskTypes.map(type => `<option value="${type.id}">${type.type}</option>`).join('');
        } catch (error) {
            console.error('Error loading task types:', error);
        }
    }
    

        function renderTask(task, taskGrid, now) {
            const dueTime = new Date(task.dueDateTime || now); // Handle missing dueDateTime
            const timeLeft = dueTime - now;
        
            // Set default values for missing fields
            const taskName = task.taskName || "Unnamed Task";
            const taskType = task.taskType ? task.taskType.type : "Unknown"; // Handle missing taskType
            const description = task.description || "No description provided.";
        
            // Task box creation
            const taskBox = document.createElement('div');
            taskBox.classList.add('task-box');
            taskBox.setAttribute('data-task-id', task.id); // Add task ID as data attribute
        
            if (task.isCompleted) {
                taskBox.classList.add('task-completed'); // Add a visual indicator for completed tasks
            }
        
            const title = document.createElement('h4');
            title.textContent = taskName;
        
            const type = document.createElement('p');
            type.textContent = `Type: ${taskType}`;
        
            const descriptionElement = document.createElement('p');
            descriptionElement.textContent = description;
        
            const dueDate = document.createElement('p');
            dueDate.textContent = `Due: ${dueTime.toLocaleString()}`;
        
            // Countdown for urgent tasks (less than 24 hours)
            if (!task.isCompleted && timeLeft > 0 && timeLeft < 86400000) {
                taskBox.style.backgroundColor = '#ffcccc';
                const countdown = document.createElement('p');
                countdown.textContent = `Time left: ${formatTimeLeft(timeLeft)}`;
                const interval = setInterval(() => {
                    const remainingTime = dueTime - new Date();
                    if (remainingTime <= 0) {
                        clearInterval(interval); // Stop updating countdown when time is up
                        countdown.textContent = "Time's up!";
                    } else {
                        countdown.textContent = `Time left: ${formatTimeLeft(remainingTime)}`;
                    }
                }, 1000);
                taskBox.appendChild(countdown);
            }
        
            // Checkbox for marking completion
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.checked = task.isCompleted;
            checkbox.addEventListener('change', () => toggleCompletion(task.id, checkbox.checked));

            const locationElement = document.createElement('p');
            locationElement.textContent = task.locationAddress
            ? `Location: ${task.locationAddress}`
            : 'No location provided.';
        
            // Edit button
            const editButton = document.createElement('button');
            editButton.textContent = 'Edit';
            editButton.classList.add('edit-button');
            editButton.addEventListener('click', () => populateTaskFormForEdit(task));
        
            // Delete button
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete';
            deleteButton.classList.add('delete-button');
            deleteButton.addEventListener('click', () => deleteTask(task.id));
        
            // Append elements to the task box
            taskBox.append(checkbox, title, type, descriptionElement, dueDate, locationElement, editButton, deleteButton);
            taskGrid.appendChild(taskBox);
        }
        
    
    
    

    // Format time left for countdown
    function formatTimeLeft(ms) {
        const hours = Math.floor(ms / 3600000);
        const minutes = Math.floor((ms % 3600000) / 60000);
        const seconds = Math.floor((ms % 60000) / 1000);
        return `${hours}h ${minutes}m ${seconds}s`;
    }

 // Toggle task completion
async function toggleCompletion(taskId, isCompleted) {
    try {
        // Fetch current task details
        const currentTaskResponse = await fetch(`/tasks/${taskId}`, {
            headers: { 'user-id': userId },
        });

        if (!currentTaskResponse.ok) {
            throw new Error(`Failed to fetch task details: ${await currentTaskResponse.text()}`);
        }

        const currentTask = await currentTaskResponse.json();
        currentTask.isCompleted = isCompleted; // Update completion status

        // Send the full updated task object to the backend
        const response = await fetch(`/tasks/${taskId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json', 'user-id': userId },
            body: JSON.stringify(currentTask), // Send the full task object
        });

        if (response.ok) {
            alert('Task updated successfully!');

            // Update the UI
            const taskBox = document.querySelector(`[data-task-id="${taskId}"]`);
            if (taskBox) {
                taskBox.style.opacity = isCompleted ? "0.5" : "1.0";

                // Remove "Edit" and "Delete" buttons for completed tasks
                if (isCompleted) {
                    const editButton = taskBox.querySelector('.edit-button');
                    const deleteButton = taskBox.querySelector('.delete-button');
                    if (editButton) editButton.remove();
                    if (deleteButton) deleteButton.remove();

                    // Add "Move to Completed Tasks" button
                    const moveButton = document.createElement('button');
                    moveButton.textContent = 'Move to Completed Tasks';
                    moveButton.classList.add('move-button');
                    moveButton.addEventListener('click', async () => {
                        await moveToDoneTasks(currentTask);
                        taskBox.remove(); // Remove from the main task grid
                    });
                    taskBox.appendChild(moveButton);
                }
            }
        } else {
            const errorText = await response.text();
            console.error(`Failed to update task: ${errorText}`);
            alert('Error updating task.');
        }
    } catch (error) {
        console.error('Error toggling task completion:', error);
    }
}


    
    

    // Populate the task form for editing
    function populateTaskFormForEdit(task) {
        document.getElementById('taskName').value = task.taskName;
        document.getElementById('taskDescription').value = task.description;
        document.getElementById('dueDate').value = new Date(task.dueDateTime).toISOString().slice(0, 16);
        taskTypeSelect.value = task.taskType.id;
        editingTaskId = task.id;
        cancelEditButton.style.display = 'inline';
    }

    // Create or update a task
    taskForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Preprečimo privzeto obnašanje obrazca
    
        // Preprečimo večkratno oddajo obrazca
        const submitButton = taskForm.querySelector('button[type="submit"]');
        submitButton.disabled = true; // Onemogočimo gumb za oddajo
    
        const taskLocation = document.getElementById('taskLocation').value;
    
        let geocodedLocation = null;
        if (taskLocation) {
            try {
                geocodedLocation = await geocodeAddress(taskLocation);
                console.log('Geocoded Location:', geocodedLocation);
            } catch (error) {
                console.error('Error geocoding address:', error);
                alert('Failed to geocode the address. Please try again or leave it blank.');
                submitButton.disabled = false; // Ponovno omogočimo gumb, če pride do napake
                return;
            }
        }
    
        const taskData = {
            taskName: document.getElementById('taskName').value,
            taskType: { id: parseInt(taskTypeSelect.value) },
            description: document.getElementById('taskDescription').value,
            dueDateTime: document.getElementById('dueDate').value,
            locationAddress: taskLocation || null, // Naslov
            latitude: geocodedLocation ? geocodedLocation.latitude : null, // Latitude
            longitude: geocodedLocation ? geocodedLocation.longitude : null, // Longitude
            user: { id: parseInt(userId) },
        };
    
        try {
            const response = await fetch('/tasks', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'user-id': userId },
                body: JSON.stringify(taskData),
            });
    
            if (response.ok) {
                alert('Task created successfully!');
                fetchTasks(); // Osvežimo seznam nalog
                taskForm.reset(); // Počistimo obrazec
            } else {
                const errorText = await response.text();
                console.error('Failed to save the task:', errorText);
                alert(`Failed to save the task: ${errorText}`);
            }
        } catch (error) {
            console.error('Error saving task:', error);
            alert('An unexpected error occurred while saving the task.');
        } finally {
            submitButton.disabled = false; // Vedno omogočimo gumb po zaključku
        }
    });
    

    // Delete a task
    async function deleteTask(taskId) {
        await fetch(`/tasks/${taskId}`, {
            method: 'DELETE',
            headers: { 'user-id': userId },
        });
        fetchTasks();
    }

    // Geokodiranje naslova (pretvorba v latitude/longitude)
async function geocodeAddress(address) {
    const response = await fetch(`https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(address)}&key=YOUR_API_KEY`);
    const data = await response.json();
    if (data.results.length > 0) {
        return {
            latitude: data.results[0].geometry.location.lat,
            longitude: data.results[0].geometry.location.lng,
        };
    }
    return null;
}

// Pošiljanje nove naloge z lokacijo
taskForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const taskLocation = document.getElementById('taskLocation').value;

        let geocodedLocation = null;
    if (taskLocation) {
        try {
            geocodedLocation = await geocodeAddress(taskLocation);
            console.log('Geocoded Location:', geocodedLocation);
        } catch (error) {
            console.error('Error geocoding address:', error);
            alert('Failed to geocode the address. Please try again or leave it blank.');
            return;
        }
    }

    const taskData = {
        taskName: document.getElementById('taskName').value,
        taskType: { id: parseInt(taskTypeSelect.value) },
        description: document.getElementById('taskDescription').value,
        dueDateTime: document.getElementById('dueDate').value,
        locationAddress: taskLocation || null, // Naslov
        latitude: geocodedLocation ? geocodedLocation.latitude : null, // Latitude
        longitude: geocodedLocation ? geocodedLocation.longitude : null, // Longitude
        user: { id: parseInt(userId) },
    };
    

    const response = await fetch('/tasks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'user-id': userId },
        body: JSON.stringify(taskData),
    });

    if (response.ok) {
        alert('Task created successfully!');
        fetchTasks();
        taskForm.reset();
    } else {
        alert('Failed to save the task.');
    }
});


async function checkUserProximity() {
    if (!navigator.geolocation) {
        console.error("Geolocation is not supported by this browser.");
        return;
    }

    try {
        navigator.geolocation.getCurrentPosition(async (position) => {
            const userLatitude = position.coords.latitude;
            const userLongitude = position.coords.longitude;

            console.log("User coordinates: ", userLatitude, userLongitude); // Dodano tukaj

            // Pridobi vse naloge z lokacijami
            const response = await fetch('/tasks', { headers: { 'user-id': localStorage.getItem('userId') } });

            if (!response.ok) {
                console.error("Failed to fetch tasks:", await response.text());
                return;
            }

            const tasks = await response.json();

            tasks.forEach(task => {
                if (task.latitude && task.longitude) {
                    const distance = calculateDistance(userLatitude, userLongitude, task.latitude, task.longitude);

                    console.log("Task coordinates: ", task.latitude, task.longitude); // Dodano tukaj
                    console.log("Calculated distance: ", distance); // Dodano tukaj

                    if (distance < 10) { // Če je uporabnik v radiju 0.5 km
                        notifyUser(task.taskName, distance);
                    }
                }
            });
        }, (error) => {
            console.error("Error obtaining geolocation:", error.message);
        });
    } catch (error) {
        console.error("Error checking user proximity:", error);
    }
}


// Funkcija za izračun razdalje med dvema točkama (Haversine formula)
function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // Polmer Zemlje v km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c; // Razdalja v km
}

// Funkcija za obveščanje uporabnika
function notifyUser(taskName, distance) {
    if ("Notification" in window && Notification.permission === "granted") {
        new Notification("Task Nearby", {
            body: `You're within ${distance.toFixed(2)} km of the location for task: ${taskName}`,
        });
    } else {
        alert(`You're near the location for task: ${taskName} (within ${distance.toFixed(2)} km)`);
    }
}

// Zahteva dovoljenje za obvestila ob nalaganju strani
if ("Notification" in window && Notification.permission !== "granted") {
    Notification.requestPermission().then(permission => {
        if (permission === "granted") {
            console.log("Notification permission granted.");
        }
    });
}




    
    
});