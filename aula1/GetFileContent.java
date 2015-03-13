package aula1;

import java.rmi.* ;

public class GetFileContent {
    
    public static void main(String[] args) {
        if( args.length != 3) {
        	System.out.println( "Use: java GetFileContent server_host path name");
        	System.exit(0);
        }
        String serverHost = args[0];
        String path = args[1];
        String name = args[2];
    	
		try {
			IFileServer server = (IFileServer) Naming.lookup("//" + serverHost + "/myFileServer");

			FileContent info = server.getFileContent(path, name);
	        System.out.println( info) ;
		} catch( Exception e) {
			System.err.println( "Erro: " + e.getMessage());
		}
    }
}
