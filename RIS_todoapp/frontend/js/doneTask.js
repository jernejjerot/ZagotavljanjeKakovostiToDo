// Fetch and display completed or overdue tasks
async function fetchDoneTasks() {
    try {
        const response = await fetch('/tasks/done', {
            headers: { 'user-id': localStorage.getItem('userId') },
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to fetch done tasks: ${errorText}`);
        }

        const tasks = await response.json();
        const doneTaskGrid = document.getElementById('doneTaskGrid');
        doneTaskGrid.innerHTML = ''; // Clear existing tasks

        tasks.forEach(task => {
            const taskBox = document.createElement('div');
            taskBox.classList.add('task-box');
            taskBox.innerHTML = `
                <h4>${task.taskName}</h4>
                <p>Type: ${task.taskType?.type || 'Unknown'}</p>
                <p>${task.description || 'No description provided.'}</p>
                <p>Due: ${new Date(task.dueDateTime).toLocaleString()}</p>
            `;
            doneTaskGrid.appendChild(taskBox);
        });
    } catch (error) {
        console.error('Error fetching done tasks:', error);
    }
}


// Call the function on page load
fetchDoneTasks();
