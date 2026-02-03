import React from 'react';

export function ProductCard({ product, addToCart }) {
    const imageUrl = (product.images && product.images.length > 0)
        ? product.images[0]
        : (product.image_url || product.image || "/placeholder-product.png");

    return (
        <div className="product-card">
            <div className="product-image-wrapper">
                <img
                    src={imageUrl}
                    alt={product.name}
                    className="product-image"
                    loading="lazy"
                />
            </div>
            <div className="product-info">
                <span className="product-category">{product.category}</span>
                <h3 className="product-name">{product.name}</h3>
                <p className="product-description">{product.description}</p>
                <div className="product-footer">
                    <span className="product-price">${product.price}</span>
                    <button
                        className="add-to-cart-btn"
                        onClick={() => addToCart(product.product_id || product.id)}
                    >
                        Add to Cart
                    </button>
                </div>
            </div>
        </div>
    );
}
