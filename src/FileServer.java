
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.lang.Thread;

public class FileServer
		extends UnicastRemoteObject
		implements IFileServer, Runnable
{
	protected FileServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	private String basePathName;
	private File basePath;
	private String contactServerURL;
	private String fileServerName;
	private String protocol;
	private boolean primary;

	private int ping_interval = 3;
	
	protected FileServer( String pathname, String url, String name) throws RemoteException 
	{
		super();
		this.basePathName = pathname;
		basePath = new File( pathname);
		this.contactServerURL = "rmi://" + url;
		this.fileServerName = name;
		this.protocol = "rmi";
	}
	
	private IContactServer connectToContact() throws Exception
	{
		IContactServer contactServer = (IContactServer) Naming.lookup(this.contactServerURL);
		return contactServer;
	}
	
	private IContactServer subscribeToContact(IContactServer contactServer) throws Exception
	{
		if(contactServer.subscribe(this.fileServerName, this.protocol))
			return contactServer;
		else throw new RemoteException("Couldn't conecto to contact server");
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
	
	/**
	 * Metodo que serve para indicar que ainda esta vivo
	 * @return "OK"
	 */	
	public String pong()
	{
		return "OK";
	}
	
	

	@Override
	public String[] dir(String path) throws RemoteException, InfoNotFoundException
	{
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'DIR' do cliente " + client_ip);
		}
		catch(ServerNotActiveException e){};
		
		File f = new File( basePath, path);
		if( f.exists())
			return f.list();
		else
			throw new InfoNotFoundException( "Directory not found :" + path);
	}
	
	@Override
	public boolean mkdir(String dir) throws RemoteException
	{
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'Make DIR' do cliente " + client_ip);
		}
		catch(ServerNotActiveException e){};
		File directorio = new File(basePath, dir);
		if(!directorio.exists())
			try
			{
				return directorio.mkdir();
			}
			catch(SecurityException e){return false;};
		
		return false;
	}
	
	@Override
	public boolean rmdir(String dir) throws RemoteException
	{
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'Remove DIR' do cliente " + client_ip);
		}
		catch(ServerNotActiveException e){};
		File directorio = new File(basePath, dir);
		String[] children = directorio.list();
		for(String child : children)
			return false;
		return directorio.delete();
	}
	
	@Override
	public boolean rmfile(String path) throws RemoteException
	{
		System.out.println("Pedido 'Remove File' do cliente " + checkClientHost());
		File ficheiro = new File(basePath, path);
		if(ficheiro.isFile())
			return ficheiro.delete();
		else return false;
	}
	
	@Override
	public boolean cp(String source, String dest) throws RemoteException, IOException
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

	@Override
	public FileInfo getFileInfo(String path) throws RemoteException, InfoNotFoundException 
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

	public FileContent getFileContent(String path) throws RemoteException, InfoNotFoundException, IOException 
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

	public boolean createFile(String path, FileContent file) throws RemoteException, InfoNotFoundException, IOException 
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

	public static void main( String args[]) throws Exception {
		try {
			String path = ".";
			if( args.length != 3)
			{
				System.out.println("Usage: java FileServer path contactServerUrl/contactServerName fileServerName");
				System.exit(0);
			}
			
			path = args[0];

			System.getProperties().put( "java.security.policy", "policy.all");

			if( System.getSecurityManager() == null) {
				System.setSecurityManager( new RMISecurityManager());
			}

			try { // start rmiregistry
				LocateRegistry.createRegistry( 1099);
			} catch( RemoteException e) { 
				// if not start it
				// do nothing - already started with rmiregistry
			}

			FileServer server = new FileServer(path, args[1], args[2]);
			String name = args[2];
			int i = 1;
			boolean binded = false;
			while(!binded)
			{
				try
				{
					Naming.bind( name, server);
					binded = true;					
					server.fileServerName = name;
				}
				catch(Exception e)
				{
					System.out.println("Server '"+name+"' Exists");
					name = args[2] + "_" + i++;
				}
			}
			
			
			System.out.println( "DirServer bound in registry with name '"+name+"'");
			
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
