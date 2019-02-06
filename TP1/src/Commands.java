
import java.io.File;


public class Commands {
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

	public static void main(String[] args) throws Exception {
		list(new File("C:\\Users\\User\\eclipse-workspace\\TP1ClientServer"));
	}
}