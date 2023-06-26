//package org;

import org.Donation;
import org.WebClient;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

public class DataManager_makeDonation_Test {
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyContributorId() {
		validDataManager.makeDonation("", "1", "10");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyFundId() {
		validDataManager.makeDonation("1", "", "10");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyAmount() {
		validDataManager.makeDonation("1", "1", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNaNAmount() {
		validDataManager.makeDonation("1", "1", "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeAmount() {
		validDataManager.makeDonation("1", "1", "-2");
	}

	@Test(expected = IllegalStateException.class)
	public void testNullDonationResponse() {
		nullStringDataManager.makeDonation("1", "1", "1");
	}

	@Test(expected = IllegalStateException.class)
	public void testEmptyFundJson() {
		emptyFundDataManager.makeDonation("1", "1", "1");
	}

	@Test(expected = IllegalStateException.class)
	public void testFailedDonation() {
		failedDonationDataManager.makeDonation("1", "1", "1");
	}

	@Test(expected = IllegalStateException.class)
	public void testFailedFind() {
		failedFindDataManager.makeDonation("1", "1", "1");
	}

	@Test
	public void testSingleDonation() {
		List<Donation> donations = validDataManager.makeDonation("1", "funId", "1");
		assertEquals(1, donations.size());
		Donation donation = donations.get(0);
		assertEquals("funId", donation.getFundId());
		assertEquals(30, donation.getAmount());
	}

	private final DataManager validDataManager = new DataManager(new WebClient("localhost", 3001) {
		@Override
		public String makeRequest(String resource, Map<String, Object> queryParams) {
			if (Objects.equals(resource, "/findFundById")) {
				return "{\"status\":\"success\",\"data\":{\"target\":100,\"_id\":\"funId\",\"name\":\"testFund\","
						+ "\"description\":\"test fund\",\"org\":\"0\",\"donations\":"
						+ "[{\"_id\":\"donId\",\"contributor\":\"conId\",\"fund\":\"funId\",\"date\":\"2023-06-04T06:28:58.218Z\",\"amount\":30,\"__v\":0}],\"__v\":0}}";
			} else if (Objects.equals(resource, "/makeDonation")) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"1\",\"contributor\":\"conId\","
						+ "\"fund\":\"funId\",\"date\":\"2023-06-26T04:20:55.651Z\",\"amount\":1,\"__v\":0}}";
			} else if (Objects.equals(resource, "/findContributorNameById")) {
				return "{\"status\":\"success\",\"data\":\"Chris\"}";
			}
			return null;
		}
	});

	private final DataManager nullStringDataManager = new DataManager(new WebClient("localhost", 3001) {
		@Override
		public String makeRequest(String resource, Map<String, Object> queryParams) {
			if (Objects.equals(resource, "/findContributorNameById")) {
				return "{\"status\":\"success\",\"data\":\"Chris\"}";
			}
			return null;
		}
	});

	private final DataManager emptyFundDataManager = new DataManager(new WebClient("localhost", 3001) {
		@Override
		public String makeRequest(String resource, Map<String, Object> queryParams) {
			if (Objects.equals(resource, "/makeDonation")) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"1\",\"contributor\":\"conId\","
						+ "\"fund\":\"funId\",\"date\":\"2023-06-26T04:20:55.651Z\",\"amount\":1,\"__v\":0}}";
			} else if (Objects.equals(resource, "/findFundById")) {
				return "";
			} else if (Objects.equals(resource, "/findContributorNameById")) {
				return "{\"status\":\"success\",\"data\":\"Chris\"}";
			}
			return null;
		}
	});

	private final DataManager failedDonationDataManager = new DataManager(new WebClient("localhost", 3001) {
		@Override
		public String makeRequest(String resource, Map<String, Object> queryParams) {
			if (Objects.equals(resource, "/makeDonation")) {
				return "{\"status\":\"failed\"}";
			} else if (Objects.equals(resource, "/findFundById")) {
				return "";
			} else if (Objects.equals(resource, "/findContributorNameById")) {
				return "{\"status\":\"success\",\"data\":\"Chris\"}";
			}
			return null;
		}
	});

	private final DataManager failedFindDataManager = new DataManager(new WebClient("localhost", 3001) {
		@Override
		public String makeRequest(String resource, Map<String, Object> queryParams) {
			if (Objects.equals(resource, "/makeDonation")) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"1\",\"contributor\":\"conId\","
						+ "\"fund\":\"funId\",\"date\":\"2023-06-26T04:20:55.651Z\",\"amount\":1,\"__v\":0}}";
			} else if (Objects.equals(resource, "/findFundById")) {
				return "{\"status\":\"failed\"}";
			} else if (Objects.equals(resource, "/findContributorNameById")) {
				return "{\"status\":\"success\",\"data\":\"Chris\"}";
			}
			return null;
		}
	});
}
