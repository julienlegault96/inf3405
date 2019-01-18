import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
	
	public static void main(String[] args) throws Exception {
		System.out.println("Le serveur roule");
		int clientNbr = 0;
		try(var listener = new ServerSocket(5000)){
			while(true) {
				new Capitalizer(listener.accept(), clientNbr++).start();
			}
		}
	}
	
	private static class Capitalizer extends Thread{
		private Socket socket;
		private int clientNbr;
		
		public Capitalizer(Socket socket, int clientNbr){
			this.socket = socket;
			this.clientNbr = clientNbr;
			System.out.println("New client #" + clientNbr + " connected at " + socket);			
		}
		
	
	public void run() {
		try {
			var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			var out = new PrintWriter(socket.getOutputStream(), true);
			
			out.println("Hello, you are client #" + clientNbr);
			while(true) {
				var input = in.readLine();
				if(input == null || input.isEmpty()) {
					break;
				}
				out.println(input.toUpperCase());
			}
		}catch(IOException e) {
			System.out.println("Error handling client #" + clientNbr);
		}finally {
			try {socket.close();} catch(IOException e) {}
			 System.out.println("Connection with client # " + clientNbr + " closed");
			}
		}
	}
}
