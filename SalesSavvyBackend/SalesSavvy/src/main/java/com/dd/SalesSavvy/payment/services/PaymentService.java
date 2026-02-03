package com.dd.SalesSavvy.payment.services;

import com.dd.SalesSavvy.Entities.CartItems;
import com.dd.SalesSavvy.Entities.OrderItem;
import com.dd.SalesSavvy.Entities.OrderStatus;
import com.dd.SalesSavvy.Entities.Order;
import com.dd.SalesSavvy.cart.cartRepo.CartRepo;
import com.dd.SalesSavvy.payment.Repo.OrderItemRepo;
import com.dd.SalesSavvy.payment.Repo.OrderRepo;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService implements PaymentServiceContract {

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final CartRepo cartRepo;

    public PaymentService(OrderRepo orderRepo, OrderItemRepo orderItemRepo, CartRepo cartRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartRepo = cartRepo;
    }

    @Override
    public String createOrder(int userId, BigDecimal totalAmount, List<OrderItem> cartItems) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        var orderRequest = new JSONObject();
        orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_"+System.currentTimeMillis());

        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        Order order = new Order();
        order.setOrderId(razorpayOrder.get("id"));
        order.setUserId(userId);
        order.setTotalAmount (totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        orderRepo.save(order);
        return razorpayOrder.get("id");
    }

    @Override
    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature, int userId) {
        try{
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            boolean isSignatureValid = com.razorpay.Utils.verifyPaymentSignature(attributes, razorpayKeySecret);

            if(isSignatureValid) {
                Order order = orderRepo.findById(razorpayOrderId).orElseThrow(() -> new RuntimeException("Order not found"));
                order.setStatus(OrderStatus.SUCCESS);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepo.save(order);

                List<CartItems> cartItems = cartRepo.findCartItemsWithProductDetails(userId);
                for(CartItems item : cartItems) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(item.getProduct().getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPricePerUnit(item.getProduct().getPrice());
                    orderItem.setTotalPrice(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    orderItemRepo.save(orderItem);
                }
                cartRepo.deleteAllCartItemsByUserId(userId);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
