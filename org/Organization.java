package org;
import java.util.LinkedList;
import java.util.List;

public class Organization {
	
	private String id;
	private String name;
	private String description;
	
	private List<Fund> funds;
	
	public Organization(String id, String name, String description) {
		// Check id value
		if(id.matches("\\w+")) {
			this.id = id;
		} else {
			throw new IllegalArgumentException("[Invalid id] word without spaces and special characters.");
		}

		// Check name value
		if(name.matches("[A-Za-z0-9\\s]*")) {
			this.name = name;
		} else {
			throw new IllegalArgumentException("[Invalid name] word without special characters.");
		}

		this.description = description;

		funds = new LinkedList<>();
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

	public List<Fund> getFunds() {
		return funds;
	}
	
	public void addFund(Fund fund) {
		funds.add(fund);
	}

}
