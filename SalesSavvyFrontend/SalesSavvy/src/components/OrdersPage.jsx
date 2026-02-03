import React, { useState, useEffect } from 'react';
import { Header } from './Header';
import { Footer } from './Footer';
import '../styles/OrdersPage.css';

export default function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [cartCount, setCartCount] = useState(0);
    const [username, setUsername] = useState('');
    const [cartError, setCartError] = useState(false); // State for cart fetch error
    const [isCartLoading, setIsCartLoading] = useState(true); // State for cart loading

    useEffect(() => {
        fetchOrders();
    }, []);

    useEffect(() => {
        if (username && username !== 'Guest') {
            fetchCartCount(); // Fetch cart count only if username is available
        }
    }, [username]); // Re-run cart count fetch if username changes

    const fetchOrders = async () => {
        try {
            // Checking for token as done in CustomerHome
            const token = localStorage.getItem("token");
            const headers = { "Content-Type": "application/json" };
            if (token) headers["Authorization"] = `Bearer ${token}`;

            const response = await fetch('http://localhost:8080/api/orders', {
                headers,
                credentials: 'include',
            });
            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error('Unauthorized. Please login again.');
                }
                throw new Error('Failed to fetch orders');
            }
            const data = await response.json();
            setOrders(data.products || []);

            // If backend returns username, use it. Otherwise fallback to localStorage
            const extractedUsername = data.username || localStorage.getItem("username") || 'Guest';
            setUsername(extractedUsername);
        } catch (err) {
            setError(err.message);
            // Fallback username if fetch fails but user might be logged in
            setUsername(localStorage.getItem("username") || 'Guest');
        } finally {
            setLoading(false);
        }
    };

    const fetchCartCount = async () => {
        setIsCartLoading(true); // Set loading state
        try {
            const token = localStorage.getItem("token");
            const headers = { "Content-Type": "application/json" };
            if (token) headers["Authorization"] = `Bearer ${token}`;

            const response = await fetch(`http://localhost:8080/api/cart/items/count?username=${username}`, {
                headers,
                credentials: 'include',
            });
            if (!response.ok) throw new Error('Failed to fetch cart count');
            const count = await response.json();
            setCartCount(count);
            setCartError(false);
        } catch (error) {
            setCartError(true);
        } finally {
            setIsCartLoading(false); // Remove loading state
        }
    };

    return (
        <div className="orders-page-container">
            <Header
                cartCount={isCartLoading ? '...' : cartError ? '!' : cartCount}
                username={username}
                onLogout={() => {
                    localStorage.clear();
                    window.location.href = "/login";
                }}
            />

            <main className="orders-main-content">
                <div className="orders-header-section">
                    <h1 className="orders-title">Your Orders</h1>
                    <p className="orders-subtitle">Check the status of recent orders and manage returns.</p>
                </div>

                {loading ? (
                    <div className="loading-state">
                        <div className="loader"></div>
                        <p>Loading your orders...</p>
                    </div>
                ) : error ? (
                    <div className="error-state">
                        <div className="error-icon">⚠️</div>
                        <p className="error-message">{error}</p>
                        <button onClick={fetchOrders} className="retry-btn">Try Again</button>
                    </div>
                ) : orders.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-cart-icon">📦</div>
                        <p>No orders found. Start shopping now!</p>
                        <button onClick={() => window.location.href = '/customerhome'} className="shop-now-btn">Explore Products</button>
                    </div>
                ) : (
                    <div className="orders-list">
                        {orders.map((order, index) => (
                            <div key={order.order_id || index} className="order-card">
                                <div className="order-card-header">
                                    <span className="order-id-label">Order ID</span>
                                    <span className="order-id-value">{order.order_id}</span>
                                </div>
                                <div className="order-card-body">
                                    <div className="product-image-container">
                                        <img
                                            src={order.image_url || 'https://via.placeholder.com/150'}
                                            alt={order.name}
                                            className="order-product-image"
                                        />
                                    </div>
                                    <div className="order-details">
                                        <div className="product-info">
                                            <h3 className="product-name">{order.name}</h3>
                                            <p className="product-description">{order.description}</p>
                                        </div>

                                        <div className="order-meta">
                                            <div className="meta-item">
                                                <span className="label">Quantity</span>
                                                <span className="value">{order.quantity}</span>
                                            </div>
                                            <div className="meta-item">
                                                <span className="label">Price per Unit</span>
                                                <span className="value">₹{order.price_per_unit.toFixed(2)}</span>
                                            </div>
                                            <div className="meta-item total">
                                                <span className="label">Total Price</span>
                                                <span className="value highlight">₹{order.total_price.toFixed(2)}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </main>
            <Footer />
        </div>
    );
}
