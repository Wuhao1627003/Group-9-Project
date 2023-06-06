# Writeup

## Group 9

### 1. Additional Tasks Choice

1.7 - 1.9.

### 2. Tasks Notes

#### 1.1 Test

      org/DataManager_attemptLogin_Test.java
      org/DataManager_createFund_Test.java
      org/DataManager_getContributorName_Test.java

#### 1.2 Debug

      1. DataManager.java
      attemptLogin()
      // Check login ID value to make sure it only contains words ([a-zA-Z_0-9]).
      // Check password value to make sure it only contains words ([a-zA-Z_0-9]).
      // Correct typo: String description = (String)data.get("description");
      createFund()
      // Check orgID value to make sure it only contains words ([a-zA-Z_0-9]).
      // Check fund name value to make sure it does not contain special characters.

      2. Organization.java
      // Check id value to make sure it only contains words ([a-zA-Z_0-9]).
      // Check name value to make sure it only contains words ([a-zA-Z_0-9]).

      3. Donation.java
      // Check fundId value to make sure it only contains words ([a-zA-Z_0-9]).
      // Check contributorName value to make sure it only contains words ([a-zA-Z_0-9]).
      // Change data to system local timestamp

      4. Fund.java
      // Check id value to make sure it only contains words ([a-zA-Z_0-9]).
      // Check name value to make sure it only contains words ([a-zA-Z_0-9]).

#### 1.3 Display Total Donations For Fund
     UserInterface.displayFund() modified.

#### 1.7 Input Error Handling
      UserInterface.start() modified.
      UserInterface.createFund() modified.

#### 1.8 Login Error Handing
     DataManager.attemptLogin() modified.
     UserInterface.main() modified.

#### 1.9 Donation
      Constructor modified to parse date

### 3. Bugs Fixed
      1. "description" was misspelled on L54 in DataManager.java
      2. Throw IllegalArgumentException() whenever the inputs are
         invalid when creating new orgnization, donation, and fund,
         and when users try to login to prevent bugs in other code
         corrupt the database.

### 4. N/A

### 5. N/A

### 6. Contribution

|        | Required Tasks | Additional Tasks |
| :----: | :------------: | :--------------: |
|  Hao   |      1.1       |       1.9        |
|   Qi   |      1.2       |       1.8        |
| Zitong |      1.3       |       1.7        |
