package Server.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.GregorianCalendar;

public class ServerService {
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
			System.out.print("Enter the correct IP address of the machine running the server: ");
			tempAddress = new Scanner(System.in).nextLine();
		}

		if (isValid(tempAddress, IPv4_PATTERN)) {
			serverAddress = tempAddress;
			System.out.println("The IP address " + serverAddress + " is valid \n");
		}
		return serverAddress;
	}

	public static String validatePortNumber(String tempPortNumber) {
		while (!isValid(tempPortNumber, port_PATTERN)) {
			System.out.println("The port number " + tempPortNumber + " isn't valid \n");
			System.out.print("Enter the correct port number of the machine running the server: ");
			tempPortNumber = new Scanner(System.in).nextLine();
		}

		if (isValid(tempPortNumber, port_PATTERN)) {
			portNumber = tempPortNumber;
			System.out.println("The IP address " + portNumber + " is valid \n");
		}
		return portNumber;
	}



	public static void checkUsername(String username, ObjectOutputStream out) throws IOException {
		File f = new File("src/Server/ClientInformations/" + username + ".txt");
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

		directory = new File("src/Server/database/" + username);
		if (directory.exists() || directory.mkdirs()) {
			System.setProperty(username, directory.getAbsolutePath());
		}
	}

	public static void createUser(String username, String newPassword, ObjectOutputStream out) throws IOException {
		File f = new File("src/Server/ClientInformations/" + username + ".txt");
		f.createNewFile();
		writeToFile(newPassword, "src/Server/ClientInformations/" + username + ".txt");
		createDirectory(username);
		ArrayList<String> messageToClient = new ArrayList<String>();
		messageToClient.add("DirectoryCreated");
		out.writeObject(messageToClient);
		out.flush();
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

	public static void validatePassword(String username, String passwordInserted, String attempt, Socket socket, ObjectOutputStream out)
			throws IOException {
		
		Integer attemptNbr = Integer.valueOf(attempt);		
		List<String> Pw = readFile("src/Server/ClientInformations/" + username + ".txt");
		String Password = String.join(", ", Pw);
		ArrayList<String> messageToClient = new ArrayList<String>();
		if (passwordInserted.equals(Password)) {
			messageToClient.add("GoodPassword");
			out.writeObject(messageToClient);
			out.flush();
		} else if (attemptNbr < MAX_ATTEMPT && !passwordInserted.equals(Password)) {
			attemptNbr++;
			attempt = Integer.toString(attemptNbr);			
			messageToClient.add("BadPassword");
			messageToClient.add(attempt);
			out.writeObject(messageToClient);
			out.flush();
		} else {
			messageToClient.add("TooManyBadPasswords");
			out.writeObject(messageToClient);
			out.flush();
			try {socket.close();} catch(IOException e) {}
		}
	}

	public static void validateCommand(String username, String input, Socket socket, ObjectOutputStream out)
			throws IOException {
		String parts[] = input.split(" ", 2);
		String file = " ";
		String command = parts[0];
		switch (command) {
		case "ls":
			System.out.println(
					"[" + serverAddress + ":" + portNumber + " - " + new GregorianCalendar().getTime() + "]:" + " ls");
			list(new File("src/Server/database/" + username), out);
			break;

		case "upload":
			file = parts[1];
			System.out.println("[" + serverAddress + ":" + portNumber + " - " + new GregorianCalendar().getTime() + "]:"
					+ " upload " + file);
			load(username, file, "upload", out);
			break;

		case "download":
			file = parts[1];
			System.out.println("[" + serverAddress + ":" + portNumber + " - " + new GregorianCalendar().getTime() + "]:"
					+ " download " + file);
			load(username, file, "download", out);
			break;

		case "delete":
			file = parts[1];
			System.out.println("[" + serverAddress + ":" + portNumber + " - " + new GregorianCalendar().getTime() + "]:"
					+ " delete " + file);
			deleteFile(username, file, out);
			break;

		case "exit":
			System.out.println("[" + serverAddress + ":" + portNumber + " - " + new GregorianCalendar().getTime() + "]:"
					+ " exit");
			disconnectUser(socket, out);
			break;

		default:
			System.out.println("[" + serverAddress + ":" + portNumber + " - " + new GregorianCalendar().getTime() + "]:"
					+ " invalid command");
			invalidComand(command, out);
			break;
		}
	}

	private static void list(File path, ObjectOutputStream out) throws IOException {
		if (path.isDirectory()) {

			File[] list = path.listFiles();
			ArrayList<String> messageToClient = new ArrayList<String>();
			messageToClient.add("list");
			if (list.length != 0) {
				for (int i = 0; i < list.length; i++) {
					if (list[i].isDirectory()) {
						messageToClient.add("[Folder] " + list[i].getName());
					} else {
						messageToClient.add("[File] " + list[i].getName());
					}

				}

			} else {
				messageToClient.add("Your storage space seem to be empty =( ");
			}
			out.writeObject(messageToClient);
			out.flush();
		}

	}

	private static void disconnectUser(Socket socket, ObjectOutputStream out) throws IOException {
		ArrayList<String> messageToClient = new ArrayList<String>();
		messageToClient.add("disconnect User");
		out.writeObject(messageToClient);
		out.flush();
		try {socket.close();} catch(IOException e) {}
	}

	private static void invalidComand(String command, ObjectOutputStream out) throws IOException {
		ArrayList<String> messageToClient = new ArrayList<String>();
		messageToClient.add("invalidComand");
		messageToClient.add(command);
		out.writeObject(messageToClient);
		out.flush();
	}
	
	private static void deleteFile(String username, String fileName, ObjectOutputStream out) throws IOException { 
		File tempFile = new File("src/Server/database/" + username + "/" + fileName);
		ArrayList<String> messageToClient = new ArrayList<String>();
		if (tempFile.delete()) {
			messageToClient.add("deletedFile");
			messageToClient.add(fileName);
		}

		else {
			messageToClient.add("cantDeleteFile");
			messageToClient.add(fileName);
		}
		out.writeObject(messageToClient);
		out.flush();
	}

	private static void copyFile(File f1, File f2) throws IOException {
		FileInputStream finput = null;
		FileOutputStream foutput = null;
		try {
			finput = new FileInputStream(f1);
			foutput = new FileOutputStream(f2);
			byte buffer[] = new byte[512 * 1024];
			int n;
			while ((n = finput.read(buffer)) != -1) {
				foutput.write(buffer, 0, n);
			}
		} finally {
			finput.close();
			foutput.close();
		}
	}

	private static void load(String username, String fileName, String operation, ObjectOutputStream out)
			throws IOException {

		File local_File = new File("./" + fileName);
		File server_File = new File("src/Server/database/" + username + "/" + fileName);
		ArrayList<String> messageToClient = new ArrayList<String>();
		if ((operation.equals("download") && !server_File.exists())
				|| (operation.equals("upload") && !local_File.exists())) {
			messageToClient.add("Doesnt exist");
			messageToClient.add(fileName);
			messageToClient.add(operation);
		} else if ((operation.equals("download") && local_File.exists())
				|| (operation.equals("upload") && server_File.exists())) {
			messageToClient.add("already exist");
			messageToClient.add(fileName);
			messageToClient.add(operation);
		} else if (operation.equals("download")) {
			copyFile(server_File, local_File);
			messageToClient.add("downloadFile");
			messageToClient.add(fileName);
		} else if (operation.equals("upload")) {
			copyFile(local_File, server_File);
			messageToClient.add("uploadFile");
			messageToClient.add(fileName);
		}
		out.writeObject(messageToClient);
		out.flush();
	}
	
}
