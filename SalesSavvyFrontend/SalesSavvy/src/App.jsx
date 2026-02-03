import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Register from "./components/Register";
import Login from "./components/Login";
import AdminDashboard from "./components/AdminDashboard";
import CustomerHome from "./components/CustomerHome";

import UserCartPage from "./components/UserCartPage";
import OrdersPage from "./components/OrdersPage";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<AdminDashboard />} />
        <Route path="/customerhome" element={<CustomerHome />} />
        <Route path="/adminhome" element={<AdminDashboard />} />
        <Route path="/UserCartPage" element={<UserCartPage />} />
        <Route path="/orders" element={<OrdersPage />} />
      </Routes>
    </Router>
  );
};

export default App;
