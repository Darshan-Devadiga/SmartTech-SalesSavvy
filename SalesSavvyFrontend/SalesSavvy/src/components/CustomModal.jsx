import React, { useEffect, useState } from "react";
import "../styles/CustomModal.css";

const CustomModal = ({ modalType, onClose, onSubmit, response }) => {
    const [formData, setFormData] = useState({
        name: "",
        description: "",
        price: "",
        stock: "",
        categoryId: "",
        imageUrl: "",
        month: "",
        year: "",
        date: "",
    });

    const [inputValue, setInputValue] = useState(""); // Generalized input for IDs

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleGeneralInputChange = (e) => {
        setInputValue(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        switch (modalType) {
            case "addProduct": {
                const processedData = {
                    ...formData,
                    price: parseFloat(formData.price),
                    stock: parseInt(formData.stock, 10),
                    categoryId: parseInt(formData.categoryId, 10),
                };
                onSubmit(processedData);
                break;
            }
            case "deleteProduct": {
                const productId = parseInt(inputValue, 10);
                onSubmit({ productId });
                break;
            }
            case "viewUser": {
                const userId = parseInt(inputValue, 10);
                onSubmit({ userId });
                break;
            }
            case "modifyUser": {
                const userId = parseInt(inputValue, 10);
                onSubmit({ userId });
                break;
            }
            case "monthlyBusiness": {
                const month = formData.month;
                const year = formData.year;
                onSubmit({ month, year });
                break;
            }
            case "dailyBusiness": {
                const date = formData.date;
                onSubmit({ date });
                break;
            }
            case "yearlyBusiness": {
                const year = formData.year;
                onSubmit({ year });
                break;
            }
            case "overallBusiness": {
                onSubmit();
                break;
            }
            default:
                break;
        }
    };

    return (
        <div className="modal-overlay" onClick={(e) => e.target.className === 'modal-overlay' && onClose()}>
            <div className="modal-content">
                {/* Add Product Form */}
                {modalType === "addProduct" &&
                    (!response ? (
                        <>
                            <h2>Add Product</h2>
                            <form className="modal-form" onSubmit={handleSubmit}>
                                <div className="modal-form-item">
                                    <label htmlFor="name">Name:</label>
                                    <input
                                        type="text"
                                        id="name"
                                        name="name"
                                        placeholder="E.g. Wireless Mouse"
                                        value={formData.name}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>

                                <div className="modal-form-item">
                                    <label htmlFor="price">Price:</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        id="price"
                                        name="price"
                                        placeholder="29.99"
                                        value={formData.price}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>

                                <div className="modal-form-item">
                                    <label htmlFor="stock">Stock:</label>
                                    <input
                                        type="number"
                                        id="stock"
                                        name="stock"
                                        placeholder="100"
                                        value={formData.stock}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>

                                <div className="modal-form-item">
                                    <label htmlFor="categoryId">Category ID:</label>
                                    <input
                                        type="number"
                                        id="categoryId"
                                        name="categoryId"
                                        placeholder="1"
                                        value={formData.categoryId}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>

                                <div className="modal-form-item">
                                    <label htmlFor="imageUrl">Image URL:</label>
                                    <input
                                        type="text"
                                        id="imageUrl"
                                        name="imageUrl"
                                        placeholder="https://example.com/image.jpg"
                                        value={formData.imageUrl}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div className="modal-form-item">
                                    <label htmlFor="description">Description:</label>
                                    <textarea
                                        id="description"
                                        name="description"
                                        placeholder="Product details..."
                                        value={formData.description}
                                        onChange={handleInputChange}
                                        required
                                    ></textarea>
                                </div>
                                <button type="submit">Submit</button>
                                <button type="button" onClick={onClose}>Cancel</button>
                            </form>
                        </>
                    ) : (
                        <>
                            <h2>Product Added Successfully</h2>
                            <div className="full-products">
                                <div className="product-details img">
                                    <img src={response.imageUrl} alt="Product" />
                                </div>
                                <div className="product-details-info">
                                    <div className="product-details">
                                        <strong>Name:</strong>
                                        <span>{response?.product?.name}</span>
                                    </div>
                                    <div className="product-details">
                                        <strong>Description:</strong>
                                        <span>{response?.product?.description}</span>
                                    </div>
                                    <div className="product-details">
                                        <strong>Price:</strong>
                                        <span>₹{response?.product?.price}</span>
                                    </div>
                                    <div className="product-details">
                                        <strong>Stock:</strong>
                                        <span>{response?.product?.stock}</span>
                                    </div>
                                    <div className="product-details">
                                        <strong>Category:</strong>
                                        <span>{response?.product?.category?.categoryName}</span>
                                    </div>
                                </div>
                            </div>
                            <button onClick={onClose}>Close</button>
                        </>
                    ))}

                {/* Delete Product Form */}
                {modalType === "deleteProduct" &&
                    (!response ? (
                        <>
                            <h2>Delete Product</h2>
                            <p style={{ marginBottom: '1rem', color: '#64748b' }}>Enter the ID of the product you wish to remove.</p>
                            <form className="modal-form" onSubmit={handleSubmit}>
                                <div className="modal-form-item">
                                    <label htmlFor="delete-product-id">Product ID:</label>
                                    <input
                                        type="number"
                                        id="delete-product-id"
                                        placeholder="Enter Product ID"
                                        value={inputValue}
                                        onChange={handleGeneralInputChange}
                                        required
                                    />
                                </div>
                                <button type="submit" style={{ background: '#dc2626' }}>Delete Product</button>
                                <button type="button" onClick={onClose}>Cancel</button>
                            </form>
                        </>
                    ) : (
                        <div>
                            <h2>{response.message}</h2>
                            <button onClick={onClose}>Close</button>
                        </div>
                    ))}

                {/* View User Details Form */}
                {modalType === "viewUser" && !response && (
                    <>
                        <h2>View User Details</h2>
                        <form className="modal-form" onSubmit={handleSubmit}>
                            <div className="modal-form-item">
                                <label htmlFor="view-user-id">User ID:</label>
                                <input
                                    type="number"
                                    id="view-user-id"
                                    placeholder="Enter User ID"
                                    value={inputValue}
                                    onChange={handleGeneralInputChange}
                                    required
                                />
                            </div>
                            <button type="submit">Fetch Details</button>
                            <button type="button" onClick={onClose}>Cancel</button>
                        </form>
                    </>
                )}

                {/* Response Display for View User */}
                {modalType === "response" && response && (
                    <>
                        {response.user ? (
                            <>
                                <h2>User Details</h2>
                                <div className="user-details">
                                    <p><strong>User ID:</strong> {response.user.userId}</p>
                                    <p><strong>Username:</strong> {response.user.username}</p>
                                    <p><strong>Email:</strong> {response.user.email}</p>
                                    <p><strong>Role:</strong> {response.user.role}</p>
                                    <p><strong>Created:</strong> {new Date(response.user.createdAt).toLocaleDateString()}</p>
                                </div>
                            </>
                        ) : (
                            <>
                                <h2>Operation Status</h2>
                                <p>{response.message || "Something went wrong."}</p>
                            </>
                        )}
                        <button onClick={onClose}>Close</button>
                    </>
                )}

                {modalType === "monthlyBusiness" && (
                    <>
                        <h2>Monthly Business</h2>
                        <form className="modal-form" onSubmit={handleSubmit}>
                            {!response && (
                                <>
                                    <div className="modal-form-item">
                                        <label htmlFor="month">Month (1-12):</label>
                                        <input
                                            type="number"
                                            id="month"
                                            name="month"
                                            min="1"
                                            max="12"
                                            placeholder="10"
                                            onChange={handleInputChange}
                                            required
                                        />
                                    </div>
                                    <div className="modal-form-item">
                                        <label htmlFor="year">Year:</label>
                                        <input
                                            type="number"
                                            id="year"
                                            name="year"
                                            placeholder="2025"
                                            onChange={handleInputChange}
                                            required
                                        />
                                    </div>
                                    <button type="submit">Get Report</button>
                                </>
                            )}
                            {response && (
                                <div className="business-summary">
                                    <div className="business-response-item highlighted">
                                        <strong>Total Monthly Revenue:</strong>
                                        <span className="revenue-value">₹{(response?.monthlyBusiness?.totalRevenue || response?.monthlyBusiness?.totalBusiness || response?.monthlyBusiness?.revenue || 0).toLocaleString()}</span>
                                    </div>
                                    <h5 className="breakdown-title">Sales by Category</h5>
                                    <div className="category-grid">
                                        {response?.monthlyBusiness?.categorySales && Object.keys(response.monthlyBusiness.categorySales).length > 0 ? (
                                            Object.keys(response.monthlyBusiness.categorySales).map((key) => (
                                                <div key={key} className="business-response-item">
                                                    <span>{key}</span>
                                                    <span className="count-value">{response.monthlyBusiness.categorySales[key]} Orders</span>
                                                </div>
                                            ))
                                        ) : (
                                            <p className="no-data">No sales data available for this month.</p>
                                        )}
                                    </div>
                                </div>
                            )}
                            <button type="button" onClick={onClose}>Cancel</button>
                        </form>
                    </>
                )}

                {modalType === "dailyBusiness" && (
                    <>
                        <h2>Daily Business</h2>
                        <form className="modal-form" onSubmit={handleSubmit}>
                            {!response && (
                                <>
                                    <div className="modal-form-item">
                                        <label htmlFor="date">Date:</label>
                                        <input
                                            type="date"
                                            id="date"
                                            name="date"
                                            onChange={handleInputChange}
                                            required
                                        />
                                    </div>
                                    <button type="submit">Get Report</button>
                                </>
                            )}
                            {response && (
                                <div className="business-summary">
                                    <div className="business-response-item highlighted">
                                        <strong>Total Daily Revenue:</strong>
                                        <span className="revenue-value">₹{(response?.dailyBusiness?.totalRevenue || response?.dailyBusiness?.totalBusiness || response?.dailyBusiness?.revenue || 0).toLocaleString()}</span>
                                    </div>
                                    <h5 className="breakdown-title">Sales by Category</h5>
                                    <div className="category-grid">
                                        {response?.dailyBusiness?.categorySales && Object.keys(response.dailyBusiness.categorySales).length > 0 ? (
                                            Object.keys(response.dailyBusiness.categorySales).map((key) => (
                                                <div key={key} className="business-response-item">
                                                    <span>{key}</span>
                                                    <span className="count-value">{response.dailyBusiness.categorySales[key]} Orders</span>
                                                </div>
                                            ))
                                        ) : (
                                            <p className="no-data">No sales data available for this date.</p>
                                        )}
                                    </div>
                                </div>
                            )}
                            <button type="button" onClick={onClose}>Cancel</button>
                        </form>
                    </>
                )}

                {modalType === "yearlyBusiness" && (
                    <>
                        <h2>Yearly Business</h2>
                        <form className="modal-form" onSubmit={handleSubmit}>
                            {!response && (
                                <>
                                    <div className="modal-form-item">
                                        <label htmlFor="year">Year:</label>
                                        <input
                                            type="number"
                                            id="year"
                                            name="year"
                                            placeholder="2025"
                                            onChange={handleInputChange}
                                            required
                                        />
                                    </div>
                                    <button type="submit">Get Report</button>
                                </>
                            )}
                            {response && (
                                <div className="business-summary">
                                    <div className="business-response-item highlighted">
                                        <strong>Total Yearly Revenue:</strong>
                                        <span className="revenue-value">₹{(response?.yearlyBusiness?.totalRevenue || response?.yearlyBusiness?.totalBusiness || response?.yearlyBusiness?.revenue || 0).toLocaleString()}</span>
                                    </div>
                                    <h5 className="breakdown-title">Sales by Category</h5>
                                    <div className="category-grid">
                                        {response?.yearlyBusiness?.categorySales && Object.keys(response.yearlyBusiness.categorySales).length > 0 ? (
                                            Object.keys(response.yearlyBusiness.categorySales).map((key) => (
                                                <div key={key} className="business-response-item">
                                                    <span>{key}</span>
                                                    <span className="count-value">{response.yearlyBusiness.categorySales[key]} Orders</span>
                                                </div>
                                            ))
                                        ) : (
                                            <p className="no-data">No sales data available for this year.</p>
                                        )}
                                    </div>
                                </div>
                            )}
                            <button type="button" onClick={onClose}>Cancel</button>
                        </form>
                    </>
                )}

                {modalType === "overallBusiness" && (
                    <>
                        <h2>Overall Business</h2>
                        <form className="modal-form" onSubmit={handleSubmit}>
                            {!response && (
                                <>
                                    <p style={{ marginBottom: '1rem', color: '#64748b' }}>Generate a summary of total revenue since inception.</p>
                                    <button type="submit">Get Overall Summary</button>
                                </>
                            )}
                            {response && (
                                <div className="business-summary">
                                    <div className="business-response-item highlighted">
                                        <strong>Total Platform Revenue:</strong>
                                        <span className="revenue-value">₹{(response?.overallBusiness?.totalRevenue || response?.overallBusiness?.totalBusiness || response?.overallBusiness?.revenue || 0).toLocaleString()}</span>
                                    </div>
                                    <h5 className="breakdown-title">Sales count by Category</h5>
                                    <div className="category-grid">
                                        {response?.overallBusiness?.categorySales && Object.keys(response.overallBusiness.categorySales).length > 0 ? (
                                            Object.keys(response.overallBusiness.categorySales).map((key) => (
                                                <div key={key} className="business-response-item">
                                                    <span>{key}</span>
                                                    <span className="count-value">{response.overallBusiness.categorySales[key]} Orders</span>
                                                </div>
                                            ))
                                        ) : (
                                            <p className="no-data">No category data available.</p>
                                        )}
                                    </div>
                                </div>
                            )}
                            <button type="button" onClick={onClose}>Cancel</button>
                        </form>
                    </>
                )}

                {modalType === "modifyUser" && (
                    <ModifyUserFormComponent onClose={onClose} prefilledId={inputValue} />
                )}
            </div>
        </div>
    );
};

export default CustomModal;

const ModifyUserFormComponent = ({ onClose, prefilledId }) => {
    const [userId, setUserId] = useState(prefilledId || "");
    const [userDetails, setUserDetails] = useState(null);
    const [updated, setUpdated] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleFetchUser = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/admin/user/getbyid?userId=${userId}`, {
                method: "GET",
                credentials: "include",
            });

            if (response.ok) {
                const user = await response.json();
                setUserDetails(user);
            } else {
                alert("User not found or error occurred.");
            }
        } catch (error) {
            console.error("Error fetching user details", error);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateUser = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const payload = {
            userId: parseInt(userId, 10),
            username: formData.get("username"),
            email: formData.get("email"),
            role: formData.get("role"),
        };

        try {
            const response = await fetch("http://localhost:8080/admin/user/modify", {
                method: "PUT",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload),
            });

            if (response.ok) {
                const user = await response.json();
                setUpdated(true);
                setUserDetails(user);
            } else {
                alert("Failed to update user.");
            }
        } catch (error) {
            console.error("Error updating user", error);
        }
    };

    if (!userDetails) {
        return (
            <>
                <h2>Modify User</h2>
                <form onSubmit={handleFetchUser} className="modal-form">
                    <div className="modal-form-item">
                        <label htmlFor="user-id">User ID:</label>
                        <input
                            type="number"
                            id="user-id"
                            name="user-id"
                            value={userId}
                            onChange={(e) => setUserId(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" disabled={loading}>{loading ? 'Fetching...' : 'Get UserDetails'}</button>
                    <button type="button" onClick={onClose}>Cancel</button>
                </form>
            </>
        );
    }

    if (userDetails && !updated) {
        return (
            <div>
                <h2>Update User Info</h2>
                <form onSubmit={handleUpdateUser} className="modal-form">
                    <div className="modal-form-item">
                        <label htmlFor="user-id-readonly">User ID:</label>
                        <input
                            type="text"
                            id="user-id-readonly"
                            value={userId}
                            readOnly
                            style={{ background: '#f1f5f9' }}
                        />
                    </div>
                    <div className="modal-form-item">
                        <label htmlFor="username">Username:</label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            defaultValue={userDetails?.username}
                            required
                        />
                    </div>
                    <div className="modal-form-item">
                        <label htmlFor="email">Email:</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            defaultValue={userDetails?.email}
                            required
                        />
                    </div>
                    <div className="modal-form-item">
                        <label htmlFor="role">Role:</label>
                        <select id="role" name="role" defaultValue={userDetails.role} className="modal-input" style={{ padding: '0.75rem', borderRadius: '12px', border: '1px solid #e2e8f0' }}>
                            <option value="CUSTOMER">CUSTOMER</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                    </div>
                    <button type="submit">Save Changes</button>
                    <button type="button" onClick={() => setUserDetails(null)}>Back</button>
                </form>
            </div>
        );
    }

    if (updated) {
        return (
            <div>
                <h2>User Updated Successfully</h2>
                <div className="user-details">
                    <p><strong>User ID:</strong> {userDetails.userId}</p>
                    <p><strong>Username:</strong> {userDetails.username}</p>
                    <p><strong>Email:</strong> {userDetails.email}</p>
                    <p><strong>Role:</strong> {userDetails.role}</p>
                </div>
                <button onClick={onClose}>Close</button>
            </div>
        );
    }
    return null;
};
