import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const MarketplacePage = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);
    const [selectedCategory, setSelectedCategory] = useState("Recommended");

    // Close dropdown on outside click
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setShowDropdown(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const CATEGORIES = [
        { id: "Recommended", icon: "🔥" },
        { id: "Trending", icon: "📈" },
        { id: "New Arrivals", icon: "✨" },
        { type: "divider" },
        { id: "Action", icon: "⚔️" },
        { id: "RPG", icon: "🛡️" },
        { id: "Strategy", icon: "♟️" },
        { id: "Puzzle", icon: "🧩" },
        { type: "divider" },
        { id: "3D Assets", icon: "🧊" },
        { id: "2D Sprites", icon: "👾" },
        { id: "Audio", icon: "🎵" },
        { id: "VFX", icon: "✨" }
    ];

    const MARKET_ITEMS = [
        { title: "Neon City Pack", author: "CyberArt", price: "Free", image: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=400", rating: 4.8, type: "Asset" },
        { title: "Fantasy Knight", author: "PixelForge", price: "$15.00", image: "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&q=80&w=400", rating: 4.9, type: "Character" },
        { title: "Space Shooter Template", author: "GameDevPro", price: "$49.99", image: "https://images.unsplash.com/photo-1614726365723-49cfae96ac6d?auto=format&fit=crop&q=80&w=400", rating: 4.7, type: "Template" },
        { title: "Ultimate RPG UI", author: "InterfaceMaster", price: "$25.00", image: "https://images.unsplash.com/photo-1611162617474-5b21e879e113?auto=format&fit=crop&q=80&w=400", rating: 4.6, type: "UI" },
        { title: "Forest Ambience", author: "SoundScape", price: "$10.00", image: "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&q=80&w=400", rating: 4.9, type: "Audio" },
        { title: "Low Poly Vehicles", author: "PolyWorks", price: "$12.00", image: "https://images.unsplash.com/photo-1555620950-c8d030999557?auto=format&fit=crop&q=80&w=400", rating: 4.5, type: "Asset" },
        { title: "Dungeon Tileset", author: "TileMaster", price: "Free", image: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&q=80&w=400", rating: 4.4, type: "Asset" },
        { title: "Epic Orchestral", author: "ComposerX", price: "$30.00", image: "https://images.unsplash.com/photo-1507838153414-b4b713384ebd?auto=format&fit=crop&q=80&w=400", rating: 5.0, type: "Audio" },
    ];

    return (
        <div style={{
            backgroundColor: 'black',
            minHeight: '100vh',
            display: 'flex',
            flexDirection: 'column',
            color: 'white',
            overflowX: 'hidden'
        }}>
            {/* Header */}
            <header style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '1rem 2rem',
                borderBottom: '1px solid #1a1a1a',
                backgroundColor: 'rgba(10, 10, 10, 0.95)',
                backdropFilter: 'blur(10px)',
                position: 'sticky',
                top: 0,
                zIndex: 100
            }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '3rem' }}>
                    <div
                        style={{ fontSize: '1.5rem', fontWeight: 'bold', cursor: 'pointer' }}
                        onClick={() => navigate('/main')}
                    >
                        <span className="gradient-text">Uniforge</span>
                        <span style={{ fontSize: '0.9rem', color: '#666', marginLeft: '10px', fontWeight: 400 }}>Marketplace</span>
                    </div>

                    {/* Search Bar */}
                    <div style={{
                        position: 'relative',
                        width: '400px'
                    }}>
                        <i className="fa-solid fa-search" style={{
                            position: 'absolute',
                            left: '12px',
                            top: '50%',
                            transform: 'translateY(-50%)',
                            color: '#666'
                        }}></i>
                        <input
                            type="text"
                            placeholder="Search assets, games, and creators..."
                            style={{
                                width: '100%',
                                backgroundColor: '#111',
                                border: '1px solid #333',
                                borderRadius: '8px',
                                padding: '10px 10px 10px 40px',
                                color: 'white',
                                fontSize: '0.95rem',
                                outline: 'none'
                            }}
                            onFocus={(e) => e.target.style.borderColor = '#2563eb'}
                            onBlur={(e) => e.target.style.borderColor = '#333'}
                        />
                    </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <button onClick={() => navigate('/library')} style={{
                        background: 'transparent', border: 'none', color: '#888', cursor: 'pointer',
                        padding: '8px 16px', borderRadius: '6px', fontSize: '0.95rem'
                    }}
                        onMouseEnter={e => e.currentTarget.style.color = 'white'}
                        onMouseLeave={e => e.currentTarget.style.color = '#888'}
                    >
                        My Library
                    </button>

                    <div style={{ position: 'relative' }} ref={dropdownRef}>
                        <button
                            onClick={() => setShowDropdown(!showDropdown)}
                            style={{
                                width: '36px',
                                height: '36px',
                                borderRadius: '50%',
                                border: '1px solid #333',
                                backgroundColor: '#1a1a1a',
                                cursor: 'pointer',
                                padding: 0,
                                overflow: 'hidden'
                            }}
                        >
                            {user?.profileImage ? (
                                <img src={user.profileImage} alt="Profile" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                            ) : (
                                <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#666' }}>
                                    <i className="fa-solid fa-user"></i>
                                </div>
                            )}
                        </button>

                        {showDropdown && (
                            <div style={{
                                position: 'absolute',
                                top: '45px',
                                right: 0,
                                backgroundColor: '#1a1a1a',
                                border: '1px solid #333',
                                borderRadius: '8px',
                                minWidth: '200px',
                                boxShadow: '0 8px 24px rgba(0,0,0,0.5)',
                                zIndex: 1000,
                                overflow: 'hidden'
                            }}>
                                <div style={{ padding: '16px', borderBottom: '1px solid #333' }}>
                                    <div style={{ fontSize: '0.9rem', fontWeight: 600 }}>{user?.name || 'User'}</div>
                                    <div style={{ fontSize: '0.8rem', color: '#888' }}>{user?.email || 'guest@uniforge.com'}</div>
                                </div>
                                <button onClick={handleLogout} style={{ width: '100%', padding: '12px 16px', background: 'transparent', border: 'none', color: '#ef4444', textAlign: 'left', cursor: 'pointer' }}>Log Out</button>
                            </div>
                        )}
                    </div>
                </div>
            </header>

            <div style={{ display: 'flex', flex: 1, maxWidth: '1600px', width: '100%', margin: '0 auto' }}>
                {/* Sidebar */}
                <aside style={{
                    width: '240px',
                    padding: '2rem 1rem',
                    borderRight: '1px solid #1a1a1a',
                    position: 'sticky',
                    top: '73px',
                    height: 'calc(100vh - 73px)',
                    overflowY: 'auto'
                }}>
                    <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                        {CATEGORIES.map((cat, index) => (
                            cat.type === 'divider' ? (
                                <div key={index} style={{ height: '1px', backgroundColor: '#222', margin: '10px 0' }}></div>
                            ) : (
                                <button
                                    key={cat.id}
                                    onClick={() => setSelectedCategory(cat.id!)}
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '12px',
                                        padding: '10px 16px',
                                        backgroundColor: selectedCategory === cat.id ? '#1a1a1a' : 'transparent',
                                        border: '1px solid',
                                        borderColor: selectedCategory === cat.id ? '#333' : 'transparent',
                                        borderRadius: '8px',
                                        color: selectedCategory === cat.id ? 'white' : '#888',
                                        fontSize: '0.95rem',
                                        cursor: 'pointer',
                                        textAlign: 'left',
                                        transition: 'all 0.2s',
                                        width: '100%'
                                    }}
                                    onMouseEnter={e => {
                                        if (selectedCategory !== cat.id) {
                                            e.currentTarget.style.backgroundColor = '#111';
                                            e.currentTarget.style.color = '#ccc';
                                        }
                                    }}
                                    onMouseLeave={e => {
                                        if (selectedCategory !== cat.id) {
                                            e.currentTarget.style.backgroundColor = 'transparent';
                                            e.currentTarget.style.color = '#888';
                                        }
                                    }}
                                >
                                    <span>{cat.icon}</span>
                                    <span>{cat.id}</span>
                                </button>
                            )
                        ))}
                    </nav>
                </aside>

                {/* Main Content */}
                <main style={{ flex: 1, padding: '2rem' }}>

                    {/* Hero Banner */}
                    <div style={{
                        width: '100%',
                        height: '300px',
                        borderRadius: '16px',
                        position: 'relative',
                        overflow: 'hidden',
                        marginBottom: '3rem',
                        border: '1px solid #333'
                    }}>
                        <img
                            src="https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&q=80&w=2000"
                            alt="Featured"
                            style={{ width: '100%', height: '100%', objectFit: 'cover', filter: 'brightness(0.6)' }}
                        />
                        <div style={{
                            position: 'absolute',
                            bottom: 0,
                            left: 0,
                            padding: '3rem',
                            background: 'linear-gradient(to top, rgba(0,0,0,0.9), transparent)',
                            width: '100%'
                        }}>
                            <span style={{
                                backgroundColor: '#2563eb', color: 'white', padding: '4px 12px',
                                borderRadius: '4px', fontSize: '0.8rem', fontWeight: 600, marginBottom: '1rem',
                                display: 'inline-block'
                            }}>FEATURED</span>
                            <h2 style={{ fontSize: '2.5rem', marginBottom: '0.5rem', fontWeight: 700 }}>Cyberpunk Streets Vol.2</h2>
                            <p style={{ color: '#ccc', maxWidth: '600px', marginBottom: '1.5rem' }}>
                                Create immersive dystopian cityscapes with over 200 high-fidelity assets. Includes modular buildings, neon signs, and detailed props.
                            </p>
                            <div style={{ display: 'flex', gap: '1rem' }}>
                                <button style={{
                                    backgroundColor: 'white', color: 'black', border: 'none',
                                    padding: '12px 24px', borderRadius: '6px', fontWeight: 600,
                                    cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px'
                                }}>
                                    View Details
                                </button>
                                <button style={{
                                    backgroundColor: 'rgba(255,255,255,0.1)', color: 'white', border: '1px solid rgba(255,255,255,0.2)',
                                    padding: '12px 24px', borderRadius: '6px', fontWeight: 600,
                                    backdropFilter: 'blur(5px)', cursor: 'pointer'
                                }}>
                                    + Wishlist
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Section Header */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                        <h2 style={{ fontSize: '1.5rem', fontWeight: 600 }}>{selectedCategory} Items</h2>
                        <div style={{ display: 'flex', gap: '8px' }}>
                            <button className="sort-btn" style={{ padding: '6px 12px', borderRadius: '4px', background: '#111', border: '1px solid #333', color: '#888', cursor: 'pointer' }}>Popular</button>
                            <button className="sort-btn" style={{ padding: '6px 12px', borderRadius: '4px', background: 'transparent', border: '1px solid transparent', color: '#666', cursor: 'pointer' }}>Newest</button>
                            <button className="sort-btn" style={{ padding: '6px 12px', borderRadius: '4px', background: 'transparent', border: '1px solid transparent', color: '#666', cursor: 'pointer' }}>Price</button>
                        </div>
                    </div>

                    {/* Grid */}
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
                        gap: '24px'
                    }}>
                        {MARKET_ITEMS.map((item, index) => (
                            <div
                                key={index}
                                style={{
                                    backgroundColor: '#0a0a0a',
                                    borderRadius: '12px',
                                    border: '1px solid #222',
                                    overflow: 'hidden',
                                    cursor: 'pointer',
                                    transition: 'transform 0.2s, border-color 0.2s',
                                    position: 'relative'
                                }}
                                onMouseEnter={e => {
                                    e.currentTarget.style.transform = 'translateY(-4px)';
                                    e.currentTarget.style.borderColor = '#444';
                                }}
                                onMouseLeave={e => {
                                    e.currentTarget.style.transform = 'translateY(0)';
                                    e.currentTarget.style.borderColor = '#222';
                                }}
                            >
                                <div style={{ height: '160px', overflow: 'hidden', position: 'relative' }}>
                                    <img src={item.image} alt={item.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                                    <span style={{
                                        position: 'absolute', top: '10px', right: '10px',
                                        backgroundColor: 'rgba(0,0,0,0.7)', padding: '4px 8px', borderRadius: '4px',
                                        fontSize: '0.75rem', fontWeight: 600, backdropFilter: 'blur(4px)'
                                    }}>
                                        {item.type}
                                    </span>
                                </div>
                                <div style={{ padding: '16px' }}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                                        <h3 style={{ fontSize: '1rem', fontWeight: 600, color: 'white', margin: 0 }}>{item.title}</h3>
                                    </div>
                                    <p style={{ fontSize: '0.85rem', color: '#888', margin: '0 0 16px 0' }}>by {item.author}</p>

                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '0.8rem', color: '#fbbf24' }}>
                                            <i className="fa-solid fa-star"></i>
                                            <span>{item.rating}</span>
                                        </div>
                                        <div style={{ fontWeight: 600, color: item.price === 'Free' ? '#22c55e' : 'white' }}>
                                            {item.price}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                </main>
            </div>
        </div>
    );
};

export default MarketplacePage;
