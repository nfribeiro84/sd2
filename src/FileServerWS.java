
import java.io.*;
import java.util.Date;

import javax.jws.*;
import javax.xml.ws.Endpoint;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

//import ws.FileContent;

//import java.util.GregorianCalendar;
//import javax.xml.datatype.DatatypeFactory;
//import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.datatype.XMLGregorianCalendar;

@WebService(targetNamespace="http://ws.srv/")
public class FileServerWS implements Runnable
{
	private String basePathName;
	private File basePath;
	private String contactServerURL;
	private String fileServerName;
	private String protocol;
	private boolean primary;

	private int ping_interval = 3;

	public FileServerWS() {
		basePath = new File(".");
	}

	protected FileServerWS( String pathname) {
		super();
		basePath = new File( pathname);
	}

	protected FileServerWS( String pathname, String url, String name)
	{
		super();
		this.basePathName = pathname;
		basePath = new File( pathname);
		this.contactServerURL = "rmi://" + url;
		this.fileServerName = name;
		this.protocol = "http";
	}
	
	private IContactServer connectToContact() throws Exception
	{
		IContactServer contactServer = (IContactServer) Naming.lookup(this.contactServerURL);
		return contactServer;
	}
	
	private IContactServer subscribeToContact(IContactServer contactServer) throws Exception
	{
		if(contactServer.subscribe(this.fileServerName, this.protocol) != -1)
			return contactServer;
		else throw new Exception("Couldn't conecto to contact server");
	}


	
	/**
	 * 
	 */
	private String checkClientHost()
	{
		try
		{
    	return java.rmi.server.RemoteServer.getClientHost();
		}
		catch(ServerNotActiveException e)
		{
			return "unknown";
		}
	}

	@WebMethod
	public String[] dir(String path) throws InfoNotFoundException
	{
		File f = new File( basePath, path);
		if( f.exists())
			return f.list();
		else
			throw new InfoNotFoundException( "Directory not found :" + path);
	}


	
	@WebMethod
	public boolean mkdir(String dir) 
	{
		String client_ip = checkClientHost();	
		System.out.println("Pedido 'Make DIR' do cliente " + client_ip);

		File directorio = new File(basePath, dir);
		if(!directorio.exists())
			try
			{
				return directorio.mkdir();
			}
			catch(SecurityException e){return false;};
		
		return false;
	}

	
	@WebMethod
	public boolean rmdir(String dir)
	{
		String client_ip = checkClientHost();	
		System.out.println("Pedido 'Remove DIR' do cliente " + client_ip);
		
		File directorio = new File(basePath, dir);
		String[] children = directorio.list();
		for(String child : children)
			return false;
		return directorio.delete();
	}
	
	
	@WebMethod
	public boolean rmfile(String path)
	{
		System.out.println("Pedido 'Remove File' do cliente " + checkClientHost());
		File ficheiro = new File(basePath, path);
		if(ficheiro.isFile())
			return ficheiro.delete();
		else return false;
	}

	
	@WebMethod
	public boolean cp(String source, String dest) throws IOException
	{
		System.out.println("Pedido 'Copy File' do cliente " + checkClientHost());

		InputStream is = null;
    OutputStream os = null;
    try {
        is = new FileInputStream(source);
        os = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
    } finally {
        is.close();
        os.close();
        return true;
    }
	}


	@WebMethod
	public FileInfo getFileInfo(String path) throws InfoNotFoundException 
	{
		System.out.println("Pedido de 'File Info' do cliente " + checkClientHost());
		File element = new File( basePath, path);
		if( element.exists())
			if(element.isFile())
				return new FileInfo(element.getName(), element.length(), new Date(element.lastModified()), element.isFile(), 0, 0);
			else
			{
				int directories = 0;
				int files = 0;				
				for(String child : element.list())
					if(new File(element,child).isDirectory())
						directories++;
					else files++;
				return new FileInfo(element.getName(), 0, new Date(element.lastModified()), element.isFile(), directories, files);
			}
				
		else
			throw new InfoNotFoundException( "Path not found :" + path);
	}



	@WebMethod
	public boolean setAsPrimary()
	{
		System.out.println("Set this as primary server");
		this.primary = true;
		return true;
	}


	@WebMethod
	public boolean syncWith(String url)
	{
		//	@TODO
		System.out.println("Start sync with: " + url);

		return false;
	}

	public FileContent getFileContent(String path) throws InfoNotFoundException, IOException 
	{		
		System.out.println("Pedido de 'File Content' do cliente " + checkClientHost());

		File f = new File( basePath, path );

		if( f.exists() && f.isFile() ) {

			RandomAccessFile raf = new RandomAccessFile( f, "r" );
			byte []b = new byte[safeLongToInt(raf.length())];
			raf.readFully(b);
			raf.close();

			return new FileContent( path, f.length(), new Date(f.lastModified()), f.isFile(), b);
		}
		else
			throw new InfoNotFoundException( "File not found :" + path);
	}


	public boolean createFile(String path, FileContent file) throws InfoNotFoundException, IOException 
	{		
		System.out.println("Pedido de 'Create file' do cliente " + checkClientHost());

    try {
      RandomAccessFile raf = new RandomAccessFile(basePath + "/" + path, "rw");

      raf.write(file.content);

      raf.close();
    } catch(Exception e) {
    	System.out.println("erro:"+e.getMessage());
    	throw new IOException(e.getMessage());
    }
    return true;
	}

	public static int safeLongToInt(long l) {
    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
        throw new IllegalArgumentException
            (l + " cannot be cast to int without changing its value.");
    }
    return (int) l;
	}


	@Override
	public void run()	{
		try {
			for(;;) {

				//System.out.println("run thread");
			
				try {
					IContactServer contactServer = connectToContact();
					contactServer.ping(this.fileServerName, this.protocol);
				} catch(Exception e ) {
					System.out.println(e.getMessage());
				}
				
				//@TODO
		  	Thread.sleep(2000);
			}
		} catch (InterruptedException e) {}
	}

	public static void main( String args[]) {

		try {
			String path = ".";
			String url = "";
			String serverName = "";
			if( args.length != 3)
			{
				System.out.println("Usage: java FileServer path contactServerUrl/contactServerName fileServerName");
				System.exit(0);
			}
			
			path = args[0];
			url = args[1];
			serverName = args[2];

			FileServerWS server = new FileServerWS(path, url, serverName);

			Endpoint.publish(
			         "http://0.0.0.0:8080/"+serverName,
			         server);
			System.out.println( serverName+" started");
			
			try 
			{
				IContactServer contactserver = server.connectToContact();
				server.subscribeToContact(contactserver);
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
				System.exit(0);
			}


			Thread thread = new Thread(server);
 			thread.start();
			
		} catch( Throwable th) {
			th.printStackTrace();
		}
	}
	


}
