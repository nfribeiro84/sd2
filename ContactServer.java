

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class ContactServer
		extends UnicastRemoteObject
		implements IContactServer
{

	private static final long serialVersionUID = 1L;

	private String basePathName;
	//private File basePath;

	/**
	*		Map that saves the FileServers info
	*		key: server name; 
	* 	value: server IPs
	*/
	private Map<String, List<String>> fileServers;


	protected ContactServer() throws RemoteException {
		super();
		this.fileServers = new HashMap<String, List<String>>();
		System.out.println("ContacServer initiated");
		//fileServers
	}


	@Override
	public String[] servers() throws RemoteException 
	{
		List<String> result = new ArrayList<String>();
		for(Map.Entry<String, List<String>> entry : fileServers.entrySet()) {
			result.add(entry.getKey());
		}
		return result.toArray(new String[fileServers.size()]);
	}


	@Override
	public String[] servers(String name) throws RemoteException {
		return new String[1];
	}

	//@Override
	private void addServer(String name, String ip) {
		List<String> ips = new ArrayList<String>();
		ips.add(ip);
		this.fileServers.put(name, ips);
		System.out.println("Added server ip: " + ip + " to servename: " + name);
	}


	public static void main( String args[]) throws Exception {
		try {

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

			ContactServer server = new ContactServer();
			Naming.rebind( "/myContactServer", server);
			System.out.println( "DirServer bound in registry");

			//add server
			server.addServer("A", "100.100.1.1");
		} catch( Throwable th) {
			th.printStackTrace();
		}
	}


}
