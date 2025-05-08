import { useState, ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import LogoImage from '../../components/images/LogoImage';
import CollectionPost from '../../components/collection/CollectionPost';
import { useCollections } from '../../hooks/useCollections';
import './Dashboard.css';

function Dashboard() {
    const navigate = useNavigate();

    const user = sessionStorage.getItem('username');

    const [searchInput, setSearchInput] = useState("");
    const [categoryFilterInput, setCategoryFilterInput] = useState("");
    const [creatorFilterInput, setCreatorFilterInput] = useState("");

    const [submittedFilters, setSubmittedFilters] = useState({
        category: "",
        creator: ""
    });

    const { collections, loading, error } = useCollections('visible', submittedFilters.category, submittedFilters.creator);

    function goToCollections() {
        navigate(`/creator-collections/${user}`);
    }

    function goToBookmarks() {
        navigate(`/bookmarks/${user}`);
    }

    function goToSavedCollections() {
        navigate(`/saved-collections/${user}`);
    }

    function handleInputChange(event: ChangeEvent<HTMLInputElement>) {
        setSearchInput(event.target.value);
    }

    function handleCategoryFilterInputChange(event: ChangeEvent<HTMLInputElement>) {
        setCategoryFilterInput(event.target.value);
    }

    function handleCreatorFilterInputChange(event: ChangeEvent<HTMLInputElement>) {
        setCreatorFilterInput(event.target.value);
    }

    function handleSearchButtonClick() {
        setSubmittedFilters({
            category: categoryFilterInput,
            creator: creatorFilterInput,
        });
    }

    function logout() {
        sessionStorage.removeItem('username');
        sessionStorage.removeItem('role');
        sessionStorage.removeItem('token');
        navigate("/login");
    }

    return (
        <div className="dashboard-container">
            <header className="dashboard-header">
                <button className="dashboard-button" onClick={logout}>
                    Logout
                </button>
                <LogoImage />
                <h1>Dashboard</h1>
            </header>
            <nav className="dashboard-navbar">
                <input
                    type="text"
                    placeholder="Search collections..."
                    className="search-input"
                    value={searchInput}
                    onChange={handleInputChange}
                />
                <input
                    type="text"
                    className="filter-input"
                    placeholder="Category"
                    value={categoryFilterInput}
                    onChange={handleCategoryFilterInputChange}
                />
                <input
                    type="text"
                    className="filter-input"
                    placeholder="Creator"
                    value={creatorFilterInput}
                    onChange={handleCreatorFilterInputChange}
                />
                <button className="search-button" onClick={handleSearchButtonClick}>
                    Search
                </button>
            </nav>
            <div className="feed-container">
                {loading ? (
                    <p>Loading collections...</p>
                ) : error ? (
                    <p>Error loading collections.</p>
                ) : collections.length === 0 ? (
                    <p>No collections found.</p>
                ) : (
                    collections.map((collection) => (
                        <CollectionPost
                            key={collection.id}
                            id={collection.id}
                            name={collection.name}
                            category={collection.category}
                            description={collection.description}
                            creator={collection.creator}
                        />
                    ))
                )}
            </div>
            <footer className="button-container">
                <button className="dashboard-button" onClick={goToCollections}>
                    Collections
                </button>
                <button className="dashboard-button" onClick={goToBookmarks}>
                    Bookmarks
                </button>
                <button className="dashboard-button" onClick={goToSavedCollections}>
                    Saved Collections
                </button>
            </footer>
        </div>
    );
}

export default Dashboard;