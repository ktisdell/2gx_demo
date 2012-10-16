package com.mycompany.demo.springone.entity;

import org.broadleafcommerce.core.catalog.domain.Sku;

public interface DemoSku extends Sku {

	public Boolean getFlashSellable();
	
	public void setFlashSellable(Boolean flashSellable);
	
}
