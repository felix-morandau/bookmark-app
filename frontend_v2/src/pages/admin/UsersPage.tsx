import { useState, useEffect, ChangeEvent, FormEvent } from 'react';
import axios from 'axios';
import GenericTable from '../../components/GenericTable.tsx';
import { TableColumn } from 'react-data-table-component';
import { User } from '../../models/user.model.tsx';
import Button from '../../components/buttons/Button.tsx';
import Modal from '../../components/Modal.tsx';
import { Dropdown } from '../../components/Dropdown.tsx';
import { API_BASE_URL } from '../../constants/api.ts';
import './UsersPage.css';
import {useNavigate} from "react-router-dom";

export function UsersPage() {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const navigate = useNavigate();

    const [isAddModalOpen, setAddModalOpen] = useState(false);
    const [isUpdateModalOpen, setUpdateModalOpen] = useState(false);

    const [formData, setFormData] = useState({
        username: '',
        password: '',
        email: '',
        type: 'CLIENT',
        bio: '',
        profilePhotoURL: '',
    });

    const userTypes = [
        { label: 'Client', value: 'CLIENT' },
        { label: 'Admin', value: 'ADMIN' },
    ];

    const columns: TableColumn<User>[] = [
        { name: 'ID', selector: (row) => row.id, sortable: true },
        { name: 'Username', selector: (row) => row.username, sortable: true },
        { name: 'Password', selector: (row) => row.password, sortable: false },
        { name: 'Email', selector: (row) => row.email, sortable: true },
        { name: 'User Type', selector: (row) => row.type, sortable: true },
        { name: 'Bio', selector: (row) => row.bio, sortable: false },
        { name: 'Profile Photo', selector: (row) => row.profilePhotoURL, sortable: false },
    ];

    useEffect(() => {
        fetchUsers();
    }, []);

    function fetchUsers() {
        axios
            .get(`${API_BASE_URL}/users`, {
                headers: {
                    Authorization: `Bearer ${sessionStorage.getItem('token')}`
                }
            })
            .then((response) => {
                const dataArray = Array.isArray(response.data)
                    ? response.data
                    : response.data.users;
                setUsers(Array.isArray(dataArray) ? dataArray : []);
                setLoading(false);
            })
            .catch((error) => {
                console.error('Error fetching users', error);
                setError(true);
                setLoading(false);
            });
    }

    function handleRowSelected(state: { selectedRows: User[] }) {
        if (state.selectedRows.length > 0) {
            setSelectedUser(state.selectedRows[0]);
        } else {
            setSelectedUser(null);
        }
    }

    function handleAdd() {
        setFormData({
            username: '',
            password: '',
            email: '',
            type: 'CLIENT',
            bio: '',
            profilePhotoURL: '',
        });
        setAddModalOpen(true);
    }

    function handleDropdownChange(newValue: string) {
        setFormData((prev) => ({ ...prev, type: newValue }));
    }

    function handleAddSubmit(event: FormEvent) {
        event.preventDefault();

        const payload = {
            username: formData.username,
            email: formData.email,
            password: formData.password,
            type: formData.type,
            bio: formData.bio,
            profilePhotoURL: formData.profilePhotoURL,
        };

        axios
            .post(`${API_BASE_URL}/users`, payload, {
                headers: {
                    'Content-Type': 'application/json' ,
                    Authorization : `Bearer ${sessionStorage.getItem('token')}`
                },
            })
            .then((response) => {
                console.log('User added:', response.data);
                setAddModalOpen(false);
                fetchUsers();
            })
            .catch((error) => {
                console.error('Error adding user', error);
            });
    }

    function handleUpdate() {
        if (!selectedUser) {
            alert('Please select a user to update.');
            return;
        }
        setFormData({
            username: selectedUser.username,
            password: selectedUser.password,
            email: selectedUser.email,
            type: selectedUser.type,
            bio: selectedUser.bio || '',
            profilePhotoURL: selectedUser.profilePhotoURL || '',
        });
        setUpdateModalOpen(true);
    }

    function handleUpdateSubmit(event: FormEvent) {
        event.preventDefault();
        if (!selectedUser) {
            alert('No user selected for update.');
            return;
        }
        axios
            .put(`${API_BASE_URL}/users/${selectedUser.username}`, formData, {
                headers: {
                    'Content-Type': 'application/json' ,
                    Authorization : `Bearer ${sessionStorage.getItem('token')}`
                },
            })
            .then(() => {
                setUpdateModalOpen(false);
                fetchUsers();
            })
            .catch((error) => {
                console.error('Error updating user', error);
            });
    }

    function handleDelete() {
        if (!selectedUser) {
            alert('Please select a user to delete.');
            return;
        }
        if (!window.confirm(`Are you sure you want to delete "${selectedUser.username}"?`)) {
            return;
        }
        axios
            .delete(`${API_BASE_URL}/users/${selectedUser.username}`, {
                headers: {
                    Authorization : `Bearer ${sessionStorage.getItem('token')}`
                },
            })
            .then(() => {
                setSelectedUser(null);
                fetchUsers();
            })
            .catch((error) => {
                console.error('Error deleting user', error);
            });
    }

    function handleInputChange(event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        const { name, value } = event.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    }

    function handleSeeBookmarks() {
        if (!selectedUser || (Array.isArray(selectedUser) && selectedUser.length !== 1)) {
            alert('Please select one user to view bookmarks.');
            return;
        }
        navigate(`/bookmarks/${selectedUser.username}`);
    }


    function handleSeeCollections() {
        if (!selectedUser || (Array.isArray(selectedUser) && selectedUser.length !== 1)) {
            alert('Please select one user to view collections.');
            return;
        }

        navigate(`/creator-collections/${selectedUser.username}`);
    }

    return (
        <div>
            <h1>Users</h1>
            <div className="button-container">
                <Button name="Add User" onClick={handleAdd} />
                <Button name="Update User" onClick={handleUpdate} />
                <Button name="Delete User" onClick={handleDelete} />
                <Button name="See Bookmarks" onClick={handleSeeBookmarks} />
                <Button name="See Collections" onClick={handleSeeCollections} />
            </div>
            <GenericTable
                title="Users"
                columns={columns}
                data={users || []}
                loading={loading}
                isError={error}
                onRowSelected={handleRowSelected}
                theme="light"
            />

            <Modal isOpen={isAddModalOpen} title="Add User" onClose={() => setAddModalOpen(false)}>
                <form onSubmit={handleAddSubmit}>
                    <label>
                        Username:
                        <input type="text" name="username" value={formData.username} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Email:
                        <input type="email" name="email" value={formData.email} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Password:
                        <input type="password" name="password" value={formData.password} onChange={handleInputChange} required />
                    </label>
                    <label>
                        User Type:
                        <Dropdown
                            options={userTypes}
                            value={formData.type}
                            onChange={handleDropdownChange}
                        />
                    </label>
                    <label>
                        Bio:
                        <textarea name="bio" value={formData.bio} onChange={handleInputChange} />
                    </label>
                    <label>
                        Profile Photo URL:
                        <input type="text" name="profilePhotoURL" value={formData.profilePhotoURL} onChange={handleInputChange} />
                    </label>
                    <div className="modal-buttons">
                        <button type="submit">Add</button>
                        <button type="button" onClick={() => setAddModalOpen(false)}>Cancel</button>
                    </div>
                </form>
            </Modal>

            <Modal isOpen={isUpdateModalOpen} title="Update User" onClose={() => setUpdateModalOpen(false)}>
                <form onSubmit={handleUpdateSubmit}>
                    <label>
                        Password:
                        <input type="password" name="password" value={formData.password} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Email:
                        <input type="email" name="email" value={formData.email} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Bio:
                        <textarea name="bio" value={formData.bio} onChange={handleInputChange} />
                    </label>
                    <label>
                        Profile Photo URL:
                        <input type="text" name="profilePhotoURL" value={formData.profilePhotoURL} onChange={handleInputChange} />
                    </label>
                    <div className="modal-buttons">
                        <button type="submit">Update</button>
                        <button type="button" onClick={() => setUpdateModalOpen(false)}>Cancel</button>
                    </div>
                </form>
            </Modal>
        </div>
    );
}

export default UsersPage;
