package com.jamesha.benzinga;


import java.io.IOException;
import java.math.BigDecimal;

import yahoofinance.YahooFinance;

public class Ticker {
	
	private String code;
	private BigDecimal priceAtYesterdayClose = new BigDecimal("0");
	private BigDecimal priceAtPosting = new BigDecimal("0");
	private BigDecimal priceCurrent = new BigDecimal("0");
	private BigDecimal change = new BigDecimal("0");
	public Ticker(String code) {
		this.code = code;
	}
	
	public boolean isTickerStatusValid() {
		if (priceAtYesterdayClose.subtract(priceAtPosting).doubleValue() < 0) {
			return (priceAtYesterdayClose.subtract(priceCurrent).doubleValue() < 0);
		} else if (priceAtYesterdayClose.subtract(priceAtPosting).doubleValue() > 0) {
			return (priceAtYesterdayClose.subtract(priceCurrent).doubleValue() > 0);
		} else {
			return false;
		}
	}
	
	public boolean isTickerStatusValid2(boolean isMovingUp) throws IOException {
		if (isMovingUp) {
			return (YahooFinance.get(code).getQuote().getChangeInPercent().compareTo(BigDecimal.ZERO) >= 0);
		} else {
			return (YahooFinance.get(code).getQuote().getChangeInPercent().compareTo(BigDecimal.ZERO) <= 0);
		}
	}
	
	public String toString() {
		return code;
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public void setChange(BigDecimal change) {
		this.change = change;
	}
	
	public BigDecimal getChange() {
		return change;
	}

	public BigDecimal getPriceAtYesterdayClose() {
		return priceAtYesterdayClose;
	}

	public void setPriceAtYesterdayClose(BigDecimal priceAtYesterdayClose) {
		this.priceAtYesterdayClose = priceAtYesterdayClose;
	}

	public BigDecimal getPriceAtPosting() {
		return priceAtPosting;
	}

	public void setPriceAtPosting(BigDecimal priceAtPosting) {
		this.priceAtPosting = priceAtPosting;
	}

	public BigDecimal getPriceCurrent() {
		return priceCurrent;
	}

	public void setPriceCurrent(BigDecimal priceCurrent) {
		this.priceCurrent = priceCurrent;
	}	
}
