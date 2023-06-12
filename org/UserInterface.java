//package org;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
	
	
	private DataManager dataManager;
	private Organization org;
	private Scanner in = new Scanner(System.in);
	
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
			System.out.println("Enter -1 to logout");
			int option = 0;
            boolean validInput = false;
            while (!validInput) {
                try{
                    option = in.nextInt();
                    if (option >= 0 && option <= org.getFunds().size()) {
                        validInput = true;
                    } else if (option == 0) {
                        System.out.println("Please enter an Integer within Range");
                        in.nextLine();
                    } else if (option == -1) {
						logout = true;
						break;
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
			} else {
				displayFund(option);
			}
		}
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
            } catch (Exception e){
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
	
	
	public void displayFund(int fundNumber) {
		
		Fund fund = org.getFunds().get(fundNumber - 1);
		
		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());
		
		List<Donation> donations = fund.getDonations();
		System.out.println("Number of donations: " + donations.size());
        long totalDonation = 0;
		for (Donation donation : donations) {
			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
            totalDonation += donation.getAmount();
		}
        long target = fund.getTarget();
        if (target == 0) {
            target =1;
        }
        double donationPercentage =  totalDonation*1.0/target;
        System.out.println("Total donation amount: $" + totalDonation + "(" +Math.round(donationPercentage*100) + "% of " +
                "the target)");
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
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
				}
				else {
					UserInterface ui = new UserInterface(ds, org);
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
