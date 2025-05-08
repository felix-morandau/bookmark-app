import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './LoginPage.css';

const RegisterPage: React.FC = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [type, setType] = useState('CLIENT');
    const [profilePhotoURL, setProfilePhotoURL] = useState('');
    const [bio, setBio] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (event: React.FormEvent) => {
        event.preventDefault();
        setErrorMessage('');

        // Basic password match validation
        if (password !== confirmPassword) {
            setErrorMessage("Passwords do not match.");
            return;
        }

        try {
            const response = await axios.post(
                'http://localhost:8080/users',
                { username, email, password, type, profilePhotoURL, bio },
                { headers: { 'Content-Type': 'application/json' } }
            );

            if (response.status === 200 || response.status === 201) {
                console.log('Registration successful:', response.data);
                navigate('/login');
            }
        } catch (error) {
            if (axios.isAxiosError(error)) {
                console.error('Registration failed:', error.response?.data);
                setErrorMessage(error.response?.data.errorMessage || 'Registration failed.');
            } else {
                console.error('An unexpected error occurred:', error);
                setErrorMessage('Failed to register. Please try again later.');
            }
        }
    };

    return (
        <div className="app-container">
            <h1>Register</h1>
            <form onSubmit={handleRegister}>
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
                    <label htmlFor="email">Email:</label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
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
                <div>
                    <label htmlFor="confirmPassword">Confirm Password:</label>
                    <input
                        type="password"
                        id="confirmPassword"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="type">User Type:</label>
                    <select
                        id="type"
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                        required
                    >
                        <option value="CLIENT">User</option>
                        <option value="ADMIN">Admin</option>
                        {/* Add other options as defined in your UserType enum */}
                    </select>
                </div>
                <div>
                    <label htmlFor="profilePhotoURL">Profile Photo URL:</label>
                    <input
                        type="text"
                        id="profilePhotoURL"
                        value={profilePhotoURL}
                        onChange={(e) => setProfilePhotoURL(e.target.value)}
                    />
                </div>
                <div>
                    <label htmlFor="bio">Bio:</label>
                    <textarea
                        id="bio"
                        value={bio}
                        onChange={(e) => setBio(e.target.value)}
                    />
                </div>
                {errorMessage && <p className="error-message">{errorMessage}</p>}
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default RegisterPage;
