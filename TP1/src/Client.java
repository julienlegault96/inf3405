import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        System.out.println("Enter the IP address of a machine running the capitalize server:");
        var serverAddress = new Scanner(System.in).nextLine();
        var socket = new Socket(serverAddress, 5000);

        // Streams for conversing with server
        var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var out = new PrintWriter(socket.getOutputStream(), true);

        // Consume and display welcome message from the server
        System.out.println(in.readLine());

        var scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nEnter a string to send to the server (empty to quit):");
            var message = scanner.nextLine();
            if (message == null || message.isEmpty()) {
                break;
            }
            out.println(message);
            System.out.println(in.readLine());
        }
    }
}
