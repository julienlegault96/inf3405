import java.io.BufferedReader;
//import java.io.IOException;
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
	private static final String port_REGEX ="^(50[0-4][0-9]|5050)$";
	
	private static final Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);	
	
	private static final Pattern port_PATTERN = Pattern.compile(port_REGEX);

	public static String portNbr;
	
	public static boolean isValid(String number, Pattern pattern) {
		if (number == null) {
			return false;
		}
		Matcher matcher = pattern.matcher(number);

		return matcher.matches();
	}
	
	
    public static void main(String[] args) throws Exception {
        System.out.println("Enter the IP address of a machine running the capitalize server:");
           String serverAddress =  new Scanner(System.in).nextLine();
        
     // Validate an IPv4 address         
           while(!isValid(serverAddress, IPv4_PATTERN)) {
        	   System.out.print("The IP address " + serverAddress + " isn't valid \n"); 
        	   System.out.println("Enter the correct IP address of the machine running the server:");
        	   serverAddress =  new Scanner(System.in).nextLine();       
     		}
        
     		if (isValid(serverAddress, IPv4_PATTERN)) {
     			System.out.print("The IP address " + serverAddress + " is valid \n");
     		}
     				
            System.out.println("Enter the port number of a machine running the server:");
            String portNumber =  new Scanner(System.in).nextLine();
           
            

     
     // validation PORT     	     
           while(!isValid(portNumber, port_PATTERN)) {
           	System.out.print("The port number " + portNumber + " isn't valid \n"); 
               System.out.println("Enter the correct port number of the machine running the server:");
               portNumber =  new Scanner(System.in).nextLine();          
           }
            
        		if (isValid(portNumber, port_PATTERN)) {
        			System.out.print("The IP address " + portNumber + " is valid \n");
        		}
        
        portNbr = portNumber;
        
       Socket socket = null;
       socket = new Socket(serverAddress, Integer.parseInt(portNbr));

        // Streams for conversing with server
        BufferedReader in = null;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = null;
         out = new PrintWriter(socket.getOutputStream(), true);

         socket.close();
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
        scanner.close();
    }
}