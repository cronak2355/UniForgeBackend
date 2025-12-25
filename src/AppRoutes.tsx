import { Routes, Route } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import AuthPage from './pages/AuthPage';
import OAuthCallback from './pages/OAuthCallback';

function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/auth" element={<AuthPage />} />
            <Route path="/oauth/callback" element={<OAuthCallback />} />
        </Routes>
    );
}

export default AppRoutes;
