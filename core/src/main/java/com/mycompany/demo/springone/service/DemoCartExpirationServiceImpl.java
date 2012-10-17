package com.mycompany.demo.springone.service;

import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.demo.springone.entity.DemoOrder;
import com.mycompany.demo.springone.entity.DemoSku;

import javax.annotation.Resource;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service("demoCartExpirationService")
public class DemoCartExpirationServiceImpl implements
		DemoCartExpirationService {
	
	@Resource(name="blOrderService")
	protected OrderService orderService;
	
	@Override
	@Transactional(value="blTransactionManager")
	public Date expireCart(Long orderId) {
		DemoOrder cart = (DemoOrder)orderService.findOrderById(orderId);
		if (cart == null) {
			return null;
		}
		
		if (OrderStatus.IN_PROCESS.equals(cart.getStatus())) {
			if (cart.getExpirationDate() != null && 
					cart.getExpirationDate().getTime() <= System.currentTimeMillis()) {
				
				//Collect the items to be removed
				List<OrderItem> items = cart.getOrderItems();
				HashSet<Long> idsToRemove = new HashSet<Long>();
				for (OrderItem item : items) {
					if (item instanceof DiscreteOrderItem) {
						DiscreteOrderItem discreteItem = (DiscreteOrderItem)item;
						DemoSku demoSku = (DemoSku)discreteItem.getSku();
						if (demoSku.getFlashSellable()) {
							idsToRemove.add(item.getId());
						}
					}
				}
				
				//Remove the items that need to be removed.
				for (Long id : idsToRemove) {
					try {
						//Remove the item from the cart
						orderService.removeItem(cart.getId(), id, false);
					} catch (RemoveFromCartException e) {
						e.printStackTrace();
					}
				}
								
				//Save the cart
				cart.setExpirationDate(null);
				try {
					orderService.save(cart, true);
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

	public Date getNextExpirationDate() {
		//We'll provide a timeout of 30 seconds for carts to expire
		return new Date(System.currentTimeMillis() + 30000L);
	}
	
}
