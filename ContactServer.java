

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.lang.Thread;

public class ContactServer
		extends UnicastRemoteObject
		implements IContactServer, Runnable
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
		try {
			//@TODO move this exception to interface

			System.out.println(java.rmi.server.RemoteServer.getClientHost());
		} catch (ServerNotActiveException e) {}


		List<String> result = new ArrayList<String>();
		for(Map.Entry<String, List<String>> entry : fileServers.entrySet()) {
			result.add(entry.getKey());
		}
		return result.toArray(new String[fileServers.size()]);
	}


	@Override
	public String[] servers(String name) throws RemoteException 
	{
		
		List<String> result = this.fileServers.get(name);
		if(result != null) {
			return result.toArray(new String[result.size()]);
		} else {
			return new String[0];
		}
	}

	//@Override
	public boolean subscribe(String name) throws RemoteException, ServerExistsException{
		String client_ip = "";
		try {
			client_ip = java.rmi.server.RemoteServer.getClientHost();
		} catch (ServerNotActiveException e) {
			System.out.println("Error subscribing:" + e.getMessage());
		}

		if (serverExists(name, client_ip))
			throw new ServerExistsException("Server " + name + "@" + client_ip + " exists");

		addServer(name, client_ip);
		return true;
	}

	//@Override
	private void addServer(String name, String ip) {
		List<String> ips = new ArrayList<String>();
		ips.add(ip);
		this.fileServers.put(name, ips);
		System.out.println("Added server ip: " + ip + " to servename: " + name);
	}

	private boolean serverExists(String name, String ip) {
		if (this.fileServers.containsKey(name)) {
			if (this.fileServers.get(name).contains(ip)) {
				return true;	
			}
		}
		return false;
	}

	//@Override
	private void removeServer(String name, String ip) {
		
		List<String> ips = this.fileServers.get(name);
		if(ips != null) {
			ips.remove(ip);
			if (ips.size() == 0) {
				//remove o ip do nome do servidor correpondente
				this.fileServers.remove(name);
				System.out.println("Removed servename: " + name);
			} else {
				// remove o nome do servidor do mapa se nao tem ips
				System.out.println("Removed server ip: " + ip + " in servename: " + name);	
			}
		}
	}

	private void startThread() {

	}

	@Override
	public void run()	{
		try {
			for(;;) {
		  	System.out.println("Runnable running" + this.fileServers.size());	
		  	if(this.fileServers.containsKey("CAE__")) {
		  		this.removeServer("CAE__", "100.100.1.2");
		  	}
		  	Thread.sleep(1000);
			}
		} catch (InterruptedException e) {}
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


			Thread thread = new Thread(server);
 			thread.start();

			//add server
			server.addServer("CAE", "100.100.1.1");
			Thread.sleep(5000);
			server.addServer("CAE__", "100.100.1.2");

		} catch( Throwable th) {
			th.printStackTrace();
		}
	}


}
