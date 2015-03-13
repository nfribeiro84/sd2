package aula1;

import java.rmi.* ;

public class GetDirList {
    
    public static void main(String[] args) {
        if( args.length != 2) {
        	System.out.println( "Use: java GetDirList server_host path");
        	System.exit(0);
        }
        String serverHost = args[0];
        String path = args[1];
    	
		try {
			IFileServer server = (IFileServer) Naming.lookup("//" + serverHost + "/myFileServer");

			try {
				String files[] = server.dir( path);
				for( int i = 0; i < files.length; i++)
					System.out.println( files[i]);
			} catch( InfoNotFoundException e) {
				System.err.println( e.getMessage());
			}
		} catch( Exception e) {
			System.err.println( "Erro: " + e.getMessage());
		}
    }
}
