
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

//import aula3.clt.FileServerImplWS;
//import aula3.clt.FileServerImplWSService;


/**
 * Classe base do cliente
 * @author nmp
 */
public class FileClient
{
	String contactServerURL;
	String username;
	IFileServer rmiServer;
	FileServerWS wsServer;
	
	protected FileClient( String url, String username) 
	{
		this.contactServerURL = url;
		this.username = username;
		this.rmiServer = null;
		this.wsServer = null;
	}
	
	/**
	 * metodo que cria a ligacao ao servidor de ficheiros
	 * Se o servidor de ficheiros for do tipo RMI cria o servidor rmiServer e coloca o de WebServices (wsServer) a null
	 * Se o servidor de ficheiros for do tipo WS cria o servidor wsServer e coloca o de rmi (rmiServer) a null
	 * @param fServer - nome do servidor. Pode ser do tipo Nome ou URL
	 * @param isUrl - boolean que indica se o servidor e do tipo Nome ou URL
	 */
	private boolean generateFileServer(String fServer, boolean isUrl)
	{
		try
		{
			String url = fServer;
			boolean isRMI = false;
			if(!isUrl)
			{
				String[] servidores = this.servers(fServer);
				url = servidores[0];
			}
			
			if(url.startsWith("http"))
			{
				FileServerWSService service = new FileServerWSService( new URL(url), new QName("http://soap.srv/", "FileServerWSService"));
				this.wsServer = service.getFileServerWSPort();
				this.rmiServer = null;
			}
			else
			{
				this.wsServer = null;
				this.rmiServer = (IFileServer) Naming.lookup(url);
			}
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	private String buildUrl(String urlIp, String serverName)
	{
		return "rmi://"+urlIp+"/"+serverName;
	}
	

	/**
	 * Devolve um array com os servidores a correr caso o name== null ou o URL dos
	 * servidores com nome name.
	 */
	protected String[] servers(String name) 
	{
		//System.err.println( "exec: servers");
		String[] result;
		try 
		{
			IContactServer contactServer = (IContactServer) Naming.lookup(this.contactServerURL);
			if (name == null)
				result = contactServer.servers();
			else
				result = contactServer.servers(name);
		} 
		catch (Exception e) 
		{
			result = new String[1];
			result[0] = "Ocorreu um erro ao ligar ao servidor de contacto. Erro: " + e.toString();			
		}
		
		return result;
	}
	
	/**
	 * Devolve um array com os ficheiros/directoria na directoria dir no servidor server
	 * (ou no sistema de ficheiros do cliente caso server == null).
	 * Se isURL for verdadeiro, server representa um URL para o servior (e.g. //127.0.0.1/myServer).
	 * Caso contrario e o nome do servidor. Nesse caso deve listar os ficheiro dum servidor com esse nome.
	 * Devolve null em caso de erro.
	 * NOTA: nao deve lancar excepcao. 
	 * 
	 */
	protected String[] dir( String server, boolean isURL, String dir)
	{
		try
		{
			if(!generateFileServer(server,isURL))
			{
				System.out.println("DIR ERROR - Cannot connect to File Server '"+server+"'");
				return null;
			}
			
			if(rmiServer == null)
			{
				//o servidor de ficheiro e do tipo WS
				return wsServer.dir(dir);
			}
			else
			{
				//o servidor de ficheiros e do tipo RMI
				return rmiServer.dir(dir);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception on 'dir': " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Cria a directoria dir no servidor server@user
	 * (ou no sistema de ficheiros do cliente caso server == null).
	 * Se isURL for verdadeiro, server representa um URL para o servior (e.g. //127.0.0.1/myServer).
	 * Caso contrario e o nome do servidor. Nesse caso deve listar os ficheiro dum servidor com esse nome.
	 * Devolve false em caso de erro.
	 * NOTA: nao deve lancar excepcao. 
	 */
	protected boolean mkdir( String server, boolean isURL, String dir) 
	{
		try
		{
			if(!generateFileServer(server,isURL))
			{
				System.out.println("MKDIR ERROR - Cannot connect to File Server '"+server+"'");
				return false;
			}
			
			if(rmiServer == null)
				wsServer.mkdir(dir);			
			else
				return rmiServer.mkdir(dir);			
		}
		catch(Exception e)
		{
			System.out.println("Exception in 'mkdir': "+ e.getMessage());
			return false;
		}		
	}

	/**
	 * Remove a directoria dir no servidor server@user
	 * (ou no sistema de ficheiros do cliente caso server == null).
	 * Se isURL for verdadeiro, server representa um URL para o servior (e.g. //127.0.0.1/myServer).
	 * Caso contrario e o nome do servidor. Nesse caso deve listar os ficheiro dum servidor com esse nome.
	 * Devolve false em caso de erro.
	 * NOTA: nao deve lancar excepcao. 
	 */
	protected boolean rmdir( String server, boolean isURL, String dir) 
	{
		try
		{
			if(!generateFileServer(server,isURL))
			{
				System.out.println("RMDIR ERROR - Cannot connect to FileServer '"+server+"'");
				return false;
			}
			
			if(rmiServer == null)
				return wsServer.rmdir(dir);
			else
				return rmiServer.rmdir(dir);
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in 'RMDIR': "+e.getMessage());
			return false;
		}
	}

	/**
	 * Remove o ficheiro path no servidor server@user.
	 * (ou no sistema de ficheiros do cliente caso server == null).
	 * Se isURL for verdadeiro, server representa um URL para o servior (e.g. //127.0.0.1/myServer).
	 * Caso contrario e o nome do servidor. Nesse caso deve listar os ficheiro dum servidor com esse nome.
	 * Devolve false em caso de erro.
	 * NOTA: nao deve lancar excepcao. 
	 */
	protected boolean rm( String server, boolean isURL, String path) 
	{
		try
		{
			if(!generateFileServer(server,isURL))
			{
				System.out.println("RM ERROR - Cannot connect to FileServer '"+server+"'");
				return false;
			}
			
			if(rmiServer == null)
				return wsServer.rmfile(path);
			else
				return rmiServer.rmfile(path);			
		}
		catch(Exception e)
		{
			System.out.println("Exception in 'RM': "+e.getMessage());
			return false;
		}
	}

	/**
	 * Devolve informacao sobre o ficheiro/directoria path no servidor server@user.
	 * (ou no sistema de ficheiros do cliente caso server == null).
	 * Se isURL for verdadeiro, server representa um URL para o servior (e.g. //127.0.0.1/myServer).
	 * Caso contrario e o nome do servidor. Nesse caso deve listar os ficheiro dum servidor com esse nome.
	 * Devolve false em caso de erro.
	 * NOTA: nao deve lancar excepcao. 
	 */
	protected FileInfo getAttr( String server, boolean isURL, String path) 
	{
		try
		{
			if(!generateFileServer(server,isURL))
			{
				System.out.println("GETATTR ERROR - Cannot connect to FileServer '"+server+"'");
				return null;
			}
			
			if(rmiServer == null)
				return new FileInfo(wsServer.getFileInfo(path));
			else
				return rmiServer.getFileInfo(path);
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in 'GETATTR': "+e.getMessage());
			return null;
		}
	}

	/**
	 * Copia ficheiro de fromPath no servidor fromServer@fromUser para o ficheiro 
	 * toPath no servidor toServer@toUser.
	 * (caso fromServer/toServer == local, corresponde ao sistema de ficheiros do cliente).
	 * Devolve false em caso de erro.
	 * NOTA: nao deve lancar excepcao. 
	 */
	protected boolean cp( String fromServer, boolean fromIsURL, String fromPath,
							String toServer, boolean toIsURL, String toPath) 
	{
		rmi.FileContent content = null;
		
		if(fromServer == null)
		{
			//o ficheiro esta na directoria do cliente
			File f = new File( fromPath );

			if( f.exists() && f.isFile() ) 
			{
				try
				{
					RandomAccessFile raf = new RandomAccessFile( f, "r" );
					byte []b = new byte[FileServer.safeLongToInt(raf.length())];
					raf.readFully(b);
					raf.close();

					content =  new rmi.FileContent( fromPath, f.length(), new Date(f.lastModified()), f.isFile(), b);
				}
				catch(Exception e)
				{
					System.out.println("Exception in 'CP' while acessing the file: " + e.getMessage());
					return false;
				}
			}
			else
				return false;
		}
		else
		{
			//o ficheiro esta na directoria de um servidor
			try
			{
				if(!generateFileServer(fromServer, fromIsURL))
				{
					System.out.println("CP ERROR - Cannot connecto to From File Server '"+fromServer+"'");
					return false;
				}
				if(rmiServer == null)
					content = (rmi.FileContent) wsServer.getFileContent(fromPath);
				else
					content = rmiServer.getFileContent(fromPath);
			}
			catch(Exception e)
			{
				System.out.println("Exception in 'CP fromServer': "+e.getMessage());
				return false;
			}			
		}
		
		if(content == null) 
		{
			System.out.println("File not found!");	
			return false;
		}


		boolean success = false;

		//copiar o ficheiro para a maquina do cliente
		if(toServer == null)
		{
			try 
			{
			      RandomAccessFile raf = new RandomAccessFile(toPath, "rw");
			      raf.write(content.content);
			      raf.close();
			      return true;
			}
			catch(Exception e) 
			{
				System.out.println("Exception in 'CP toServer':"+e.getMessage());
			    return false;
			}
		}
		else
		{//copiar para um servidor
			try
			{
				if(!generateFileServer(toServer, toIsURL))
				{
					System.out.println("CP ERROR - Cannot connecto to To File Server '"+toServer+"'");
					return false;
				}
				if(rmiServer == null)
					return wsServer.createFile(toPath, content);
				else
					return rmiServer.createFile(toPath, content);
			}
			catch(Exception e)
			{
				System.out.println("Exception in 'CP toServer': "+e.getMessage());
				return false;
			}
		}
	}

	
	protected void doit() throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in));		
		for( ; ; ) 
		{
			System.out.print("> ");
			String line = reader.readLine();
			if( line == null)
				break;
			String[] cmd = line.split(" ");
			if( cmd[0].equalsIgnoreCase("servers")) 
			{
				String[] s = servers( cmd.length == 1 ? null : cmd[1]);
				
				if( s == null)
					System.out.println( "error");
				else 
				{
					System.out.println( s.length);
					for( int i = 0; i < s.length; i++)
						System.out.println( s[i]);
				}
			} 
			else if( cmd[0].equalsIgnoreCase("ls")) 
			{
				String[] dirserver = cmd[1].split("@");
				String server = dirserver.length == 1 ? null : dirserver[0];
				boolean isURL = dirserver.length == 1 ? false : dirserver[0].indexOf('/') >= 0;
				String dir = dirserver.length == 1 ? dirserver[0] : dirserver[1];
				
				String[] res = dir( server, isURL, dir);
				if( res != null) 
				{
					System.out.println( res.length);
					for( int i = 0; i < res.length; i++)
						System.out.println( res[i]);
				} 
				else
					System.out.println( "error");
			} 
			else if( cmd[0].equalsIgnoreCase("mkdir")) 
			{
				String[] dirserver = cmd[1].split("@");
				String server = dirserver.length == 1 ? null : dirserver[0];
				boolean isURL = dirserver.length == 1 ? false : dirserver[0].indexOf('/') >= 0;
				String dir = dirserver.length == 1 ? dirserver[0] : dirserver[1];

				boolean b = mkdir( server, isURL, dir);
				if( b)
					System.out.println( "success");
				else
					System.out.println( "error");
			} 
			else if( cmd[0].equalsIgnoreCase("rmdir")) 
			{
				String[] dirserver = cmd[1].split("@");
				String server = dirserver.length == 1 ? null : dirserver[0];
				boolean isURL = dirserver.length == 1 ? false : dirserver[0].indexOf('/') >= 0;
				String dir = dirserver.length == 1 ? dirserver[0] : dirserver[1];

				boolean b = rmdir( server, isURL, dir);
				if( b)
					System.out.println( "success");
				else
					System.out.println( "error");
			} 
			else if( cmd[0].equalsIgnoreCase("rm")) 
			{
				String[] dirserver = cmd[1].split("@");
				String server = dirserver.length == 1 ? null : dirserver[0];
				boolean isURL = dirserver.length == 1 ? false : dirserver[0].indexOf('/') >= 0;
				String path = dirserver.length == 1 ? dirserver[0] : dirserver[1];

				boolean b = rm( server, isURL, path);
				if( b)
					System.out.println( "success");
				else
					System.out.println( "error");
			} 
			else if( cmd[0].equalsIgnoreCase("getattr")) 
			{
				String[] dirserver = cmd[1].split("@");
				String server = dirserver.length == 1 ? null : dirserver[0];
				boolean isURL = dirserver.length == 1 ? false : dirserver[0].indexOf('/') >= 0;
				String path = dirserver.length == 1 ? dirserver[0] : dirserver[1];

				FileInfo info = getAttr( server, isURL, path);
				if( info != null) 
				{
					System.out.println( info);
					System.out.println( "success");
				} 
				else
					System.out.println( "error");
			} 
			else if( cmd[0].equalsIgnoreCase("cp")) 
			{
				String[] dirserver1 = cmd[1].split("@");
				String server1 = dirserver1.length == 1 ? null : dirserver1[0];
				boolean isURL1 = dirserver1.length == 1 ? false : dirserver1[0].indexOf('/') >= 0;
				String path1 = dirserver1.length == 1 ? dirserver1[0] : dirserver1[1];

				String[] dirserver2 = cmd[2].split("@");
				String server2 = dirserver2.length == 1 ? null : dirserver2[0];
				boolean isURL2 = dirserver2.length == 1 ? false : dirserver2[0].indexOf('/') >= 0;
				String path2 = dirserver2.length == 1 ? dirserver2[0] : dirserver2[1];

				boolean b = cp( server1, isURL1, path1, server2, isURL2, path2);
				if( b)
					System.out.println( "success");
				else
					System.out.println( "error");
			} 
			else if( cmd[0].equalsIgnoreCase("help")) 
			{
				System.out.println("servers - lista nomes de servidores a executar");
				System.out.println("servers nome - lista URL dos servidores com nome nome");
				System.out.println("ls server@dir - lista ficheiros/directorias presentes na directoria dir (. e .. tem o significado habitual), caso existam ficheiros com o mesmo nome devem ser apresentados como nome@server;");
				System.out.println("mkdir server@dir - cria a directoria dir no servidor server");
				System.out.println("rmdir server@udir - remove a directoria dir no servidor server");
				System.out.println("cp path1 path2 - copia o ficheiro path1 para path2; quando path representa um ficheiro num servidor deve ter a forma server:path, quando representa um ficheiro local deve ter a forma path");
				System.out.println("rm path - remove o ficheiro path");
				System.out.println("getattr path - apresenta informacao sobre o ficheiro/directoria path, incluindo: nome, boolean indicando se e ficheiro, data da criacao, data da ultima modificacao");
			} 
			else if( cmd[0].equalsIgnoreCase("exit"))
				break;

		}
	}
	
	public static void main( String[] args) {
		if( args.length != 2) {
			System.out.println("Use: java trab1.FileClient ContactServerURL nome_utilizador");
			return;
		}
		try {
			new FileClient( args[0], args[1]).doit();
		} catch (IOException e) {
			System.err.println("Error:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
