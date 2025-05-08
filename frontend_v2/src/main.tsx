import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/LoginPage.tsx';
import RegisterPage from "./pages/RegisterPage.tsx";
import Dashboard from './pages/client/DashboardPage.tsx';
import UsersPage from './pages/admin/UsersPage.tsx';
import BookmarksPage from './pages/BookmarksPage.tsx';
import SavedCollectionsPage from './pages/client/SavedCollectionsPage.tsx';
import CollectionsPage from './pages/CollectionsPage.tsx'
import AuthenticatedRouteGuard from "./config/authenticatedRouteGuard.tsx";
import ForgotPasswordPage from "./pages/ForgotPasswordPage.tsx";
import ResetPasswordPage from "./pages/ResetPasswordPage.tsx";

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/forgot-password" element={<ForgotPasswordPage/>}/>
                <Route path="/reset-password" element={<ResetPasswordPage/>}/>
                <Route element={<AuthenticatedRouteGuard role="CLIENT"/>} >
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/admin/users" element={<UsersPage />} />
                    <Route path="/creator-collections/:username" element={<CollectionsPage />} />
                    <Route path="/bookmarks/:username" element={<BookmarksPage />} />
                    <Route path="/saved-collections/:username" element={<SavedCollectionsPage />} />
                </Route>
                <Route
                    path="*"
                    element={
                        <div className="app-container">
                            <h1>Page Not Found</h1>
                        </div>
                    }
                />
            </Routes>
        </Router>
    </StrictMode>,
);
