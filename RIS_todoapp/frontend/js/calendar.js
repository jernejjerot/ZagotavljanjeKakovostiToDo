import { Calendar } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';

// Inicializacija koledarja
document.addEventListener('DOMContentLoaded', async () => {
    const calendarEl = document.getElementById('calendar');

    if (!calendarEl) {
        console.error("Element with id 'calendar' not found.");
        return;
    }

    const calendar = new Calendar(calendarEl, {
        plugins: [dayGridPlugin, interactionPlugin],
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek',
        },
        events: async function(fetchInfo, successCallback, failureCallback) {
            try {
                const userId = localStorage.getItem("userId");
                if (!userId) {
                    console.error("User ID not found in localStorage.");
                    failureCallback(new Error("User not logged in."));
                    return;
                }

                const response = await fetch('/tasks', {
                    headers: { 'user-id': userId },
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch tasks');
                }

                const tasks = await response.json();
                const events = tasks.map(task => ({
                    id: task.id,
                    title: task.taskName,
                    start: task.dueDateTime,
                    description: task.description || 'No description provided',
                }));

                successCallback(events);
            } catch (error) {
                console.error("Error fetching events:", error);
                failureCallback(error);
            }
        },
        dateClick: function(info) {
            alert('Clicked on: ' + info.dateStr);
        },
    });

    calendar.render();

    // Funkcija za dodajanje novih nalog v koledar
    async function addTaskToCalendar(task) {
        try {
            const response = await fetch('/tasks', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'user-id': localStorage.getItem("userId"),
                },
                body: JSON.stringify(task),
            });

            if (!response.ok) {
                throw new Error(`Failed to save task: ${await response.text()}`);
            }

            const savedTask = await response.json();
            calendar.addEvent({
                id: savedTask.id,
                title: savedTask.taskName,
                start: savedTask.dueDateTime,
                description: savedTask.description || 'No description provided',
            });

            console.log("Task added to calendar:", savedTask);
            alert('Task successfully added to calendar!');
        } catch (error) {
            console.error("Error adding task:", error);
        }
    }

    // Povezava s formo za naloge
    const taskForm = document.getElementById('taskForm');
    if (taskForm) {
        taskForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const task = {
                taskName: document.getElementById('taskName').value,
                dueDateTime: document.getElementById('dueDate').value,
                description: document.getElementById('taskDescription').value,
            };
            await addTaskToCalendar(task);
            taskForm.reset();
        });
    }
});
