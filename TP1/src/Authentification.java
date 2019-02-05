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
   public static void main(String[] args) {      
      //File f = null;
      int n=0,comparaison;
      Scanner clavier = new Scanner(System.in); 
      System.out.println("Enter your user name:");
      String FileName =  clavier.nextLine();
      
      try {
         // create logic file
        // f = new File(FileName+".txt");
  		File f = new File("C:\\Users\\User\\eclipse-workspace\\TP1ClientServer\\database\\"+FileName+".txt");          
         // tests if file exists
         if(f.exists())
             {
        	 // lire le mot de passe à partir du fichier du client
 			 List<String> Pw = readFile("C:\\Users\\User\\eclipse-workspace\\TP1ClientServer\\database\\"+FileName+".txt");
 			 //conversion list vers string
 			 String Pword = String.join(", ", Pw); 
 			 // lire le mot de passe entré au clavier par le client
 		     System.out.println("Enter your password:");
 		     String UserPword =  clavier.nextLine();
 		     //comparaison des deux mot de passes: retourne 0 si identiques
 		     comparaison = 	Pword.compareTo(UserPword);
 		     // 3 chances au maximum pour que l'utilisateur introduise un mot de passe valide
 		     while(n<2 && comparaison!=0) {
 		    	 n++;
 		    	System.out.println("Enter a valid password:");
 		    	UserPword =  clavier.nextLine();
 		    	comparaison = 	Pword.compareTo(UserPword);
 		     }
 		     
 		     if(n<2 && comparaison==0) //mot de passe valide
 		    	System.out.println("Welcome "+FileName);
 	 		 if(n>=2 && comparaison!=0) 
 	 		    System.out.println("3 failed authentication attempts, the application will terminate !");
 		    	
 		     } 
 		      
         else { // il n'existe pas de fichier portant le num d'utilisateur introduit
             System.out.println("Your user name doesn't exist, would you like to use it to create your acount? Yes/No:");
             String answer =  clavier.nextLine();
             comparaison = "Yes".compareTo(answer);
             
             if(comparaison==0) { // création d'un compte pour l'utilisateur
                 // création du fichier 
                 f.createNewFile();
                 System.out.println("Enter a new passeword:");
                 String PS =  clavier.nextLine();
                 System.out.println("Confirm again your passeword:");
                 String PS2 =  clavier.nextLine();
                 comparaison = PS.compareTo(PS2);
                 if(comparaison==0) {
                 // écrire le mot de passe dans le fichier et dire compte crée avec succès
                 writeToFile(PS, "C:\\Users\\User\\eclipse-workspace\\TP1ClientServer\\database\\"+FileName+".txt");
                 System.out.println("Your profile has been created "+FileName+" !");
                 }
                 else
                 System.out.println("incompatible passwords");
             } 
             
             else if(answer=="No") {
            	 System.out.println("The application is terminating ...");
             }
             else 
                  System.out.println("Invalid answer ! The application is terminating...");

              } 
      } // try
      catch(Exception e) {
         // if any error occurs
         e.printStackTrace();
      }
   
   } // main
   
	// Fonction permettant de lire un fichier et de stocker son contenu dans une liste (pris des fichier fournis par la chargée de lab
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
	// Fonction permettant d'écrire dans un fichier (fournis par la chargée de lab)
		private static void writeToFile(String myStack, String nomFichier) throws IOException {
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new FileWriter(nomFichier));
				//while (!myStack.isEmpty()) {
					out.write(myStack + "\n");
				//}
			} finally {
				out.close();
			}
		}

}