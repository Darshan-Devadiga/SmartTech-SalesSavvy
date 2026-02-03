import React from "react";
import "../styles/Register.css"; // Reuse some auth styles or create new ones

export default function Dashboard() {
    const getUsername = () => {
        const raw = localStorage.getItem("username");
        if (raw) return raw;
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        return user.username || user.name || user.email || "User";
    };
    const username = getUsername();

    const handleLogout = () => {
        localStorage.clear();
        window.location.href = "/login";
    };

    return (
        <div className="dashboard-page" style={{ padding: "40px", background: "#f8fafc", minHeight: "100vh" }}>
            <nav style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "40px", background: "white", padding: "20px 40px", borderRadius: "16px", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)" }}>
                <h1 style={{ fontFamily: "'Outfit', sans-serif", fontSize: "24px", fontWeight: "700", color: "#4f46e5" }}>SalesSavvy</h1>
                <div style={{ display: "flex", alignItems: "center", gap: "20px" }}>
                    <span style={{ fontWeight: "500", color: "#1e293b" }}>Welcome, {username}</span>
                    <button onClick={handleLogout} style={{ padding: "10px 20px", background: "#fee2e2", color: "#dc2626", border: "none", borderRadius: "10px", fontWeight: "600", cursor: "pointer" }}>Logout</button>
                </div>
            </nav>

            <main>
                <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))", gap: "24px" }}>
                    <div style={{ background: "white", padding: "32px", borderRadius: "20px", boxShadow: "0 10px 15px -3px rgba(0,0,0,0.05)" }}>
                        <h3 style={{ marginBottom: "16px", color: "#64748b" }}>Total Sales</h3>
                        <p style={{ fontSize: "32px", fontWeight: "700", color: "#1e293b" }}>$24,500</p>
                    </div>
                    <div style={{ background: "white", padding: "32px", borderRadius: "20px", boxShadow: "0 10px 15px -3px rgba(0,0,0,0.05)" }}>
                        <h3 style={{ marginBottom: "16px", color: "#64748b" }}>Active Orders</h3>
                        <p style={{ fontSize: "32px", fontWeight: "700", color: "#1e293b" }}>128</p>
                    </div>
                    <div style={{ background: "white", padding: "32px", borderRadius: "20px", boxShadow: "0 10px 15px -3px rgba(0,0,0,0.05)" }}>
                        <h3 style={{ marginBottom: "16px", color: "#64748b" }}>New Customers</h3>
                        <p style={{ fontSize: "32px", fontWeight: "700", color: "#1e293b" }}>42</p>
                    </div>
                </div>
            </main>
        </div>
    );
}
