package org;

import java.util.*;

public class Fund {

	private final String id;
	private final String name;
	private final String description;
	private final long target;
	private Map<String, Long[]> aggregateDonations;
	private List<Donation> donations;

	public Fund(String id, String name, String description, long target) {
		// Check id value
		if (id.matches("\\w+")) {
			this.id = id;
		} else {
			throw new IllegalArgumentException("[Invalid id] word without spaces and special characters.");
		}

		// Check name value
		if (name.matches("[A-Za-z0-9\\s]*")) {
			this.name = name;
		} else {
			throw new IllegalArgumentException("[Invalid name] word without special characters.");
		}

		this.description = description;

		this.target = target;

		donations = new LinkedList<>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public long getTarget() {
		return target;
	}

	public List<Donation> getDonations() {
		return donations;
	}

	public void setDonations(List<Donation> donations) {
		this.donations = donations;
	}

	public void calAggregateDonations() {
		aggregateDonations = new HashMap<>();
		for (Donation donation : donations) {
			String contributorName = donation.getContributorName();
			if (!aggregateDonations.containsKey(contributorName)) {
				aggregateDonations.put(contributorName, new Long[]{Long.valueOf(0), Long.valueOf(0)});
			}
			//for each key:first long represents the donation count
			aggregateDonations.get(contributorName)[0]++;
			//the second long represents the donation amount;
			aggregateDonations.get(contributorName)[1] += donation.getAmount();
		}
	}

	public List<Map.Entry<String, Long[]>> getAggregateDonations() {
		List<Map.Entry<String, Long[]>> entryList =
				new ArrayList<Map.Entry<String, Long[]>>(aggregateDonations.entrySet());

		// Sort the list based on values in descending order
		Collections.sort(entryList, new Comparator<Map.Entry<String, Long[]>>() {
			@Override
			public int compare(Map.Entry<String, Long[]> o1, Map.Entry<String, Long[]> o2) {
				// Compare values in descending order
				return o2.getValue()[1].compareTo(o1.getValue()[1]);
			}
		});
		return entryList;
	}
}
