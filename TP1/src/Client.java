import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
	
	
	// an IPv4 address
	//private static final String INET4ADDRESS = "172.8.9.28";

	private static final String IPv4_REGEX =
					"^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
					"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
					"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
					"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

	private static final Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);


	public static boolean isValidInet4Address(String ip) {
		if (ip == null) {
			return false;
		}

		Matcher matcher = IPv4_PATTERN.matcher(ip);

		return matcher.matches();
	}
	
	
	
    public static void main(String[] args) throws Exception {
        System.out.println("Enter the IP address of a machine running the capitalize server:");
        String serverAddress =  new Scanner(System.in).nextLine();
        
         String INET4ADDRESS = serverAddress;
        
     // Validate an IPv4 address 
         if (!isValidInet4Address(INET4ADDRESS)) {
        while(!isValidInet4Address(INET4ADDRESS)) {
        	System.out.print("The IP address " + INET4ADDRESS + " isn't valid \n"); 
            System.out.println("Enter the correct IP address of the machine running the capitalize server:");
            serverAddress =  new Scanner(System.in).nextLine();
            INET4ADDRESS = serverAddress;
       
        }
         }
     		if (isValidInet4Address(INET4ADDRESS)) {
     			System.out.print("The IP address " + INET4ADDRESS + " is valid \n");
     		}

        
        
        Socket socket = null;
        socket = new Socket(serverAddress, 9898);

        // Streams for conversing with server
        BufferedReader in = null;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = null;
         out = new PrintWriter(socket.getOutputStream(), true);

        // Consume and display welcome message from the server
        System.out.println(in.readLine());
        Scanner scanner = null;
        scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nEnter a string to send to the server (empty to quit):");
            String message = scanner.nextLine();
            if (message == null || message.isEmpty()) {
                break;
            }
            out.println(message);
            System.out.println(in.readLine());
            
        }
    }
}