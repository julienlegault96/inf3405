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
	}
}