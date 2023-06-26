//package org;

import org.WebClient;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataManager_getContributorName_Test {

	@Test
	public void testSuccessfulCreation() {
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

	@Test(expected = IllegalStateException.class)
	public void testFailedCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"failed\"}";
			}
		});

		String name = dm.getContributorName("1");
	}

	@Test(expected = IllegalStateException.class)
	public void testExceptionCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{}";
			}
		});

		String name = dm.getContributorName("1");
	}
}