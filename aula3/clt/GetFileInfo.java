package aula3.clt;

import java.net.URL;

import javax.xml.namespace.QName;

import aula3.clt.ws.*;

public class GetFileInfo {
    
    public static void main(String[] args) {
        if( args.length != 2) {
        	System.out.println( "Use: java GetFileInfo server_host path");
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

			FileInfo info = server.getFileInfo(path);
	        System.out.println( "Name : " + info.getName() + "\nLength: " + info.getLength() + 
	        		"\nData modified: " + info.getModified() + "\nisFile : " + info.isIsFile()) ;
		} catch( Exception e) {
			System.err.println( "Erro: " + e.getMessage());
		}
    }
}
