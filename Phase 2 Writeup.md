# Writeup

## Group 9

### 1. Additional Tasks Choice

2.6 2.8 2.9

### 2. Tasks Notes

#### 2.1 Organization App caching
      org/DataManager/getContributorName added in cache

#### 2.2 Organization App defensive programming
      DataManager.java modified to pass all the tests.
      UserInterface.java modified to display error messages.
      

#### 2.3 Organization App aggregate donations by contributor
      Contributor/Contributor, DataManager, MakeDonationActivity, ViewDonationActivity
      added in aggregated donations map to quickly look up summed donations by fund.

#### 2.6 Contributor App aggregate donations by fund
      Fund added new field  Map<String, Long[]> aggregateDonations 
      added new method calAggregateDonations() to aggregate the donations
      added getAggregateDonations() to return a list of aggregated donations, in descending order of total donations amount
      Data Manager called the  calAggregateDonations()
      UserInferface modified the method displayFund(option) to offer the option of display individual/aggregate donations
      added printIndividualDonation(donations) and  printAggregateDonation(aggregateDonations) to display information suggested by the option

#### 2.8 Organization App logout/login
      Login prompt added before starting the UI.
      Logout option added to the menu that lists the organization’s funds.
      
#### 2.9 Organization App all contributions
      UserInferface modified to add the option of displaying all contributions
      added method displayAllContributions() to collect all contributions, order them in descending order by date and display.


### 3. N/A

### 4. N/A

### 5. Contribution

|        | Required Tasks | Additional Tasks |
| :----: | :------------: |:----------------:|
|  Hao   |      2.1       |       2.6        |
|   Qi   |      2.2       |       2.8        |
| Zitong |      2.3       |       2.9        |
