package com.mycompany.demo.springone.workflow;

import java.util.HashMap;

import javax.annotation.Resource;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.order.service.OrderItemService;
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

public class DemoUpdateFlashItemActivity extends BaseActivity implements ApplicationContextAware {
	
	@Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blInventoryService")
    protected InventoryService inventoryService;
    
    @Resource(name = "demoCartExpirationService")
    protected DemoCartExpirationService orderService;
    
    protected ApplicationContext context;

    public ProcessContext execute(ProcessContext context) throws Exception {

        CartOperationRequest request = ((CartOperationContext) context).getSeedData();
        Long skuId = request.getItemRequest().getSkuId();
        DemoOrder order = (DemoOrder)request.getOrder();
        
        HashMap<Sku, Integer> inventoryToChange = new HashMap<Sku, Integer>();
        
        //First, get the Sku
        DemoSku sku = null;
        if (skuId != null) {
            sku = (DemoSku)catalogService.findSkuById(skuId);
        }
        
        //Check if this logic even applies
        if (sku.getFlashSellable()) {
        	//Find the inventory delta
	        Integer inventory = request.getOrderItemQuantityDelta();
	        if (inventory == 0) {
	        	//No change, so return
	        	return context;
	        }
	        
	        boolean increment = true;
	        if (inventory > 0) {
	        	increment = false;
	        }
	        
	        //Put the absolute value of the inventory to change in a map
	        inventoryToChange.put(sku, Math.abs(inventory));
	        
	        //Call service to increment or decrement inventory
	        if (increment) {
	        	inventoryService.incrementInventory(inventoryToChange);
	        } else {
	        	inventoryService.decrementInventory(inventoryToChange);
	        }
	        
	        //Schedule the cart to expire
	        DemoCartExpirationEvent event = (DemoCartExpirationEvent)this.context.getBean("demoCartExpirationEvent");
	        order.setExpirationDate(orderService.getNextExpirationDate());
	        event.schedule(order.getId(), order.getExpirationDate());
        }
        
        return context;
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

}
