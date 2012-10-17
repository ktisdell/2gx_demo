package com.mycompany.demo.springone.entity;

import org.broadleafcommerce.core.order.domain.Order;

import java.util.Date;

public interface DemoOrder extends Order {

	public Date getExpirationDate();
	
	public void setExpirationDate(Date date);

    public Long getSecondsUntilExpiration();
	
}
