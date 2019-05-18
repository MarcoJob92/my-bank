package com.abc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import consts.Constants;

// Developed by Marco Furone
public class Bank {
    private String name;
    private List<Customer> customers = new ArrayList<Customer>();
    
    public Bank(String name) {
    	this.name = name;
    }
    public Bank(String name, List<Customer> customers) {
    	this.name = name;
    	this.customers = customers;
    }
    
    public String getName() {
    	return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public String getBankSummary() {
        String summary = "Bank Summary";
        summary += customers.stream()
        		.map(Customer::getCustomerSummary)
        		.sorted()
        		.collect(Collectors.joining(Constants.NEW_LINE_DASH, Constants.NEW_LINE_DASH, ""));
        return summary;
    }

    // Calculates Total Interest Paid for each currency, returns a String to be printed
    public String getTotalInterestPaid() {
    	List<Account> allAccounts = customers.stream()
    			.flatMap(customer -> customer.getAccounts().stream())
    			.collect(Collectors.toList());
    	
        HashMap<String, Double> interests = Customer.totalInterestEarned(allAccounts);
        
        return interests.entrySet().stream()
        		 .map(e -> e.getKey() + e.getValue())
        		 .collect(Collectors.joining(Constants.NEW_LINE_DASH, "\nTotal Interest Paid:\n", ""));
    }
    
    public Customer getCustomerByFullName(String fullName) throws NoSuchElementException {
    	return customers.stream()
    			.filter(c -> c.getFullName().equals(fullName))
    			.findFirst()
    			.get();
    }
    
}
