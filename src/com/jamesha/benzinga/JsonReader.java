package com.jamesha.benzinga;



//TODO: price at posting save and get data from elsewhere if error results
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class JsonReader {

	//TODO: Remove, not using REGEX to fetch tickers
	private static final Pattern TAG_REGEX = Pattern.compile("<name>([A-Z]+?)</name></item></stocks>", Pattern.DOTALL);
	private static final String WIIM_URL = "https://api.benzinga.com/api/v2/news?pageSize=100&displayOutput=headline&token=8d9813ef6a5a4b2097ab6e4789efa56f";
	private static final String WIIM_TOKEN = "8d9813ef6a5a4b2097ab6e4789efa56f";
	private static final String WIIM_URL_WITHOUT_TOKEN = "https://api.benzinga.com/api/v2/news?pageSize=100&displayOutput=headline&token=";
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;


	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	
	public static JSONObject readJsonFromJsonUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String text = readAll(rd);
			JSONObject json = new JSONObject(text);
			return json;
		} finally {
			is.close();
		}
	}

	
    public static JSONObject readJsonFromXmlUrl(String url) throws MalformedURLException, IOException, JSONException {
		InputStream is = new URL(url).openStream();
        try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String text = readAll(rd);
            JSONObject xmlJSONObj = XML.toJSONObject(text);
            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            //System.out.println(jsonPrettyPrintString);
            JSONObject json = new JSONObject(jsonPrettyPrintString);
            return json;
        } finally {
            is.close();
        }
    }
	
    
    @Deprecated
	private static List<String> getTickersFromUrl(final String url) throws MalformedURLException, IOException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String text = readAll(rd);
			final List<String> tagValues = new ArrayList<String>();
			final Matcher matcher = TAG_REGEX.matcher(text);
			while (matcher.find()) {
				tagValues.add(matcher.group(1));
			}
			//TODO: Remove duplicate tags
			//tagValues = tagValues.stream().distinct().collect(Collectors.toList());
			return tagValues;
		} finally {
			is.close();
		}
	}

	
	private static List<Article> createArticles(String url) throws MalformedURLException, IOException, JSONException {
		JSONObject json = readJsonFromXmlUrl(url);
///		System.out.println(json);
		//JSONObject json2 = readJsonFromJsonUrl("https://api.benzinga.io/data/rest/v2/chart?apikey=54b595f497164e0499409ca93342e394&symbol=FCX&from=1d&interval=1m");
		List<Article> articles = new ArrayList<Article>();
		for (int i = 0; i < json.getJSONObject("result").getJSONArray("item").length(); i++) {
			Article article = new Article();
			article.setCreated(DateHelper.createDateFromString(json.getJSONObject("result").getJSONArray("item").getJSONObject(i).get("created").toString()));
			article.setUpdated(DateHelper.createDateFromString(json.getJSONObject("result").getJSONArray("item").getJSONObject(i).get("updated").toString()));

			//article.setUpdated(DateHelper.createDateFromString(json.getJSONObject("result").getJSONArray("item").getJSONObject(i).get("created").toString()));

			//TODO: Assumes you only want to look at todays
			//TODO: Log the latest article time accessed, skip
			
			//TODO: Use timezone instead of system time

			if (article.getCreated().toLocalDate().isEqual(LocalDate.now())) {
				article.setTitle(json.getJSONObject("result").getJSONArray("item").getJSONObject(i).get("title").toString());
				//TODO TRADING HIGHER, CATCH OUTLIER
				
				//TODO: MOVING LOWER (just lower)
				if (article.getTitle().contains("trading lower") || 
				    article.getTitle().contains("shares are continuing lower") ||
					article.getTitle().contains("shares are moving lower")) {
					article.setIsMovingUp(false);
				}
				try {
					for (int j = 0; j < json.getJSONObject("result").getJSONArray("item").getJSONObject(i).getJSONObject("stocks").getJSONArray("item").length(); j++) {
						String tickerCode = json.getJSONObject("result").getJSONArray("item").getJSONObject(i).getJSONObject("stocks").getJSONArray("item").getJSONObject(j).get("name").toString();
						article.addTicker(tickerCode);
					} 
				} catch (JSONException e) {
					try {
						String tickerCode = json.getJSONObject("result").getJSONArray("item").getJSONObject(i).getJSONObject("stocks").getJSONObject("item").get("name").toString();
						article.addTicker(tickerCode);
					} catch (JSONException e2) {
						//No stocks are listed
					}
					
				}
				
				
				articles.add(article);
			}
		}

		return articles;
	}
	
	
	public static void main(String[] args) throws IOException, JSONException {
	   while (true) {
		try {
	        
	        	checkForInvalidArticles();
	            Thread.sleep(60000);
	        
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    } catch (Throwable t) {
	    	System.out.println("retrying");
	    }
	   }
    }


	private static void checkForInvalidArticles() throws MalformedURLException, IOException, JSONException {
		List<Article> articles = createArticles(WIIM_URL);
		//System.out.println("At time " + LocalDate.now().toString() + ", there are " + articles.size() + " articles.");
		List<Article> invalidArticles = new ArrayList<Article>();
		for (Article article : articles) {
			if (!article.isWiimValid()) {
				invalidArticles.add(article);
			}
		}
		
		System.out.println("Among " + articles.size() + " WIIM articles, " + invalidArticles.size() + " is/are invalid.");
		System.out.println("");
//		if (invalidArticles.size() > 0) {
//			System.out.println("WARNING!! WARNING!! WARNING!!");
//		}
//		System.out.println("");

		
		for (Article article2 : invalidArticles) {
			System.out.println(article2.toString());
		}
		

		
		if (!invalidArticles.isEmpty()) {
			//1. Create the frame.
			JFrame frame = new JFrame("Do not close");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			
		    StringBuilder sb = new StringBuilder();
		    sb.append("The following WIIM articles are no longer valid: ").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
		    for (Article article : invalidArticles) {
			    sb.append(article.getTitle()).append(System.getProperty("line.separator")); 
		    }
			JOptionPane.showMessageDialog(frame,
				    sb,
				    "Warning",
				    JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public static String checkForInvalidArticles3(String token) throws MalformedURLException, IOException, JSONException {
		List<Article> articles = createArticles(WIIM_URL_WITHOUT_TOKEN + token);
		//System.out.println("At time " + LocalDate.now().toString() + ", there are " + articles.size() + " articles.");
		List<Article> invalidArticles = new ArrayList<Article>();
		for (Article article : articles) {
			if (!article.isWiimValid()) {
				invalidArticles.add(article);
			}
		}
		
		System.out.println("Among " + articles.size() + " WIIM articles, " + invalidArticles.size() + " is/are invalid.");
		System.out.println("");
//		if (invalidArticles.size() > 0) {
//			System.out.println("WARNING!! WARNING!! WARNING!!");
//		}
//		System.out.println("");

		
		for (Article article2 : invalidArticles) {
			System.out.println(article2.toString());
		}
		
	    StringBuilder sb = new StringBuilder();

		
		if (!invalidArticles.isEmpty()) {
			
		    sb.append("The following WIIM articles are no longer valid: ").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
		    for (Article article : invalidArticles) {
			    sb.append(article.getTitle()).append(System.getProperty("line.separator")); 
		    }

		}
		return sb.toString();
	}
	
	public static String checkForInvalidArticles2() throws MalformedURLException, IOException, JSONException {
		List<Article> articles = createArticles(WIIM_URL);
		//System.out.println("At time " + LocalDate.now().toString() + ", there are " + articles.size() + " articles.");
		List<Article> invalidArticles = new ArrayList<Article>();
		for (Article article : articles) {
			if (!article.isWiimValid()) {
				invalidArticles.add(article);
			}
		}
		
		System.out.println("Among " + articles.size() + " WIIM articles, " + invalidArticles.size() + " is/are invalid.");
		System.out.println("");
//		if (invalidArticles.size() > 0) {
//			System.out.println("WARNING!! WARNING!! WARNING!!");
//		}
//		System.out.println("");

	    StringBuilder sb = new StringBuilder();

		for (Article article2 : invalidArticles) {
		    sb.append(article2.toString()).append(System.getProperty("line.separator")); 
		}
		
		return sb.toString();
		


//		
//		if (!invalidArticles.isEmpty()) {
//			//1. Create the frame.
//			JFrame frame = new JFrame("Do not close");
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.pack();
//			frame.setVisible(true);
//			
//		    StringBuilder sb = new StringBuilder();
//		    sb.append("The following WIIM articles are no longer valid: ").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
//		    for (Article article : invalidArticles) {
//			    sb.append(article.getTitle()).append(System.getProperty("line.separator")); 
//		    }
//			JOptionPane.showMessageDialog(frame,
//				    sb,
//				    "Warning",
//				    JOptionPane.WARNING_MESSAGE);
//		}
	}

}
