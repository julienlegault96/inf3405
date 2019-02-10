import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server {
	

	public static void main(String[] args) throws Exception {		
		
		ServerService serverService = new ServerService(); 
		int clientNumber = 0;
		
		System.out.println("Enter the IP address:");
		String tempAddress = new Scanner(System.in).nextLine();
		String serverAddress = serverService.validateIPaddress(tempAddress);

		System.out.println("Enter the port number:");
		String tempPortNumber = new Scanner(System.in).nextLine();
		String portNumber = serverService.validatePortNumber(tempPortNumber);

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
		
		private void request() throws Exception {	
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.flush();
			System.out.println("Debug1");
			ArrayList<String> messageFromClient  = (ArrayList<String>) in.readObject();
			ArrayList<String> responseToClient = new ArrayList<String>();
			System.out.println("Debug2");
			// Client insert username
			switch(messageFromClient.get(0)) {
			case "username":
				File f = new File("../ClientInformations/" + messageFromClient.get(1) + ".txt");
				if (f.exists()) {
					List<String> Pw = readFile("../ClientInformations/" + messageFromClient.get(1) + ".txt");
					String Pword = String.join(", ", Pw);
					responseToClient.add("password");						
					out.writeObject(responseToClient);
				}
				else {	
					responseToClient.add("newUsername");
					System.out.println(responseToClient);
					out.writeObject(responseToClient);
				}
				break;			
			}
		}
//						
//						System.out.println("Enter your password:");
//						String UserPword = input.nextLine();
//						comparaison = Pword.compareTo(UserPword);
//						while (attempt < MAX_ATTEMPT && comparaison != VALID) {
//							attempt++;
//							System.out.println("Enter a valid password:");
//							UserPword = input.nextLine();
//							comparaison = Pword.compareTo(UserPword);
//						}
//
//						if (attempt < MAX_ATTEMPT && comparaison == VALID) {
//							System.out.println("Welcome " + FileName);
//						} else {
//							System.out.println("3 failed authentication attempts, the application will terminate !");
//							// TODO : close socket
//						}
//
//					}
//
//					else { 
//						System.out.println(
//							"Your user name doesn't exist, would you like to use it to create your acount? Yes/No:");
//						String answer = input.nextLine();
//						if ( answer.equals("Yes") || answer.equals("yes") ||  answer.equals("y") || answer.equals("Y") || answer.equals("YES") ) { 
//							System.out.println("Enter a new password:");
//							String PS = input.nextLine();
//							System.out.println("Confirm again your password:");
//							String PS2 = input.nextLine();
//							comparaison = PS.compareTo(PS2);
//							if (comparaison == VALID) {
//								f.createNewFile();
//								writeToFile(PS, "../ClientInformations/" + FileName + ".txt");
//								System.out.println("Your profile has been created " + FileName + " !");
//								createDirectory(FileName);
//							} 
//							else {
//								System.out.println("incompatible password");
//							}	
//						}
//
//						else if( answer.equals("No") || answer.equals("no") ||  answer.equals("n") || answer.equals("N") || answer.equals("NO")) {
//							System.out.println("The application is terminating ...");
//						}
//						else {
//							System.out.println("Invalid answer ! The application is terminating...");
//						}
//					}
//				}	
//				
//
//			}
//				/*if (message.get(1).equals("Julien")) {
//					response.add("registered");
//					out.writeObject(response);
//				}*/
//		   else {
//					response.add("unregistered");
//					out.writeObject(response);
//				}
//		}

		public void run() {
			try {

				while (true) {
					request();
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