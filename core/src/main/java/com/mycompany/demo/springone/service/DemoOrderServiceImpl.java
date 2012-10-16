package com.mycompany.demo.springone.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderServiceImpl;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.demo.springone.entity.DemoOrder;
import com.mycompany.demo.springone.entity.DemoSku;

public class DemoOrderServiceImpl extends OrderServiceImpl implements
		DemoOrderService {
	
	@Resource(name="blInventoryService")
	protected InventoryService inventoryService;
	
	@Override
	@Transactional(value="blTransactionManager")
	public Date expireCart(Long orderId) {
		DemoOrder cart = (DemoOrder)super.findOrderById(orderId);
		if (cart == null) {
			return null;
		}
		
		if (OrderStatus.IN_PROCESS.equals(cart.getStatus())) {
			if (cart.getExpirationDate() != null && 
					cart.getExpirationDate().getTime() <= System.currentTimeMillis()) {
				
				//Remove the items that need to be removed.
				List<OrderItem> items = cart.getOrderItems();
				for (OrderItem item : items) {
					if (item instanceof DiscreteOrderItem) {
						DiscreteOrderItem discreteItem = (DiscreteOrderItem)item;
						DemoSku demoSku = (DemoSku)discreteItem.getSku();
						if (demoSku.getFlashSellable()) {
							try {
								//Remove the item from the cart
								super.removeItem(cart.getId(), item.getId(), false);
							} catch (RemoveFromCartException e) {
								e.printStackTrace();
							}
						}
					}
				}
								
				//Save the cart
				cart.setExpirationDate(null);
				try {
					super.save(cart, true);
				} catch (PricingException e) {
					e.printStackTrace();
				}
				return null;
			} else {
				return cart.getExpirationDate();
			}
		}
		
		return null;
	}

	
}
