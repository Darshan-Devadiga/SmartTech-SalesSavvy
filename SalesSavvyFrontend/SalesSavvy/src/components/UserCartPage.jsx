import React, { useEffect, useState, useCallback, useMemo } from "react";
import "../styles/UserCartPage.css";
import { Header } from "./Header";
import { Footer } from "./Footer";
import { useNavigate } from "react-router-dom";

const UserCartPage = () => {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [username, setUsername] = useState("");
    const [subtotal, setSubtotal] = useState(0);
    const [checkoutLoading, setCheckoutLoading] = useState(false);
    const checkoutLock = React.useRef(false); // Hard lock to prevent concurrent execution
    const navigate = useNavigate();

    const handleLogout = useCallback(() => {
        localStorage.clear();
        sessionStorage.clear();
        navigate("/login");
    }, [navigate]);

    // Fetch cart items on component load
    useEffect(() => {
        const fetchCartItems = async () => {
            setLoading(true);
            try {
                const token = localStorage.getItem("token");
                const storedUser = localStorage.getItem("user");
                let currentUsername = "";

                if (storedUser) {
                    try {
                        const parsed = JSON.parse(storedUser);
                        if (typeof parsed === 'string') {
                            currentUsername = parsed;
                        } else {
                            currentUsername = parsed.username || parsed.name || parsed.email || localStorage.getItem("username") || "";
                        }
                    } catch (e) {
                        currentUsername = storedUser || localStorage.getItem("username") || "";
                    }
                } else {
                    currentUsername = localStorage.getItem("username") || "";
                }

                const headers = { "Content-Type": "application/json" };
                if (token) headers["Authorization"] = `Bearer ${token}`;

                // Attempting to fetch using the pattern from user's reference but on port 8080
                const response = await fetch(`http://localhost:8080/api/cart/items?username=${currentUsername}`, {
                    headers,
                    credentials: "include",
                });

                if (!response.ok) throw new Error("Failed to fetch cart items");
                const data = await response.json();

                // Parsing logic from user's reference with robust fallback
                let products = data?.cart?.products || data?.products || data?.items || (Array.isArray(data) ? data : []);
                let overallTotal = data?.cart?.overall_total_price || data?.totalPrice || data?.total || 0;

                setCartItems(
                    products.map((item, index) => ({
                        ...item,
                        product_id: item.product_id || item.productId || item.id || `temp-${index}`,
                        name: item.name || item.productName || "Product",
                        description: item.description || "",
                        total_price: parseFloat(item.total_price || item.price || 0).toFixed(2),
                        price_per_unit: parseFloat(item.price_per_unit || item.price || 0).toFixed(2),
                        quantity: parseInt(item.quantity || 1, 10),
                        image_url: item.image_url || item.imageUrl || "https://via.placeholder.com/80?text=No+Image"
                    }))
                );

                // If the backend returns the username, use it. Otherwise, use what we have.
                setUsername(data?.username || currentUsername || "");
            } catch (error) {
                console.error("Error fetching cart items:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchCartItems();
    }, []);

    // Calculate subtotal whenever cart items change
    useEffect(() => {
        const total = cartItems
            .reduce((total, item) => total + parseFloat(item.total_price), 0)
            .toFixed(2);
        setSubtotal(total);
    }, [cartItems]);

    // Remove item from the cart
    const handleRemoveItem = async (productId) => {
        try {
            const token = localStorage.getItem("token");
            const headers = { "Content-Type": "application/json" };
            if (token) headers["Authorization"] = `Bearer ${token}`;

            const response = await fetch("http://localhost:8080/api/cart/delete", {
                method: "DELETE",
                headers,
                credentials: "include",
                body: JSON.stringify({ username, productId }),
            });
            if (response.status === 204) {
                setCartItems((prevItems) => prevItems.filter((item) => item.product_id !== productId));
            } else throw new Error("Failed to remove item");
        } catch (error) {
            console.error("Error removing item:", error);
        }
    };

    // Update quantity of an item
    const handleQuantityChange = async (productId, newQuantity) => {
        try {
            if (newQuantity <= 0) {
                handleRemoveItem(productId);
                return;
            }
            const token = localStorage.getItem("token");
            const headers = { "Content-Type": "application/json" };
            if (token) headers["Authorization"] = `Bearer ${token}`;

            const response = await fetch("http://localhost:8080/api/cart/update", {
                method: "PUT",
                headers,
                credentials: "include",
                body: JSON.stringify({ username, productId, quantity: newQuantity }),
            });
            if (response.ok) {
                setCartItems((prevItems) =>
                    prevItems.map((item) =>
                        item.product_id === productId
                            ? {
                                ...item,
                                quantity: newQuantity,
                                total_price: (item.price_per_unit * newQuantity).toFixed(2),
                            }
                            : item
                    )
                );
            } else throw new Error("Failed to update quantity");
        } catch (error) {
            console.error("Error updating quantity:", error);
        }
    };

    // Razorpay integration for payment
    const handleCheckout = async () => {
        if (checkoutLoading || checkoutLock.current) return;

        if (parseFloat(subtotal) <= 0) {
            alert("Your cart is empty or the subtotal is invalid.");
            return;
        }

        if (parseFloat(subtotal) <= 0) {
            alert("Your cart is empty or the subtotal is invalid.");
            return;
        }

        setCheckoutLoading(true);
        checkoutLock.current = true;

        try {
            const requestBody = {
                totalAmount: subtotal,
                cartItems: cartItems.map((item) => ({
                    productId: parseInt(item.product_id, 10),
                    quantity: parseInt(item.quantity, 10),
                    price: parseFloat(item.price_per_unit || item.total_price || 0),
                })),
            };

            const token = localStorage.getItem("token");
            const headers = { "Content-Type": "application/json" };
            if (token) headers["Authorization"] = `Bearer ${token}`;

            // Create Razorpay order via backend
            const response = await fetch("http://localhost:8080/api/payment/create", {
                method: "POST",
                headers,
                credentials: "include",
                body: JSON.stringify(requestBody),
            });

            if (!response.ok) throw new Error(await response.text());
            const razorpayOrderId = await response.text();

            const options = {
                key: "rzp_test_LqWBBDbgwot5lh",
                amount: Math.round(subtotal * 100),
                currency: "INR",
                name: "SalesSavvy",
                description: "Test Transaction",
                order_id: razorpayOrderId,
                handler: async function (response) {
                    try {
                        const verifyResponse = await fetch("http://localhost:8080/api/payment/verify", {
                            method: "POST",
                            headers,
                            credentials: "include",
                            body: JSON.stringify({
                                razorpayOrderId: response.razorpay_order_id,
                                razorpayPaymentId: response.razorpay_payment_id,
                                razorpaySignature: response.razorpay_signature,
                            }),
                        });
                        const result = await verifyResponse.text();
                        if (verifyResponse.ok) {
                            alert("Payment verified successfully!");
                            navigate("/customerhome");
                        } else {
                            alert("Payment verification failed: " + result);
                        }
                    } catch (error) {
                        console.error("Error verifying payment:", error);
                        alert("Payment verification failed. Please try again.");
                    }
                },
                prefill: {
                    name: username,
                    email: "test@example.com",
                    contact: "9999999999",
                },
                theme: {
                    color: "#3399cc",
                },
            };

            const rzp = new window.Razorpay(options);
            rzp.open();
        } catch (error) {
            alert("Payment failed. Please try again.");
            console.error("Error during checkout:", error);
        } finally {
            setCheckoutLoading(false);
            checkoutLock.current = false;
        }
    };

    const totalProducts = () => cartItems.reduce((acc, item) => acc + item.quantity, 0);
    const shipping = (5.0 * 74).toFixed(2);

    if (loading) {
        return (
            <div style={{ width: "100vw" }}>
                <Header cartCount={0} username={username} onLogout={handleLogout} />
                <div className="cart-page loading">
                    <h2>Loading your cart...</h2>
                </div>
                <Footer />
            </div>
        );
    }

    if (cartItems.length === 0) {
        return (
            <div style={{ width: "100vw" }}>
                <Header cartCount={0} username={username} onLogout={handleLogout} />
                <div className="cart-content">
                    <div className="empty-cart">
                        <div className="empty-cart-icon">🛒</div>
                        <h2>Your Cart is Empty</h2>
                        <p>It looks like you haven't added any items to your cart yet. Explore our featured products and start shopping!</p>
                        <button
                            className="continue-shopping-btn"
                            onClick={() => navigate("/customerhome")}
                        >
                            Continue Shopping
                        </button>
                    </div>
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="cart-page">
            <Header cartCount={totalProducts()} username={username} onLogout={handleLogout} />
            <div className="cart-content">
                <div className="cart-grid">
                    <div className="cart-main">
                        <div className="cart-header">
                            <h1>Shopping Cart</h1>
                            <p>You have {cartItems.length} items in your bag</p>
                        </div>

                        <div className="cart-items-list">
                            {cartItems.map((item) => (
                                <div key={item.product_id} className="cart-item">
                                    <div className="item-image">
                                        <img
                                            src={item.image_url || "https://via.placeholder.com/80?text=No+Image"}
                                            alt={item.name}
                                        />
                                    </div>
                                    <div className="item-details">
                                        <h3>{item.name}</h3>
                                        <p className="item-description">{item.description}</p>
                                        <div className="item-price-unit">₹{item.price_per_unit} per unit</div>
                                    </div>
                                    <div className="item-actions">
                                        <div className="quantity-controls">
                                            <button className="qty-btn" onClick={() => handleQuantityChange(item.product_id, item.quantity - 1)}>
                                                -
                                            </button>
                                            <span className="qty-value">{item.quantity}</span>
                                            <button className="qty-btn" onClick={() => handleQuantityChange(item.product_id, item.quantity + 1)}>
                                                +
                                            </button>
                                        </div>
                                        <div className="item-total-price">₹{item.total_price}</div>
                                        <button className="remove-btn" onClick={() => handleRemoveItem(item.product_id)}>
                                            Remove
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="cart-summary">
                        <h2>Order Summary</h2>
                        <div className="summary-row">
                            <span>Subtotal</span>
                            <span>₹{subtotal}</span>
                        </div>
                        <div className="summary-row">
                            <span>Shipping</span>
                            <span>₹{shipping}</span>
                        </div>
                        <div className="summary-divider"></div>
                        <div className="summary-row total">
                            <span>Total</span>
                            <span>₹{(parseFloat(subtotal) + parseFloat(shipping)).toFixed(2)}</span>
                        </div>
                        <button
                            type="button"
                            className="checkout-btn"
                            onClick={handleCheckout}
                            disabled={checkoutLoading}
                        >
                            {checkoutLoading ? "Creating Order..." : "Proceed to Checkout"}
                        </button>
                        <button
                            className="continue-shopping-btn"
                            style={{ width: '100%', marginTop: '1rem', background: 'transparent', color: '#4f46e5', border: '1px solid #e2e8f0', boxShadow: 'none' }}
                            onClick={() => navigate("/customerhome")}
                        >
                            Continue Shopping
                        </button>
                    </div>
                </div>
            </div>
            <Footer />
        </div>
    );
};

export default UserCartPage;
