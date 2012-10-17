package com.mycompany.demo.springone.workflow;

import java.util.HashMap;

import javax.annotation.Resource;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.workflow.CartOperationContext;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.mycompany.demo.springone.entity.DemoOrder;
import com.mycompany.demo.springone.entity.DemoSku;
import com.mycompany.demo.springone.service.DemoCartExpirationEvent;
import com.mycompany.demo.springone.service.DemoCartExpirationService;

public class DemoRemoveFlashItemActivity extends BaseActivity implements ApplicationContextAware {
	
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    @Resource(name = "blInventoryService")
    protected InventoryService inventoryService;
    
    @Resource(name = "demoCartExpirationService")
    protected DemoCartExpirationService orderService;
    
    protected ApplicationContext context;
    
    public ProcessContext execute(ProcessContext context) throws Exception {
        CartOperationRequest request = ((CartOperationContext) context).getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
        DemoOrder order = (DemoOrder)request.getOrder();

        // Find the OrderItem from the database based on its ID
		OrderItem orderItem = orderItemService.readOrderItemById(orderItemRequestDTO.getOrderItemId());
        
		if (orderItem instanceof DiscreteOrderItem) {
			DemoSku demoSku = (DemoSku)((DiscreteOrderItem)orderItem).getSku();
			if (demoSku.getFlashSellable()) {
				HashMap<Sku, Integer> inventoryToIncrement = new HashMap<Sku, Integer>();
				inventoryToIncrement.put(demoSku, orderItem.getQuantity());
				inventoryService.incrementInventory(inventoryToIncrement);
			}
		}
		
		//Schedule the cart to expire
        DemoCartExpirationEvent event = (DemoCartExpirationEvent)this.context.getBean("demoCartExpirationEvent");
        order.setExpirationDate(orderService.getNextExpirationDate());
        event.schedule(order.getId(), order.getExpirationDate());
        
        return context;
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

}
