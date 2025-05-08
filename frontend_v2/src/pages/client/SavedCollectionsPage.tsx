// SavedCollectionsPage.tsx
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import CollectionPost from '../../components/collection/CollectionPost';
import Button from '../../components/buttons/Button';
import { API_BASE_URL } from '../../constants/api';
import '../CollectionsPage.css';

function SavedCollectionsPage() {
    const { username } = useParams<{ username: string }>();
    const user = username ?? '';

    const [collections, setCollections] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    const fetchCollections = () => {
        setLoading(true);
        axios
            .get(`${API_BASE_URL}/users/${user}/saved-collections`, {
                headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` },
            })
            .then((response) => {
                setCollections(response.data);
                setLoading(false);
            })
            .catch(() => {
                setError(true);
                setLoading(false);
            });
    };

    useEffect(() => {
        if (user) {
            fetchCollections();
        }
    }, [user]);

    const handleDelete = (collectionId: string) => {
        axios
            .delete(`${API_BASE_URL}/users/${user}/unsave-collection/${collectionId}`, {
                headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` },
            })
            .then(() => {
                fetchCollections();
            })
            .catch((err) => {
                console.error('Error deleting collection:', err);
            });
    };

    return (
        <div className="collections-page">
            <header>
                <h1>{user}'s Saved Collections</h1>
            </header>

            <div className="collections-feed">
                {loading ? (
                    <p>Loading collections...</p>
                ) : error ? (
                    <p>Error loading collections.</p>
                ) : collections.length === 0 ? (
                    <p>No collections found.</p>
                ) : (
                    collections.map((collection) => (
                        <div className="post-container" key={collection.id}>
                            <CollectionPost
                                id={collection.id}
                                name={collection.name}
                                category={collection.category}
                                description={collection.description}
                                creator={collection.creator}
                            />
                            <Button name="Delete" onClick={() => handleDelete(collection.id)} />
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default SavedCollectionsPage;
