
import java.io.*;
import java.util.*;
//import java.util.Date;

import javax.jws.*;
import javax.xml.ws.Endpoint;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import ws.FileServerWSService;

//checksum
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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

	//SYNC VARS
  private static final String SYNC_PATH = "./sync_dir";
  private static final String TMP_FOLDER = "./sync_dir/.tmp";
  
	private IFileServer rmiServer;
	private ws.FileServerWS wsServer;



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
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'Make DIR' do cliente " + client_ip);
		}
		catch(ServerNotActiveException e){};

		return createDir( basePath, dir );
	}



	private boolean createDir(File basePath, String dir) {

		File directorio = new File(basePath, dir);
		
		if(!directorio.exists()) {
			try
			{
				boolean success = directorio.mkdir();
				if(success)
				{
					try
					{
						IContactServer contato = connectToContact();
						contato.orderSync(this.fileServerName);	
						return success;
					}
					catch(Exception e)
					{
						System.out.println("Erro ordering Sync");
						e.printStackTrace();
					}	
				}
			}
			catch(SecurityException e){ System.out.println(e.getMessage() );};
		}	
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
		System.out.println("Pedido de 'File Info' do cliente " + checkClientHost() + " para o ficheiro " + basePath+"/"+path);
		
		File element = new File( basePath, path);
		if( element.exists())
			if(element.isFile())
				return new FileInfo(element.getName(), element.length(), new Date(element.lastModified()), element.isFile(), 0, 0, checkSum(basePath+"/"+path));
			else
			{
				int directories = 0;
				int files = 0;				
				for(String child : element.list())
					if(new File(element,child).isDirectory())
						directories++;
					else files++;
				return new FileInfo(element.getName(), 0, new Date(element.lastModified()), element.isFile(), directories, files, checkSum(basePath+"/"+path));
			}
				
		else
			throw new InfoNotFoundException( "Path not found :" + path);
	}







	/**
	*		SYNC
	*
	*/



	@WebMethod
	public boolean setAsPrimary()
	{
		System.out.println("Set this as primary server");
		this.primary = true;
		return true;
	}


	private FileServerWSService createWsServer(String url) throws Exception {
		return new FileServerWSService( new URL(url), new QName("http://ws.srv/", "FileServerWSService"));
	}


	@WebMethod
	public boolean syncWith(String url)
	{
		System.out.println("Start sync with: " + url + " on path: " + SYNC_PATH);
		try {
			//	@TODO
			String[] folders;

			if(url.startsWith("http"))
			{
				FileServerWSService service = createWsServer(url);
				this.wsServer = service.getFileServerWSPort();
			}
			else
			{
				this.rmiServer = (IFileServer) Naming.lookup(url);
			}

			//Sync root directory
			if( this.syncAllFilesAndFolders( SYNC_PATH ) ) 
			{
				return true;
			}
			else
			{
				return false;
			}


		} catch(Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean isFileSyncable(String path)
	{
		try {
			if(rmiServer == null) {
				ws.FileInfo info = wsServer.getFileInfo(path);
				System.out.println("file "+path+" md5: "+info.getMd5());
				System.out.println(checkSum(path));
				return info.isIsFile() && !info.getMd5().equals( checkSum(path) );
			}
			else
			{
				FileInfo info = rmiServer.getFileInfo(path);
				System.out.println("file "+path+" md5: "+info.md5);
				System.out.println(checkSum(path));
				return info.isFile && !info.md5.equals( checkSum(path) );
			}
		} catch(Exception e) {
			e.getMessage();
			return true;
		}
	}

	private byte[] getRemoteFileContent( String file ) {
		try
			{
				if(rmiServer == null)
				{
					ws.FileContent content = wsServer.getFileContent( file );
					return content.getContent();
				}
				else
				{
					FileContent content = rmiServer.getFileContent( file );
					return content.content;
				}
			}
			catch(Exception e)
			{
				System.out.println("Exception in 'CP fromServer': "+e.getMessage());
				return new byte[0];
			}			
	}

	private boolean syncFile(String basePath, String file) 
	{
		try {

			OutputStream os = null;

			try {
				byte[] content = getRemoteFileContent( basePath + "/" + file );
        
        //os = new FileOutputStream(abs_path);
        os = new FileOutputStream("/Users/kae/Documents/workspace/eclipse-projects/fct/sd/sd2/sync_dir/.tmp/" + file);
        
        os.write(content);
        
	    } finally {
        os.close();
        return true;
	    }
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean syncAllFilesAndFolders(String path) {
			//System.out.println("entraaaa");
		try {
			String[] folders;

			if(this.rmiServer == null)
			{
				List<String> list = wsServer.dir(path);
				folders = list.toArray(new String[list.size()]);
			}
			else
			{
				folders = rmiServer.dir(path);
			}


			if( folders != null) 
			{
				//System.out.println( folders.length + " " +path);
				for( int i = 0; i < folders.length; i++)
				{
					//System.out.println( folders[i] );
					String abs_path = path + "/" + folders[i];
					//System.out.println(isFile(abs_path));

					if( !isFileSyncable(abs_path) ) 
					{
						createDir(new File( path ), folders[i] );
						syncAllFilesAndFolders(abs_path);
					}
					else
					{
    				if( syncFile( path, folders[i] ) ) {
  						System.out.println("Synchronized file: " + abs_path);
    				} else {
    					System.out.println("Couldn't sync file: " + abs_path);
    				}
					}
				}
				return true;
			} 
			else
			{
				System.out.println( "Invalid folders array" );
				return false;
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}



	/*
	* Calculate checksum of a File using MD5 algorithm
	*/
	public static String checkSum(String path){
	  String checksum = null;
	  try {
      FileInputStream fis = new FileInputStream(path);
      MessageDigest md = MessageDigest.getInstance("MD5");
    
      //Using MessageDigest update() method to provide input
      byte[] buffer = new byte[8192];
      int numOfBytesRead;
      while( (numOfBytesRead = fis.read(buffer)) > 0){
          md.update(buffer, 0, numOfBytesRead);
      }
      byte[] hash = md.digest();
      checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
	  } catch (IOException ex) {
      System.out.println(ex.getMessage());
	  } catch (NoSuchAlgorithmException ex) {
      System.out.println(ex.getMessage());
	  }
	    
	 return checksum;
	}





	/**
	*			END SYNC
	*
	*/

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

			IContactServer contato = connectToContact();
			contato.orderSync(this.fileServerName);	

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
