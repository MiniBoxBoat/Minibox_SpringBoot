package com.minibox.controller;

import com.minibox.dto.OrderDto;
import com.minibox.dto.ResponseEntity;
import com.minibox.service.OrderService;
import com.minibox.service.SaleService;
import com.minibox.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SaleService saleService;


    @PostMapping("order.do")
    public ResponseEntity<Object> order(OrderDto orderDto, String taken) {
        orderService.addOrder(orderDto, taken);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }

    @PostMapping("endOrder.do")
    public ResponseEntity<Object> endOrder(int orderId, double cost){
        saleService.addSaleInfoAndRemoveOrder(orderId, cost);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }
}
