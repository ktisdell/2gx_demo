package com.mycompany.demo.springone.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;

@Entity
@Table(name="DEMO_SKU")
public class DemoSkuImpl extends SkuImpl implements DemoSku {

	private static final long serialVersionUID = 1L;

	@Column(name="FLASH_SELLABLE")
	@AdminPresentation(friendlyName = "Flash Sellable", order = 99, group = "ProductImpl_Product_Description", prominent = true, groupOrder = 1)
    protected Boolean flashSellable;
	
	@Override
	public Boolean getFlashSellable() {
		if (this.flashSellable == null) {
			return false;
		}
		return this.flashSellable;
	}

	@Override
	public void setFlashSellable(Boolean flashSellable) {
		this.flashSellable = flashSellable;
	}

}
