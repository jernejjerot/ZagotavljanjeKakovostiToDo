// index.js
import { geocodeAddress } from './locationUtils.js';

const API = 'http://localhost:8080';

document.addEventListener('DOMContentLoaded', () => {
  // --- state & elements ---
  const userId = localStorage.getItem('userId');
  const userName = localStorage.getItem('userName');
  const isAdmin = localStorage.getItem('isAdmin') === 'true';

  const navLinks = document.getElementById('navLinks');
  const userNameElement = document.getElementById('userName');
  const userActions = document.getElementById('userActions');
  const taskGrid = document.getElementById('taskGrid');
  const taskForm = document.getElementById('taskForm');
  const taskTypeSelect = document.getElementById('taskType');
  const tasksSection = document.getElementById('tasksSection');
  const createUserSection = document.getElementById('createUserSection');

  if (createUserSection) createUserSection.style.display = 'none';

  // ---------- helpers ----------
  function formatTimeLeft(ms) {
    const h = Math.floor(ms / 3600000);
    const m = Math.floor((ms % 3600000) / 60000);
    const s = Math.floor((ms % 60000) / 1000);
    return `${h}h ${m}m ${s}s`;
  }

  function setTasks(tasks) {
    taskGrid.innerHTML = '';
    const now = new Date();
    (tasks || []).forEach(t => renderTask(t, taskGrid, now));
  }

  function toIsoOrNull(value) {
    // Sprejme npr. "2025-10-30T10:00" ali prazen niz
    if (!value) return null;
    // Če je value že ISO, vrni kar je; sicer ga normaliziraj
    try {
      const dt = new Date(value);
      if (isNaN(dt.getTime())) return null;
      return dt.toISOString();
    } catch { return null; }
  }

  // ---------- ADMIN ----------
  if (isAdmin && userId) {
    if (createUserSection) createUserSection.style.display = 'block';

    const adminLink = document.createElement('li');
    adminLink.innerHTML = `<a href="/html/admin.html">Admin Page</a>`;
    navLinks?.appendChild(adminLink);

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
        const res = await fetch(`${API}/users/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(userData),
        });
        if (res.ok) {
          alert('User created successfully!');
          e.target.reset();
        } else {
          alert(`Failed to create user: ${await res.text()}`);
        }
      } catch (err) {
        console.error('Error creating user:', err);
      }
    });
  }

  // ---------- AUTH UI ----------
  if (!userId) {
    userActions.innerHTML = `
      <form id="loginForm">
        <input type="email" id="email" placeholder="Email" required>
        <input type="password" id="password" placeholder="Password" required>
        <button type="submit">Login</button>
      </form>`;
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
  } else {
    userActions.innerHTML = `
      <div class="logout-container">
        <p>Logged in as: ${userName}</p>
        <button id="logoutButton" class="logout-button">Logout</button>
      </div>`;
    document.getElementById('logoutButton').addEventListener('click', handleLogout);
    userNameElement.textContent = userName ?? '';
    tasksSection.style.display = 'block';
    loadTaskTypes();
    fetchTasks();
  }

  // gumb Cancel Edit
  if (taskForm) {
    const cancelEditButton = document.createElement('button');
    cancelEditButton.id = 'cancelEditButton';
    cancelEditButton.textContent = 'Cancel Edit';
    cancelEditButton.style.display = 'none';
    cancelEditButton.type = 'button';
    cancelEditButton.addEventListener('click', () => {
      editingTaskId = null;
      cancelEditButton.style.display = 'none';
      taskForm.reset();
    });
    taskForm.appendChild(cancelEditButton);
  }
  let editingTaskId = null;

  // ---------- API: GET tasks ----------
  async function fetchTasks() {
    const uid = localStorage.getItem('userId');
    if (!uid) {
      console.error('No userId in localStorage');
      return;
    }
    try {
      const res = await fetch(`${API}/tasks?userid=${uid}`, {
        headers: { 'user-id': uid }
      });
      if (!res.ok) {
        console.error('Failed to fetch tasks:', await res.text());
        return alert('Failed to load tasks!');
      }
      const tasks = await res.json();
      setTasks(tasks);
    } catch (err) {
      console.error('Error loading tasks:', err);
      alert('Failed to load tasks!');
    }
  }

  // ---------- AUTH handlers ----------
  async function handleLogin(e) {
    e.preventDefault();
    const loginData = {
      email: document.getElementById('email').value,
      password: document.getElementById('password').value,
    };
    try {
      const res = await fetch(`${API}/users/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loginData),
      });
      if (!res.ok) return alert('Login failed.');
      const user = await res.json();
      localStorage.setItem('userId', user.id);
      localStorage.setItem('userName', user.name ?? '');
      localStorage.setItem('isAdmin', String(!!user.admin));
      location.reload();
    } catch (err) {
      console.error('Error logging in:', err);
    }
  }

  function handleLogout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('isAdmin');
    alert('You have been logged out.');
    location.reload();
  }

  // ---------- Task types ----------
  async function loadTaskTypes() {
    try {
      const res = await fetch(`${API}/task-types`);
      if (!res.ok) throw new Error(await res.text());
      const types = await res.json();
      taskTypeSelect.innerHTML = (types || [])
        .map(t => `<option value="${t.id}">${t.type}</option>`)
        .join('');
    } catch (err) {
      console.error('Error loading task types:', err);
    }
  }

  // ---------- Render task ----------
  function renderTask(task, grid, now) {
    const dueTime = task.dueDateTime ? new Date(task.dueDateTime) : now;
    const timeLeft = dueTime - now;

    const taskName = task.taskName || 'Unnamed Task';
    const taskType = task.taskType?.type || 'Unknown';
    const description = task.description || 'No description provided.';

    const box = document.createElement('div');
    box.classList.add('task-box');
    box.dataset.taskId = task.id;
    if (task.isCompleted) box.classList.add('task-completed');

    const h = document.createElement('h4'); h.textContent = taskName;
    const ty = document.createElement('p'); ty.textContent = `Type: ${taskType}`;
    const de = document.createElement('p'); de.textContent = description;
    const dd = document.createElement('p'); dd.textContent = `Due: ${dueTime.toLocaleString()}`;

    if (!task.isCompleted && timeLeft > 0 && timeLeft < 86400000) {
      box.style.backgroundColor = '#ffcccc';
      const cd = document.createElement('p');
      const tick = () => {
        const left = new Date(task.dueDateTime) - new Date();
        cd.textContent = left <= 0 ? "Time's up!" : `Time left: ${formatTimeLeft(left)}`;
      };
      tick(); setInterval(tick, 1000);
      box.appendChild(cd);
    }

    const cb = document.createElement('input');
    cb.type = 'checkbox';
    cb.checked = !!task.isCompleted;
    cb.addEventListener('change', () => toggleCompletion(task.id, cb.checked));

    const loc = document.createElement('p');
    loc.textContent = task.locationAddress ? `Location: ${task.locationAddress}` : 'No location provided.';

    if (task.picture) {
      const img = document.createElement('img');
      img.src = task.picture;
      img.alt = 'Task Image';
      img.style.maxWidth = '200px';
      img.style.marginBottom = '10px';
      box.appendChild(img);
    }

    const editBtn = document.createElement('button');
    editBtn.textContent = 'Edit';
    editBtn.classList.add('edit-button');
    editBtn.addEventListener('click', () => populateTaskFormForEdit(task));

    const delBtn = document.createElement('button');
    delBtn.textContent = 'Delete';
    delBtn.classList.add('delete-button');
    delBtn.addEventListener('click', () => deleteTask(task.id));

    box.append(cb, h, ty, de, dd, loc, editBtn, delBtn);
    grid.appendChild(box);
  }

  // ---------- Toggle completion ----------
  async function toggleCompletion(taskId, complete) {
    const uid = localStorage.getItem('userId');
    if (!uid) return;

    try {
      const cur = await fetch(`${API}/tasks/${taskId}`, { headers: { 'user-id': uid } });
      if (!cur.ok) throw new Error(await cur.text());
      const task = await cur.json();
      task.isCompleted = !!complete;

      const res = await fetch(`${API}/tasks/${taskId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'user-id': uid },
        body: JSON.stringify(task),
      });
      if (!res.ok) throw new Error(await res.text());
      fetchTasks();
    } catch (err) {
      console.error('Error updating task:', err);
      alert('Error updating task.');
    }
  }

  // ---------- Edit ----------
  function populateTaskFormForEdit(task) {
    document.getElementById('taskName').value = task.taskName ?? '';
    document.getElementById('taskDescription').value = task.description ?? '';
    document.getElementById('dueDate').value = task.dueDateTime
      ? new Date(task.dueDateTime).toISOString().slice(0, 16)
      : '';
    taskTypeSelect.value = task.taskType?.id ?? '';
    document.getElementById('taskLocation').value = task.locationAddress ?? '';
    editingTaskId = task.id;
    document.getElementById('cancelEditButton').style.display = 'inline';
  }

  // ---------- Create / Update ----------
  taskForm?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const submitBtn = taskForm.querySelector('button[type="submit"]');
    if (submitBtn) submitBtn.disabled = true;

    try {
      const uid = localStorage.getItem('userId');
      if (!uid) { alert('You must be logged in.'); return; }

      const name = document.getElementById('taskName').value.trim();
      const desc = document.getElementById('taskDescription').value.trim();
      const typeId = parseInt(document.getElementById('taskType').value, 10);
      const dueIso = toIsoOrNull(document.getElementById('dueDate').value);
      const address = document.getElementById('taskLocation').value.trim();

      let coords = null;
      if (address) {
        try { coords = await geocodeAddress(address); }
        catch { /* geocoding optional */ }
      }

      const payload = {
        taskName: name,
        description: desc,
        taskType: { id: typeId },
        dueDateTime: dueIso,
        locationAddress: address || null,
        latitude: coords ? coords.latitude : null,
        longitude: coords ? coords.longitude : null,
        isCompleted: false,
        user: { id: parseInt(uid, 10) },
      };

      // CREATE ali UPDATE
      let createdTaskId = editingTaskId;
      if (editingTaskId) {
        const res = await fetch(`${API}/tasks/${editingTaskId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', 'user-id': uid },
          body: JSON.stringify(payload),
        });
        if (!res.ok) throw new Error(await res.text());
      } else {
        const res = await fetch(`${API}/tasks`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'user-id': uid },
          body: JSON.stringify(payload),
        });
        if (!res.ok) throw new Error(await res.text());
        const created = await res.json();
        createdTaskId = created.id;
      }

      // Upload slike (če je izbrana)
      const pictureInput = document.getElementById('taskPicture');
      if (pictureInput?.files?.length > 0 && createdTaskId) {
        const fd = new FormData();
        fd.append('picture', pictureInput.files[0]);
        const up = await fetch(`${API}/tasks/${createdTaskId}/upload`, {
          method: 'POST',
          headers: { 'user-id': uid }, // brez Content-Type
          body: fd,
        });
        if (!up.ok) throw new Error(await up.text());
      }

      alert(editingTaskId ? 'Task updated successfully!' : 'Task created successfully!');
      editingTaskId = null;
      document.getElementById('cancelEditButton').style.display = 'none';
      taskForm.reset();
      fetchTasks();
    } catch (err) {
      console.error('Error saving task:', err);
      alert('An unexpected error occurred while saving the task.');
    } finally {
      if (submitBtn) submitBtn.disabled = false;
    }
  });

  // ---------- Delete ----------
  async function deleteTask(taskId) {
    const uid = localStorage.getItem('userId');
    if (!uid) return;
  
    if (!confirm('Are you sure you want to delete this task?')) return;
  
    try {
      const res = await fetch(`${API}/tasks/${taskId}`, {
        method: 'DELETE',
        headers: { 'user-id': uid },
      });
  
      if (res.ok) {
        alert('Task deleted successfully.');
        fetchTasks(); // refresh list
      } else {
        const msg = await res.text();
        console.error('Delete failed:', msg);
        alert(`Failed to delete task: ${msg}`);
      }
    } catch (err) {
      console.error('Error deleting task:', err);
      alert('An unexpected error occurred while deleting the task.');
    }
  }

  // ---------- Proximity (opcijsko) ----------
  async function checkUserProximity() {
    if (!navigator.geolocation) return;
    const uid = localStorage.getItem('userId');
    if (!uid) return;

    navigator.geolocation.getCurrentPosition(async (pos) => {
      const { latitude: lat, longitude: lon } = pos.coords;
      const resp = await fetch(`${API}/tasks?userid=${uid}`, { headers: { 'user-id': uid } });
      if (!resp.ok) return;
      const tasks = await resp.json();
      tasks.forEach(t => {
        if (t.latitude && t.longitude) {
          const d = calculateDistance(lat, lon, t.latitude, t.longitude);
          if (d < 10) notifyUser(t.taskName, d);
        }
      });
    });
  }

  function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371, dLat = (lat2 - lat1) * Math.PI/180, dLon = (lon2 - lon1) * Math.PI/180;
    const a = Math.sin(dLat/2)**2 + Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dLon/2)**2;
    return 2*R*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  }

  function notifyUser(taskName, d) {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('Task Nearby', { body: `You're within ${d.toFixed(2)} km of: ${taskName}` });
    } else {
      console.log(`Near task: ${taskName} (${d.toFixed(2)} km)`);
    }
  }

  if ('Notification' in window && Notification.permission !== 'granted') {
    Notification.requestPermission();
  }
  checkUserProximity();
  setInterval(checkUserProximity, 60000);
});