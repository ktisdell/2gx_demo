package com.mycompany.demo.springone.workflow;

import java.util.HashMap;

import javax.annotation.Resource;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
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

public class DemoAddFlashItemActivity extends BaseActivity implements ApplicationContextAware {
	
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
        
        HashMap<Sku, Integer> inventoryToDecrement = new HashMap<Sku, Integer>();
        
        //Find the sku
        Sku sku = null;
        if (skuId != null) {
            sku = catalogService.findSkuById(skuId);
        }
        
        if (sku == null) {
	        for (OrderItem oi : order.getOrderItems()) {
				if (oi.getId().equals(request.getItemRequest().getOrderItemId())) {
					if (oi instanceof DiscreteOrderItem) {
						sku = ((DiscreteOrderItem)oi).getSku();
					}
					break;
				}
			}
        }
        
        //Decrement inventory
        if (sku != null && sku instanceof DemoSku) {
	        if (((DemoSku)sku).getFlashSellable()) {
		        inventoryToDecrement.put(sku, request.getItemRequest().getQuantity());
		        inventoryService.decrementInventory(inventoryToDecrement);
	        }
        }
        
        for (OrderItem item : order.getOrderItems()) {
        	if (item instanceof DiscreteOrderItem) {
        		sku = ((DiscreteOrderItem)item).getSku();
        		if (sku instanceof DemoSku && ((DemoSku)sku).getFlashSellable()) {
        			//Schedule the cart to expire
        	        DemoCartExpirationEvent event = (DemoCartExpirationEvent)this.context.getBean("demoCartExpirationEvent");
        			order.setExpirationDate(orderService.getNextExpirationDate());
        			event.schedule(order.getId(), order.getExpirationDate());
        			break;
        		}
        	}
        }
        
        return context;
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

}
