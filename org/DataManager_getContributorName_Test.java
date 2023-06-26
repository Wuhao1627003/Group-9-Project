package org;

import org.WebClient;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataManager_getContributorName_Test {

	@Test
	public void testSuccessfulLookup() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":\"Chris\"}";
			}
		});

		String name = dm.getContributorName("1");

		assertNotNull(name);
		assertEquals("Chris", name);
	}

	@Test
	public void testSuccessfulLookupWithCache() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":\"Chris\"}";
			}
		});

		String name = dm.getContributorName("1");
		String newName = dm.getContributorName("1");

		assertNotNull(name);
		assertEquals("Chris", name);
		assertNotNull(newName);
		assertEquals("Chris", newName);
	}

	@Test(expected = IllegalStateException.class)
	public void testFailedLookup() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"failed\"}";
			}
		});

		String name = dm.getContributorName("1");
	}

	@Test(expected = IllegalStateException.class)
	public void testExceptionLookup() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{}";
			}
		});

		String name = dm.getContributorName("1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgument() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{}";
			}
		});

		String name = dm.getContributorName("@");
	}
}