import { useState, useEffect } from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../constants/api';

export function useCollections(visibility: string, category: string, creator: string) {
    const [collections, setCollections] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    useEffect(() => {
        setLoading(true);

        const params = {
            visible: (visibility === 'visible') ? true : null,
            category,
            creator
        };

        axios.get(`${API_BASE_URL}/collections`, {
            params,
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('token')}`,
            }
        })
            .then(response => {
                setCollections(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Error fetching collections", err);
                setError(true);
                setLoading(false);
            });

    }, [visibility, category, creator]);

    return { collections, loading, error };
}
