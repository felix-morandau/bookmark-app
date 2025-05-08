import { useState, ChangeEvent, FormEvent } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { useCollections } from '../hooks/useCollections';
import CollectionPost from '../components/collection/CollectionPost';
import Modal from '../components/Modal';
import { API_BASE_URL } from '../constants/api';
import './CollectionsPage.css';

interface Collection {
    id: string;
    name: string;
    category: string;
    description: string;
    creator: string;
    visible: boolean;
}

interface CollectionFormData {
    name: string;
    category: string;
    description: string;
    creator: string;
    visible: boolean;
}

function CollectionsPage() {

    const { username } = useParams<{ username: string }>();
    const user = username ?? '';

    const { collections, loading, error } = useCollections('', '', user);

    const [selectedCollection, setSelectedCollection] = useState<Collection | null>(null);
    const [isAddModalOpen, setAddModalOpen] = useState(false);
    const [isUpdateModalOpen, setUpdateModalOpen] = useState(false);

    const [formData, setFormData] = useState<CollectionFormData>({
        name: '',
        category: 'EDUCATION',
        description: '',
        creator: user,
        visible: false,
    });

    function handleRowSelected(collection: Collection) {
        setSelectedCollection(collection);
    }

    function handleAdd() {
        setFormData({ name: '', category: 'EDUCATION', description: '', creator: user, visible: false });
        setAddModalOpen(true);
    }

    function handleAddSubmit(event: FormEvent) {
        event.preventDefault();
        axios.post(`${API_BASE_URL}/${user}/collections/add_collection`, formData, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${sessionStorage.getItem('token')}`
            },
        })
            .then(response => {
                console.log('Collection added:', response.data);
                setAddModalOpen(false);
            })
            .catch(error => console.error('Error adding collection', error));
    }

    function handleUpdate() {
        if (!selectedCollection) { alert('Please select a collection to update.'); return; }
        setFormData({
            name: selectedCollection.name,
            category: selectedCollection.category,
            description: selectedCollection.description,
            creator: selectedCollection.creator,
            visible: selectedCollection.visible,
        });
        setUpdateModalOpen(true);
    }

    function handleUpdateSubmit(event: FormEvent) {
        event.preventDefault();
        if (!selectedCollection) { alert('No collection selected for update.'); return; }
        axios.put(`${API_BASE_URL}/${user}/collections/collection/update/${selectedCollection.id}`, formData, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${sessionStorage.getItem('token')}`
            },
        })
            .then(response => {
                console.log('Collection updated:', response.data);
                setUpdateModalOpen(false);
            })
            .catch(error => console.error('Error updating collection', error));
    }

    function handleDelete() {
        if (!selectedCollection) { alert('Please select a collection to delete.'); return; }
        if (!window.confirm(`Are you sure you want to delete "${selectedCollection.name}"?`)) return;
        axios.delete(`${API_BASE_URL}/${user}/collections/collection/${selectedCollection.id}`, {
            headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
        })
            .then(response => {
                console.log('Collection deleted:', response.data);
                setSelectedCollection(null);
            })
            .catch(error => console.error('Error deleting collection', error));
    }

    function handleInputChange(event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        const { name, value } = event.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    }

    function handleCheckboxChange(event: ChangeEvent<HTMLInputElement>) {
        const { name, checked } = event.target;
        setFormData(prev => ({ ...prev, [name]: checked }));
    }

    return (
        <div className="collections-page">
            <header><h1>{user}'s Collections</h1></header>
            <div className="action-buttons">
                <button className="action-button" onClick={handleAdd}>Add Collection</button>
                <button className="action-button" onClick={handleUpdate} disabled={!selectedCollection}>Update Collection</button>
                <button className="action-button" onClick={handleDelete} disabled={!selectedCollection}>Delete Collection</button>
            </div>
            <div className="collections-feed">
                {loading ? <p>Loading collections...</p>
                    : error ? <p>Error loading collections.</p>
                        : collections.length === 0 ? <p>No collections found for {user}.</p>
                            : collections.map(collection => (
                                <div key={collection.id}
                                     onClick={() => handleRowSelected(collection)}
                                     className={`post-container ${selectedCollection?.id === collection.id ? 'selected' : ''}`}
                                >
                                    <CollectionPost
                                        id={collection.id}
                                        name={collection.name}
                                        category={collection.category}
                                        description={collection.description}
                                        creator={collection.creator}
                                    />
                                </div>
                            ))}
            </div>
            <Modal isOpen={isAddModalOpen} title="Add Collection" onClose={() => setAddModalOpen(false)}>
                <form onSubmit={handleAddSubmit}>
                    <label>
                        Name:
                        <input type="text" name="name" value={formData.name} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Category:
                        <input type="text" name="category" value={formData.category} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Description:
                        <textarea name="description" value={formData.description} onChange={handleInputChange} />
                    </label>
                    <label>
                        Creator:
                        <input type="text" name="creator" value={formData.creator} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Visible:
                        <input type="checkbox" name="visible" checked={formData.visible} onChange={handleCheckboxChange} />
                    </label>
                    <div className="modal-buttons">
                        <button type="submit" className="action-button">Add</button>
                        <button type="button" className="action-button" onClick={() => setAddModalOpen(false)}>Cancel</button>
                    </div>
                </form>
            </Modal>
            <Modal isOpen={isUpdateModalOpen} title="Update Collection" onClose={() => setUpdateModalOpen(false)}>
                <form onSubmit={handleUpdateSubmit}>
                    <label>
                        Name:
                        <input type="text" name="name" value={formData.name} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Category:
                        <input type="text" name="category" value={formData.category} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Description:
                        <textarea name="description" value={formData.description} onChange={handleInputChange} />
                    </label>
                    <label>
                        Creator:
                        <input type="text" name="creator" value={formData.creator} onChange={handleInputChange} required />
                    </label>
                    <label>
                        Visible:
                        <input type="checkbox" name="visible" checked={formData.visible} onChange={handleCheckboxChange} />
                    </label>
                    <div className="modal-buttons">
                        <button type="submit" className="action-button">Update</button>
                        <button type="button" className="action-button" onClick={() => setUpdateModalOpen(false)}>Cancel</button>
                    </div>
                </form>
            </Modal>
        </div>
    );
}

export default CollectionsPage;
