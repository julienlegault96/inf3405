import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerService {
	private static int attempt = 1;
	final static int MAX_ATTEMPT = 3;
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

	// Repris du code fourni
	private static List<String> readFile(String nomFichier) throws IOException {
		List<String> listOfLines = new ArrayList<String>();
		String line = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(nomFichier);

			bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				listOfLines.add(line);
			}
		} finally {
			fileReader.close();
			bufferedReader.close();
		}
		return listOfLines;
	}

	public static void checkUsername(String username, ObjectOutputStream out) throws IOException {
		File f = new File("../ClientInformations/" + username + ".txt");
		ArrayList<String> messageToClient = new ArrayList<String>();
		if (f.exists()) {
			messageToClient.add("Password");
			out.writeObject(messageToClient);
			out.flush();
		} else {
			messageToClient.add("newUsername");
			out.writeObject(messageToClient);
			out.flush();
		}
	}

	// Repris du code fourni
	private static void writeToFile(String myStack, String nomFichier) throws IOException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(nomFichier));
			out.write(myStack + "\n");
		} finally {
			out.close();
		}
	}

	private static void createDirectory(String username) {
		File directory;

		directory = new File("../database/" + username).getAbsoluteFile();
		if (directory.exists() || directory.mkdirs()) {
			System.setProperty(username, directory.getAbsolutePath());
		}
	}

	public static void createUser(String username, String newPassword, ObjectOutputStream out) throws IOException {
		File f = new File("../ClientInformations/" + username + ".txt");
		f.createNewFile();
		writeToFile(newPassword, "../ClientInformations/" + username + ".txt");
		createDirectory(username);
		ArrayList<String> messageToClient = new ArrayList<String>();
		messageToClient.add("DirectoryCreated");
		out.writeObject(messageToClient);
		out.flush();
	}

	public static void validatePassword(String username, String passwordInserted, ObjectOutputStream out)
			throws IOException {
		List<String> Pw = readFile("../ClientInformations/" + username + ".txt");
		String Password = String.join(", ", Pw);
		ArrayList<String> messageToClient = new ArrayList<String>();
		if (passwordInserted.equals(Password)) {
			messageToClient.add("GoodPassword");
			out.writeObject(messageToClient);
			out.flush();
		} else if (attempt < MAX_ATTEMPT || passwordInserted.equals(Password)) {
			messageToClient.add("BadPassword");
			out.writeObject(messageToClient);
			out.flush();
			attempt++;
		} else {
			messageToClient.add("TooManyBadPasswords");
			out.writeObject(messageToClient);
			out.flush();
		}
	}
}
