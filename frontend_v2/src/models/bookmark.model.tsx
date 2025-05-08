// models/bookmark.model.ts
import { Link } from './link.model';

export interface Bookmark {
    id: string;
    title: string;
    description: string;
    category: string;
    createdAt: string;
    link: Link;
}
