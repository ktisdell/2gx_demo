package com.mycompany.demo.springone.entity;

import java.util.Date;

import org.broadleafcommerce.core.order.domain.Order;

public interface DemoOrder extends Order {

	public Date getExpirationDate();
	
	public void setExpirationDate(Date date);
	
}
