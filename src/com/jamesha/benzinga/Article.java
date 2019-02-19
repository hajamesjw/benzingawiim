package com.jamesha.benzinga;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import yahoofinance.*;

public class Article {
	
	private String title;
	private List<Ticker> tickers = new ArrayList<Ticker>();
	private LocalDateTime created;
	private LocalDateTime updated;
	private boolean isMovingUp = true;

	
	public boolean isWiimValid() throws IOException {
		for (Ticker ticker : tickers) {
			if (ticker.isTickerStatusValid2(isMovingUp) == false) {
				return false;
			}
		}
		return true;
	}
	
	//TODO: Create ticker
	public void addTicker(String tickerCode) throws IOException, JSONException {
		Ticker ticker = new Ticker(tickerCode);
		ticker.setChange(YahooFinance.get(tickerCode).getQuote().getChange());	
		// TODO: HOLIDAYS AND WEEKENDS, maket closes
		
		tickers.add(ticker);
		/*
		JSONObject json = JsonReader.readJsonFromJsonUrl("https://api.benzinga.io/data/rest/v2/chart?apikey=54b595f497164e0499409ca93342e394&symbol=" + ticker.getCode() +"&from=5d&interval=1m");
		JSONObject jsonToday = JsonReader.readJsonFromJsonUrl("https://api.benzinga.io/data/rest/v2/chart?apikey=54b595f497164e0499409ca93342e394&symbol=" + ticker.getCode() +"&from=1d&interval=1m");
		ticker.setPriceCurrent(new BigDecimal(jsonToday.getJSONArray("candles").getJSONObject(jsonToday.getJSONArray("candles").length()-1).get("close").toString()));
//		System.out.println(created.toLocalDate().minusDays(1).toString() + "T15:59:00.000-05:00");
//		System.out.println(json.getJSONArray("candles").getJSONObject(0).get("dateTime").toString());
		int indexOfYesterdayClose = -1;
		for (int i = 0; i < json.getJSONArray("candles").length()-1; i++) {

			// TODO: HOLIDAYS AND WEEKENDS, maket closes

			if (json.getJSONArray("candles").getJSONObject(i).get("dateTime").toString().equals(created.toLocalDate().minusDays(3).toString() + "T15:59:00.000-05:00") ||
				json.getJSONArray("candles").getJSONObject(i).get("dateTime").toString().equals(created.toLocalDate().minusDays(3).toString() + "T15:59:00.000Z")) {
				//TODO: Not efficient
				indexOfYesterdayClose = i;
				ticker.setPriceAtYesterdayClose(new BigDecimal(json.getJSONArray("candles").getJSONObject(i).get("close").toString()));
			}
		}
		for (int i = 0; i < json.getJSONArray("candles").length()-1; i++) {
			//TODO: Use Epoch second
			
//			System.out.println(created.toString());
//			System.out.println(created.toString().substring(11, 13));
//			System.out.println(created.toString().substring(14, 16));
			
			if (json.getJSONArray("candles").getJSONObject(i).get("dateTime").toString().substring(0, 16).equals(created.toString())) {
				//TODO: Not efficient
				ticker.setPriceAtPosting(new BigDecimal(json.getJSONArray("candles").getJSONObject(i).get("close").toString()));
			}
		}
		

		if (ticker.getPriceAtPosting().equals(BigDecimal.ZERO)) {
			ticker.setPriceAtPosting(new BigDecimal(jsonToday.getJSONArray("candles").getJSONObject(0).get("open").toString()));

			
		}
		
		//ticker.setPriceAtYesterdayClose(priceAtYesterdayClose);
		//System.out.println("Current price: " + currentPrice.toString());
		//final is the current
		//time of posting - get thru created, get the price
		//time of yesterday close, see if i can get a hard rule on this one
		 *
		 */
	}
	
	public String getTitle() {
		return title;
	}

	public LocalDateTime getCreated() {
		return created;
	}
	
	public LocalDateTime getUpdated() {
		return updated;
	}

	public List<Ticker> getTickers() {
		return tickers;
	}
	
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	
	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setIsMovingUp(boolean isMovingUp) {
		this.isMovingUp = isMovingUp;
	}
	
	
    public String toString() {
	   
	   StringBuilder tickersString = new StringBuilder();
	   for (Ticker ticker : tickers) {
		   tickersString.append(ticker.getCode() + " Change: " + ticker.getChange() + " ");
	   }

	    StringBuilder sb = new StringBuilder();
	    sb.append("Title: " + title).append(System.getProperty("line.separator")); 
	    sb.append("Tickers: " + tickersString).append(System.getProperty("line.separator")); 
	    sb.append("Created: " + created.toString()).append(System.getProperty("line.separator")); 
	    sb.append("Updated: " + updated.toString()).append(System.getProperty("line.separator")).append(System.getProperty("line.separator")); 

	    return sb.toString();

   }
}
