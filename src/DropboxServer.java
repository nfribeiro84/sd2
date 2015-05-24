
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.lang.Thread;
import java.awt.Desktop;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import ws.FileServerWSService;

//REST
import org.json.simple.*;
import org.json.simple.parser.*;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

//SYNC
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.SecureRandom;



public class DropboxServer
		extends UnicastRemoteObject
		implements IFileServer, Runnable
{
	protected DropboxServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	// variaveis para ligação à dropbox
	private static final String API_KEY = "y77jdgxjghny2f5";
	private static final String API_SECRET = "cqgryzv937jcmog";
	private static final String SCOPE = "dropbox";		//""
	private static final String AUTHORIZE_URL = "https://www.dropbox.com/1/oauth/authorize?oauth_token=";
	private Token accessToken;
	private OAuthService service;
	private String baseUrl = "https://api.dropbox.com/1/";

	//SYNC VARS
  	private static final String SYNC_PATH = "./sync_dir";
	private IFileServer rmiServer;
	private ws.FileServerWS wsServer;

	private String basePathName;
	private File basePath;
	private String contactServerURL;
	private String fileServerName;
	private String protocol;
	private boolean primary;

	private int ping_interval = 3;
	
	protected DropboxServer( String pathname, String url, String name) throws RemoteException 
	{
		super();
		this.basePathName = pathname;
		basePath = new File( pathname);
		this.contactServerURL = "rmi://" + url;
		this.fileServerName = name;
		this.protocol = "rmi";
		this.primary = false;
	}

	private boolean connectToDropbox()
	{
		try {
			this.service = new ServiceBuilder().provider(DropBoxApi.class).apiKey(API_KEY)
					.apiSecret(API_SECRET).scope(SCOPE).build();
			Scanner in = new Scanner(System.in);

			// Obter Request token
			Token requestToken = service.getRequestToken();
			
			Runtime rt = Runtime.getRuntime();
			String url = AUTHORIZE_URL + requestToken.getToken();
			rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);

			System.out.println("Tem de aceder ao link indicado para autorizar o servidor a ligar à Dropbox:");
			System.out.println(AUTHORIZE_URL + requestToken.getToken());
			System.out.println("Depois de aceder ao link e introduzir as suas credenciais, pressione 'Enter'");
			System.out.print(">>");
			Verifier verifier = new Verifier(in.nextLine());

			// O Dropbox usa como verifier o mesmo segredo do request token, ao
			// contrario de outros
			// sistemas, que usam um codigo fornecido na pagina web
			// Com esses sistemas a linha abaixo esta a mais
			verifier = new Verifier(requestToken.getSecret());
			// Obter access token
			this.accessToken = this.service.getAccessToken(requestToken, verifier);

			System.out.println(System.getProperty("os.name"));
			
			return true;
		} catch (Exception e) 
		{
			System.out.println("Ocorreu um erro ao ligar à Dropbox");			e.printStackTrace();

			return false;
		}
	}
	
	private IContactServer connectToContact() throws Exception
	{
		IContactServer contactServer = (IContactServer) Naming.lookup(this.contactServerURL);
		return contactServer;
	}
	
	private IContactServer subscribeToContact(IContactServer contactServer) throws Exception
	{
		int res = contactServer.subscribe(this.fileServerName, this.protocol);
		if( res != -1)
		{
			if (res == 1) this.primary = true;
			//@todo fazer isto ao WS
			return contactServer;
		}
		else throw new RemoteException("Couldn't connect to contact server");
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
	public boolean setAsPrimary()
	{
		System.out.println("Set this as primary server");
		this.primary = true;
		return true;
	}

	/**
	* Metodo que verifica se a path indicada corresponde a um directorio ou nao
	*/
	private boolean isDir(String path) throws Exception
	{
		if(path.endsWith("."))
			path = path.replace(".","");
		else
			if(!path.endsWith("/"))
				path += "/";
		

		String url = baseUrl+"metadata/auto/" + path + "?list=false";
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();

		if (response.getCode() != 200)
			throw new RuntimeException("Metadata response code:" + response.getCode());

		try
		{
			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(response.getBody());
			return (boolean)res.get("is_dir");	
		}
		catch(Exception e)
		{
			throw e;
		}
	}


	@Override
	public String[] dir(String path) throws RemoteException, InfoNotFoundException
	{
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'DIR' do cliente " + client_ip);
			System.out.println("Para a path "+ path);
		}
		catch(ServerNotActiveException e){};
		
		if(path.endsWith("."))
			path = path.replace(".","");
		else
			if(!path.endsWith("/"))
				path += "/";

		String url = baseUrl+"metadata/auto/" + path + "?list=true";
		System.out.println(url);
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();

		if (response.getCode() != 200)
			throw new RuntimeException("Metadata response code:" + response.getCode());

		JSONParser parser = new JSONParser();
		JSONArray items;
		try
		{
			JSONObject res = (JSONObject) parser.parse(response.getBody());

			items = (JSONArray) res.get("contents");			
		
			Iterator it = items.iterator();
			//System.out.println(items.length());
			String[] result = new String[items.size()];
			int i=0;
			while (it.hasNext())
			{
				JSONObject file = (JSONObject) it.next();
				result[i] = (String)file.get("path");
				i++;
			}
			return result;
		}
		catch(Exception e) { return null;}
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

		String url = baseUrl + "fileops/create_folder";

		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addBodyParameter("root", "auto");
		request.addBodyParameter("path", dir);
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();

		if (response.getCode() != 200)
			return false;

		return true;
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
		
		String url = baseUrl + "fileops/delete";
		try
		{
			if(this.isDir(dir))
			{
				OAuthRequest request = new OAuthRequest(Verb.POST, url);
				request.addBodyParameter("root", "auto");
				request.addBodyParameter("path", dir);
				this.service.signRequest(this.accessToken, request);
				Response response = request.send();

				if (response.getCode() != 200)
					return false;

				return true;	
			}
			else
			{
				System.out.println("Not a directory");
				return false;
			}
				
		}
		catch(Exception e)
		{
			return false;
		}
		
	}
	
	@Override
	public boolean rmfile(String path) throws RemoteException
	{
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'Remove File' do cliente " + client_ip);
		}
		catch(ServerNotActiveException e){};
		
		String url = baseUrl + "fileops/delete";
		try
		{
			if(!this.isDir(path))
			{
				OAuthRequest request = new OAuthRequest(Verb.POST, url);
				request.addBodyParameter("root", "auto");
				request.addBodyParameter("path", path);
				this.service.signRequest(this.accessToken, request);
				Response response = request.send();

				if (response.getCode() != 200)
					return false;

				return true;	
			}
			else
			{
				System.out.println("Not a file");
				return false;
			}
				
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	// TO DO
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
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'Get File Info' do cliente " + client_ip);
			System.out.println("Para o ficheiro " + path);
		}
		catch(ServerNotActiveException e){};

		String url = baseUrl+"metadata/auto/" + path + "?list=true";
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		if(response.getCode() == 404)
			throw new InfoNotFoundException("File "+ path + " not found");
		else
			if (response.getCode() != 200)
				throw new RuntimeException("Metadata response code:" + response.getCode());

		JSONParser parser = new JSONParser();
		try
		{
			JSONObject file = (JSONObject) parser.parse(response.getBody());
				
			String name = (String) file.get("path");
			int index = name.lastIndexOf("/");
			if(index > -1);
				name = name.substring(index+1);
			Date dt = new Date((String)file.get("modified"));
			boolean isDir = (boolean)file.get("is_dir");
			int childrenFiles = 0;
			int childrenDirectories = 0;
			if(isDir)
			{
				System.out.println("Is Dir");
				JSONArray contents = (JSONArray)file.get("contents");
				Iterator it = contents.iterator();
				while(it.hasNext())
				{
					JSONObject content = (JSONObject) it.next();
					if((boolean)content.get("is_dir"))
						childrenDirectories++;
					else childrenFiles++;			
				}
				System.out.println("childrenDirectories: " + childrenDirectories);
				System.out.println("childrenFiles: " + childrenFiles);
			}

			return new FileInfo(name, (long)file.get("bytes"), dt, !isDir, childrenDirectories, childrenFiles, (String)file.get("rev"));			
		}
		catch(Exception e)
		{			
			e.printStackTrace();
			return null;
		}

	}


	// TODO - NOT WORKING YET
	public FileContent getFileContent(String path) throws RemoteException, InfoNotFoundException, IOException 
	{
		System.out.println();		
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'Get File Content' do cliente " + client_ip);
			System.out.println("Para o ficheiro " + path);
		}
		catch(ServerNotActiveException e){};		

		String url = "https://api-content.dropbox.com/1/"+"files/auto/" + path;
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		if(response.getCode() == 404)
		{
			System.out.println("Not found: " + url);
			throw new InfoNotFoundException("File "+ path + " not found");
		}
			
		else
			if (response.getCode() != 200)
			{
				System.out.println("not 200: " + response.getCode());
				throw new RuntimeException("Metadata response code:" + response.getCode());
			}
				
		JSONParser parser = new JSONParser();
		try
		{
			JSONObject file = (JSONObject) parser.parse(response.getHeader("x-dropbox-metadata"));
			System.out.println(file);
		
			String name = (String) file.get("path");
			int index = name.lastIndexOf("/");
			if(index > -1);
				name = name.substring(index+1);
			Date dt = new Date((String)file.get("modified"));
			long size = (long) file.get("bytes");
			int sizeInt = (int) size;
			InputStream is = response.getStream();
			byte[] bytes = new byte[sizeInt];
			int read = 0;
			int len = 0;
			while(len != -1)
			{			
				len = is.read(bytes,read,sizeInt-read);
				read += len;
			}
			return new FileContent( name, (long)file.get("bytes"), dt, !(boolean)file.get("is_dir"), bytes);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}		
	}


	// TODO
	public boolean createFile(String path, FileContent file) throws RemoteException, InfoNotFoundException, IOException 
	{		
		System.out.println();		
		try
		{
			String client_ip = java.rmi.server.RemoteServer.getClientHost();	
			System.out.println("Pedido 'Create File' do cliente " + client_ip);
			System.out.println("Novo ficheiro " + path);
		}
		catch(ServerNotActiveException e){};		

		try
		{
			String tmp_path = writeTmpFile(file.content);

			String url = "https://api-content.dropbox.com/1/files_put/auto/" + path+"?locale=English&parent_rev="+checkSum(tmp_path);
			
			//deletes tmp file
			//deleteFile(tmp_path);

			OAuthRequest request = new OAuthRequest(Verb.PUT, url);
			this.service.signRequest(this.accessToken, request);
			request.addHeader("Content-Length", Long.toString(file.length));
			request.addHeader("Content-Type", "application/octet-stream");
			request.addPayload(file.content);
			Response response = request.send();

			if(response.getCode() == 409)
			{
				System.out.println("Conflict (409)");
				return false;
			}
				
			else
				if (response.getCode() == 411)
				{
					System.out.println("Missing Content-Length (411)");
					return false;
				}
				else
					if(response.getCode() != 200)
					{
						System.out.println("Status Code " + response.getCode());
						JSONParser parser = new JSONParser();
						JSONObject file2 = (JSONObject) parser.parse(response.getBody());
						System.out.println((String) file2.get("error"));
						return false;
					}			
			return true;	
		}
		catch(Exception e)
		{
			System.out.println("Excepção...");
			e.printStackTrace();
			return false;
		}	
	}
	

	public static int safeLongToInt(long l) {
    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
        throw new IllegalArgumentException
            (l + " cannot be cast to int without changing its value.");
    }
    return (int) l;
	}


	/**
	*
	*	SYNC
	*
	**/

	private FileServerWSService createWsServer(String url) throws Exception {
		return new FileServerWSService( new URL(url), new QName("http://ws.srv/", "FileServerWSService"));
	}



	@Override
	public boolean syncWith(String url)
	{
		System.out.println("Start sync with: " + url + " Oon path: " + SYNC_PATH);
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
			if( syncAllFilesAndFolders( SYNC_PATH ) ) 
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

	private boolean syncAllFilesAndFolders(String path) {
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
				System.out.println( folders.length);
				for( int i = 0; i < folders.length; i++)
				{
					System.out.println( folders[i] );
					String curr_file = path + "/" + folders[i];

					//@TODO
					//if is dir 
					//{
						//if folder doesnt exist create folder
						//return syncAllFilesAndFolders( curr_file );
					//}
					//else
						//return syncFile( curr_file );
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


	private static String writeTmpFile(byte[] content)
	{
		try {
			String base = "./syn_dir/.tmp/";
			SecureRandom random = new SecureRandom();

			String path = new BigInteger(130, random).toString(32);
			System.out.println("new tmp file created"+path);
			RandomAccessFile raf = new RandomAccessFile(base+path, "rw");

	    raf.write(content);

	    raf.close();

	    return path;

		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	

	private static boolean deleteFile(String path)
	{
		try {
			File ficheiro = new File(path);
			return ficheiro.delete();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}




	/*
	* Calculate checksum of a File using MD5 algorithm
	*/
	private static String checkSum(String path){
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
	*
	*	END SYNC
	*
	**/




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

			DropboxServer server = new DropboxServer(path, args[1], args[2]);
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
			
			
			System.out.println( "Dropbox Server bound in registry with name '"+name+"'");
			
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


			server.connectToDropbox();
			
		} catch( Throwable th) {
			th.printStackTrace();
		}
	}
}