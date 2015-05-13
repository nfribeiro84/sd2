
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.lang.Thread;
import java.awt.Desktop;

//REST
import org.json.simple.*;
import org.json.simple.parser.*;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;


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

		String url = baseUrl+"metadata/auto/" + path + "?list=false";
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
			boolean isFile = !(boolean)file.get("is_dir");
			if(!isFile)
			{
				System.out.println("O path " + path + "não corresponde a um ficheiro");
				throw new InfoNotFoundException("O path " + path + "não corresponde a um ficheiro");
			}
				
			String name = (String) file.get("path");
			int index = name.lastIndexOf("/");
			if(index > -1);
				name = name.substring(index+1);
			Date dt = new Date((String)file.get("modified"));
			return new FileInfo(name, (long)file.get("bytes"), dt, isFile, 0, 0);
		}
		catch(Exception e)
		{
			return null;
		}

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