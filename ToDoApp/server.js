const express = require('express');
const path = require('path');
const axios = require('axios');

const app = express();
const port = 3000;

app.use(express.json()); // For parsing JSON requests
app.use(express.static(path.join(__dirname, 'frontend/html')));

// Fetch all tasks from the Spring Boot backend
app.get('/tasks', async (req, res) => {
    try {
        const response = await axios.get('http://localhost:8080/tasks');
        res.send(response.data);
    } catch (error) {
        console.error('Error fetching tasks from Spring Boot:', error);
        res.status(500).send('Error fetching tasks');
    }
});

// Add a new task
app.post('/tasks', async (req, res) => {
    try {
        const response = await axios.post('http://localhost:8080/tasks', req.body);
        res.send(response.data);
    } catch (error) {
        console.error('Error adding task to Spring Boot:', error);
        res.status(500).send('Error adding task');
    }
});

// Update a task
app.put('/tasks/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const response = await axios.put(`http://localhost:8080/tasks/${id}`, req.body);
        res.send(response.data);
    } catch (error) {
        console.error('Error updating task in Spring Boot:', error);
        res.status(500).send('Error updating task');
    }
});

// Delete a task
app.delete('/tasks/:id', async (req, res) => {
    try {
        const { id } = req.params;
        await axios.delete(`http://localhost:8080/tasks/${id}`);
        res.sendStatus(204);
    } catch (error) {
        console.error('Error deleting task in Spring Boot:', error);
        res.status(500).send('Error deleting task');
    }
});

// Proxy the login request to the Spring Boot backend
app.post('/users/login', async (req, res) => {
    try {
        const response = await axios.post('http://localhost:8080/users/login', req.body);
        res.send(response.data);
    } catch (error) {
        console.error('Error logging in to Spring Boot:', error);
        res.status(500).send('Error logging in');
    }
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
