import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server {

	private static final String IPv4_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
											 "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."  + 
											 "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."  +
											 "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	
	private static final String port_REGEX = "^(50[0-4][0-9]|5050)$";

	private static final Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);

	private static final Pattern port_PATTERN = Pattern.compile(port_REGEX);

	private static boolean isValid(String number, Pattern pattern) {
		if (number == null) {
			return false;
		}
		Matcher matcher = pattern.matcher(number);

		return matcher.matches();
	}

	static String serverAddress;
	static int clientNumber = 0;

	public static void main(String[] args) throws Exception {

		System.out.println("Enter the IP address:");
		serverAddress = new Scanner(System.in).nextLine();

		// Validate an IPv4 address
		while (!isValid(serverAddress, IPv4_PATTERN)) {
			System.out.print("The IP address " + serverAddress + " isn't valid \n");
			System.out.println("Enter the correct IP address of the machine running the server:");
			serverAddress = new Scanner(System.in).nextLine();
		}

		if (isValid(serverAddress, IPv4_PATTERN)) {
			System.out.print("The IP address " + serverAddress + " is valid \n");
		}

		System.out.println("Enter the port number:");
		String portNumber = new Scanner(System.in).nextLine();

		// validation PORT
		while (!isValid(portNumber, port_PATTERN)) {
			System.out.print("The port number " + portNumber + " isn't valid \n");
			System.out.println("Enter the correct port number of the machine running the server:");
			portNumber = new Scanner(System.in).nextLine();
		}

		if (isValid(portNumber, port_PATTERN)) {
			System.out.print("The IP address " + portNumber + " is valid \n");
		}

		System.out.println("The server is running.");
		try (ServerSocket listener = new ServerSocket(Integer.parseInt(portNumber), 10,
				InetAddress.getByName(serverAddress))) {
			while (true) {
				new Capitalizer(listener.accept(), clientNumber++).start();
			}
		}
	}

	private static class Capitalizer extends Thread {
		private Socket socket;
		private int clientNumber;

		private Capitalizer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New client #" + clientNumber + " connected at " + socket);
		}

		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				out.println("Hello, you are client #" + clientNumber);

				while (true) {
					String input = in.readLine();
					if (input == null || input.isEmpty()) {
						break;
					}
					out.println(input.toUpperCase());
				}
			} catch (IOException e) {
				System.out.println("Error handling client #" + clientNumber);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
				System.out.println("Connection with client # " + clientNumber + " closed");
			}
		}
	}
}