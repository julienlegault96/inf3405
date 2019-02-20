package Client.src;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	private static String username = null;
	private static String password;

	public static void main(String[] args) throws Exception {
		Socket socket = null;
		Scanner scanner = null;
		try {
			scanner = new Scanner(System.in);

			System.out.print("Enter the IP address of a machine running the server: ");
			String tempAddress = scanner.nextLine();
			String serverAddress = ClientService.validateIPaddress(tempAddress, scanner);

			System.out.print("Enter the port number of a machine running the server: ");
			String tempPortNumber = scanner.nextLine();
			String portNumber = ClientService.validatePortNumber(tempPortNumber, scanner);

			socket = new Socket(serverAddress, Integer.parseInt(portNumber));

		try {
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.flush();
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

			initialRequest(scanner, out);
			while (true) {
				request(socket, scanner, in, out);
			}
		}
		catch (Exception e) {}
		} finally {
			scanner.close();
		}
	}

	/* Utilisation d une requete initiale pour eviter l interblocage du a la creation 
	*  simultanee des deux buffers de lecture 
	*/	
	private static void initialRequest(Scanner scanner, ObjectOutputStream out)
			throws ClassNotFoundException, IOException {
		System.out.print("Enter your username : ");
		username = scanner.nextLine();
		String type = "username";
		ClientService.sendInitialRequest(username, type, out);
	}

	private static void request(Socket socket, Scanner scanner, ObjectInputStream in, ObjectOutputStream out)
			throws Exception {
		ArrayList<String> responseFromServer = (ArrayList<String>) in.readObject();
		String type;
		switch (responseFromServer.get(0)) {
		case "newUsername":
			System.out.print("It is your first connection, please enter your password : ");
			password = scanner.nextLine();
			type = "newPassword";
			ClientService.validateUser(password, username, "1", type, out);
			break;
			
		case "Password":
			System.out.print("Hello " + username + " please enter your password : ");
			password = scanner.nextLine();
			type = "Password";
			ClientService.validateUser(password, username, "1", type, out);
			break;
			
		case "GoodPassword":
			System.out.println("welcome " + username + " to your personal storage space!");
			ClientService.enterCommands(username, scanner, out);
			break;
			
		case "BadPassword":
			System.out.print("Wrong password! Insert a valid password : ");
			password = scanner.nextLine();
			type = "Password";
			ClientService.validateUser(password, username, responseFromServer.get(1), type, out);
			break;			
		case "TooManyBadPasswords":
			System.out.println("3 failed authentication attempts, the application will terminate!");
			break;
			
		case "disconnect User":
			System.out.println(">> exit");
			System.out.println("User " + username + " was disconnected properly");
			break;			
		case "DirectoryCreated":
			System.out.println("Congratulations " + username + " your own storage space has been created!");
			ClientService.enterCommands(username, scanner, out);
			break;
			
		case "list":
			System.out.println(">> ls");
			ClientService.listing(responseFromServer);
			ClientService.enterCommands(username, scanner, out);
			break;
			
		case "downloadFile":
			System.out.println(">> download " + responseFromServer.get(1));
			System.out.println("The file " + responseFromServer.get(1) + " has been downloaded!");
			ClientService.enterCommands(username, scanner, out);
			break;
			
		case "uploadFile":
			System.out.println(">> upload " + responseFromServer.get(1));
			System.out.println("The file " + responseFromServer.get(1) + " has been uploaded!");
			ClientService.enterCommands(username, scanner, out);
			break;
			
		case "deletedFile":
			System.out.println(">> delete " + responseFromServer.get(1));
			System.out.println("The file " + responseFromServer.get(1) + " has been deleted!");
			ClientService.enterCommands(username, scanner, out);
			break;
			
		case "cantDeleteFile":
			System.out.println(">> delete " + responseFromServer.get(1));
			System.out.println("The file " + responseFromServer.get(1) + " cant be deleted because it doesn't exist.");
			ClientService.enterCommands(username, scanner, out);	
			break;
			
		case "invalidComand":
			System.out.println("Command " + responseFromServer.get(1) + " doesn't exist, enter a valid command!");
			ClientService.enterCommands(username, scanner, out);
			break;
			
		case "Doesnt exist":
			System.out.println("File " + responseFromServer.get(1) + " can't be " + responseFromServer.get(2) +"ed because it doesn't exist.");
			ClientService.enterCommands(username, scanner, out);
			break;
			
		case "already exist":
			System.out.println("File " + responseFromServer.get(1) + " can't be " + responseFromServer.get(2) +"ed because it already exists.");
			ClientService.enterCommands(username, scanner, out);
			break;
			
		default:
			break;
		}

	}
}