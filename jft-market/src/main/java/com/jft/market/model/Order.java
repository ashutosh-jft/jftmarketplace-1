package com.jft.market.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_orders")
@Setter
@Getter
@NoArgsConstructor
public class Order {

	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "order_id")
	private Long Id;
	@Column(name = "product_id")
	private Long productId;
	@Column(name = "order_status")
	private String orderStatus;
	private String uuid;

	@ManyToOne
	Customer customer;
}