package com.techelevator.view;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}

	public void printUsers(List<User> users) {
		System.out.println("----------------------------------");
		System.out.println("User         User");
		System.out.println(" ID          Name");
		System.out.println("----------------------------------");
		for (User user : users) {
			System.out.println(user.getId() + ":        " + user.getUsername());
		}
		System.out.println("----------------------------------");
	}

	public void printTransferMessage(Transfer transfer) {
		System.out.println();
		System.out.println(transfer.getFromUsername() + " successfully transferred " + NumberFormat.getCurrencyInstance().format(transfer.getAmount()) + " to " + transfer.getToUsername() + "!");
		System.out.println("Your transfer ID is: " + transfer.getTransferId());
	}

	public void getTransfers(List<Transfer> transfersList, User user) {
		String stringOfTransfers = "";

		System.out.println("Transfer Details \n" +
						"-----------------------------------------------------------" + "\n" +
						"Transfer     To/               Amount               Status" + "\n" +
						"   ID        From                               " + "\n" +
						"-----------------------------------------------------------");

		for (Transfer transfer: transfersList) {
			if (user.getId() == transfer.getToUserId()) {
				stringOfTransfers += String.format("%1$-1s %2$-8s %3$-6s %4$-12s %5$-19s %6$8s \n", "", transfer.getTransferId(), "From: ", transfer.getFromUsername(),
						NumberFormat.getCurrencyInstance().format(transfer.getAmount()), transfer.getTransferStatus());
			} else if (user.getId() == transfer.getFromUserId()) {
				stringOfTransfers += String.format("%1$-1s %2$-8s %3$-6s %4$-12s %5$-19s %6$8s \n", "", transfer.getTransferId(), "To: ", transfer.getToUsername(),
						NumberFormat.getCurrencyInstance().format(transfer.getAmount()), transfer.getTransferStatus());
							}

		}

		System.out.print(stringOfTransfers);
		System.out.println("-----------------------------------------------------------");
	}

	public void getTransferDetails(Transfer transfer) {
		String stringofTransferDetails = "";
		System.out.println("-------------------------------------------" + "\n" +
				"       Transfer Details for " + transfer.getTransferId() + "\n" +
				"-------------------------------------------");

		System.out.println("Amount: " + NumberFormat.getCurrencyInstance().format(transfer.getAmount()) + "\n");
		stringofTransferDetails += String.format("%1$-20s %2$-12s", "To: " + transfer.getToUsername(), "From: " + transfer.getFromUsername());
		System.out.println(stringofTransferDetails);
		System.out.println("Type: " + transfer.getTransferType());
		System.out.println("Status: " + transfer.getTransferStatus());

		System.out.println("-------------------------------------------");
	}

	public void getApproveOrRejectOptions() {
		System.out.println("1: Approve");
		System.out.println("2: Reject");
		System.out.println("0: Don't approve or reject");
		System.out.println("------------------------------");
	}
}
