import { useState } from 'react';
import Button from "../buttons/Button";
import axios from 'axios';
import Modal from '../Modal';
import { API_BASE_URL } from '../../constants/api';
import "./CollectionPost.css";
import { Bookmark } from '../../models/bookmark.model';

interface CollectionProps {
    id: string;
    name: string;
    category: string;
    description: string;
    creator: string;
}

function CollectionPost({ id, name, category, description, creator }: CollectionProps) {
    const currentUser = sessionStorage.getItem('username');

    const [collectionBookmarks, setCollectionBookmarks] = useState<Bookmark[]>([]);
    const [isViewModalOpen, setViewModalOpen] = useState(false);

    const [showAddBookmarkTable, setShowAddBookmarkTable] = useState(false);
    const [userBookmarks, setUserBookmarks] = useState<Bookmark[]>([]);
    const [selectedBookmarks, setSelectedBookmarks] = useState<string[]>([]);

    const [exportFormat, setExportFormat] = useState<'json'|'csv'|'plaintext'>('json');
    const [exportedData, setExportedData] = useState<string>('');
    const [isExportModalOpen, setExportModalOpen] = useState(false);

    function handleViewButton() {
        axios.get(`${API_BASE_URL}/collections/${id}/bookmarks`,
            {
                headers: {
                    Authorization: `Bearer ${sessionStorage.getItem("token")}`
                }
            })
            .then((response) => {
                const bookmarks = Array.isArray(response.data) ? response.data : [];
                setCollectionBookmarks(bookmarks);
                setViewModalOpen(true);
            })
            .catch((error) => {
                console.error('Error fetching collection bookmarks:', error);
            });
    }

    function handleSaveButton() {
        const token = sessionStorage.getItem("token");
        axios.post(
            `${API_BASE_URL}/users/${currentUser}/save-collection/${id}`,
            {},                                 // <-- empty body
            {                                   // <-- this is the config object
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        )
            .then(resp => console.log("Collection saved:", resp.data))
            .catch(err => console.error("Error saving collection:", err));
    }


    function handleAddBookmarkClick() {
        axios
            .get(
                `${API_BASE_URL}/${currentUser}/bookmarks`,
                {
                    headers: {
                        Authorization: `Bearer ${sessionStorage.getItem("token")}`
                    }
                })
            .then((response) => {
                const bookmarks = Array.isArray(response.data) ? response.data : [];
                setUserBookmarks(bookmarks);
                setShowAddBookmarkTable(true);
            })
            .catch((error) => {
                console.error('Error fetching user bookmarks:', error);
            });
    }

    function handleBookmarkSelection(bookmarkId: string, isChecked: boolean) {
        if (isChecked) {
            setSelectedBookmarks(prev => [...prev, bookmarkId]);
        } else {
            setSelectedBookmarks(prev => prev.filter(id => id !== bookmarkId));
        }
    }

    function handleFinalAddBookmarks() {
        axios.post(`${API_BASE_URL}/${id}/bookmarks`,
            selectedBookmarks,
            { headers: {
                    'Content-Type': 'application/json' ,
                    Authorization: `Bearer ${sessionStorage.getItem("token")}`
                }
            }
        )
            .then((response) => {
                console.log('Bookmarks added:', response.data);
                setCollectionBookmarks(prev => [...prev, ...userBookmarks.filter(bm => selectedBookmarks.includes(bm.id))]);
                setSelectedBookmarks([]);
                setShowAddBookmarkTable(false);
            })
            .catch((error) => {
                console.error('Error adding bookmarks to collection:', error);
            });
    }

    function handleCancelAddBookmark() {
        setShowAddBookmarkTable(false);
        setSelectedBookmarks([]);
    }

    function handleExport() {
        axios.get(
            `${API_BASE_URL}/collections/${id}/export`,
            {
                params: { format: exportFormat },
                responseType: 'text',
                headers: {
                    Authorization: `Bearer ${sessionStorage.getItem("token")}`
                }
            }
        )
            .then(response => {
                setExportedData(response.data);
                setExportModalOpen(true);
            })
            .catch(error => {
                console.error('Error exporting collection:', error);
            });
    }

    function renderAddBookmarkTable() {
        return (
            <div className="add-bookmark-table">
                <h3>Select Bookmarks to Add</h3>
                <table>
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Select</th>
                    </tr>
                    </thead>
                    <tbody>
                    {userBookmarks.map((bookmark) => (
                        <tr key={bookmark.id}>
                            <td>{bookmark.title}</td>
                            <td>
                                <input
                                    type="checkbox"
                                    onChange={(e) => handleBookmarkSelection(bookmark.id, e.target.checked)}
                                    checked={selectedBookmarks.includes(bookmark.id)}
                                />
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <div className="bookmark-table-actions">
                    <Button onClick={handleFinalAddBookmarks} name="Add" />
                    <Button onClick={handleCancelAddBookmark} name="Cancel" />
                </div>
            </div>
        );
    }

    function renderViewModal() {
        return (
            <Modal
                isOpen={isViewModalOpen}
                title="Collection Bookmarks"
                onClose={() => {
                    setViewModalOpen(false);
                    setShowAddBookmarkTable(false);
                }}
            >
                <div className="view-collection-modal">
                    <h2>{name}</h2>
                    <p><strong>Category:</strong> {category}</p>
                    <p><strong>Description:</strong> {description}</p>
                    <p><strong>Created by:</strong> {creator}</p>

                    <h3>Bookmarks:</h3>
                    {collectionBookmarks.length > 0 ? (
                        <ul>
                            {collectionBookmarks.map((bookmark) => (
                                <li key={bookmark.id}>{bookmark.title}</li>
                            ))}
                        </ul>
                    ) : (
                        <p>No bookmarks found.</p>
                    )}

                    {currentUser === creator && !showAddBookmarkTable && (
                        <Button onClick={handleAddBookmarkClick} name="Add Bookmark" />
                    )}
                    {showAddBookmarkTable && renderAddBookmarkTable()}

                    {/* Export controls */}
                    <div className="export-section">
                        <label htmlFor="exportFormat">Export as: </label>
                        <select
                            id="exportFormat"
                            value={exportFormat}
                            onChange={e => setExportFormat(e.target.value as never)}
                        >
                            <option value="json">JSON</option>
                            <option value="csv">CSV</option>
                            <option value="plaintext">Plain Text</option>
                        </select>
                        <Button onClick={handleExport} name="Export" />
                    </div>
                </div>
            </Modal>
        );
    }

    // New export modal
    function renderExportModal() {
        return (
            <Modal
                isOpen={isExportModalOpen}
                title={`Export (${exportFormat.toUpperCase()})`}
                onClose={() => setExportModalOpen(false)}
            >
                <pre className="exported-data">
                    {exportedData}
                </pre>
            </Modal>
        );
    }

    return (
        <div className="collection-post">
            <header className="collection-header">
                <h2 className="collection-title">{name}</h2>
                <p className="collection-creator">Created by {creator}</p>
            </header>

            <section className="collection-body">
                <div className="collection-category">
                    <strong>Category: </strong>{category}
                </div>
                <div className="collection-description">
                    {description}
                </div>
            </section>

            <footer className="collection-footer">
                <Button onClick={handleViewButton} name="View" />
                {currentUser !== creator && (
                    <Button onClick={handleSaveButton} name="Save" />
                )}
            </footer>

            {isViewModalOpen && renderViewModal()}
            {isExportModalOpen && renderExportModal()}
        </div>
    );
}

export default CollectionPost;
