package com.mycompany.demo.springone.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.broadleafcommerce.core.order.domain.OrderImpl;

@Entity
@Table(name="DEMO_ORDER")
public class DemoOrderImpl extends OrderImpl implements DemoOrder {

	private static final long serialVersionUID = 1L;

	@Column(name="EXPIRY_DATE")
	@Temporal(TemporalType.TIME)
	protected Date expirationDate;
	
	@Override
	public Date getExpirationDate() {
		return this.expirationDate;
	}

	@Override
	public void setExpirationDate(Date date) {
		this.expirationDate = date;
	}

}
