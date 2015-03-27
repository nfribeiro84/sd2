package aula3.srv;

import java.io.*;
import java.util.Date;

import javax.jws.*;
import javax.xml.ws.Endpoint;

@WebService
public class FileServerImplWS
{
	private File basePath;

	public FileServerImplWS() {
		basePath = new File(".");
	}

	protected FileServerImplWS( String pathname) {
		super();
		basePath = new File( pathname);
	}

	@WebMethod
	public String[] dir(String path) throws InfoNotFoundException {
		File f = new File( basePath, path);
		if( f.exists())
			return f.list();
		else
			throw new InfoNotFoundException( "Directory not found :" + path);
	}

	@WebMethod
	public FileInfo getFileInfo(String path) throws InfoNotFoundException {
		File f = new File( basePath, path);
		if( f.exists())
			return new FileInfo( f.getName(), f.length(), new Date(f.lastModified()), f.isFile());
		else
			throw new InfoNotFoundException( "File not found :" + path);
	}

	public static void main( String args[]) throws Exception {
		try {
			String path = ".";
			if( args.length > 0)
				path = args[0];

			Endpoint.publish(
			         "http://localhost:8080/FileServer",
			         new FileServerImplWS( path));
			System.out.println( "FileServer started");
		} catch( Throwable th) {
			th.printStackTrace();
		}
	}


}
