
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;


public class Commands {
	static ArrayList<String> messageToClient = new ArrayList<String>();
		public static void list(File path)
	    {
	        if (path.isDirectory())
	        {
	        	
	            File[] list = path.listFiles();
	            if (list != null)
	            {
	                for (int i = 0; i < list.length; i++)
	                {
	                	if (list[i].isDirectory()) {
	                    System.out.println("[Folder] " + list[i].getName());
	                	}
	                	else { 
	                	System.out.println("[File] " + list[i].getName()); 	
	                	} 
	                }
	            }
	            else
	            {
	                System.err.println("Error !");
	            } 
	        }   
	    }
		
		//le fileName doit contenir l'xrtention du fichier
		public static void deleteFile(String username, String fileName, ObjectOutputStream out) throws IOException 
	    {   // là il faut rajouter chez toi les .. avant !
			File tempFile= new File("../database/" + username + "/" + fileName);
			if(tempFile.delete())
				messageToClient.add("The file " + fileName + " has been deleted !");
				
			else
				messageToClient.add("The file " + fileName + " doesn't exist or can't be deleted !"); 
			out.writeObject(messageToClient);
			out.flush();
	    }
		
		

		public static void copyFile(File f1, File f2) throws IOException {
			FileInputStream finput= null;
			FileOutputStream foutput= null;
			try {
				finput = new FileInputStream(f1);
	            foutput = new FileOutputStream(f2);
	            byte buffer[]=new byte[512*1024];
                int n;
                while((n=finput.read(buffer))!=-1){
                	foutput.write(buffer,0,n);
            	}
                }
			finally {
				finput.close();
				foutput.close();
			}
            }

		public static void downUpload(String username, String fileName, String operation, ObjectOutputStream out) throws IOException{
			
	        File local_File = new File("./" + fileName ); // le fileName doit contenir l'extention du fichier 
	        File server_File = new File("../database/" + username + "/" + fileName);
			
			
	        if((operation.equals("download") && !server_File.exists()) || (operation.equals("upload") && !local_File.exists()))
	        	messageToClient.add("The file " + "'"+ fileName +"'" + " doesn't exist !");
	        else if((operation.equals("download") && local_File.exists()) || (operation.equals("upload") && server_File.exists()))
	        	messageToClient.add("The file " + "'"+ fileName +"'" + " already exists !");
	        
	        else if(operation.equals("download")){
	        		copyFile(server_File,local_File);
	        		messageToClient.add("The file " + "'"+ fileName +"'" + " has been downloaded successfully !");
	        }
	        
			else if(operation.equals("upload")) {
					copyFile(local_File,server_File);
					messageToClient.add("The file " + "'"+ fileName +"'" + " has been uploaded successfully !");
			}
	        out.writeObject(messageToClient);
			out.flush();
	        }     	
		

		
	public static void main(String[] args) throws Exception {
		
	}
}