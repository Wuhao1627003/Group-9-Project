package org;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserInterface {


	private final DataManager dataManager;
	private final Organization org;
	private final Scanner in = new Scanner(System.in);

	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
	}

	public void start() {
		boolean logout = false;

		while (!logout) {
			System.out.println("\n\n");
			if (org.getFunds().size() > 0) {
				System.out.println("There are " + org.getFunds().size() + " funds in this organization:");

				int count = 1;
				for (Fund f : org.getFunds()) {

					System.out.println(count + ": " + f.getName());

					count++;
				}
				System.out.println("Enter the fund number to see more information.");
			}
			System.out.println("Enter 0 to create a new fund");
			System.out.println("Enter 99 to see all contributions");
			System.out.println("Enter 100 to make a contribution on behalf of a contributor");
			System.out.println("Enter -1 to logout");

			int option = 0;
			boolean validInput = false;
			while (!validInput) {
				try {
					option = in.nextInt();
					if ((option >= 0 && option <= org.getFunds().size()) || option == 99 || option == 100) {
						validInput = true;
					} else if (option == -1) {
						logout = true;
						break;
					} else {
						System.out.println("Please enter an Integer within Range");
						in.nextLine();
					}
				} catch (Exception e) {
					System.out.println("Please enter an Integer");
					in.nextLine();
				}
			}
			in.nextLine();
			if (option == -1) {

			} else if (option == 0) {
				createFund();
			} else if (option == 99) {
				displayAllContributions();
			} else if (option == 100) {
				makeDonation();
			} else {
				displayFund(option);
			}
		}
	}

	private void makeDonation() {
		while (true) {
			System.out.println("Making a donation...");
			System.out.print("Enter the fund number: ");
			String fundId = "";
			int fundNumber = 0;
			try {
				fundNumber = in.nextInt();
				Fund fund = org.getFunds().get(fundNumber - 1);
				fundId = fund.getId();
			} catch (Exception e) {
				System.out.println("[Invalid Input] Invalid fund number. Try Again.");
				continue;
			}

			while (true) {
				System.out.print("Enter the contributor Id: ");
				String contributorId = in.next().trim();

				System.out.print("Enter the donation amount: ");
				String amountStr = in.next().trim();
				System.out.println();

				try {
					List<Donation> donations = dataManager.makeDonation(contributorId, fundId, amountStr);
					org.getFunds().get(fundNumber - 1).setDonations(donations);
					printIndividualDonation(donations);
					return;
				} catch (IllegalStateException | IllegalArgumentException e) {
					System.out.println(e.getMessage());
					System.out.println("Try Again.");
				}
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
					" Donation amount: " + donation.getAmount() +
					" Donation Date: " + donation.getDate());
		}
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
	}

	public void createFund() {

		String name = "";
		while (true) {
			System.out.print("Enter the fund name: ");
			name = in.nextLine().trim();
			if (!name.equals("")) break;
		}

		System.out.print("Enter the fund description: ");
		String description = in.nextLine().trim();

		System.out.print("Enter the fund target: ");
		long target = 0;
		while (true) {
			try {
				target = in.nextInt();
				in.nextLine();
				break;
			} catch (Exception e) {
				System.out.print("Please enter an Integer: ");
				in.nextLine();
			}
		}

		try {
			Fund fund = dataManager.createFund(org.getId(), name, description, target);
			org.getFunds().add(fund);
		} catch (IllegalStateException | IllegalArgumentException e) {
			System.out.println(e);
		}
	}

	public void changePassword(DataManager ds, String login) {
		System.out.println("Login successfully");
		while (true) {
			System.out.println("Would you like to change your password?(Y/N)");
			String option = in.nextLine();
			if (option.equals("N")) return;
			if (option.equals("Y")) {
				String password = userLoginPassword();
				try {
					Organization org = ds.attemptLogin(login, password);
					String newPassword = userLoginNewPassword();
					if (ds.updatePassword(org.getId(), newPassword)) {
						System.out.println("password updated successfully");
						break;
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
	}

	private String userLoginNewPassword() {
		System.out.println("Please enter your new password");
		String passwordOne = in.nextLine();
		System.out.println("Please enter your new password again");
		String passwordTwo = in.nextLine();

		if (!passwordOne.equals(passwordTwo)) {
			throw new IllegalStateException("New Password does not match");
		}

		return passwordOne;
	}


	public void displayFund(int fundNumber) {

		Fund fund = org.getFunds().get(fundNumber - 1);

		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());

		System.out.println("Enter 1 to see individual donations");
		System.out.println("Enter 2 to see aggregate donations");

		int option = 0;
		boolean validInput = false;
		while (!validInput) {
			try {
				option = in.nextInt();
				if (option == 1 || option == 2) {
					validInput = true;
				} else {
					System.out.println("Please enter an Integer within Range");
					in.nextLine();
				}
			} catch (Exception e) {
				System.out.println("Please enter an Integer");
				in.nextLine();
			}
		}
		in.nextLine();
		List<Donation> donations = fund.getDonations();
		List<Map.Entry<String, Long[]>> aggregateDonations = fund.getAggregateDonations();
		if (option == 1) {
			printIndividualDonation(donations);
		} else if (option == 2) {
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
		System.out.println("Total donation amount: $" + totalDonation + "(" + Math.round(donationPercentage * 100) + "% of " +
				"the target)");
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
	}

	private static void printIndividualDonation(List<Donation> donations) {
		System.out.println("Number of donations: " + donations.size());
		for (Donation donation : donations) {
			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
		}
	}

	private static void printAggregateDonation(List<Map.Entry<String, Long[]>> aggregateDonations) {
		for (Map.Entry<String, Long[]> entry : aggregateDonations) {
			System.out.println("* " + entry.getKey() + ", " + entry.getValue()[0] + " donations, " + "$" + entry.getValue()[1] + " total");
		}
	}

	private static String userLoginID() {
		Scanner loginScanner = new Scanner(System.in);
		System.out.print("Enter your login ID: ");
		String login = loginScanner.nextLine();
		if (login.equals("")) {
			return null;
		}
		return login;
	}

	private static String userLoginPassword() {
		Scanner loginScanner = new Scanner(System.in);
		System.out.print("Enter your password: ");
		String password = loginScanner.nextLine();
		if (password.equals("")) {
			return null;
		}
		return password;
	}


	public static void main(String[] args) {

		DataManager ds = new DataManager(new WebClient("localhost", 3001));

		// Login
		while (true) {
			String login = userLoginID();
			String password = userLoginPassword();


			try {
				Organization org = ds.attemptLogin(login, password);
				if (org == null) {
					System.out.println("Login failed.");
				} else {
					UserInterface ui = new UserInterface(ds, org);
					ui.changePassword(ds, login);
					ui.start();
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
}
