package com.mycompany.demo.springone.service;

import java.util.Date;

import org.broadleafcommerce.core.order.service.OrderService;

public interface DemoOrderService extends OrderService {

	public Date expireCart(Long orderId);
	
}
