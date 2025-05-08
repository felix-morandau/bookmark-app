import { useState } from 'react';
import axios from 'axios';

function ForgotPasswordPage() {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');

    async function sendEmail() {
        try {
            await axios.post('http://localhost:8080/forgot-password', { email });
            setMessage('Password reset link sent to your email.');
        } catch (error) {
            console.error(error);
            setMessage('Failed to send reset link.');
        }
    }


    return (
        <div>
            <h2>Forgot Password</h2>
            <input
                type="email"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <button onClick={sendEmail}>Send Reset Link</button>
            {message && <p>{message}</p>}
        </div>
    );
}

export default ForgotPasswordPage;
