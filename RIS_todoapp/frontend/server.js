const express = require('express');
const path = require('path');
const axios = require('axios');

const app = express();
const port = 3000;

// Middleware to parse JSON requests
app.use(express.json());

// Serve static files
app.use('/css', express.static(path.join(__dirname, 'css'))); // Serve CSS
app.use('/js', express.static(path.join(__dirname, 'js')));   // Serve JS
app.use('/html', express.static(path.join(__dirname, 'html'))); // Serve HTML

app.get('/favicon.ico', (req, res) => {
    res.sendFile(path.join(__dirname, 'favicon.ico'));
});


// Default route to serve index.html
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'html/index.html'));
});

// Serve specific HTML pages
app.get('/register.html', (req, res) => {
    res.sendFile(path.join(__dirname, 'html/register.html'));
});

app.get('/login.html', (req, res) => {
    res.sendFile(path.join(__dirname, 'html/login.html'));
});

app.get('/editUser.html', (req, res) => {
    res.sendFile(path.join(__dirname, 'html/editUser.html'));
});

// Backend API base URL
const backendUrl = 'http://localhost:8080';

// Proxy API requests to the backend
// User registration
app.post('/users/register', async (req, res) => {
    try {
        const response = await axios.post(`${backendUrl}/users/register`, req.body);
        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error registering user.');
    }
});

// User login
app.post('/users/login', async (req, res) => {
    try {
        const response = await axios.post(`${backendUrl}/users/login`, req.body);
        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error logging in.');
    }
});

// Fetch task types
app.get('/task-types', async (req, res) => {
    try {
        const response = await axios.get(`${backendUrl}/task-types`);
        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error fetching task types.');
    }
});

// Fetch all tasks for a user
app.get('/tasks', async (req, res) => {
    try {
        const userId = req.headers['user-id'];
        if (!userId) {
            return res.status(401).send('User ID is required to fetch tasks.');
        }
        const response = await axios.get(`${backendUrl}/tasks`, {
            headers: { 'user-id': userId },
        });
        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error fetching tasks.');
    }
});

// Create a new task
app.post('/tasks', async (req, res) => {
    try {
        const userId = req.headers['user-id'];
        if (!userId) {
            return res.status(401).send('User ID is required to create a task.');
        }
        const response = await axios.post(`${backendUrl}/tasks`, req.body, {
            headers: { 'user-id': userId },
        });
        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error creating task.');
    }
});

// Fetch a single task by ID
app.get('/tasks/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const userId = req.headers['user-id'];
        if (!userId) {
            return res.status(401).send('User ID is required to fetch task.');
        }

        const response = await axios.get(`${backendUrl}/tasks/${id}`, {
            headers: { 'user-id': userId },
        });

        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error fetching task.');
    }
});

// Update a task
app.put('/tasks/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const userId = req.headers['user-id'];
        if (!userId) {
            return res.status(401).send('User ID is required to update a task.');
        }
        const response = await axios.put(`${backendUrl}/tasks/${id}`, req.body, {
            headers: { 'user-id': userId },
        });
        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error updating task.');
    }
});

// Delete a task
app.delete('/tasks/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const userId = req.headers['user-id'];
        if (!userId) {
            return res.status(401).send('User ID is required to delete a task.');
        }
        const response = await axios.delete(`${backendUrl}/tasks/${id}`, {
            headers: { 'user-id': userId },
        });
        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error deleting task.');
    }
});

// Fetch completed tasks for a user
app.get('/tasks/done', async (req, res) => {
    try {
        const userId = req.headers['user-id'];
        if (!userId) {
            return res.status(401).send('User ID is required to fetch completed tasks.');
        }

        const response = await axios.get(`${backendUrl}/tasks/done`, {
            headers: { 'user-id': userId },
        });
        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error fetching completed tasks.');
    }
});

// Move overdue/completed tasks to doneTasks.html
app.post('/tasks/done', async (req, res) => {
    try {
        const { id } = req.body; // Use task ID to update in the backend
        const userId = req.headers['user-id'];

        if (!userId) {
            return res.status(401).send('User ID is required to mark tasks as done.');
        }

        // Mark task as completed in the backend
        const response = await axios.put(`${backendUrl}/tasks/${id}`, {
            isCompleted: true,
        }, {
            headers: { 'user-id': userId },
        });

        res.status(response.status).send(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).send(error.response?.data || 'Error moving task to done.');
    }
});

//admin
app.get('/admin.html', (req, res) => {
    res.sendFile(path.join(__dirname, 'html/admin.html'));
});

// Start the server
app.listen(port, () => {
    console.log(`Frontend server running at http://localhost:${port}`);
});

