package Client.src;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientService {
	private static final String IPv4_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
											+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." 
											+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
											+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

	private static final String port_REGEX = "^(50[0-4][0-9]|5050)$";

	private static final Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);

	private static final Pattern port_PATTERN = Pattern.compile(port_REGEX);

	public static String serverAddress;
	public static String portNumber;

	private static boolean isValid(String number, Pattern pattern) {
		if (number == null) {
			return false;
		}
		Matcher matcher = pattern.matcher(number);

		return matcher.matches();
	}

	public static String validateIPaddress(String tempAddress) {
		while (!isValid(tempAddress, IPv4_PATTERN)) {
			System.out.println("The IP address " + tempAddress + " isn't valid \n");
			System.out.println("Enter the correct IP address of the machine running the server:");
			tempAddress = new Scanner(System.in).nextLine();
		}

		if (isValid(tempAddress, IPv4_PATTERN)) {
			serverAddress = tempAddress;
			System.out.print("The IP address " + serverAddress + " is valid \n");
		}
		return serverAddress;
	}

	public static String validatePortNumber(String tempPortNumber) {
		while (!isValid(tempPortNumber, port_PATTERN)) {
			System.out.println("The port number " + tempPortNumber + " isn't valid \n");
			System.out.println("Enter the correct port number of the machine running the server:");
			tempPortNumber = new Scanner(System.in).nextLine();
		}

		if (isValid(tempPortNumber, port_PATTERN)) {
			portNumber = tempPortNumber;
			System.out.print("The IP address " + portNumber + " is valid \n");
		}
		return portNumber;
	}

	public static void validateUser(String Password, String username, String type, ObjectOutputStream out)
			throws IOException, ClassNotFoundException {
		ArrayList<String> messageToServer = new ArrayList<String>();
		messageToServer.add(type);
		messageToServer.add(username);
		messageToServer.add(Password);
		out.writeObject(messageToServer);
		out.flush();
	}

	public static void sendInitialRequest(String input, String type, ObjectOutputStream out)
			throws IOException, ClassNotFoundException {
		ArrayList<String> messageToServer = new ArrayList<String>();
		messageToServer.add(type);
		messageToServer.add(input);
		out.writeObject(messageToServer);
		out.flush();
	}

	public static void enterCommands(String username, ObjectOutputStream out) throws IOException {		
		System.out.print("Enter a command : ");
		String input = new Scanner(System.in).nextLine();
		while(input.equals("download") || input.equals("upload") || input.equals("delete")) {
			System.out.println("File is missing, please enter the command you want with the file : ");
			input = new Scanner(System.in).nextLine();
		}
		String parts[] = input.split(" ", 2);
		String command = parts[0];
		String file;
		if( command.equals("download") || command.equals("upload") || command.equals("delete") ) {
			file = parts[1];
		}
		else {
			file = " ";
		}		 
		ArrayList<String> messageToServer = new ArrayList<String>();
		messageToServer.add("commands");
		messageToServer.add(username);
		messageToServer.add(command);
		messageToServer.add(file);
		out.writeObject(messageToServer);
		out.flush();
	}

	public static void listing(ArrayList<String> list) throws ClassNotFoundException, IOException {
		for (int i = 1; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
	
}
