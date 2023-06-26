package org;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserInterface {

	private static String login;
	private final DataManager dataManager;
	private final Organization org;
	private final static Scanner in = new Scanner(System.in);

	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
	}

	public static void main(String[] args) {

		DataManager ds = new DataManager(new WebClient("localhost", 3001));

		// Login
		while (true) {
			System.out.println("Please enter username and password.");
			System.out.println("If username doesn't exist yet, it will be registered");

			login = userLoginID();
			String password = userLoginPassword();

			try {
				boolean registered = ds.checkUniqueLoginName(login);
				if (!registered) {
					register(ds, password);
				}

				Organization org = ds.attemptLogin(login, password);
				if (org == null) {
					System.out.println("Login failed.");
				} else {
					System.out.println("Logged in successfully");
					UserInterface ui = new UserInterface(ds, org);
					ui.start(!registered);
				}
			} catch (IllegalStateException | IllegalArgumentException e) {
				System.out.println(e);
			}

			// exit() or re-login
			System.out.print("Exit [Y] | Login [ENTER]: ");
			Scanner in = new Scanner(System.in);
			String choice = in.nextLine();
			if (choice.equals("Y") || choice.equals("y")) {
				System.out.println("Closing the app...");
				System.exit(0);
			}
		}
	}

	public static void register(DataManager ds, String password) {
		System.out.println("You are registering for a new organization.");
		System.out.println("Please enter a name and a description.");

		String name = userNewName(false);
		String description = userNewDescription(false);
		boolean success = ds.createOrg(login, password, name, description);
		if (!success) {
			throw new IllegalStateException("Can't create the organization");
		}
	}

	public void start(boolean newOrg) {
		if (newOrg) {
			System.out.println("Please create a new fund to start.");
			createFund();
			System.out.println("Press the Enter key to go back to the listing of funds");
			in.nextLine();
		}

		while (true) {
			System.out.println();
			List<Fund> funds = org.getFunds();
			int numFunds = funds.size();
			if (numFunds > 0) {
				System.out.println("There are " + numFunds + " funds in organization " + org.getName() + ":");

				int count = 1;
				for (Fund f : funds) {
					System.out.println(count + ": " + f.getName());
					count++;
				}
				System.out.println("================== MENU ==================");
				System.out.println("Enter the fund number to see more information or make a donation.");
			}
			System.out.println("Enter 'n' to create a new fund");
			System.out.println("Enter 'c' to see all contributions");
			System.out.println("Enter 'e' to edit account info");
			System.out.println("Enter 'q' to logout");
			System.out.println("==========================================");
			System.out.println("Please enter your option: ");

			boolean validInput = false;
			while (!validInput) {
				String option = in.nextLine();
				try {
					// existing fund
					int fundNumber = Integer.parseInt(option);
					if (fundNumber >= 1 && fundNumber <= numFunds) {
						validInput = true;
						displayFund(fundNumber);
					} else {
						System.out.println("Please enter an Integer within Range");
					}
				} catch (Exception e) {
					if (option == null || option.length() != 1) {
						System.out.println("Please enter a valid option: n / c / e / q ");
						continue;
					}
					char action = option.charAt(0);
					validInput = true;
					switch (action) {
						case 'n':
							createFund();
							break;
						case 'c':
							displayAllContributions();
							break;
						case 'e':
							editAccountInfo();
							break;
						case 'q':
							return;
						default:
							System.out.println("Please enter a valid option: n / c / e / q ");
							validInput = false;
					}
				}
				System.out.println("Press the Enter key to go back to the listing of funds");
				in.nextLine();
			}
		}
	}

	public void displayAllContributions() {
		HashMap<String, String> fundIDToName = new HashMap<>();
		List<Fund> funds = org.getFunds();
		List<Donation> donations = new ArrayList<>();
		for (Fund fund : funds) {
			donations.addAll(fund.getDonations());
			fundIDToName.put(fund.getId(), fund.getName());
		}
		Collections.sort(donations, new Comparator<Donation>() {
			@Override
			public int compare(Donation o1, Donation o2) {
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				try {
					Date date1 = sdf.parse(o1.getDate());
					Date date2 = sdf.parse(o2.getDate());
					return date2.compareTo(date1);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});

		for (Donation donation : donations) {
			System.out.println("fund name: " + fundIDToName.get(donation.getFundId()) +
					", Donation amount: " + donation.getAmount() +
					", Donation Date: " + donation.getDate());
		}
	}

	public void createFund() {
		String name = "";
		while (true) {
			System.out.println("Enter the fund name: ");
			name = in.next();
			in.nextLine();
			if (!name.isEmpty())
				break;
		}

		System.out.println("Enter the fund description: ");
		String description = in.next();
		in.nextLine();

		System.out.println("Enter the fund target: ");
		long target = 0;
		while (true) {
			try {
				target = in.nextInt();
				in.nextLine();
				break;
			} catch (Exception e) {
				System.out.print("Please enter an Integer: ");
			}
			in.nextLine();
		}

		try {
			Fund fund = dataManager.createFund(org.getId(), name, description, target);
			org.getFunds().add(fund);
		} catch (IllegalStateException | IllegalArgumentException e) {
			System.out.println(e);
		}
	}

	public void displayFund(int fundNumber) {
		Fund fund = org.getFunds().get(fundNumber - 1);

		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());

		System.out.println("Enter 1 to see individual donations");
		System.out.println("Enter 2 to see aggregate donations");
		System.out.println("Enter 3 to make a donation");
		System.out.println("Please enter your option: ");

		int option = 0;
		boolean validInput = false;
		while (!validInput) {
			try {
				option = in.nextInt();
				if (option >= 1 && option <= 3) {
					validInput = true;
				} else {
					System.out.println("Please enter an Integer within Range");
				}
			} catch (Exception e) {
				System.out.println("Please enter an Integer");
			}
			in.nextLine();
		}

		if (option == 3) {
			this.makeDonation(fund, fundNumber);
			return;
		}
		List<Donation> donations = fund.getDonations();
		List<Map.Entry<String, Long[]>> aggregateDonations = fund.getAggregateDonations();
		if (option == 1) {
			printIndividualDonation(donations);
		} else {
			printAggregateDonation(aggregateDonations);
		}

		long totalDonation = 0;
		for (Donation donation : donations) {
			totalDonation += donation.getAmount();
		}
		long target = fund.getTarget();
		if (target == 0) {
			target = 1;
		}
		double donationPercentage = totalDonation * 1.0 / target;
		System.out.println(
				"Total donation amount: $" + totalDonation + "(" + Math.round(donationPercentage * 100) + "% of " +
						"the target)");
	}

	public void makeDonation(Fund fund, int fundNumber) {
		System.out.println("Making a donation...");
		while (true) {
			System.out.print("Enter the contributor Id: ");
			String contributorId = in.next().trim();

			System.out.print("Enter the donation amount: ");
			String amountStr = in.next().trim();
			System.out.println();

			try {
				List<Donation> donations = dataManager.makeDonation(contributorId, fund.getId(), amountStr);
				fund.setDonations(donations);
				fund.calAggregateDonations();
				printIndividualDonation(donations);
				in.nextLine();
				return;
			} catch (IllegalStateException | IllegalArgumentException e) {
				System.out.println(e.getMessage());
				System.out.println("Try Again.");
			}
		}
	}

	public void editAccountInfo() {
		while (true) {
			System.out.println("Enter 1 to Change Password");
			System.out.println("Enter 2 to Edit Organization Name");
			System.out.println("Enter 3 to Edit Organization Description");
			System.out.println("Enter 4 to Edit Organization Name and Description");
			System.out.println("Please enter your option: ");
			int option = 0;
			try {
				option = in.nextInt();
			} catch (Exception e) {
				System.out.println("Please enter an Integer");
			}

			try {
				boolean success = false;
				switch (option) {
					case 1:
						success = changePassword();
						break;
					case 2:
					case 3:
					case 4:
						success = changeAccountInfo(option);
						break;
					default:
						System.out.println("Please enter an Integer within Range");
				}
				if (success) {
					in.nextLine();
					return;
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	public boolean changeAccountInfo(int editInfoFlag) {
		String password = userLoginPassword();
		try {
			Organization org = dataManager.attemptLogin(login, password);
			if (editInfoFlag == 2) {
				String newName = userNewName(true);
				if (newName != null && dataManager.updateOrgName(org.getId(), newName)) {
					System.out.println("Name updated successfully.");
					return true;
				}
			} else if (editInfoFlag == 3) {
				String newDescription = userNewDescription(true);
				if (newDescription != null && dataManager.updateOrgDescription(org.getId(), newDescription)) {
					System.out.println("Description updated successfully.");
					return true;
				}
			} else {
				String newName = userNewName(true);
				String newDescription = userNewDescription(true);
				boolean nameFlag = false;
				boolean desFlag = false;

				if (newName != null && dataManager.updateOrgName(org.getId(), newName)) {
					nameFlag = true;
				}

				if (newDescription != null && dataManager.updateOrgDescription(org.getId(), newDescription)) {
					desFlag = true;
				}

				if (nameFlag && desFlag) {
					System.out.println("Name and description updated successfully.");
					return true;
				} else if (nameFlag && !desFlag) {
					System.out.println("Only name updated successfully; description updated failed.");
					return true;
				} else if (!nameFlag && desFlag) {
					System.out.println("Only description updated successfully; name updated failed.");
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return false;
	}

	public boolean changePassword() {
		String password = userLoginPassword();
		try {
			Organization org = dataManager.attemptLogin(login, password);
			String newPassword = userLoginNewPassword();
			if (dataManager.updatePassword(org.getId(), newPassword)) {
				System.out.println("Password updated successfully.");
				return true;
			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return false;
	}

	private String userLoginNewPassword() {
		while (true) {
			System.out.println("Please enter your new password: ");
			String passwordOne = in.next();
			System.out.println("Please enter your new password again: ");
			String passwordTwo = in.next();

			if (passwordOne.equals(passwordTwo)) {
				return passwordOne;
			} else {
				System.out.println("New Password does not match. Try Again.");
			}
		}
	}

	private static String userNewName(boolean needsConfirmation) {
		while (true) {
			System.out.println("Please enter the new organization name: ");
			String newName = in.next();

			// Check name value
			if (newName.matches("[A-Za-z0-9\\s]*")) {
				if (!needsConfirmation || editConfirm()) {
					return newName;
				} else {
					return null;
				}
			} else {
				System.out.println("[Invalid org name] word without special characters. Try Again.");
			}
		}
	}

	private static String userNewDescription(boolean needsConfirmation) {
		System.out.println("Please enter the new organization description: ");
		String newDescription = in.next();
		if (!needsConfirmation || editConfirm()) {
			return newDescription;
		} else {
			return null;
		}
	}

	private static boolean editConfirm() {
		System.out.println("Confirm your change [Y/N]: ");
		while (true) {
			String choice = in.next();
			if (choice.equals("Y") || choice.equals("y")) {
				return true;
			} else if (choice.equals("N") || choice.equals("n")) {
				return false;
			}
			System.out.println("Please enter [Y/N]: ");
		}
	}

	private static void printIndividualDonation(List<Donation> donations) {
		System.out.println("Number of donations: " + donations.size());
		for (Donation donation : donations) {
			System.out.println(
					"* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
		}
	}

	private static void printAggregateDonation(List<Map.Entry<String, Long[]>> aggregateDonations) {
		for (Map.Entry<String, Long[]> entry : aggregateDonations) {
			System.out.println("* " + entry.getKey() + ", " + entry.getValue()[0] + " donations, " + "$"
					+ entry.getValue()[1] + " total");
		}
	}

	private static String userLoginID() {
		Scanner loginScanner = new Scanner(System.in);
		System.out.print("Enter your login ID: ");
		String login = loginScanner.nextLine();
		if (login.isEmpty()) {
			return null;
		}
		return login;
	}

	private static String userLoginPassword() {
		Scanner loginScanner = new Scanner(System.in);
		System.out.print("Enter your password: ");
		String password = loginScanner.nextLine();
		if (password.isEmpty()) {
			return null;
		}
		return password;
	}
}
