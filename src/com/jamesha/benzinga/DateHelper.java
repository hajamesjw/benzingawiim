package com.jamesha.benzinga;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;

public class DateHelper {
	public static LocalDateTime createDateFromString(String dateString) {
		int dayOfMonth = Integer.parseInt(dateString.substring(5, 7));
		int month = getIntMonthFromStringMonth(dateString.substring(8, 11));
		int year = Integer.parseInt(dateString.substring(12, 16));
		LocalDate date = LocalDate.of(year, month, dayOfMonth);
		
		
		int hour = Integer.parseInt(dateString.substring(17, 19));
		int minute = Integer.parseInt(dateString.substring(20, 22));
		LocalTime time = LocalTime.of(hour, minute);
		
		LocalDateTime dateTime = LocalDateTime.of(date, time);
		return dateTime;
	}
	
	
	public static LocalDateTime createDateFromEpochMilli(long epochMilli) {
		//return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.of(zoneId)).toLocalDate();
		return null;
	}
	
	
	private static int getIntMonthFromStringMonth(String monthString) {
		if (monthString.equals("Jan")) {
			return 1;
		} else if (monthString.equals("Feb")) {
			return 2;
		} else if (monthString.equals("Mar")) {
			return 3;
		} else if (monthString.equals("Apr")) {
			return 4;
		} else if (monthString.equals("May")) {
			return 5;
		} else if (monthString.equals("Jun")) {
			return 6;
		} else if (monthString.equals("Jul")) {
			return 7;
		} else if (monthString.equals("Aug")) {
			return 8;
		} else if (monthString.equals("Sep")) {
			return 9;
		} else if (monthString.equals("Oct")) {
			return 10;
		} else if (monthString.equals("Nov")) {
			return 11;
		} else if (monthString.equals("Dec")) {
			return 12;
		} else {
			//System.out.println(monthString);
			return -1;
		}

		
	}
}
