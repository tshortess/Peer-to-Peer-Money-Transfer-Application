package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.view.ConsoleService;

import java.text.NumberFormat;
import java.util.List;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_COMPLETED_TRANSFERS = "View completed transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_COMPLETED_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final int APPROVE_TRANSFER = 1;
	private static final int REJECT_TRANSFER = 2;
	private static final int EXIT = 0;

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
	private AccountService accountService;
	private TransferService transferService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL), new TransferService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService, TransferService transferService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.transferService = transferService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_COMPLETED_TRANSFERS.equals(choice)) {
				viewCompletedTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
    	String balanceMessage = "Your current account balance is: " + NumberFormat.getCurrencyInstance().format(accountService.getBalance(currentUser.getToken()));
    	System.out.println(balanceMessage);
	}

	private void viewCompletedTransferHistory() {
		console.getTransfers(transferService.getCompletedTransfers(currentUser.getToken()), currentUser.getUser());
		int transferId = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");
		if (transferId != EXIT && transferIsInList(transferId, transferService.getCompletedTransfers(currentUser.getToken()))) {
			Transfer transfer = getTransferDetails(transferId, transferService.getCompletedTransfers(currentUser.getToken()));
			console.getTransferDetails(transfer);
		} else if (transferId == EXIT) {
			mainMenu();
		} else {
			System.out.println("You entered an invalid transfer ID.");
			mainMenu();
		}

	}

	private Transfer getTransferDetails(int transferId, List<Transfer> transferList) {
		Transfer transferToGet = new Transfer();
		for (Transfer transfer: transferList) {
			if (transfer.getTransferId() == transferId) {
				transferToGet = transfer;
			}
		}
		return transferToGet;
	}

	private void viewPendingRequests() {
		console.getTransfers(transferService.getPendingTransfers(currentUser.getToken()), currentUser.getUser());
		int transferId = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");
		if (transferId != EXIT && transferIsInList(transferId, transferService.getPendingTransfers(currentUser.getToken()))) {
			Transfer transfer = getTransferDetails(transferId, transferService.getPendingTransfers(currentUser.getToken()));
			console.getTransferDetails(transfer);
			console.getApproveOrRejectOptions();
			int option = console.getUserInputInteger("Please choose an option");
			Transfer updatedTransfer = new Transfer();
			if (option == APPROVE_TRANSFER) {
				updatedTransfer = transferService.approveTransfer(currentUser.getToken(), transfer);
				console.getTransferDetails(updatedTransfer);
			} else if (option == REJECT_TRANSFER) {
				updatedTransfer = transferService.rejectTransfer(currentUser.getToken(), transfer);
				console.getTransferDetails(updatedTransfer);
			} else if (option == EXIT) {
				mainMenu();
			} else {
				System.out.println("You entered an invalid option.");
				mainMenu();
			}
		} else if (transferId == EXIT) {
			mainMenu();
		} else {
			System.out.println("You entered an invalid transfer ID.");
			mainMenu();
		}
	}

	private boolean transferIsInList(int transferId, List<Transfer> transferList) {
		boolean transferIsInList = false;
		for (Transfer transfer: transferList) {
			if (transfer.getTransferId() == transferId) {
				transferIsInList = true;
			}
		}
		return transferIsInList;
	}

	private void sendBucks() {
		console.printUsers(transferService.getUsers(currentUser.getToken()));
		int toUserId = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
		if (toUserId != EXIT && userIsInList(toUserId)) {
			int amount = console.getUserInputInteger("Enter amount");
			if (amount > EXIT) {
				Transfer transfer = transferService.initiateTransfer(toUserId, amount, currentUser.getToken());
				if (transfer.getTransferStatus().equals("Approved")) {
					console.printTransferMessage(transfer);
				} else {
					System.out.println("Sorry, your balance of " + NumberFormat.getCurrencyInstance().format(accountService.getBalance(currentUser.getToken())) + " is less than " + NumberFormat.getCurrencyInstance().format(amount) + ".");
					System.out.println("Your transfer failed, but was logged as transfer ID: " + transfer.getTransferId());
				}
			} else {
				System.out.println("You entered an invalid amount. Please try again.");
				mainMenu();
			}
		} else if (toUserId == EXIT) {
			mainMenu();
		} else {
			System.out.println("You entered an invalid user ID.");
			mainMenu();
		}
    }

    private boolean userIsInList(int toUserId) {
    	boolean userIsInList = false;
		List<User> userList = transferService.getUsers(currentUser.getToken());
    	for (User user: userList) {
    		if (user.getId() == toUserId) {
    			userIsInList = true;
			}
		}
    	return userIsInList;
	}

	private void requestBucks() {
		console.printUsers(transferService.getUsers(currentUser.getToken()));
		int fromUserId = console.getUserInputInteger("Enter ID of user you are requesting from (0 to cancel)");
		if (fromUserId != EXIT && userIsInList(fromUserId)) {
			int amount = console.getUserInputInteger("Enter amount");
			if (amount > EXIT) {
				Transfer transfer = transferService.requestTransfer(fromUserId, amount, currentUser.getToken());
				System.out.println("Your request for " + NumberFormat.getCurrencyInstance().format(amount) + " has been sent to " + transfer.getFromUsername() + ".");
				System.out.println("The transfer ID is: " + transfer.getTransferId());
			} else {
				System.out.println("You entered an invalid amount. Please try again.");
				mainMenu();
			}
		} else if (fromUserId == EXIT) {
			mainMenu();
		} else {
			System.out.println("You entered an invalid user ID.");
			mainMenu();
		}
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
