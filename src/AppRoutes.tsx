import { Routes, Route } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import AuthPage from './pages/AuthPage';
import OAuthCallback from './pages/OAuthCallback';
import MainPage from './pages/MainPage';
import { useAuth } from './contexts/AuthContext';

function AppRoutes() {
    const { isAuthenticated } = useAuth();

    return (
        <Routes>
            <Route path="/" element={isAuthenticated ? <MainPage /> : <LandingPage />} />
            <Route path="/auth" element={<AuthPage />} />
            <Route path="/oauth/callback" element={<OAuthCallback />} />
        </Routes>
    );
}


export default AppRoutes;
