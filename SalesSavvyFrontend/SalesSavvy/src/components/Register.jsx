import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Register.css";

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    role: "CUSTOMER",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/api/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(form),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Registration failed");
      }

      setSuccess("Account created successfully! Redirecting to login...");
      setForm({
        username: "",
        email: "",
        password: "",
        role: "CUSTOMER",
      });

      // Redirect to login after 1 seconds
      setTimeout(() => {
        navigate("/login");
      }, 1000);
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
            <h1>Experience Future <br /><span>of Commerce.</span></h1>
            <p>Join thousands of users exploring premium gadgets and smart home solutions on the world's most advanced platform.</p>
            <div className="hero-features">
              <div className="feature-item"><span>✦</span> Premium Selection</div>
              <div className="feature-item"><span>✦</span> Smart Integration</div>
              <div className="feature-item"><span>✦</span> 24/7 Support</div>
            </div>
          </div>
          <div className="hero-gradient-overlay"></div>
          <img src="/smarttech-hero.png" alt="SmartTech Products" className="hero-image" />
        </div>

        {/* Right Side: Registration Form */}
        <div className="form-panel">
          <div className="auth-card">
            <div className="mobile-logo">SmartTech</div>
            <h2>Create Account</h2>
            <p className="subtitle">Join the SmartTech community today</p>

            {error && <div className="error">{error}</div>}
            {success && <div className="success">{success}</div>}

            <form onSubmit={handleSubmit}>
              <div className="input-group">
                <input
                  id="username"
                  name="username"
                  placeholder="Choose a username"
                  value={form.username}
                  onChange={handleChange}
                  required
                  autoComplete="username"
                />
              </div>

              <div className="input-group">
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="Enter your email address"
                  value={form.email}
                  onChange={handleChange}
                  required
                  autoComplete="email"
                />
              </div>

              <div className="input-group">
                <input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="Create a strong password"
                  value={form.password}
                  onChange={handleChange}
                  required
                  autoComplete="new-password"
                  minLength="6"
                />
              </div>

              <div className="input-group">
                <select
                  id="role"
                  name="role"
                  value={form.role}
                  onChange={handleChange}
                >
                  <option value="CUSTOMER">Customer Account</option>
                  <option value="ADMIN">Admin Account</option>
                </select>
              </div>

              <button type="submit" disabled={loading}>
                {loading ? "Creating your account..." : "Start Shopping Now"}
              </button>
            </form>

            <p className="login">
              Already have an account? <a href="/login">Sign in</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
