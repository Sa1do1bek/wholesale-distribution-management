// Main Application JavaScript
const API_BASE = '/api';

// Check authentication
function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login';
        return false;
    }
    
    displayUserInfo();
    return true;
}

// Display user info in sidebar
function displayUserInfo() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userInfoDiv = document.getElementById('userInfo');
    if (userInfoDiv && user.username) {
        userInfoDiv.innerHTML = `
            <div><strong>${user.username}</strong></div>
            <div style="font-size: 12px; color: #dfe6e9;">${user.role}</div>
        `;
    }
}

// Logout function
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
}

// API request helper
async function apiRequest(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem('token');
    
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };
    
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    const response = await fetch(`${API_BASE}${endpoint}`, options);
    
    if (response.status === 401) {
        logout();
        throw new Error('Unauthorized');
    }
    
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Request failed');
    }
    
    return response;
}

// Format currency
function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '$0.00';
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// Format date
function formatDate(dateString) {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Render pagination
function renderPagination(data, containerId, loadFunction) {
    const container = document.getElementById(containerId);
    if (!container) return;
    
    let html = '';
    
    // Previous button
    html += `<button ${data.first ? 'disabled' : ''} onclick="${loadFunction.name}(${data.page - 1})">Previous</button>`;
    
    // Page numbers
    const startPage = Math.max(0, data.page - 2);
    const endPage = Math.min(data.totalPages - 1, data.page + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        html += `<button class="${i === data.page ? 'active' : ''}" onclick="${loadFunction.name}(${i})">${i + 1}</button>`;
    }
    
    // Next button
    html += `<button ${data.last ? 'disabled' : ''} onclick="${loadFunction.name}(${data.page + 1})">Next</button>`;
    
    // Page info
    html += `<span style="margin-left: 16px; color: #636e72;">Page ${data.page + 1} of ${data.totalPages} (${data.totalElements} items)</span>`;
    
    container.innerHTML = html;
}
