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
			ClientService clientService = new ClientService();

			System.out.println("Enter the IP address of a machine running the capitalize server:");
			String tempAddress = scanner.nextLine();
			String serverAddress = clientService.validateIPaddress(tempAddress);

			System.out.println("Enter the port number of a machine running the server:");
			String tempPortNumber = scanner.nextLine();
			String portNumber = clientService.validatePortNumber(tempPortNumber);

			socket = new Socket(serverAddress, Integer.parseInt(portNumber));

			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.flush();
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

			initialRequest(scanner, in, out);
			while (true) {
				request(socket, scanner, in, out);
			}

		} finally {
			socket.close();
			scanner.close();

		}
	}

	private static void initialRequest(Scanner scanner, ObjectInputStream in, ObjectOutputStream out)
			throws ClassNotFoundException, IOException {
		System.out.println("Enter your username :");
		username = scanner.nextLine();
		String type = "username";
		ClientService.sendInput(username, type, out, in);
	}

	private static void request(Socket socket, Scanner scanner, ObjectInputStream in, ObjectOutputStream out)
			throws Exception {
		ArrayList<String> responseFromServer = (ArrayList<String>) in.readObject();
		String type;
		switch (responseFromServer.get(0)) {
		case "newUsername":
			System.out.println("It is your first connection, please enter your password : ");
			password = scanner.nextLine();
			type = "newPassword";
			ClientService.validateUser(password, username, type, out, in);
			break;

		case "Password":
			System.out.println("Hello " + username + " please enter your password : ");
			password = scanner.nextLine();
			type = "Password";
			ClientService.validateUser(password, username, type, out, in);
			break;

		case "GoodPassword":
			System.out.println("welcome " + username + " to your personal storage space!");
			break;

		case "BadPassword":
			System.out.println("Wrong password! Insert a valid password : ");
			password = scanner.nextLine();
			type = "Password";
			ClientService.validateUser(password, username, type, out, in);
			break;
		case "TooManyBadPasswords":
			System.out.println("3 failed authentication attempts, the application will terminate !");
			socket.close();
			break;
		case "DirectoryCreated":
			System.out.println("Congratulations " + username + " your own storage space has been created!");
			break;

		default:
			break;
		}

	}
}