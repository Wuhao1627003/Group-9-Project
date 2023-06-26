//package org;

import java.util.*;

import org.Donation;
import org.Fund;
import org.Organization;
import org.WebClient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

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
					newFund.calAggregateDonations();

					org.addFund(newFund);

				}

				return org;
			} else {
				throw new IllegalStateException("Error when getting organization info.");
			}
		} catch (NullPointerException e) {
			// e.printStackTrace();
			throw new IllegalStateException();
		} catch (IllegalStateException | IllegalArgumentException e) {
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
				throw new IllegalStateException("Contributor doesn't exist.");
			}

		} catch (NullPointerException e) {
			// e.printStackTrace();
			throw new IllegalStateException();
		} catch (IllegalStateException | IllegalArgumentException e) {
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

			if (target <= 0) {
				throw new IllegalArgumentException("[Invalid target] target should be positive.");
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
				throw new IllegalStateException("Error when creating new fund.");
			}
		} catch (NullPointerException e) {
			// e.printStackTrace();
			throw new IllegalStateException();
		} catch (IllegalStateException | IllegalArgumentException e) {
			throw e;
		}
	}

	/**
	 * This method makes a donation on behalf of a contributor in the database using
	 * the /makeDonation endpoint in the API
	 *
	 * @return list of donations for the fund after making current donation
	 */
	public List<Donation> makeDonation(String contributorId, String fundId, String amountStr) {
		if (contributorId == null || contributorId.isBlank() ||
				fundId == null || fundId.isBlank() ||
				amountStr == null || amountStr.isBlank()) {
			throw new IllegalArgumentException("[Invalid Input] contributorId, fundId, or amount cannot be empty.");
		}
		long amount = 0;
		try {
			amount = Long.parseLong(amountStr);
		} catch (Exception e) {
			throw new IllegalArgumentException("[Invalid amount] amount cannot be parsed: " + e.getMessage());
		}
		if (amount < 0) {
			throw new IllegalArgumentException("[Invalid amount] amount cannot be negative.");
		}
		this.getContributorName(contributorId);

		Map<String, Object> map = new HashMap<>();
		map.put("contributor", contributorId);
		map.put("fund", fundId);
		map.put("amount", amountStr);
		String response = client.makeRequest("/makeDonation", map);

		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(response);
		} catch (Exception e) {
			throw new IllegalStateException("Error when parsing response from makeDonation: " + response);
		}

		String status = (String) json.get("status");
		if (status == null || !status.equals("success")) {
			throw new IllegalStateException("Error when making donation: " + json);
		} else {
			map = new HashMap<>();
			map.put("id", fundId);
			response = client.makeRequest("/findFundById", map);

			try {
				json = (JSONObject) parser.parse(response);
			} catch (Exception e) {
				throw new IllegalStateException("Error when parsing response from findFundById: " + response);
			}

			status = (String) json.get("status");
			if (status != null && status.equals("success")) {
				JSONObject fund = (JSONObject) json.get("data");
				JSONArray donations = (JSONArray) fund.get("donations");
				List<Donation> donationList = new LinkedList<>();

				for (Object o : donations) {
					JSONObject donation = (JSONObject) o;
					contributorId = (String) donation.get("contributor");
					String contributorName = this.getContributorName(contributorId);
					amount = (Long) donation.get("amount");
					String date = (String) donation.get("date");
					donationList.add(new Donation(fundId, contributorName, amount, date));
				}

				return donationList;
			} else {
				throw new IllegalStateException("Error when parsing fund donations: " + json);
			}
		}
	}

	/**
	 * This method updates the org name in the database using the
	 * /updateOrgName endpoint in the API
	 *
	 * @return true if update is successful, false otherwise
	 */
	public boolean updateOrgName(String id, String name) {
		if (id == null) {
			throw new IllegalArgumentException("[Invalid Input] orgID cannot be empty.");
		}

		if (!id.matches("\\w+")) {
			throw new IllegalArgumentException("[Invalid organization ID] word without spaces and special characters.");
		}

		Map<String, Object> map = new HashMap<>();

		map.put("id", id);

		if (name != null) {
			map.put("name", name);
		}

		String response = client.makeRequest("/updateOrgName", map);
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(response);
		} catch (Exception e) {
			throw new IllegalStateException("[Error in communicating with server] fail to update password.");
		}


		String status = (String) json.get("status");
		if (status.equals("success")) {
			return true;
		} else{
			return false;
		}

	}

	/**
	 * This method updates the org description in the database using the
	 * /updateOrgDescription endpoint in the API
	 *
	 * @return true if update is successful, false otherwise
	 */
	public boolean updateOrgDescription(String id, String description) {
		if (id == null) {
			throw new IllegalArgumentException("[Invalid Input] orgID cannot be empty.");
		}

		if (!id.matches("\\w+")) {
			throw new IllegalArgumentException("[Invalid organization ID] word without spaces and special characters.");
		}

		Map<String, Object> map = new HashMap<>();

		map.put("id", id);

		if (description != null) {
			map.put("description", description);
		}

		String response = client.makeRequest("/updateOrgDescription", map);
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(response);
		} catch (Exception e) {
			throw new IllegalStateException("[Error in communicating with server] fail to update password.");
		}


		String status = (String) json.get("status");
		if (status.equals("success")) {
			return true;
		} else{
			return false;
		}

	}

    /**
     * This method updates a new password in the database using the /updatePassword endpoint
     * in the API
     *
     * @return true if update is successful, false otherwise
     */
    public boolean updatePassword(String id, String password) {
        if (id == null || password == null) {
            throw new IllegalArgumentException("[Invalid Input] orgID or password cannot be empty" +
                    ".");
        }

		if (!id.matches("\\w+")) {
			throw new IllegalArgumentException(
					"[Invalid organization ID] word without spaces and special characters.");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("password", password);

		String response = client.makeRequest("/updatePassword", map);
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(response);
		} catch (Exception e) {
			throw new IllegalStateException("[Error in communicating with server] fail to update " +
					"password");
		}

		String status = (String) json.get("status");
		return status.equals("success");
	}

	/**
	 * This method checks whether an Organization's login name already exits using
	 * the /findOrgByName endpoint in the API
	 *
	 * @return true if already exits, false otherwise
	 */
	public boolean checkUniqueLoginName(String login) {
		if (login == null) {
			throw new IllegalArgumentException("[Invalid Input] login name cannot be empty" +
					".");
		}
		Map<String, Object> map = new HashMap<>();

		if (login.matches("\\w+")) {
			map.put("login", login);
		} else {
			throw new IllegalArgumentException("[Invalid login ID] word without spaces and special characters.");
		}

		String response = client.makeRequest("/findOrgByName", map);
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(response);
		} catch (Exception e) {
			throw new IllegalStateException("[Error in communicating with server] fail to find " +
					"Organization by name");
		}

		String status = (String) json.get("status");
		JSONObject data = (JSONObject) json.get("data");
		if (status.equals("success") && data != null) {
			return true;
		}
		return false;
	}

	/**
	 * This method creates a new organization in the database using the /createOrg
	 * endpoint
	 * in the API
	 *
	 * @return true if successful; false if unsuccessful
	 */
	public boolean createOrg(String login, String password, String name, String description) {
		if (login == null || password == null || name == null || description == null) {
			throw new IllegalArgumentException("[Invalid Input] login, password, name and " +
					"description" +
					" cannot be empty" +
					".");
		}

		Map<String, Object> map = new HashMap<>();

		if (login.matches("\\w+")) {
			map.put("login", login);
		} else {
			throw new IllegalArgumentException("[Invalid login ID] word without spaces and special characters.");
		}

		if (password.matches("\\w+")) {
			map.put("password", password);
		} else {
			throw new IllegalArgumentException("[Invalid password] word without spaces and special characters.");
		}

		if (name.matches("[A-Za-z0-9\\s]*")) {
			map.put("name", name);
		} else {
			throw new IllegalArgumentException("[Invalid fund name] word without special characters.");
		}

		map.put("description", description);

		String response = client.makeRequest("/createOrg", map);
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(response);
		} catch (Exception e) {
			throw new IllegalStateException("[Error in communicating with server] fail to create " +
					"a new organization");
		}
		String status = (String) json.get("status");
		if (status.equals("success")) {
			return true;
		}
		return false;
	}
}
