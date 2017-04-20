package com.jft.market.service;

import static com.jft.market.model.QPurchaseOrder.purchaseOrder;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jft.market.api.OrderStatus;
import com.jft.market.api.ws.PaymentResponseWS;
import com.jft.market.exceptions.ExceptionConstants;
import com.jft.market.model.Customer;
import com.jft.market.model.PaymentInstrument;
import com.jft.market.model.Product;
import com.jft.market.model.PurchaseOrder;
import com.jft.market.repository.CustomerRepository;
import com.jft.market.repository.OrderRepository;
import com.jft.market.repository.PaymentInstrumentRepository;
import com.jft.market.repository.ProductRepository;
import com.jft.market.util.EntityPredicates;
import com.jft.market.util.Preconditions;
import com.litle.sdk.generate.SaleResponse;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Predicate;


@Slf4j
@Service("purchaseService")
public class PurchaseServiceimpl implements PurchaseService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private VantivServiceImpl vantivService;

	@Autowired
	private PaymentInstrumentRepository paymentInstrumentRepository;

	@Autowired
	private OrderRepository orderRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public final String successResponse = "000";

	@Override
	@Transactional
	public PaymentResponseWS purchaseProduct(String customerUuid, String productUuid) {
		Customer customer = getCustomer(customerUuid);
		Product product = getProduct(productUuid);
		Preconditions.check(product == null, ExceptionConstants.PRODUCT_NOT_FOUND);
		PaymentInstrument paymentInstrument = getPaymentInstrument(customerUuid);
		log.info("Payment initiated for product " + product.getName() + " with uuid " + productUuid);
		SaleResponse saleResponse = vantivService.createSale(paymentInstrument, product);
		Preconditions.checkPaymentResponse(!saleResponse.getResponse().equals(successResponse), ExceptionConstants.PATMENT_ERROR);
		associateCustomerWithOrder(product, customer);
		return buildPaymentResponse(saleResponse);
	}

	@Override
	@Transactional
	public Customer getCustomer(String customerUuid) {
		Customer customer = customerRepository.findByUuid(customerUuid);
		Preconditions.check(customer == null, ExceptionConstants.CUSTOMER_NOT_FOUND);
		return customer;
	}

	@Override
	public Product getProduct(String productUuid) {
		Product product = productRepository.findByUuid(productUuid);
		Preconditions.check(product == null, ExceptionConstants.PRODUCT_NOT_FOUND);
		return product;
	}

	@Override
	@Transactional
	public void associateCustomerWithOrder(Product product, Customer customer) {
		List<PurchaseOrder> purchaseOrders = getOrdersByProductId(product);
		if (!purchaseOrders.isEmpty()) {
			purchaseOrders.forEach(order -> {
				Preconditions.check(order.getProduct().getId() == product.getId() &&
								order.getOrderStatus().equals(OrderStatus.ACTIVE) &&
								order.getCustomer().getId() == customer.getId(),
						ExceptionConstants.PRODUCT_ALREADY_PURCHASED);
			});
			createOrder(customer, product);
		} else {
			log.info("Creating first order");
			createOrder(customer, product);
		}
	}

	@Override
	@Transactional
	public PaymentInstrument getPaymentInstrument(String customerUuid) {
		return paymentInstrumentRepository.findByCustomerUuid(customerUuid).get(0);
	}

	@Override
	public PaymentResponseWS buildPaymentResponse(SaleResponse saleResponse) {
		Preconditions.check(saleResponse == null, ExceptionConstants.PATMENT_GATEWAY_ERROR);
		PaymentResponseWS paymentResponseWS = new PaymentResponseWS();
		paymentResponseWS.setStatus(HttpStatus.OK);
		paymentResponseWS.setMessage(saleResponse.getMessage());
		return paymentResponseWS;
	}

	@Override
	@Transactional
	public List<PurchaseOrder> getOrdersByProductId(Product product) {
		JPQLQuery query = new JPAQuery(entityManager);
		Predicate predicate = purchaseOrder.product.eq(product).
				and(EntityPredicates.isTimestampedFieldEnabledAndNotDeleted(purchaseOrder._super));
		return query.from(purchaseOrder).where(predicate).list(purchaseOrder);
	}

	@Override
	@Transactional
	public void createOrder(Customer customer, Product product) {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setOrderStatus(OrderStatus.ACTIVE);
		//order.setProductId(product.getId());
		purchaseOrder.setProduct(product);
		customer.getPurchaseOrders().add(purchaseOrder);
		purchaseOrder.setCustomer(customer);
		if (StringUtils.isEmpty(purchaseOrder.getUuid())) {
			purchaseOrder.setUuid(UUID.randomUUID().toString());
		}
		customerRepository.save(customer);
	}
}

