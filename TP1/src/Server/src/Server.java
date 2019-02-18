package Server.src;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Server {

	public static void main(String[] args) throws Exception {

		ServerService serverService = new ServerService();
		int clientNumber = 0;

		System.out.print("Enter the IP address:");
		// String tempAddress = new Scanner(System.in).nextLine();
		String tempAddress = "127.0.0.1";
		String serverAddress = serverService.validateIPaddress(tempAddress);

		System.out.print("Enter the port number:");
		// String tempPortNumber = new Scanner(System.in).nextLine();
		String tempPortNumber = "5000";
		String portNumber = serverService.validatePortNumber(tempPortNumber);

		System.out.println("The server is running.");
		try (ServerSocket listener = new ServerSocket(Integer.parseInt(portNumber), 10,
				InetAddress.getByName(serverAddress))) {
			while (true) {
				new Connector(listener.accept(), clientNumber++).start();
			}
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

		private void request(ObjectInputStream in, ObjectOutputStream out) throws Exception {
			ArrayList<String> responseFromClient = (ArrayList<String>) in.readObject();

			switch (responseFromClient.get(0)) {
			case "username":
				ServerService.checkUsername(responseFromClient.get(1), out);
				break;
			case "newPassword":
				ServerService.createUser(responseFromClient.get(1), responseFromClient.get(2), out);
				break;
			case "Password":
				ServerService.validatePassword(responseFromClient.get(1), responseFromClient.get(2),responseFromClient.get(3), out);
				break;
			case "commands":
				ServerService.validateCommand(responseFromClient.get(1), responseFromClient.get(2), 
						responseFromClient.get(3), out);
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
					request(in, out);
				}
			} catch (Exception e) {
				e.printStackTrace();
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