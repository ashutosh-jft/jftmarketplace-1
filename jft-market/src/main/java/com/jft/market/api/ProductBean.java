package com.jft.market.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductBean {

	private Integer id;
	private String name;
	private Integer price;
	private String description;
	private String features;

	public ProductBean(Integer id, String name, Integer price, String description, String features) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.description = description;
		this.features = features;
	}
}
