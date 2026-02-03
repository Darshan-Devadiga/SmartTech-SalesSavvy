import React, { useState } from 'react';

export function ProfileDropdown({ username, onLogout }) {
    const [isOpen, setIsOpen] = useState(false);
    const timeoutRef = React.useRef(null);

    // Robust display name extraction
    const displayName = React.useMemo(() => {
        if (username && username !== "Guest") return username;

        const rawUsername = localStorage.getItem("username");
        if (rawUsername) return rawUsername;

        const storedUser = localStorage.getItem("user");
        if (storedUser) {
            try {
                const parsed = JSON.parse(storedUser);
                return typeof parsed === 'string' ? parsed : (parsed.username || parsed.name || parsed.email || "User");
            } catch (e) {
                return storedUser;
            }
        }
        return "User";
    }, [username]);

    const handleMouseEnter = () => {
        if (timeoutRef.current) clearTimeout(timeoutRef.current);
        setIsOpen(true);
    };

    const handleMouseLeave = () => {
        timeoutRef.current = setTimeout(() => {
            setIsOpen(false);
        }, 300); // 300ms delay for smoother UX
    };

    const handleLogoutClick = () => {
        setIsOpen(false);
        if (onLogout) onLogout();
    };

    return (
        <div
            className="profile-dropdown"
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
        >
            <button className="profile-toggle" aria-expanded={isOpen}>
                <img
                    src={`https://ui-avatars.com/api/?name=${displayName}&background=4f46e5&color=fff`}
                    alt="User Avatar"
                    className="profile-img"
                />
                <span className="username-text">{displayName}</span>
            </button>

            {isOpen && (
                <div className="dropdown-menu">
                    <button
                        className="dropdown-item"
                        style={{ width: '100%', textAlign: 'left', border: 'none', background: 'none', cursor: 'pointer' }}
                        onClick={() => window.location.href = '/orders'}
                    >
                        Order History
                    </button>
                    <hr style={{ border: '0', borderTop: '1px solid #f1f5f9', margin: '0.5rem 0' }} />
                    <button className="dropdown-item logout-btn" onClick={handleLogoutClick}>Logout</button>
                </div>
            )}
        </div>
    );
}
