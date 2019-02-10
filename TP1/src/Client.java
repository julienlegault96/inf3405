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
	

	public static void main(String[] args) throws Exception {
		ClientService clientService = new ClientService(); 

		System.out.println("Enter the IP address of a machine running the capitalize server:");
		String tempAddress = new Scanner(System.in).nextLine();
		String serverAddress = clientService.validateIPaddress(tempAddress);

		System.out.println("Enter the port number of a machine running the server:");
		String tempPortNumber = new Scanner(System.in).nextLine();
		String portNumber = clientService.validatePortNumber(tempPortNumber);

		Socket socket = null;
		socket = new Socket(serverAddress, Integer.parseInt(portNumber));
		
		
		
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        
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