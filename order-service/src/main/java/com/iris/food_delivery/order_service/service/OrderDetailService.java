package com.iris.food_delivery.order_service.service;

import com.iris.food_delivery.order_service.entity.OrderDetail;
import com.iris.food_delivery.order_service.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public List<OrderDetail> getAllOrderDetails(Long orderId) {
        return orderDetailRepository.findByOrderOrderId(orderId);
    }

    public OrderDetail saveOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    public void deleteOrderDetail(Long orderDtlId) {
        orderDetailRepository.deleteById(orderDtlId);
    }
}
