package com.abc;

import static java.lang.Math.abs;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import consts.Constants;

public class Account {

    private final long id;
    private final int accountType;
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private final Date creationDate = new Date();
    private double interestEarned = 0;
    private final Locale locale;		// Used for the currency
    private static Integer counter = 0;	        // Used to assign a unique ID to accounts
    
    public Account(int accountType, Locale locale) throws IllegalArgumentException {
    	// This make sure that only valid objects are created
    	if (accountType != Constants.ACCOUNT_CHECKING_ID &&
    			accountType != Constants.ACCOUNT_SAVINGS_ID && 
    			accountType != Constants.ACCOUNT_MAXI_SAVINGS_ID) {
    		throw new IllegalArgumentException("accountType Not Allowed!");
    	} else {
    		this.accountType = accountType;
    	}
    	this.locale = locale;
        this.id = ++counter;
    }
    
    public long getId() {
	return id;
    }
    public int getAccountType() {
        return accountType;
    }
    public List<Transaction> getTransactions() {
	return transactions;
    }
    public double getInterestEarned() {
    	return interestEarned;
    }
    
    // returns Map.Entry containing the Account's Earned Interest and the Currency
    public Entry<String, Double> getInterestAndCurrency() {	
    	return new AbstractMap.SimpleEntry<String, Double>(
    			getCurrencySymbol(), interestEarned);
    }
    public String getCurrencySymbol(){
    	return Currency.getInstance(locale).getSymbol(locale);
    }
	
    // Utility Methods
    
    public double getTotalAmount() {
    	return transactions.stream()
    			   .map(t -> t.getAmount())
    			   .reduce(0.0, (sum, a) -> sum + a);
    }
    
    public void deposit(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException(AMOUNT_ERROR_MEX);
        } else {
            transactions.add(new Transaction(amount, Constants.DEPOSIT));
        }
    }
    
    public void withdraw(double amount) throws IllegalArgumentException, UnsupportedOperationException  {
        // account amount can't be less than 0.
	if (amount > getTotalAmount()) {
	    throw new UnsupportedOperationException(
			"The withdrawal amount is greater than total account amount.");
	}
        if (amount <= 0) {
	    throw new IllegalArgumentException(AMOUNT_ERROR_MEX);
        } else {
	    transactions.add(new Transaction(-amount, Constants.WITHDRAWAL));
        }
    }
	
    // Accrues one-day interest rates. Ideally it should be called once a day.
    public void accrueInterests() {
        double amount = getTotalAmount();
        switch(accountType){
	        case Constants.ACCOUNT_CHECKING_ID:
	        	interestEarned += calculateDailyInterest(amount, Constants.ZERO_ONE_PERCENT);
	        	break;
	        case Constants.ACCOUNT_SAVINGS_ID:
	            if (amount <= 1000)
	            	interestEarned += calculateDailyInterest(amount, Constants.ZERO_ONE_PERCENT);
	            else
	            	interestEarned += ( ((double) 1/365) + calculateDailyInterest((amount-1000), Constants.ZERO_TWO_PERCENT) );
	            break;
	        case Constants.ACCOUNT_MAXI_SAVINGS_ID:
	        	Transaction lastWithdrawal = getLastWithdrawal();
	        	if( lastWithdrawal==null || lastWithdrawal.isOlder10Days() ) {
	        		interestEarned += calculateDailyInterest(amount, Constants.FIVE_PERCENT);
	        	} else {
	        		interestEarned += calculateDailyInterest(amount, Constants.ZERO_ONE_PERCENT);
	        	}
	        	break;
	    }
    }
    
    public double calculateDailyInterest(double amount, double percentage) {
    	return (amount * percentage) / 365;
    }
    
    public Transaction getLastWithdrawal() {
    	return transactions.stream()
    			   .filter(t -> t.getTransactionType().equals(Constants.WITHDRAWAL))
    			   .reduce((x, last) -> last).orElse(null);
    }
    
    // Account Statement with all the transactions
    public String getStatement() {
    	return transactions.stream()
    			   .map(t -> t.getTransactionType() + " " + formatAmount(t.getAmount()))
    			   .collect(Collectors.joining(
    					"\n\t",
    					"\n" + getAccountTypeName() + "\n\t",
    					"\nTotal: " + formatAmount(getTotalAmount()) + "\n"));
    }
    
    public String getAccountInformation() {
    	String s = "\n" + getAccountTypeName() + "\n";
        s += "ID: " + id + "\n";
        s += "Currency: " + getCurrencySymbol() + "\n";
        s += "Open on: " + creationDate + "\n";
        s += "Total: " + formatAmount(getTotalAmount());
        return s;
    }
    
    // Formats amount including the related currency
    private String formatAmount(double amount) {
    	String formattedStr = getCurrencySymbol();
    	formattedStr += String.format("%,.2f", abs(amount));
    	return formattedStr;
    }
    
    public String getAccountTypeName() {
        switch(accountType){
            case Constants.ACCOUNT_CHECKING_ID:
                return Constants.ACCOUNT_CHECKING_NAME;
            case Constants.ACCOUNT_SAVINGS_ID:
            	return Constants.ACCOUNT_SAVINGS_NAME;
            case Constants.ACCOUNT_MAXI_SAVINGS_ID:
            	return Constants.ACCOUNT_MAXI_SAVINGS_NAME;
            default:
            	return "Account type not recognized";
        }
    }
    
    private static final String AMOUNT_ERROR_MEX = "Amount must be greater than zero.";
    
}
