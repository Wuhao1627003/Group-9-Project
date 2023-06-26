package org;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Donation {

	private final String fundId;
	private final String contributorName;
	private final long amount;
	private final String date;

	public Donation(String fundId, String contributorName, long amount, String date) {
		// Check fundId value
		if (fundId.matches("\\w+")) {
			this.fundId = fundId;
		} else {
			throw new IllegalArgumentException("[Invalid fundId] word without spaces and special characters.");
		}

		// Check contributorName value
		if (contributorName.matches("[A-Za-z0-9\\s]*")) {
			this.contributorName = contributorName;
		} else {
			System.out.println(contributorName);
			throw new IllegalArgumentException("[Invalid contributorName] word without special characters.");
		}

		this.amount = amount;

		// Format date value
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
