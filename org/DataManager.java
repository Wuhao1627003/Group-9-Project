
//package org;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataManager {

	private final WebClient client;

	public DataManager(WebClient client) {
		this.client = client;
	}

	/**
	 * Attempt to log the user into an Organization account using the login and
	 * password.
	 * This method uses the /findOrgByLoginAndPassword endpoint in the API
	 * 
	 * @return an Organization object if successful; null if unsuccessful
	 */
	public Organization attemptLogin(String login, String password) {
		// login ID or password is null
		if (login == null || password == null) {
			throw new IllegalArgumentException("[Invalid Input] Login ID or password cannot be empty.");
		}

		try {
			Map<String, Object> map = new HashMap<>();

			// Check login ID value
			if (login.matches("\\w+")) {
				map.put("login", login);
			} else {
				throw new IllegalArgumentException("[Invalid login ID] word without spaces and special characters.");
			}

			// Check password value
			if (password.matches("\\w+")) {
				map.put("password", password);
			} else {
				throw new IllegalArgumentException("[Invalid password] word without spaces and special characters.");
			}

			String response = client.makeRequest("/findOrgByLoginAndPassword", map);
			if (response == null) {
				throw new IllegalStateException("[Error in communicating with server] login fails.");
			}

			JSONParser parser = new JSONParser();
			JSONObject json = null;
			try {
				json = (JSONObject) parser.parse(response);
			} catch (Exception e) {

			}

			String status = (String) json.get("status");

			if (status.equals("success")) {
				JSONObject data = (JSONObject) json.get("data");
				String fundId = (String) data.get("_id");
				String name = (String) data.get("name");
				String description = (String) data.get("description"); // Correct typo
				Organization org = new Organization(fundId, name, description);

				JSONArray funds = (JSONArray) data.get("funds");
				Iterator it = funds.iterator();
				while (it.hasNext()) {
					JSONObject fund = (JSONObject) it.next();
					fundId = (String) fund.get("_id");
					name = (String) fund.get("name");
					description = (String) fund.get("description");
					long target = (Long) fund.get("target");

					Fund newFund = new Fund(fundId, name, description, target);

					JSONArray donations = (JSONArray) fund.get("donations");
					List<Donation> donationList = new LinkedList<>();
					Iterator it2 = donations.iterator();
					while (it2.hasNext()) {
						JSONObject donation = (JSONObject) it2.next();
						String contributorId = (String) donation.get("contributor");
						String contributorName = this.getContributorName(contributorId);
						long amount = (Long) donation.get("amount");
						String date = (String) donation.get("date");
						donationList.add(new Donation(fundId, contributorName, amount, date));
					}

					newFund.setDonations(donationList);

					org.addFund(newFund);

				}

				return org;
			} else {
				throw new IllegalStateException("Error when getting organization info.");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Look up the name of the contributor with the specified ID.
	 * This method uses the /findContributorNameById endpoint in the API.
	 * 
	 * @return the name of the contributor on success; null if no contributor is
	 *         found
	 */
	public String getContributorName(String id) {
		// ID is null
		if (id == null) {
			throw new IllegalArgumentException("[Invalid Input] Contributor ID cannot be empty.");
		}

		try {
			Map<String, Object> map = new HashMap<>();
			Map<String, String> cache = new HashMap<>();

			if (cache.containsKey(id)) {
				return cache.get(id);
			}

			// Check contributor ID value
			if (!id.matches("\\s+")) {
				map.put("id", id);
			} else {
				throw new IllegalArgumentException("Invalid contributor ID: contributor ID should not have spaces.");
			}

			String response = client.makeRequest("/findContributorNameById", map);
			if (response == null) {
				throw new IllegalStateException("[Error in communicating with server] fail to find contributor.");
			}

			JSONParser parser = new JSONParser();
			JSONObject json = null;
			try {
				json = (JSONObject) parser.parse(response);
			} catch (Exception e) {

			}
			String status = (String) json.get("status");

			if (status.equals("success")) {
				String name = (String) json.get("data");
				cache.put(id, name);
				return name;
			} else {
				throw new IllegalStateException("Error when getting contributor name.");
			}

		} catch (Exception e) {
			// throw new IllegalStateException("Error in communicating with server.");
			throw e;
		}
	}

	/**
	 * This method creates a new fund in the database using the /createFund endpoint
	 * in the API
	 * 
	 * @return a new Fund object if successful; null if unsuccessful
	 */
	public Fund createFund(String orgId, String name, String description, long target) {
		// orgId, name, or description is null
		if (orgId == null | name == null | description == null) {
			throw new IllegalArgumentException("[Invalid Input] orgId, name, or description cannot be empty.");
		}

		try {

			Map<String, Object> map = new HashMap<>();

			// Check organization ID value
			if (orgId.matches("\\w+")) {
				map.put("orgId", orgId);
			} else {
				throw new IllegalArgumentException(
						"[Invalid contributor ID] word without spaces and special characters.");
			}

			// Check name value
			if (name.matches("[A-Za-z0-9\\s]*")) {
				map.put("name", name);
			} else {
				throw new IllegalArgumentException("[Invalid fund name] word without special characters.");
			}

			map.put("description", description);
			map.put("target", target);

			String response = client.makeRequest("/createFund", map);
			if (response == null) {
				throw new IllegalStateException("[Error in communicating with server] fail to create fund.");
			}

			JSONParser parser = new JSONParser();
			JSONObject json = null;
			try {
				json = (JSONObject) parser.parse(response);
			} catch (Exception e) {

			}
			String status = (String) json.get("status");

			if (status.equals("success")) {
				JSONObject fund = (JSONObject) json.get("data");
				String fundId = (String) fund.get("_id");
				return new Fund(fundId, name, description, target);
			} else {
				throw new IllegalStateException("Web client return error when creating new fund.");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}
	}
}
