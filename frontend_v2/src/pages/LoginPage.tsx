import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './LoginPage.css';

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    function handleLogin(event: React.FormEvent) {
        event.preventDefault();
        setErrorMessage('');

        axios
            .post(
                'http://localhost:8080/login',
                { username, password },
                { headers: { 'Content-Type': 'application/json' } }
            )
            .then((response) => {
                if (response.status === 200) {
                    const data = response.data;

                    sessionStorage.setItem('token', data.token);
                    sessionStorage.setItem('role', data.role);
                    sessionStorage.setItem('username', username);
                    console.log('Login successful:', data);

                    if (response.data.role === 'ADMIN') {
                        navigate('/admin/users');
                    } else if (response.data.role === 'CLIENT') {
                        navigate('/dashboard');
                    } else {
                        navigate('/dashboard');
                    }
                }
            })
            .catch((error) => {
                if (axios.isAxiosError(error) && error.response?.status === 401) {
                    console.error('Login failed:', error.response.data);
                    setErrorMessage(error.response.data.errorMessage || 'Unauthorized');
                } else {
                    console.error('An unexpected error occurred:', error);
                    setErrorMessage('Failed to login. Please try again later.');
                }
            });
    }
    return (
        <div className="app-container">
            <h1>Login</h1>
            <form onSubmit={handleLogin}>
                <div>
                    <label htmlFor="username">Username:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                {errorMessage && <p className="error-message">{errorMessage}</p>}
                <button type="submit">Login</button>
                <button onClick={() => navigate('/forgot-password')}>Forgot Password?</button>
            </form>
            <div className="register-label">
                <p>Don't have an account?</p>
                <button onClick={() => navigate('/register')}>Register</button>
            </div>
        </div>
    );
}

export default LoginPage;
