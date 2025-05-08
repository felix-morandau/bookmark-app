// BookmarksPage.tsx
import { useState, useEffect, ChangeEvent, FormEvent } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import GenericTable from '../components/GenericTable';
import { TableColumn } from 'react-data-table-component';
import { Bookmark } from '../models/bookmark.model';
import Button from '../components/buttons/Button';
import Modal from '../components/Modal';
import { API_BASE_URL } from '../constants/api';
import { Dropdown, Option } from '../components/Dropdown';
import './BookmarksPage.css';

interface CollectionFormData {
    title: string;
    description: string;
    category: string;
    url: string;
}

function BookmarksPage() {
    // grab username from URL
    const { username } = useParams<{ username: string }>();
    const user = username ?? '';

    const [bookmarks, setBookmarks] = useState<Bookmark[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [selectedBookmark, setSelectedBookmark] = useState<Bookmark | null>(null);

    const [isAddModalOpen, setAddModalOpen] = useState(false);
    const [isUpdateModalOpen, setUpdateModalOpen] = useState(false);

    const [formData, setFormData] = useState<CollectionFormData>({
        title: '',
        description: '',
        category: 'EDUCATION',
        url: '',
    });

    const bookmarkCategories: Option[] = [
        { label: 'EDUCATION', value: 'EDUCATION' },
        { label: 'WORK', value: 'WORK' },
        { label: 'LEARNING', value: 'LEARNING' },
        { label: 'PRODUCTIVITY', value: 'PRODUCTIVITY' },
        { label: 'TECHNOLOGY', value: 'TECHNOLOGY' },
        { label: 'WEB_DEVELOPMENT', value: 'WEB_DEVELOPMENT' },
        { label: 'FINANCE', value: 'FINANCE' },
        { label: 'DESIGN', value: 'DESIGN' },
        { label: 'RESEARCH', value: 'RESEARCH' },
        { label: 'NEWS', value: 'NEWS' },
        { label: 'GAMING', value: 'GAMING' },
        { label: 'MUSIC', value: 'MUSIC' },
        { label: 'MOVIES_AND_TV', value: 'MOVIES_AND_TV' },
        { label: 'SHOPPING', value: 'SHOPPING' },
        { label: 'COOKING', value: 'COOKING' },
        { label: 'FITNESS', value: 'FITNESS' },
        { label: 'TRAVEL', value: 'TRAVEL' },
        { label: 'ENVIRONMENT', value: 'ENVIRONMENT' },
        { label: 'RESOURCES_AND_TOOLS', value: 'RESOURCES_AND_TOOLS' },
        { label: 'COMEDY', value: 'COMEDY' },
    ];

    const columns: TableColumn<Bookmark>[] = [
        { name: 'ID', selector: row => row.id, sortable: true },
        { name: 'Title', selector: row => row.title, sortable: true },
        { name: 'Description', selector: row => row.description, sortable: false },
        { name: 'Category', selector: row => row.category, sortable: true },
        { name: 'Created At', selector: row => row.createdAt, sortable: true },
        { name: 'URL', selector: row => row.link.url, sortable: false },
    ];

    useEffect(() => {
        fetchBookmarks();
    }, [user]);

    function fetchBookmarks() {
        setLoading(true);
        axios
            .get(`${API_BASE_URL}/${user}/bookmarks`, {
                headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` },
            })
            .then(response => {
                const data = Array.isArray(response.data)
                    ? response.data
                    : response.data.bookmarks;
                setBookmarks(data);
                setLoading(false);
            })
            .catch(err => {
                console.error('Error fetching bookmarks', err);
                setError(true);
                setLoading(false);
            });
    }

    function handleRowSelected(state: { selectedRows: Bookmark[] }) {
        setSelectedBookmark(state.selectedRows[0] || null);
    }

    function handleAdd() {
        setFormData({ title: '', description: '', category: 'EDUCATION', url: '' });
        setAddModalOpen(true);
    }

    function handleCategoryChange(value: string) {
        setFormData(prev => ({ ...prev, category: value }));
    }

    function handleAddSubmit(e: FormEvent) {
        e.preventDefault();
        axios
            .post(
                `${API_BASE_URL}/${user}/bookmarks/add_bookmark`,
                formData,
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
                    },
                }
            )
            .then(() => {
                setAddModalOpen(false);
                fetchBookmarks();
            })
            .catch(err => console.error('Error adding bookmark', err));
    }

    function handleUpdate() {
        if (!selectedBookmark) {
            alert('Please select a bookmark to update.');
            return;
        }
        setFormData({
            title: selectedBookmark.title,
            description: selectedBookmark.description,
            category: selectedBookmark.category,
            url: selectedBookmark.link.url,
        });
        setUpdateModalOpen(true);
    }

    function handleUpdateSubmit(e: FormEvent) {
        e.preventDefault();
        if (!selectedBookmark) return;
        axios
            .put(
                `${API_BASE_URL}/${user}/bookmarks/bookmark/${selectedBookmark.id}`,
                formData,
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
                    },
                }
            )
            .then(() => {
                setUpdateModalOpen(false);
                fetchBookmarks();
            })
            .catch(err => console.error('Error updating bookmark', err));
    }

    function handleDelete() {
        if (!selectedBookmark) {
            alert('Please select a bookmark to delete.');
            return;
        }
        if (!window.confirm(`Delete "${selectedBookmark.title}"?`)) return;
        axios
            .delete(
                `${API_BASE_URL}/${user}/bookmarks/bookmark/${selectedBookmark.id}`,
                {
                    headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` },
                }
            )
            .then(() => {
                setSelectedBookmark(null);
                fetchBookmarks();
            })
            .catch(err => console.error('Error deleting bookmark', err));
    }

    function handleInputChange(e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    }

    return (
        <div className="bookmarks-page">
            <h1>Bookmarks for {user}</h1>
            <div className="button-container">
                <Button name="Add" onClick={handleAdd} />
                <Button name="Update" onClick={handleUpdate} />
                <Button name="Delete" onClick={handleDelete} />
            </div>

            <GenericTable
                title="Bookmarks"
                columns={columns}
                data={bookmarks}
                loading={loading}
                isError={error}
                onRowSelected={handleRowSelected}
                theme="light"
            />

            <Modal isOpen={isAddModalOpen} title="Add Bookmark" onClose={() => setAddModalOpen(false)}>
                <form onSubmit={handleAddSubmit}>
                    <label>
                        Title:
                        <input name="title" value={formData.title} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Description:
                        <textarea name="description" value={formData.description} onChange={handleInputChange} />
                    </label>
                    <label>
                        Category:
                        <Dropdown options={bookmarkCategories} value={formData.category} onChange={handleCategoryChange} />
                    </label>
                    <label>
                        URL:
                        <input name="url" value={formData.url} onChange={handleInputChange} required />
                    </label>
                    <div className="modal-buttons">
                        <button type="submit">Add</button>
                        <button type="button" onClick={() => setAddModalOpen(false)}>Cancel</button>
                    </div>
                </form>
            </Modal>

            <Modal isOpen={isUpdateModalOpen} title="Update Bookmark" onClose={() => setUpdateModalOpen(false)}>
                <form onSubmit={handleUpdateSubmit}>
                    <label>
                        Title:
                        <input name="title" value={formData.title} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Description:
                        <textarea name="description" value={formData.description} onChange={handleInputChange} />
                    </label>
                    <label>
                        Category:
                        <Dropdown options={bookmarkCategories} value={formData.category} onChange={handleCategoryChange} />
                    </label>
                    <label>
                        URL:
                        <input name="url" value={formData.url} onChange={handleInputChange} required />
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

export default BookmarksPage;
