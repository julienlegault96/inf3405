import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server program which accepts requests from clients to capitalize strings.
 * When clients connect, a new thread is started to handle an interactive dialog
 * in which the client sends in a string and the server thread sends back the
 * capitalized version of the string.
 *
 * The program is runs in an infinite loop, so shutdown in platform dependent.
 * If you ran it from a console window with the "java" interpreter, Ctrl+C will
 * shut it down.
 */

public class Server {
	
    public static void main(String[] args) throws Exception {
        System.out.println("The capitalization server is running.");
        int clientNumber = 0;
        try (ServerSocket listener = new ServerSocket(Integer.parseInt(Client.portNbr))) {
            while (true) {
                new Capitalizer(listener.accept(), clientNumber++).start();
            }
        }
    }

    /**
     * A private thread to handle capitalization requests on a particular socket.
     */
    private static class Capitalizer extends Thread {
        private Socket socket;
        private int clientNumber;

        public Capitalizer(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            System.out.println("New client #" + clientNumber + " connected at " + socket);
        }

        /**
         * Services this thread's client by first sending the client a welcome
         * message then repeatedly reading strings and sending back the capitalized
         * version of the string.
         */
        public void run() {
            try {
            	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber);

                // Get messages from the client, line by line; return them capitalized
                while (true) {
                    String input = in.readLine();
                    if (input == null || input.isEmpty()) {
                        break;
                    }
                    out.println(input.toUpperCase());
                }
            } catch (IOException e) {
                System.out.println("Error handling client #" + clientNumber);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Connection with client # " + clientNumber + " closed");
            }
        }
    }
}