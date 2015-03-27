package aula3.clt;

import aula3.clt.ws.*;

import java.net.URL;
import java.util.*;

import javax.xml.namespace.QName;

public class GetDirList {
    
    public static void main(String[] args) {
        if( args.length != 2) {
        	System.out.println( "Use: java GetDirList server_host path");
        	System.exit(0);
        }
        String serverHost = args[0];
        String path = args[1];
    	
		try {
			// Sem parametros liga automaticamente ao servidor no qual foi feito o 
			// wsimport
//			FileServerImplWSService service = new FileServerImplWSService();
			FileServerImplWSService service = new FileServerImplWSService( new URL( "http://" + serverHost + "/FileServer?wsdl"), new QName("http://srv.aula3/", "FileServerImplWSService"));
			FileServerImplWS server = service.getFileServerImplWSPort();

			try {
				List<String> files = server.dir( path);
				for( int i = 0; i < files.size(); i++)
					System.out.println( files.get(i));
			} catch( InfoNotFoundException_Exception e) {
				System.err.println( e.getMessage());
			}
		} catch( Exception e) {
			e.printStackTrace();
			System.err.println( "Erro: " + e.getMessage());
		}
    }
}
