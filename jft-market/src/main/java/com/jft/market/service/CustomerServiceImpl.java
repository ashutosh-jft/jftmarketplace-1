package com.jft.market.service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jft.market.api.ws.CustomerWS;
import com.jft.market.api.ws.RoleWS;
import com.jft.market.api.ws.Roles;
import com.jft.market.api.ws.UserWS;
import com.jft.market.model.Customer;
import com.jft.market.model.User;
import com.jft.market.repository.CustomerRepository;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void convertWStoEntityAndSave(CustomerWS customerWS) {
		Customer customer = convertWStoEntity(customerWS);
		createCustomer(customer);
	}

	@Override
	public Customer convertWStoEntity(CustomerWS customerWS) {
		if (customerWS != null) {
			Customer customer = new Customer();
			customer.setName(customerWS.getCustomerName());
			customer.setPhone(customerWS.getPhone());
			customer.setPassword(passwordEncoder.encode(customerWS.getPassword()));
			customer.setEnabled(Boolean.TRUE);
			customer.setEmail(customerWS.getEmail());
			customer.setGender(customerWS.getGender());
			UserWS userWS = createUserWSFromCustomerWS(customerWS);
			User user = userService.convertWStoEntity(userWS);
			customer.setUser(user);
			user.setCustomer(customer);
			return customer;
		}
		return null;
	}

	@Override
	public void createCustomer(Customer customer) {
		if (StringUtils.isEmpty(customer.getUuid())) {
			customer.setUuid(UUID.randomUUID().toString());
		}
		customerRepository.save(customer);
	}

	@Override
	public UserWS createUserWSFromCustomerWS(CustomerWS customerWS) {
		if (customerWS != null) {
			UserWS userWS = new UserWS();
			userWS.setUsername(customerWS.getCustomerName());
			userWS.setUuid(customerWS.getUuid());
			userWS.setPhone(customerWS.getPhone());
			userWS.setPassword(customerWS.getPassword());
			userWS.setEnabled(Boolean.TRUE);
			userWS.setEmail(customerWS.getEmail());
			userWS.setGender(customerWS.getGender());
			userWS.setUuid(UUID.randomUUID().toString());
			RoleWS roleWS = new RoleWS();
			roleWS.setName(Roles.ROLE_USER.getName());
			userWS.getRoles().add(roleWS);
			return userWS;
		}
		return null;
	}

	@Override
	@Transactional
	public Customer readCustomerByEmailId(CustomerWS customerWS) {
		Customer customer = customerRepository.findByEmail(customerWS.getEmail());
		return customer;
	}

	@Override
	@Transactional
	public Customer readCustomerByUuid(String customerUuid) {
		Customer customer = customerRepository.findByUuid(customerUuid);
		return customer;
	}

	@Override
	public CustomerWS convertEntityToWS(Customer customer) {
		if (customer != null) {
			CustomerWS customerWS = new CustomerWS();
			customerWS.setCustomerName(customer.getName());
			customerWS.setEmail(customer.getEmail());
			customerWS.setUuid(customer.getUuid());
			customerWS.setPhone(customer.getPhone());
			customerWS.setGender(customer.getGender());
			customerWS.setEnabled(customer.getEnabled());
			return customerWS;
		}
		return null;
	}

	@Override
	@Transactional
	public List<CustomerWS> getAllCustomers() {
		List<Customer> customers = customerRepository.findAll();
		return convertEntityListToWSList(customers);

	}

	@Override
	public List<CustomerWS> convertEntityListToWSList(List<Customer> customers) {

		List<CustomerWS> customerWSList = new ArrayList<CustomerWS>();
		if (!customers.isEmpty()) {
			customers.forEach(customer -> {
				CustomerWS customerWS = new CustomerWS();
				customerWS.setCustomerName(customer.getName());
				customerWS.setPassword(customer.getPassword());
				customerWS.setEnabled(customer.getEnabled());
				customerWS.setGender(customer.getGender());
				customerWS.setUuid(customer.getUuid());
				customerWS.setEmail(customer.getEmail());
				customerWS.setPhone(customer.getPhone());

				customerWSList.add(customerWS);
			});
			return customerWSList;
		}
		return null;
	}

	@Override
	public void deleteCustomer(Customer customer) {
		customerRepository.delete(customer);
	}

}
