package org;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class Donation {
	
	private String fundId;
	private String contributorName;
	private long amount;
	private String date;
	
	public Donation(String fundId, String contributorName, long amount, String date) {
		this.fundId = fundId;
		this.contributorName = contributorName;
		this.amount = amount;
		LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		this.date = dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
	}

	public String getFundId() {
		return fundId;
	}

	public String getContributorName() {
		return contributorName;
	}

	public long getAmount() {
		return amount;
	}

	public String getDate() {
		return date;
	}
	
	

}
