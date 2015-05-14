
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.sql.Timestamp;
import java.io.*;


import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import ws.FileServerWSService;

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


	private FileServerWSService createWsServer(String url) throws Exception {
		return new FileServerWSService( new URL(url), new QName("http://ws.srv/", "FileServerWSService"));
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

	/**
	*		Returns -1 in case of error
	*		Returns 1 in case the server is primary
	*		Returns > 1 in case the server is not primary
	*/
	public int subscribe(String name, String protocol) throws RemoteException, ServerExistsException{
		String client_ip = "";
		try {
			client_ip = java.rmi.server.RemoteServer.getClientHost();
		} catch (ServerNotActiveException e) {
			System.out.println("Error subscribing:" + e.getMessage());
			return -1;
		}

		if (serverExists(name, client_ip, protocol))
			throw new ServerExistsException("Server " + name + "@" + client_ip + " exists");

		return addServer(name, client_ip, protocol);
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
	private int addServer(String name, String ip, String protocol) {

		List<String> ips = new CopyOnWriteArrayList<String>();
		try {

			String url = buildUrl(ip, name, protocol);
			//System.out.println(url);

			if(this.fileServers.containsKey(name))
			{
				ips = this.fileServers.get(name);

				if(!ips.contains(url))
				{
					ips.add(url);

					this.fileServers.put(name, ips);
					System.out.println("Added server ip: " + ip + " to servename: " + name);

					// trigger servers sync process
					if(syncServers(getPrimaryServer(name), url))
						System.out.println("Sync success");
					else
						System.out.println("Sync error occured");					
				}
			} else {
				ips.add(url);
				this.fileServers.put(name, ips);
				System.out.println("Added server ip: " + ip + " to servename: " + name);

				if(setServerAsPrimary(url))
					System.out.println("Set success");
				else
					System.out.println("An error occured");
			}


			Date date = new Date();
			this.timetable.put(url, new Timestamp(date.getTime()));
			System.out.println("Added timetable to server: " + name + "@" + ip + ": " + this.timetable.get(name + "@" + ip));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return ips.size();

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

		try {
			List<String> ips = this.fileServers.get(name);
			
			if(ips != null) {

				boolean is_primary = ips.indexOf(url) == 0;
				
				ips.remove(url);
				
				if (ips.size() == 0) {
					//remove o ip do nome do servidor correpondente
					this.fileServers.remove(name);

					//unbind server name from registry
					Naming.unbind(name);

					System.out.println("Removed servename: " + name);
				}
				else if (is_primary)
				{
					System.out.println("Set "+ips.get(0)+" as primary");
					if(setServerAsPrimary(ips.get(0)))
						System.out.println("Set success");
					else
						System.out.println("An error occured");
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
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
					/*System.out.println((now.getTime() - ping_date.getTime())/1000);	
					if((now.getTime() - ping_date.getTime())/1000 > this.time_check)
					{
						removeServer(name, url);
					}*/
				}
			}
		}	
		
	}

	/**
	*
	*		SYNC
	*
	*/

	private boolean setServerAsPrimary(String url) throws Exception {

		try
		{	
			if(url.startsWith("http"))
			{
				FileServerWSService service = createWsServer(url);
				ws.FileServerWS wsServer = service.getFileServerWSPort();
				return wsServer.setAsPrimary();
			}
			else
			{
				IFileServer rmiServer = (IFileServer) Naming.lookup(url);
				return rmiServer.setAsPrimary();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}

	public String getPrimaryServer(String name) {
		List<String> result = this.fileServers.get(name);
		return result.get(0);
	}

	public boolean syncServers(String primary, String slave) throws Exception {
		System.out.println("Syncinn servers " + primary + " and " + slave);

		try
		{	
			if(slave.startsWith("http"))
			{
				FileServerWSService service = createWsServer(slave);
				ws.FileServerWS wsServer = service.getFileServerWSPort();
				return wsServer.syncWith(primary);
			}
			else
			{
				IFileServer rmiServer = (IFileServer) Naming.lookup(slave);
				return rmiServer.syncWith(primary);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}

	/**
	*
	*		END SYNC
	*
	*/





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
