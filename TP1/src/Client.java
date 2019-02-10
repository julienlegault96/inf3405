import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
//import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

	private static final String IPv4_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
											 "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."  + 
											 "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."  + 
											 "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

	private static final String port_REGEX = "^(50[0-4][0-9]|5050)$";

	private static final Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);

	private static final Pattern port_PATTERN = Pattern.compile(port_REGEX);

	private static String serverAddress;
	static String portNumber;

	private static boolean isValid(String number, Pattern pattern) {
		if (number == null) {
			return false;
		}
		Matcher matcher = pattern.matcher(number);

		return matcher.matches();
	}
	
	private static String validateIPaddress(String tempAddress) {
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
	
	private static String validatePortNumber(String tempPortNumber) {
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

	public static void main(String[] args) throws Exception {

		System.out.println("Enter the IP address of a machine running the capitalize server:");
		String tempAddress = new Scanner(System.in).nextLine();
		serverAddress = validateIPaddress(tempAddress);

		System.out.println("Enter the port number of a machine running the server:");
		String tempPortNumber = new Scanner(System.in).nextLine();
		portNumber = validatePortNumber(tempPortNumber);

		Socket socket = null;
		socket = new Socket(serverAddress, Integer.parseInt(portNumber));
		
		
		
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		out.flush();
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		Scanner scanner = new Scanner(System.in);
		ArrayList<String> messageToServer = new ArrayList<String>();
		
		//Username
		System.out.println("Enter your username : ");
		String input = scanner.nextLine();
		messageToServer.add("username");
		messageToServer.add(input);
		
		System.out.print("messageToServer" + messageToServer);		
		out.writeObject(messageToServer);
		
		messageToServer.clear();
		ArrayList<String> responseFromServer  = (ArrayList<String>) in.readObject();
		System.out.print("responseFromServer" + responseFromServer);
			switch(responseFromServer.get(0)) {
			case "password":
				System.out.println("Enter your password : ");
				input = scanner.nextLine();
				messageToServer.add(input);
				out.writeObject(messageToServer);
				responseFromServer.clear();
				break;
			case "newUsername":
				System.out.println("You seem to be a new user would you like to create your own space");
				input = scanner.nextLine();
				messageToServer.add(input);
				out.writeObject(messageToServer);
				responseFromServer.clear();
				break;
			default : System.out.println("Error");
				responseFromServer.clear();
				break;
			}

	}
		


		//Authentification authentifier =  new Authentification();
		
		//authentifier.authentification();
		
		//BufferedReader in = null;
		//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//PrintWriter out = null;ObjectOutputStream
		//out = new PrintWriter(socket.getOutputStream(), true);

		/*System.out.println(in.readLine());
		Scanner scanner = null;
		scanner = new Scanner(System.in);
		boolean connected = true;
		while (connected) {
			Commands commands = new Commands();
			
			System.out.println("\nEnter a  command");
			String command = scanner.nextLine();			
			switch(command) {			
			case "exit": connected = false; 
				break;				
			case "ls": 
				System.out.println(">> ls");
				commands.list( new File( "../database/" + authentifier.FileName));
				break;
			case "upload": System.out.println(">> upload");
				break;
			case "download": System.out.println(">> download");
				break;
			case "delete": System.out.println(">> delete");
				break;
			default : System.out.println("command " + "'" + command + "'" + " not found");
				break;
			}
		}*/
		//scanner.close();
		
	
}