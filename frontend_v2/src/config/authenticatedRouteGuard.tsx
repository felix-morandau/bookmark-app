import { Outlet, Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import TokenPayload from "../models/tokenPayload.tsx";

type Role = 'CLIENT' | 'ADMIN';

interface AuthenticatedRouteGuardProps {
    role: Role;
}

const AuthenticatedRouteGuard = ({role}: AuthenticatedRouteGuardProps) => {
    const token = sessionStorage.getItem("token");

    if (!token) {
        return <Navigate to="/login" />;
    }

    try {
        const decodedToken = jwtDecode<TokenPayload>(token);

        const isIssuerValid = decodedToken.iss === "bookmark-app-backend";
        const isNotExpired = decodedToken.exp * 1000 > Date.now();
        const hasRequiredClaims = decodedToken.username && decodedToken.role === 'ADMIN' || role;

        if (!isIssuerValid || !isNotExpired || !hasRequiredClaims) {
            return <Navigate to="/login" />;
        }
    } catch (error) {
        console.error("Invalid token:", error);
        return <Navigate to="/login" />;
    }

    return (
        <div>
            <Outlet />
        </div>
    );
};

export default AuthenticatedRouteGuard;