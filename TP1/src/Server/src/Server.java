package Server.src;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Server {

	public static void main(String[] args) throws Exception {

		int clientNumber = 0;
		Scanner scanner= null;
		try {
			scanner = new Scanner(System.in);
		System.out.print("Enter the IP address:");
		String tempAddress = scanner.nextLine();
		//String tempAddress = "127.0.0.1";
		String serverAddress = ServerService.validateIPaddress(tempAddress);

		System.out.print("Enter the port number:");
		String tempPortNumber = scanner.nextLine();
		//String tempPortNumber = "5000";
		String portNumber = ServerService.validatePortNumber(tempPortNumber);

		System.out.println("The server is running.");
		try (ServerSocket listener = new ServerSocket(Integer.parseInt(portNumber), 10,
				InetAddress.getByName(serverAddress))) {
			while (true) {
				new Connector(listener.accept(), clientNumber++).start();
			}
		}
		
		} finally {
			scanner.close();
		}
	}

	private static class Connector extends Thread {
		private Socket socket;
		private int clientNumber;

		private Connector(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New client #" + clientNumber + " connected at " + socket);
		}

		private void request(Socket socket, ObjectInputStream in, ObjectOutputStream out) throws Exception {
			ArrayList<String> responseFromClient = (ArrayList<String>) in.readObject();

			switch (responseFromClient.get(0)) {
			case "username":
				ServerService.checkUsername(responseFromClient.get(1), out);
				break;
			case "newPassword":
				ServerService.createUser(responseFromClient.get(1), responseFromClient.get(2), out);
				break;
			case "Password":
				ServerService.validatePassword(responseFromClient.get(1), responseFromClient.get(2),responseFromClient.get(3), socket, out);
				break;
			case "commands":
				ServerService.validateCommand(responseFromClient.get(1), responseFromClient.get(2), 
						responseFromClient.get(3), socket, out);
				break;
			default:
				break;

			}

		}

		public void run() {
			try {
				ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				out.flush();
				while (true) {
					request(socket, in, out);
				}
			} catch (Exception e) {} 
				finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
				System.out.println("Connection with client # " + clientNumber + " closed");
			}
		}
	}
}