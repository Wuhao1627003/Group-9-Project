package org;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

public class DataManager_attemptLogin_Test {

	@Test
	public void testSuccessfulCreation() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				if (Objects.equals(resource, "/findOrgByLoginAndPassword")) {
					return "{\"status\":\"success\",\"data\":{\"_id\":\"123\",\"name\":\"new org\",\"description\":\"this is the new org\", "
							+
							"\"funds\": [{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,"
							+
							"\"donations\":[{\"contributor\":\"1\", \"amount\":1, \"date\":\"2023-06-05T23:00:59.238Z\"}]}]}}";
				} else if (resource.equals("/findContributorNameById")) {
					return "{\"status\":\"success\",\"data\":\"Chris\"}";
				}
				return null;
			}
		});

		Organization org = dm.attemptLogin("chrism", "IamChris");

		assertNotNull(org);
		assertEquals("this is the new org", org.getDescription());
		assertEquals("123", org.getId());
		assertEquals("new org", org.getName());

		List<Fund> funds = org.getFunds();
		assertNotNull(funds);
		assertEquals(1, funds.size());
		Fund f = funds.get(0);
		assertNotNull(f);
		assertEquals("this is the new fund", f.getDescription());
		assertEquals("12345", f.getId());
		assertEquals("new fund", f.getName());
		assertEquals(10000, f.getTarget());

		List<Donation> donations = f.getDonations();
		assertNotNull(donations);
		assertEquals(1, donations.size());
		Donation d = donations.get(0);
		assertEquals("12345", d.getFundId());
		assertEquals("Chris", d.getContributorName());
		assertEquals(1, d.getAmount());
		assertEquals("Jun 05, 2023", d.getDate());
	}

	@Test(expected = IllegalStateException.class)
	public void testFailedCreation() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				if (Objects.equals(resource, "/findOrgByLoginAndPassword")) {
					return "{\"status\":\"nope\"}";
				}
				return null;
			}
		});

		Organization org = dm.attemptLogin("chrism", "IamChris");
	}

	@Test(expected = IllegalStateException.class)
	public void testExceptionCreation() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				if (Objects.equals(resource, "/findOrgByLoginAndPassword")) {
					return "{\"status\":\"success\",\"data\":{\"_id\":\"123\",\"name\":\"new org\",\"description\":\"this is the new org\", "
							+
							"\"funds\": [\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":\"abc\","
							+
							"\"donations\":[\"contributor\":\"1\", \"amount\":\"hhhhh\", \"date\":\"2023/06/02\"]]}}";
				} else if (resource.equals("/findContributorNameById")) {
					return "Chris";
				}
				return null;
			}
		});

		Organization org = dm.attemptLogin("chrism", "IamChris");
	}
}