import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Authentification {
	public static void authentification() {
		int attempt = 1, comparaison;
		final int MAX_ATTEMPT = 3;
		final int VALID = 0;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter your user name:");
		String FileName = input.nextLine();

		try {
			File f = new File("../ClientInformations/" + FileName + ".txt");
			if (f.exists()) {
				List<String> Pw = readFile("../ClientInformations/" + FileName + ".txt");
				String Pword = String.join(", ", Pw);
				System.out.println("Enter your password:");
				String UserPword = input.nextLine();
				comparaison = Pword.compareTo(UserPword);
				while (attempt < MAX_ATTEMPT && comparaison != VALID) {
					attempt++;
					System.out.println("Enter a valid password:");
					UserPword = input.nextLine();
					comparaison = Pword.compareTo(UserPword);
				}

				if (attempt < MAX_ATTEMPT && comparaison == VALID) {
					System.out.println("Welcome " + FileName);
				} else {
					System.out.println("3 failed authentication attempts, the application will terminate !");
					// TODO : close socket
				}

			}

			else { 
				System.out.println(
					"Your user name doesn't exist, would you like to use it to create your acount? Yes/No:");
				String answer = input.nextLine();
				if ( answer.equals("Yes") || answer.equals("yes") ||  answer.equals("y") || answer.equals("Y") || answer.equals("YES") ) { 
					System.out.println("Enter a new password:");
					String PS = input.nextLine();
					System.out.println("Confirm again your password:");
					String PS2 = input.nextLine();
					comparaison = PS.compareTo(PS2);
					if (comparaison == VALID) {
						f.createNewFile();
						writeToFile(PS, "../ClientInformations/" + FileName + ".txt");
						System.out.println("Your profile has been created " + FileName + " !");
						createDirectory(FileName);
					} 
					else {
						System.out.println("incompatible password");
					}	
				}

				else if( answer.equals("No") || answer.equals("no") ||  answer.equals("n") || answer.equals("N") || answer.equals("NO")) {
					System.out.println("The application is terminating ...");
				}
				else {
					System.out.println("Invalid answer ! The application is terminating...");
				}
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void createDirectory(String username) {
		File directory;

		directory = new File("../database/" + username).getAbsoluteFile();
		if (directory.exists() || directory.mkdirs()) {
			System.setProperty(username, directory.getAbsolutePath());
		}
	}

	// Fonction permettant de lire un fichier et de stocker son contenu dans une
	// liste (pris des fichier fournis par la chargee de lab
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

	// Fonction permettant d'�crire dans un fichier (fournis par la chargee de lab)
	private static void writeToFile(String myStack, String nomFichier) throws IOException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(nomFichier));
			out.write(myStack + "\n");
		} finally {
			out.close();
		}
	}

}