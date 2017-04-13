package com.jft.market.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.jft.market.model.OrderCart;

public interface OrderCartRepository extends JpaRepository<OrderCart, Long> {

	public OrderCart findByUuid(String uuid);

	public OrderCart findByCustomerUuid(String customerUuid);
}
