document.addEventListener("DOMContentLoaded", () => {
    const adminId = localStorage.getItem("userId");

    if (!adminId) {
        alert("Unauthorized access! Only admins can view this page.");
        window.location.href = "/login.html";
        return;
    }

    const logoutButton = document.getElementById("logoutButton");
    const usersTable = document.getElementById("usersTable").querySelector("tbody");
    const tasksTable = document.getElementById("tasksTable").querySelector("tbody");

    logoutButton.addEventListener("click", () => {
        localStorage.removeItem("userId");
        window.location.href = "/login.html";
    });

    async function fetchUsers() {
        try {
            const response = await fetch(`/users/all`, {
                headers: { "admin-id": adminId }
            });

            if (!response.ok) {
                throw new Error(await response.text());
            }

            const users = await response.json();
            usersTable.innerHTML = "";

            users.forEach(user => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.name}</td>
                    <td>${user.email}</td>
                    <td>${user.admin}</td>
                    <td>
                        <button onclick="deleteUser(${user.id})">Delete</button>
                        <button onclick="viewTasks(${user.id})">View Tasks</button>
                    </td>
                `;
                usersTable.appendChild(row);
            });
        } catch (error) {
            console.error("Error fetching users:", error);
        }
    }

    async function deleteUser(userId) {
        try {
            const response = await fetch(`/users/${userId}`, {
                method: "DELETE",
                headers: { "admin-id": adminId }
            });

            if (!response.ok) {
                throw new Error(await response.text());
            }

            alert("User deleted successfully!");
            fetchUsers();
        } catch (error) {
            console.error("Error deleting user:", error);
        }
    }

    async function viewTasks(userId) {
        try {
            const response = await fetch(`/users/${userId}/tasks`, {
                headers: { "admin-id": adminId }
            });

            if (!response.ok) {
                throw new Error(await response.text());
            }

            const tasks = await response.json();
            tasksTable.innerHTML = "";

            tasks.forEach(task => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${task.id}</td>
                    <td>${task.taskName}</td>
                    <td>${task.user.id}</td>
                    <td>${task.isCompleted}</td>
                    <td>
                        <button onclick="deleteTask(${task.id})">Delete</button>
                        <button onclick="editTask(${task.id})">Edit</button>
                    </td>
                `;
                tasksTable.appendChild(row);
            });
        } catch (error) {
            console.error("Error fetching tasks:", error);
        }
    }

    async function deleteTask(taskId) {
        try {
            const response = await fetch(`/users/tasks/${taskId}`, {
                method: "DELETE",
                headers: { "admin-id": adminId }
            });

            if (!response.ok) {
                throw new Error(await response.text());
            }

            alert("Task deleted successfully!");
            tasksTable.innerHTML = ""; // Clear tasks
        } catch (error) {
            console.error("Error deleting task:", error);
        }
    }

    fetchUsers();

    
});
