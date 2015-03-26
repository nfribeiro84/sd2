

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class FileServer
		extends UnicastRemoteObject
		implements IFileServer
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
	
	protected FileServer( String pathname, String url, String name) throws RemoteException 
	{
		super();
		this.basePathName = pathname;
		basePath = new File( pathname);
		this.contactServerURL = "rmi://" + url;
		this.fileServerName = name;
	}
	
	private String connectToContact()
	{
		try
		{
			IContactServer contactServer = (IContactServer) Naming.lookup(this.contactServerURL);
			if(contactServer.subscribe(this.fileServerName))
				return "Successfully subscribed to Contact Server";
			else return "Error subscribing to Contact Server. No Response";
		}
		catch(Exception e)
		{
			return "Error subscribing to Contact Server. Error: " + e.getMessage();
		}		
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

	public FileContent getFileContent(String path, String name) throws RemoteException, InfoNotFoundException, IOException 
	{		
		File dir = new File( basePath, path);
		if( dir.exists()) {
			File f = new File( dir, name);
			if( f.exists()) {

				RandomAccessFile raf = new RandomAccessFile( f, "r" );
				byte []b = new byte[safeLongToInt(raf.length())];
				raf.readFully(b);
				raf.close();

				return new FileContent( name, f.length(), new Date(f.lastModified()), f.isFile(), b);
			}
			else
				throw new InfoNotFoundException( "File not found :" + name);

		} else
			throw new InfoNotFoundException( "Directory not found :" + path);

	}

	public static int safeLongToInt(long l) {
    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
        throw new IllegalArgumentException
            (l + " cannot be cast to int without changing its value.");
    }
    return (int) l;
	}

	public static void main( String args[]) throws Exception {
		try {
			String path = ".";
			if( args.length != 3)
			{
				System.out.println("Usage: java FileServer path contactServerUrl fileServerName");
				System.exit(0);
			}
			
			path = args[0];

			System.getProperties().put( "java.security.policy", "aula1/policy.all");

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
			Naming.rebind( args[2], server);
			System.out.println( "DirServer bound in registry");
			
			String connect = server.connectToContact();
			System.out.println(connect);
			if(!connect.equals("Successfully subscribed to Contact Server"))
				System.exit(0);
			
		} catch( Throwable th) {
			th.printStackTrace();
		}
	}


}
