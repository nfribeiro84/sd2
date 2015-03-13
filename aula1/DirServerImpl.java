package aula1;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class DirServerImpl
		extends UnicastRemoteObject
		implements IFileServer
{
	protected DirServerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	private String basePathName;
	private File basePath;

	protected DirServerImpl( String pathname) throws RemoteException {
		super();
		this.basePathName = pathname;
		basePath = new File( pathname);
	}

	@Override
	public String[] dir(String path) throws RemoteException, InfoNotFoundException {
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

	public static void main( String args[]) throws Exception {
		try {
			String path = ".";
			if( args.length > 0)
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

			DirServerImpl server = new DirServerImpl( path);
			Naming.rebind( "/myFileServer", server);
			System.out.println( "DirServer bound in registry");
		} catch( Throwable th) {
			th.printStackTrace();
		}
	}


}
