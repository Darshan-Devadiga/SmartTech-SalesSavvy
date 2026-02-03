import React from 'react';
import { ProductCard } from './ProductCard';

export function ProductList({ products, loading, error, onRetry, addToCart }) {
    if (loading) {
        return (
            <div className="loading-state">
                <span className="loader"></span>
                <p>Discovering amazing products...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="error-state">
                <p>⚠️ {error}</p>
                <button onClick={onRetry} className="add-to-cart-btn">Try Again</button>
            </div>
        );
    }

    if (!Array.isArray(products) || products.length === 0) {
        return (
            <div className="empty-state">
                <p>No products available.</p>
            </div>
        );
    }

    return (
        <div className="products-grid">
            {products.map(product => (
                <ProductCard
                    key={product.product_id || product.id}
                    product={product}
                    addToCart={addToCart}
                />
            ))}
        </div>
    );
}
