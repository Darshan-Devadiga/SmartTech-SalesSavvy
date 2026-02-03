import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Register.css";

export default function Login() {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        username: "",
        password: "",
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        // Frontend validation
        if (!form.username || !form.password) {
            setError("Please fill in all fields");
            return;
        }

        setLoading(true);

        try {
            const res = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                credentials: "include",
                body: JSON.stringify(form),
            });

            const data = await res.json();

            if (!res.ok) {
                throw new Error(data.error || "Login failed");
            }

            // Store token/user robustly
            if (data.token) localStorage.setItem("token", data.token);

            // Capture username from any possible field, or fallback to the form
            const userObj = data.user || {
                username: data.username || form.username,
                role: data.role || "CUSTOMER"
            };
            localStorage.setItem("user", typeof userObj === 'string' ? userObj : JSON.stringify(userObj));
            localStorage.setItem("username", typeof userObj === 'string' ? userObj : (userObj.username || form.username));

            // Redirect user based on their role as per sample
            if (data.role === "CUSTOMER") {
                navigate("/customerhome");
            } else if (data.role === "ADMIN") {
                navigate("/adminhome");
            } else {
                throw new Error("Invalid user role");
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="split-container">
                {/* Left Side: Product Image Hero */}
                <div className="hero-panel">
                    <div className="hero-content">
                        <div className="brand-badge">SmartTech Ecosystem</div>
                        <h1>Welcome Back<br /><span>to SmartTech.</span></h1>
                        <p>Sign in to access your account, track orders, and explore the latest in smart technology.</p>
                        <div className="hero-features">
                            <div className="feature-item"><span>✦</span> Secure Login</div>
                            <div className="feature-item"><span>✦</span> Fast Checkout</div>
                            <div className="feature-item"><span>✦</span> Order Tracking</div>
                        </div>
                    </div>
                    <div className="hero-gradient-overlay"></div>
                    <img src="/smarttech-hero.png" alt="SmartTech Products" className="hero-image" />
                </div>

                {/* Right Side: Login Form */}
                <div className="form-panel">
                    <div className="auth-card">
                        <div className="mobile-logo">SmartTech</div>
                        <h2>Sign In</h2>
                        <p className="subtitle">Enter your credentials to continue</p>

                        {error && <div className="error">{error}</div>}

                        <form onSubmit={handleSubmit}>
                            <div className="input-group">
                                <input
                                    id="username"
                                    name="username"
                                    placeholder="Username"
                                    value={form.username}
                                    onChange={handleChange}
                                    required
                                    autoComplete="username"
                                />
                            </div>

                            <div className="input-group">
                                <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    placeholder="Password"
                                    value={form.password}
                                    onChange={handleChange}
                                    required
                                    autoComplete="current-password"
                                />
                            </div>

                            <div className="forgot-password">
                                <a href="/forgot-password">Forgot password?</a>
                            </div>

                            <button type="submit" disabled={loading}>
                                {loading ? "Signing in..." : "Sign In"}
                            </button>
                        </form>

                        <p className="login">
                            Don't have an account? <a href="/register">Create one</a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}
