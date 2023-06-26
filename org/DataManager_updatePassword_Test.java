package org;

import org.junit.Test;

import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;


public class DataManager_updatePassword_Test {
    @Test(expected = IllegalArgumentException.class)
    public void testIlegalArgument() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (Objects.equals(resource, "/updatePassword")) {
                    return "{\"status\":\"success\",\"data\":{\"_id\":\"123\",\"name\":\"new org\",\"description\":\"this is the new org\", "
                            +
                            "\"funds\": [{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,"
                            +
                            "\"donations\":[{\"contributor\":\"1\", \"amount\":1, \"date\":\"2023-06-05T23:00:59.238Z\"}]}]}}";
                }
                return null;
            }
        });

        dm.updatePassword(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIlegalIdArgument() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (Objects.equals(resource, "/updatePassword")) {
                    return "{\"status\":\"success\",\"data\":{\"_id\":\"123\",\"name\":\"new org\",\"description\":\"this is the new org\", "
                            +
                            "\"funds\": [{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,"
                            +
                            "\"donations\":[{\"contributor\":\"1\", \"amount\":1, \"date\":\"2023-06-05T23:00:59.238Z\"}]}]}}";
                }
                return null;
            }
        });

        dm.updatePassword("!", "12345");
    }

    @Test
    public void testSuccessfulUpdate() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (Objects.equals(resource, "/updatePassword")) {
                    return "{\"status\":\"success\",\"data\":{\"_id\":\"123\",\"name\":\"new org\",\"description\":\"this is the new org\", "
                            +
                            "\"funds\": [{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,"
                            +
                            "\"donations\":[{\"contributor\":\"1\", \"amount\":1, \"date\":\"2023-06-05T23:00:59.238Z\"}]}]}}";
                }
                return null;
            }
        });
        assertTrue(dm.updatePassword("123", "12345"));
    }

    @Test
    public void testFailedUpdate() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (Objects.equals(resource, "/updatePassword")) {
                    return "{\"status\":\"error\",\"data\": null}";
                }
                return null;
            }
        });
        assertFalse(dm.updatePassword("123", "12345"));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateException() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.updatePassword("123", "12345");
    }





}
