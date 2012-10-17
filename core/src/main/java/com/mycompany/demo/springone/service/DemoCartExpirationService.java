package com.mycompany.demo.springone.service;

import java.util.Date;

public interface DemoCartExpirationService {

	public Date expireCart(Long orderId);
	
	public Date getNextExpirationDate();
	
}
