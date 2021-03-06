
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.sql.Timestamp;
import java.io.*;

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

	private Map<String, Timestamp> timetable;

	private int time_check = 6;


	protected ContactServer() throws RemoteException {
		super();
		this.fileServers = new ConcurrentHashMap<String, List<String>>();
		this.timetable = new ConcurrentHashMap<String, Timestamp>();
		System.out.println("ContacServer initiated");
		//fileServers
	}


	@Override
	public String[] servers() throws RemoteException 
	{
		List<String> result = new CopyOnWriteArrayList<String>();
		for(String key : fileServers.keySet()) 
		{
			result.add(key);
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
	public boolean subscribe(String name, String protocol) throws RemoteException, ServerExistsException{
		String client_ip = "";
		try {
			client_ip = java.rmi.server.RemoteServer.getClientHost();
		} catch (ServerNotActiveException e) {
			System.out.println("Error subscribing:" + e.getMessage());
		}

		if (serverExists(name, client_ip, protocol))
			throw new ServerExistsException("Server " + name + "@" + client_ip + " exists");

		addServer(name, client_ip, protocol);
		return true;
	}


	public boolean ping(String name, String protocol) throws RemoteException {
		try 
		{
			String ip = java.rmi.server.RemoteServer.getClientHost();
			String url = buildUrl(ip, name, protocol);
			if(this.timetable.containsKey(url)) 
			{
				Date date = new Date();
				this.timetable.put(url, new Timestamp(date.getTime()));
			}
			//System.out.println("server "+url+" just pinged");
			return true;
		} 
		catch (ServerNotActiveException e) 
		{
			System.out.println("Error subscribing:" + e.getMessage());
			return false;
		}
	}

	private String buildUrl(String urlIp, String serverName, String protocol)
  {
  	String port = protocol.startsWith("http") ? ":8080" : "";
  	String wsdl = protocol.startsWith("http") ? "?wsdl" : "";
    return protocol+"://"+urlIp+port+"/"+serverName+wsdl;
  }

	//@Override
	private void addServer(String name, String ip, String protocol) {
		List<String> ips = new CopyOnWriteArrayList<String>();

		String url = buildUrl(ip, name, protocol);
		System.out.println(url);

		if(this.fileServers.containsKey(name))
		{
			ips = this.fileServers.get(name);

			if(!ips.contains(url))
			{
				ips.add(url);

				this.fileServers.put(name, ips);
				System.out.println("Added server ip: " + ip + " to servename: " + name);
			}
		} else {
			ips.add(url);
			this.fileServers.put(name, ips);
			System.out.println("Added server ip: " + ip + " to servename: " + name);
		}

		Date date = new Date();
		this.timetable.put(url, new Timestamp(date.getTime()));
		System.out.println("Added timetable to server: " + name + "@" + ip + ": " + this.timetable.get(name + "@" + ip));

	}

	private boolean serverExists(String name, String ip, String protocol) {

		String url = buildUrl(ip, name, protocol);

		if (this.fileServers.containsKey(name)) {
			if (this.fileServers.get(name).contains(url)) {
				return true;	
			}
		}
		return false;
	}

	//@Override
	private void removeServer(String name, String url) {

		List<String> ips = this.fileServers.get(name);
		if(ips != null) {
			ips.remove(url);
			if (ips.size() == 0) {
				//remove o ip do nome do servidor correpondente
				this.fileServers.remove(name);
				System.out.println("Removed servename: " + name);
			}
		}
	}


	private void checkServerStatus(){
		
		List<String> result = new CopyOnWriteArrayList<String>();
		for (Map.Entry<String, List<String>> entry : fileServers.entrySet())
		{
			String name = entry.getKey();
			for(String url : entry.getValue()) 
			{
				//String url = buildUrl(ip, name);
				Date date = new Date();
				Timestamp now = new Timestamp(date.getTime());	
				Timestamp ping_date = this.timetable.get(url);
				
				if(date != null	) 
				{
					//System.out.println((now.getTime() - ping_date.getTime())/1000);	
					if((now.getTime() - ping_date.getTime())/1000 > this.time_check)
					{
						removeServer(name, url);
					}
				}
			}
		}	
		
	}






	@Override
	public void run()	{
		try {
			for(;;) {

				//System.out.println("run check serves");
			
				this.checkServerStatus();
				
				//@TODO
		  	Thread.sleep(4000);
			}
		} catch (InterruptedException e) {}
	}




	public static void main( String args[]) throws Exception {
		try {

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

			ContactServer server = new ContactServer();
			Naming.rebind( "/myContactServer", server);
			System.out.println( "DirServer bound in registry");



			//add server
//			server.addServer("CAE", "100.100.1.1", "rmi");

//			server.addServer("CAE__", "100.100.1.2", "rmi");



			Thread thread = new Thread(server);
 			thread.start();

		} catch( Throwable th) {
			th.printStackTrace();
		}
	}


}
