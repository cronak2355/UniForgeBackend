import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useState, useRef, useEffect } from 'react';

const MainPage = () => {
    const { logout, user } = useAuth();
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

    // 더미 데이터: 인기 게임
    const POPULAR_GAMES = [
        { title: "Neon Racer 2077", author: "CyberDev", image: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=300" },
        { title: "Mystic Forest RPG", author: "FantasyWorks", image: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&q=80&w=300" },
        { title: "Space Commander", author: "StarLab", image: "https://images.unsplash.com/photo-1534237710431-e2fc698436d0?auto=format&fit=crop&q=80&w=300" },
        { title: "Pixel Dungeon", author: "RetroKing", image: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=300" },
        { title: "Sky Island", author: "CloudBreaker", image: "https://images.unsplash.com/photo-1579373903781-fd5c0c30c4cd?auto=format&fit=crop&q=80&w=300" },
        { title: "Shadow Ninja", author: "DarkBlade", image: "https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&q=80&w=300" },
        { title: "Block Builder", author: "VoxelMaster", image: "https://images.unsplash.com/photo-1574169208507-84376194878a?auto=format&fit=crop&q=80&w=300" },
    ];

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    // 드롭다운 외부 클릭 시 닫기
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setShowDropdown(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    return (
        <div className="dashboard-layout">
            <header className="glass-header">
                <div className="logo-container">
                    <div style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>
                        <span className="gradient-text">Uniforge</span>
                    </div>
                    <nav style={{ display: 'flex', gap: '1rem' }}>
                        <button className="nav-link" onClick={() => navigate('/assets')}>
                            Store
                        </button>
                        <button className="nav-link" onClick={() => navigate('/marketplace')}>
                            Explore
                        </button>
                    </nav>
                </div>

                <div style={{ position: 'relative' }} ref={dropdownRef}>
                    <button
                        className={`profile-btn ${showDropdown ? 'active' : ''}`}
                        onClick={() => setShowDropdown(!showDropdown)}
                    >
                        {user?.profileImage ? (
                            <img
                                src={user.profileImage}
                                alt="프로필"
                                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                            />
                        ) : (
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ccc" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                                <circle cx="12" cy="7" r="4" />
                            </svg>
                        )}
                    </button>

                    {showDropdown && (
                        <div className="profile-dropdown">
                            <div style={{ padding: '20px', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                                <div style={{ fontSize: '1rem', fontWeight: 600, color: 'white', marginBottom: '4px' }}>
                                    {user?.name}
                                </div>
                                <div style={{ fontSize: '0.85rem', color: '#888' }}>
                                    {user?.email}
                                </div>
                            </div>
                            <button
                                className="dropdown-item"
                                onClick={() => {
                                    setShowDropdown(false);
                                    navigate('/library');
                                }}
                            >
                                <span>📚</span> 라이브러리
                            </button>
                            <button
                                className="dropdown-item"
                                onClick={handleLogout}
                                style={{ color: '#ef4444' }}
                            >
                                <span>🚪</span> 로그아웃
                            </button>
                        </div>
                    )}
                </div>
            </header>

            <main className="main-content">
                {/* 액션 카드 섹션 */}
                <div className="actions-grid">
                    <div className="action-card" onClick={() => {/* 동작 없음 */ }}>
                        <div className="action-icon-box">
                            <span>➕</span>
                        </div>
                        <div className="action-text">
                            <h3 className="action-title">새 프로젝트</h3>
                            <p className="action-desc">처음부터 새로운 게임 만들기</p>
                        </div>
                    </div>

                    <div className="action-card" onClick={() => {/* 동작 없음 */ }}>
                        <div className="action-icon-box">
                            <span>📂</span>
                        </div>
                        <div className="action-text">
                            <h3 className="action-title">프로젝트 가져오기</h3>
                            <p className="action-desc">기존 프로젝트 파일 열기</p>
                        </div>
                    </div>
                </div>

                {/* 내 프로젝트 섹션 */}
                <div style={{ marginBottom: '80px' }}>
                    <div className="section-title-wrapper">
                        <h2 className="section-title">내 프로젝트</h2>
                        <span className="view-all">최근 수정순 &gt;</span>
                    </div>

                    <div className="project-grid">
                        <div className="empty-project-card">
                            <div className="empty-icon">🎮</div>
                            <h3 style={{
                                color: '#ccc',
                                fontSize: '1.2rem',
                                fontWeight: 500,
                                marginBottom: '10px'
                            }}>아직 프로젝트가 없습니다</h3>
                            <p style={{ color: '#666' }}>
                                위 버튼을 눌러 첫 번째 게임을 만들어보세요!
                            </p>
                        </div>
                    </div>
                </div>

                {/* 인기 게임 섹션 (Marquee) */}
                <div>
                    <div className="section-title-wrapper">
                        <h2 className="section-title">🔥 지금 뜨는 인기 게임</h2>
                        <span className="view-all" onClick={() => navigate('/marketplace')}>더보기 &gt;</span>
                    </div>

                    <div className="marquee-container-modern">
                        <div className="marquee-content">
                            {[...POPULAR_GAMES, ...POPULAR_GAMES].map((game, index) => (
                                <div key={index} className="game-card-modern">
                                    <img src={game.image} alt={game.title} className="game-bg" />
                                    <div className="game-info">
                                        <div className="game-title-modern">{game.title}</div>
                                        <div className="game-author-modern">{game.author}</div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default MainPage;

