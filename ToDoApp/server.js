const express = require('express');
const path = require('path');
const axios = require('axios');

const app = express();
const port = 3000;

app.use(express.json()); // For parsing JSON requests
app.use(express.static(path.join(__dirname, 'frontend/html')));

// Serve register and editUser pages
app.get('/register.html', (req, res) => {
    res.sendFile(path.join(__dirname, 'frontend/html/register.html'));
});

app.get('/editUser.html', (req, res) => {
    res.sendFile(path.join(__dirname, 'frontend/html/editUser.html'));
});

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

// Proxy the create account request
app.post('/users/create-account', async (req, res) => {
    try {
        const response = await axios.post('http://localhost:8080/users/create-account', req.body);
        res.send(response.data);
    } catch (error) {
        console.error('Error creating account:', error);
        res.status(error.response?.status || 500).send(error.response?.data || 'Error creating account');
    }
});

// Proxy the edit user request
app.put('/users/edit-user/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const response = await axios.put(`http://localhost:8080/users/edit-user/${id}`, req.body);
        res.send(response.data);
    } catch (error) {
        console.error('Error editing user:', error);
        res.status(error.response?.status || 500).send(error.response?.data || 'Error editing user');
    }
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
