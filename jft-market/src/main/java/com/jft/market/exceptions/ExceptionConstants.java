package com.jft.market.exceptions;


public class ExceptionConstants {

	public static final String USER_NOT_FOUND = "User not found";
	public static final String USER_NOT_ENABLED = "User is not enabled or is deleted.";

	public static final String CUSTOMER_NOT_FOUND = "Customer not found";
	public static final String CUSTOMER_ALREADY_EXISTS = "Customer already exists";
	public static final String CUSTOMER_NOT_ENABLED = "Customer is not enabled or is deleted.";


	public static final String PRODUCT_NOT_FOUND = "Product not found";
	public static final String PRODUCT_ALREADY_PURCHASED = "Product not found";
	public static final String PRODUCT_NOT_FOUND_TO_DELETE = "Product not found. Please provide valid product Id to delete.";

	public static final String PAYMENT_INSTRUMENT_NOT_FOUND = "Paymnet Instrument not found";

	public static final String PATMENT_ERROR = "Payment not succeeded. Please try after sometime";
	public static final String PATMENT_GATEWAY_ERROR = "No response from payment gateway";

	public static final String INVALID_REQUEST = "Invalid request";

	public static final String CUSTOMER_NAME_CANNOT_BE_EMPTY = "Customer name cannot be empty";
	public static final String USER_NAME_CANNOT_BE_EMPTY = "User name cannot be empty";
	public static final String PHONE_NUMBER_CANNOT_BE_EMPTY = "Phone number cannot be empty";
	public static final String EMAIL_CANNOT_BE_EMPTY = "Email cannot be empty";


}
