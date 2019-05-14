package com.abc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Customer {
	
    private String fullName;
    private List<Account> accounts = new ArrayList<Account>();

    // Constructor to create a Customer and open MULTIPLE Accounts
    public Customer(String fullName, List<Account> accounts) {
        this.fullName = fullName;
        for (Account a : accounts) {
        	openAccount(a);
        }
    }
    // Constructor to create a Customer and open ONE Account
    public Customer(String fullName, Account account) {
        this.fullName = fullName;
        openAccount(account);
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
    	this.fullName = fullName;
    }
    
    public List<Account> getAccounts() {
        return accounts;
    }
    
    // Utility Methods
    
    public void openAccount(Account account) {
        accounts.add(account);
    }

    public int getNumberOfAccounts() {
        return accounts.size();
    }
    
    public String getCustomerSummary() {
    	int number = getNumberOfAccounts();
    	String summary = getFullName() + ": " + number;
    	summary += (number == 1 ? " account" : " accounts");
        return summary;
    }

    // returns Interests earned by the Customer, splitted by the currency
    public HashMap<String, Double> totalInterestEarned() {
    	return totalInterestEarned(accounts);
    }
    static public HashMap<String, Double> totalInterestEarned(List<Account> accounts) {
    	HashMap<String, Double> interests = new HashMap<String, Double>();
    	accounts.stream()
    		.map(a -> a.getInterestAndCurrency())
    		.forEach(e -> {
    			Double amountValue = interests.getOrDefault(e.getKey(), 0.0);
    			interests.put( e.getKey(), amountValue + e.getValue() );
    		}
    	);
        return interests;
    }
    
    // Statement that shows transactions and totals for each of the Customer's accounts
    public String getStatementForAllAccounts() {
    	return fullName + "'s statement \n" +
    	accounts.stream()
    		.map(Account::getStatement)
    		.collect(Collectors.joining());
    }
    
    public String getInformationOnCustomerAccounts() {
    	return accounts.stream()
    		       .map(Account::getAccountInformation)
    		       .collect(Collectors.joining());
    }
    
    // Transfers amount between two Customer's accounts, using Account IDs
    public void transferBetweenTwoAccounts(double amount, int senderId, int destinationId) 
    		throws IllegalArgumentException, UnsupportedOperationException {
    	Account sender = getAccountById(senderId);
    	Account destination = getAccountById(destinationId);
    	transferBetweenTwoAccounts(amount, sender, destination);
    } 
    // Transfers amount between two Customer's accounts, using Account Objects
    public void transferBetweenTwoAccounts(double amount, Account sender, Account dest)
    		throws IllegalArgumentException, UnsupportedOperationException {
    	if (sender.equals(dest)) {
    		throw new UnsupportedOperationException(
					"Sender Account must be different than Destination Account.");
    	}
    	try {
    		sender.withdraw(amount);
    		dest.deposit(amount);
    	} catch(NullPointerException e) {
    		e.printStackTrace();
    	}
    }
    
    public Account getAccountById(int id) throws NoSuchElementException {
    	return accounts.stream()
    			.filter(a -> a.getId() == id)
    			.findFirst().get();
    }
    
}
