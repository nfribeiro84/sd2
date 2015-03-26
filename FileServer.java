

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

	private int ping_interval = 3;
	
	protected FileServer( String pathname, String url, String name) throws RemoteException 
	{
		super();
		this.basePathName = pathname;
		basePath = new File( pathname);
		this.contactServerURL = "rmi://" + url;
		this.fileServerName = name;
	}
	
	private IContactServer connectToContact() throws Exception
	{

		IContactServer contactServer = (IContactServer) Naming.lookup(this.contactServerURL);
		return contactServer;
	}
	
	private IContactServer subscribeToContact(IContactServer contactServer) throws Exception
	{
		if(contactServer.subscribe(this.fileServerName))
			return contactServer;
		else throw new RemoteException("Couldn't conecto to contact server");
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
	public FileInfo getFileInfo(String path, String name) throws RemoteException, InfoNotFoundException {
		File dir = new File( basePath, path);
		if( dir.exists()) {
			File f = new File( dir, name);
			if( f.exists())
				return new FileInfo( name, f.length(), new Date(f.lastModified()), f.isFile());
			else
				throw new InfoNotFoundException( "File not found :" + name);
		} else
			throw new InfoNotFoundException( "Directory not found :" + path);
	}

	public FileContent getFileContent(String path, String name) throws RemoteException, InfoNotFoundException, IOException {
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



	@Override
	public void run()	{
		try {
			for(;;) {

				//System.out.println("run thread");
			
				try {
					IContactServer contactServer = connectToContact();
					contactServer.ping(this.fileServerName);
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
			
			try {

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
