import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useState, useRef, useEffect } from 'react';

const MainPage = () => {
    const { logout, user } = useAuth();
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

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
                <div style={{ display: 'flex', alignItems: 'center', gap: '3rem' }}>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>
                        <span className="gradient-text">Uniforge</span>
                    </div>
                    <nav style={{ display: 'flex', gap: '0.5rem' }}>
                        <button
                            onClick={() => navigate('/assets')}
                            style={{
                                background: 'transparent',
                                border: 'none',
                                color: '#b0b0b0',
                                fontSize: '1rem',
                                fontWeight: 500,
                                cursor: 'pointer',
                                padding: '10px 16px',
                                borderRadius: '8px',
                                transition: 'all 0.2s',
                                letterSpacing: '0.3px'
                            }}
                            onMouseEnter={(e) => {
                                e.currentTarget.style.color = '#fff';
                                e.currentTarget.style.backgroundColor = '#1a1a1a';
                            }}
                            onMouseLeave={(e) => {
                                e.currentTarget.style.color = '#b0b0b0';
                                e.currentTarget.style.backgroundColor = 'transparent';
                            }}
                        >
                            Store
                        </button>
                        <button
                            onClick={() => navigate('/marketplace')}
                            style={{
                                background: 'transparent',
                                border: 'none',
                                color: '#b0b0b0',
                                fontSize: '1rem',
                                fontWeight: 500,
                                cursor: 'pointer',
                                padding: '10px 16px',
                                borderRadius: '8px',
                                transition: 'all 0.2s',
                                letterSpacing: '0.3px'
                            }}
                            onMouseEnter={(e) => {
                                e.currentTarget.style.color = '#fff';
                                e.currentTarget.style.backgroundColor = '#1a1a1a';
                            }}
                            onMouseLeave={(e) => {
                                e.currentTarget.style.color = '#b0b0b0';
                                e.currentTarget.style.backgroundColor = 'transparent';
                            }}
                        >
                            Explore
                        </button>
                    </nav>
                </div>
                <div style={{ position: 'relative' }} ref={dropdownRef}>
                    <button
                        onClick={() => setShowDropdown(!showDropdown)}
                        style={{
                            width: '40px',
                            height: '40px',
                            borderRadius: '50%',
                            border: '2px solid #333',
                            backgroundColor: '#1a1a1a',
                            cursor: 'pointer',
                            padding: 0,
                            overflow: 'hidden',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            transition: 'border-color 0.2s'
                        }}
                        onMouseEnter={(e) => e.currentTarget.style.borderColor = '#555'}
                        onMouseLeave={(e) => e.currentTarget.style.borderColor = '#333'}
                    >
                        {user?.profileImage ? (
                            <img
                                src={user.profileImage}
                                alt="프로필"
                                style={{
                                    width: '100%',
                                    height: '100%',
                                    objectFit: 'cover'
                                }}
                            />
                        ) : (
                            <svg
                                width="24"
                                height="24"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="#666"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            >
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                                <circle cx="12" cy="7" r="4" />
                            </svg>
                        )}
                    </button>

                    {showDropdown && (
                        <div style={{
                            position: 'absolute',
                            top: '50px',
                            right: 0,
                            backgroundColor: '#1a1a1a',
                            border: '1px solid #333',
                            borderRadius: '8px',
                            minWidth: '200px',
                            boxShadow: '0 8px 24px rgba(0,0,0,0.5)',
                            zIndex: 1000,
                            overflow: 'hidden'
                        }}>
                            <div style={{
                                padding: '16px',
                                borderBottom: '1px solid #333'
                            }}>
                                <div style={{
                                    fontSize: '0.9rem',
                                    fontWeight: 600,
                                    marginBottom: '4px'
                                }}>
                                    {user?.name}
                                </div>
                                <div style={{
                                    fontSize: '0.8rem',
                                    color: '#888'
                                }}>
                                    {user?.email}
                                </div>
                            </div>
                            <button
                                onClick={() => {
                                    setShowDropdown(false);
                                    navigate('/library');
                                }}
                                style={{
                                    width: '100%',
                                    padding: '12px 16px',
                                    backgroundColor: 'transparent',
                                    color: '#fff',
                                    border: 'none',
                                    borderBottom: '1px solid #333',
                                    textAlign: 'left',
                                    cursor: 'pointer',
                                    fontSize: '0.9rem',
                                    transition: 'background-color 0.2s',
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: '10px'
                                }}
                                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#222'}
                                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                            >
                                <span>📚</span> 라이브러리
                            </button>
                            <button
                                onClick={handleLogout}
                                style={{
                                    width: '100%',
                                    padding: '12px 16px',
                                    backgroundColor: 'transparent',
                                    color: '#ef4444',
                                    border: 'none',
                                    textAlign: 'left',
                                    cursor: 'pointer',
                                    fontSize: '0.9rem',
                                    transition: 'background-color 0.2s'
                                }}
                                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#222'}
                                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                            >
                                로그아웃
                            </button>
                        </div>
                    )}
                </div>
            </header>
            <main style={{
                flex: 1,
                padding: '40px 20px',
                maxWidth: '1200px',
                width: '95%',
                margin: '0 auto'
            }}>
                {/* 액션 버튼 섹션 */}
                <div style={{
                    display: 'flex',
                    gap: '20px',
                    marginBottom: '50px'
                }}>
                    {/* 새 프로젝트 */}
                    <button
                        style={{
                            flex: 1,
                            padding: '40px 30px',
                            backgroundColor: '#0a0a0a',
                            border: '1px solid #333',
                            borderRadius: '16px',
                            cursor: 'pointer',
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            gap: '16px',
                            transition: 'all 0.3s ease'
                        }}
                        onMouseEnter={(e) => {
                            e.currentTarget.style.borderColor = '#2563eb';
                            e.currentTarget.style.backgroundColor = '#111';
                            e.currentTarget.style.transform = 'translateY(-4px)';
                        }}
                        onMouseLeave={(e) => {
                            e.currentTarget.style.borderColor = '#333';
                            e.currentTarget.style.backgroundColor = '#0a0a0a';
                            e.currentTarget.style.transform = 'translateY(0)';
                        }}
                    >
                        <div style={{
                            width: '60px',
                            height: '60px',
                            borderRadius: '12px',
                            backgroundColor: '#1a1a1a',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '1.8rem',
                            border: '1px solid #333'
                        }}>
                            ➕
                        </div>
                        <div>
                            <h3 style={{
                                color: '#fff',
                                fontSize: '1.1rem',
                                fontWeight: 600,
                                marginBottom: '6px'
                            }}>새 프로젝트</h3>
                            <p style={{
                                color: '#666',
                                fontSize: '0.85rem'
                            }}>처음부터 새로운 게임 만들기</p>
                        </div>
                    </button>

                    {/* 프로젝트 가져오기 */}
                    <button
                        style={{
                            flex: 1,
                            padding: '40px 30px',
                            backgroundColor: '#0a0a0a',
                            border: '1px solid #333',
                            borderRadius: '16px',
                            cursor: 'pointer',
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            gap: '16px',
                            transition: 'all 0.3s ease'
                        }}
                        onMouseEnter={(e) => {
                            e.currentTarget.style.borderColor = '#2563eb';
                            e.currentTarget.style.backgroundColor = '#111';
                            e.currentTarget.style.transform = 'translateY(-4px)';
                        }}
                        onMouseLeave={(e) => {
                            e.currentTarget.style.borderColor = '#333';
                            e.currentTarget.style.backgroundColor = '#0a0a0a';
                            e.currentTarget.style.transform = 'translateY(0)';
                        }}
                    >
                        <div style={{
                            width: '60px',
                            height: '60px',
                            borderRadius: '12px',
                            backgroundColor: '#1a1a1a',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '1.8rem',
                            border: '1px solid #333'
                        }}>
                            📂
                        </div>
                        <div>
                            <h3 style={{
                                color: '#fff',
                                fontSize: '1.1rem',
                                fontWeight: 600,
                                marginBottom: '6px'
                            }}>프로젝트 가져오기</h3>
                            <p style={{
                                color: '#666',
                                fontSize: '0.85rem'
                            }}>기존 프로젝트 파일 열기</p>
                        </div>
                    </button>
                </div>

                {/* 내 프로젝트 섹션 */}
                <div>
                    <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        marginBottom: '24px'
                    }}>
                        <h2 style={{
                            fontSize: '1.3rem',
                            fontWeight: 600,
                            color: '#fff'
                        }}>내 프로젝트</h2>
                        <span style={{
                            color: '#666',
                            fontSize: '0.9rem'
                        }}>최근 수정순</span>
                    </div>

                    {/* 프로젝트 그리드 */}
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
                        gap: '20px'
                    }}>
                        {/* 빈 상태 */}
                        <div style={{
                            gridColumn: '1 / -1',
                            padding: '80px 40px',
                            textAlign: 'center',
                            backgroundColor: '#0a0a0a',
                            border: '1px dashed #333',
                            borderRadius: '16px'
                        }}>
                            <div style={{
                                fontSize: '3rem',
                                marginBottom: '16px',
                                opacity: 0.5
                            }}>🎮</div>
                            <h3 style={{
                                color: '#888',
                                fontSize: '1.1rem',
                                fontWeight: 500,
                                marginBottom: '8px'
                            }}>아직 프로젝트가 없습니다</h3>
                            <p style={{
                                color: '#555',
                                fontSize: '0.9rem'
                            }}>새 프로젝트를 만들어 게임 개발을 시작하세요!</p>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default MainPage;

