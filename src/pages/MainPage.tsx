import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
    const { logout, user } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <div style={{
            backgroundColor: 'black',
            minHeight: '100vh',
            display: 'flex',
            flexDirection: 'column',
            color: 'white'
        }}>
            <header style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '1rem 2rem',
                border: '1px solid #333',
                margin: '20px auto 0',
                width: '95%',
                maxWidth: '1200px',
                borderRadius: '16px',
                backgroundColor: '#0a0a0a',
                boxShadow: '0 4px 20px rgba(0,0,0,0.5)'
            }}>
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>
                    <span className="gradient-text">Uniforge</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <span>{user?.name}님 환영합니다</span>
                    <button
                        onClick={handleLogout}
                        style={{
                            padding: '0.5rem 1rem',
                            backgroundColor: '#333',
                            color: 'white',
                            border: '1px solid #555',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            fontSize: '0.9rem'
                        }}
                    >
                        로그아웃
                    </button>
                </div>
            </header>
            <main style={{
                flex: 1,
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                flexDirection: 'column',
                gap: '1rem'
            }}>
                <h1>Welcome to Your Workspace</h1>
                <p style={{ color: '#888' }}>Select a project or create a new one to get started.</p>
            </main>
        </div>
    );
};

export default MainPage;
