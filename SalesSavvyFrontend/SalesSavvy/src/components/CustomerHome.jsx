import React, { useState, useEffect, useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { Header } from "./Header";
import { CategoryNavigation } from "./CategoryNavigation";
import { ProductList } from "./ProductList";
import { Footer } from "./Footer";
import "../styles/CustomerHome.css";

export default function CustomerHome() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("All");
    const [cartCount, setCartCount] = useState(0);
    const navigate = useNavigate();

    // Simplified user extraction helper
    const getActiveUsername = () => {
        const userData = localStorage.getItem("user");
        const rawUsername = localStorage.getItem("username");
        if (rawUsername) return rawUsername;

        if (userData && userData !== "undefined") {
            try {
                const parsed = JSON.parse(userData);
                return typeof parsed === 'string' ? parsed : (parsed.username || parsed.name || parsed.email || "");
            } catch (e) {
                return userData;
            }
        }
        return "";
    };

    const currentUser = { username: getActiveUsername() };


    const fetchCartCount = React.useCallback(async () => {
        const username = getActiveUsername();
        if (!username) return;
        const token = localStorage.getItem("token");
        const headers = { "Content-Type": "application/json" };
        if (token) headers["Authorization"] = `Bearer ${token}`;

        try {
            // Strategy 1: Items List (Sync with Cart Page)
            const res = await fetch(`http://localhost:8080/api/cart/items?username=${username}`, {
                headers, credentials: "include"
            });

            if (res.ok) {
                const data = await res.json();
                const products = data?.cart?.products || data?.products || data?.items || (Array.isArray(data) ? data : []);
                const totalQty = products.reduce((acc, item) => acc + (parseInt(item.quantity, 10) || 0), 0);

                if (totalQty > 0) {
                    setCartCount(totalQty);
                    return;
                }
            }

            // Strategy 2: Direct Count Fallback
            const countRes = await fetch(`http://localhost:8080/api/cart/items/count?username=${username}`, {
                headers, credentials: "include"
            });
            if (countRes.ok) {
                const val = await countRes.json();
                const actualCount = typeof val === 'number' ? val : (val.count || val.cartCount || 0);
                setCartCount(parseInt(actualCount, 10));
            }
        } catch (err) {
            console.error("Dashboard Sync Error:", err);
        }
    }, []);

    const fetchProducts = React.useCallback(async (category = "All") => {
        setLoading(true);
        setError("");
        try {
            const token = localStorage.getItem("token");
            const headers = {};
            if (token) headers["Authorization"] = `Bearer ${token}`;

            const url = category === "All"
                ? "http://localhost:8080/api/products"
                : `http://localhost:8080/api/products?category=${category}`;

            const res = await fetch(url, {
                headers,
                credentials: "include"
            });

            if (!res.ok) {
                throw new Error(`Failed to fetch products: ${res.status} ${res.statusText}`);
            }

            const data = await res.json();

            // Defensive check: ensure data is an array
            if (Array.isArray(data)) {
                setProducts(data);
            } else if (data && Array.isArray(data.products)) {
                setProducts(data.products);
            } else {
                console.error("Unexpected API response format:", data);
                setProducts([]);
                setError("Received unexpected data format from server.");
            }
        } catch (err) {
            console.error("Fetch error:", err);
            setError(err.message);
            setProducts([]); // Ensure state is reset on error
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchProducts();
        fetchCartCount();

        // Refresh when tab becomes visible again
        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') fetchCartCount();
        };
        window.addEventListener('focus', fetchCartCount); // More robust focus trigger
        document.addEventListener('visibilitychange', handleVisibilityChange);

        // Safety retry for fresh sessions
        const timer = setTimeout(fetchCartCount, 2500);

        return () => {
            window.removeEventListener('focus', fetchCartCount);
            document.removeEventListener('visibilitychange', handleVisibilityChange);
            clearTimeout(timer);
        };
    }, [fetchProducts, fetchCartCount]);

    const handleCategoryClick = (category) => {
        setSelectedCategory(category);
        fetchProducts(category);
    };

    const handleLogout = useCallback(() => {
        localStorage.clear();
        navigate("/login");
    }, [navigate]);

    const addToCart = async (productId) => {
        try {
            const token = localStorage.getItem("token");
            const headers = {
                "Content-Type": "application/json",
            };
            if (token) headers["Authorization"] = `Bearer ${token}`;

            const res = await fetch("http://localhost:8080/api/cart/add", {
                method: "POST",
                headers,
                credentials: "include",
                body: JSON.stringify({
                    username: currentUser.username,
                    productId: productId
                }),
            });

            if (res.ok) {
                fetchCartCount();
            } else {
                const data = await res.json();
                console.error("Failed to add product to cart:", data.error || "Unknown error");
            }
        } catch (err) {
            console.error("Error adding to cart:", err);
        }
    };

    return (
        <div className="customer-home">
            <Header
                cartCount={cartCount}
                username={currentUser.username}
                onLogout={handleLogout}
            />

            <CategoryNavigation
                onCategoryClick={handleCategoryClick}
                selectedCategory={selectedCategory}
            />

            <main className="content-container" id="products-section">
                <div className="section-header">
                    <h2>{selectedCategory === "All" ? "Featured Products" : `${selectedCategory} Collection`}</h2>
                </div>

                <ProductList
                    products={products}
                    loading={loading}
                    error={error}
                    onRetry={() => fetchProducts(selectedCategory)}
                    addToCart={addToCart}
                    selectedCategory={selectedCategory}
                />
            </main>

            <Footer />
        </div>
    );
}
