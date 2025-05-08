import { useState } from 'react';
import axios from 'axios';
import { useSearchParams } from 'react-router-dom';

function ResetPasswordPage() {
    const [searchParams] = useSearchParams();
    const [newPassword, setNewPassword] = useState('');
    const [message, setMessage] = useState('');

    const token = searchParams.get('token');

    async function resetPassword() {
        try {
            await axios.post('http://localhost:8080/reset-password', {
                token,
                newPassword
            });
            setMessage('Password has been reset successfully.');
        } catch (error) {
            console.error(error);
            setMessage('Failed to reset password.');
        }
    }

    return (
        <div>
            <h2>Reset Password</h2>
            <input
                type="password"
                placeholder="Enter new password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
            />
            <button onClick={resetPassword}>Reset Password</button>
            {message && <p>{message}</p>}
        </div>
    );
}

export default ResetPasswordPage;
