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

    // Added for admin page visibility
    if (isAdmin) {
        const adminLink = document.createElement("li");
        adminLink.innerHTML = `<a href="/html/admin.html">Admin Page</a>`;
        navLinks.appendChild(adminLink);
    }
    
    cancelEditButton.id = 'cancelEditButton';
    cancelEditButton.textContent = 'Cancel Edit';
    cancelEditButton.style.display = 'none';
    taskForm.appendChild(cancelEditButton);

    let editingTaskId = null;


    async function moveToDoneTasks(task) {
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
        console.log("Payload sent to /tasks/done:", payload); // Debugging
    
        // Ensure task object contains all necessary fields
        if (!task.taskName || !task.taskType || !task.taskType.id) {
            console.error("Task data is incomplete. Ensure taskName and taskType are present.");
            return;
        }
    
        try {
            const response = await fetch('/tasks/done', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'user-id': userId },
                body: JSON.stringify(task), // Send the full task object
            });
    
            if (!response.ok) {
                const errorText = await response.text();
                console.error(`Failed to move task to done: ${errorText}`);
                alert('Error moving task to done: ' + errorText);
                return;
            }
    
            console.log(`Task with ID ${task.id} moved to completed successfully.`);
            alert(`Task "${task.taskName}" moved to completed successfully!`);
        } catch (error) {
            console.error('Error moving task to done:', error.message);
            alert('Error moving task to done.');
        }
    }

    async function fetchTasks() {
        const response = await fetch('/tasks', { headers: { 'user-id': userId } });
        const tasks = await response.json();
        console.log("Fetched tasks:", tasks); // Debugging
        taskGrid.innerHTML = ''; // Clear the task grid
        const now = new Date();
    
        for (const task of tasks) {
            console.log("Rendering task:", task);
    
            const dueTime = new Date(task.dueDateTime);
            const timeLeft = dueTime - now;
    
            // If overdue and not completed, move to done tasks
            if (timeLeft <= 0 && !task.isCompleted) {
                await moveToDoneTasks(task);
                continue;
            }
    
            // Render only non-completed tasks
            if (!task.isCompleted) {
                renderTask(task, taskGrid, now);
            }
        }
    }

    // Display login/logout functionality
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

    // Login functionality
    async function handleLogin(event) {
        event.preventDefault();
        const loginData = {
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
        };
        const response = await fetch('/users/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginData),
        });

        if (response.ok) {
            const user = await response.json();
            localStorage.setItem('userId', user.id);
            localStorage.setItem('userName', user.name);
            location.reload();
        } else {
            alert('Login failed.');
        }
    }

    // Logout functionality
    function handleLogout() {
        localStorage.removeItem('userId');
        localStorage.removeItem('userName');
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

/*// Move tasks to completed tasks
async function moveToDoneTasks(task) {
    console.log("Payload sent to /tasks/done:", task);
    try {
        const response = await fetch('/tasks/done', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'user-id': userId },
            body: JSON.stringify(task), // Send the full task object
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error(`Failed to move task to done: ${errorText}`);
            return; // Stop further execution without showing alert
        }

        console.log(`Task with ID ${task.id} moved to completed successfully.`);
    } catch (error) {
        console.error('Error moving task to done:', error.message);
    }
}*/










  /*  // Fetch and render tasks
    async function fetchTasks() {
        const response = await fetch('/tasks', { headers: { 'user-id': userId } });
        const tasks = await response.json();
        console.log("Fetched tasks:", tasks); // Debugging
        taskGrid.innerHTML = ''; // Clear the task grid
        const now = new Date();
    
        for (const task of tasks) {

            // Debugging each task
        console.log("Rendering task:", task);

            const dueTime = new Date(task.dueDateTime);
            const timeLeft = dueTime - now;
    
            // If overdue and not completed, move to done tasks
            if (timeLeft <= 0 && !task.isCompleted) {
                await moveToDoneTasks(task);
                continue;
            }
    
            // Render only non-completed tasks
            if (!task.isCompleted) {
                renderTask(task, taskGrid, now);
            }
        }
    }*/
    

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
            taskBox.append(checkbox, title, type, descriptionElement, dueDate, editButton, deleteButton);
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
    taskForm.addEventListener('submit', async event => {
        event.preventDefault();
        const taskData = {
            taskName: document.getElementById('taskName').value,
            taskType: { id: parseInt(taskTypeSelect.value) },
            description: document.getElementById('taskDescription').value,
            dueDateTime: document.getElementById('dueDate').value,
        };

        const response = await fetch(editingTaskId ? `/tasks/${editingTaskId}` : '/tasks', {
            method: editingTaskId ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json', 'user-id': userId },
            body: JSON.stringify(taskData),
        });

        if (response.ok) {
            alert(editingTaskId ? 'Task updated successfully!' : 'Task created successfully!');
            fetchTasks();
            taskForm.reset();
            editingTaskId = null;
            cancelEditButton.style.display = 'none';
        } else {
            alert('Failed to save the task.');
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

    
    
});
