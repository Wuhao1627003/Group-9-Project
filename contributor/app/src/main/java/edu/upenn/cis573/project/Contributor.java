package edu.upenn.cis573.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Contributor implements Serializable {

    private String id;
    private String name;
    private String email;
    private String creditCardNumber;
    private String creditCardCVV;
    private String creditCardExpiryMonth;
    private String creditCardExpiryYear;
    private String creditCardPostCode;
    private List<Donation> donations;
    private Map<String, List<Long>> aggregatedDonations;

    public Contributor(String id, String name, String email, String creditCardNumber, String creditCardCVV, String creditCardExpiryMonth, String creditCardExpiryYear, String creditCardPostCode) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.creditCardNumber = creditCardNumber;
        this.creditCardCVV = creditCardCVV;
        this.creditCardExpiryMonth = creditCardExpiryMonth;
        this.creditCardExpiryYear = creditCardExpiryYear;
        this.creditCardPostCode = creditCardPostCode;
        donations = new LinkedList<>();
        aggregatedDonations = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getCreditCardCVV() {
        return creditCardCVV;
    }

    public String getCreditCardExpiryMonth() {
        return creditCardExpiryMonth;
    }

    public String getCreditCardExpiryYear() {
        return creditCardExpiryYear;
    }

    public String getCreditCardPostCode() {
        return creditCardPostCode;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void aggregateDonation(Donation donation) {
        String fundName = donation.getFundName();
        if (aggregatedDonations.containsKey(fundName)) {
            List<Long> amounts = aggregatedDonations.get(fundName);
            amounts.add(donation.getAmount());
        } else {
            List<Long> amounts = new LinkedList<>();
            amounts.add(donation.getAmount());
            aggregatedDonations.put(fundName, amounts);
        }
    }

    public List<String> getAggregatedDonations() {
        List<String> donationsStrings = new ArrayList<>();
        for (Map.Entry<String, List<Long>> entry: aggregatedDonations.entrySet()) {
            String fundName = entry.getKey();
            List<Long> amounts = entry.getValue();
            int numDonations = amounts.size();
            long total = 0;
            for (long amount : amounts) {
                total += amount;
            }

            String retString = fundName + ", " + numDonations + " donation" +
                    ((amounts.size() > 1) ? "s" : "") + ", $" + total + " total";
            donationsStrings.add(retString);
        }

        return donationsStrings;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }
}
