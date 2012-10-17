package com.mycompany.demo.springone.service;

import java.util.Date;
import java.util.HashSet;

import javax.annotation.Resource;

import org.springframework.scheduling.TaskScheduler;

public class DemoCartExpirationEvent implements Runnable {

	private static final HashSet<Long> cartsInQueue = new HashSet<Long>();
	
	@Resource(name="demoCartExpirationService")
	private DemoCartExpirationService cartExpirationService;
	
	@Resource(name="cartExpirationTaskScheduler")
	private TaskScheduler taskScheduler;
	
	private Long cartId;
	
	/**
	 * Don't call this method directly.  It should ONLY be called by the TaskScheduler.
	 */
	@Override
	public void run() {
		try {
			if (cartId == null) {
				return;
			}
			Date expiration = cartExpirationService.expireCart(cartId);
			if (expiration != null) {
				//if the cart is not expired, then reschedule...
				taskScheduler.schedule(this, expiration);
			} else {
				synchronized (cartsInQueue) {
					cartsInQueue.remove(cartId);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			synchronized (cartsInQueue) {
				cartsInQueue.remove(cartId);
			}
		}
	}
	
	public void schedule(Long cartId, Date dateToExpire) {
		this.cartId = cartId;
		synchronized (cartsInQueue) {
			if (cartsInQueue.contains(cartId)) {
				return;
			}
			cartsInQueue.add(cartId);
		}
		taskScheduler.schedule(this, dateToExpire);
	}
	
}
