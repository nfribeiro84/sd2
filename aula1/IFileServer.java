package aula1;

import java.rmi.*;
import java.util.*;
import java.io.*;


public interface IFileServer extends Remote
{
	/**
	 * Lista nome de ficheiros num dado directorio
	 */
	public String[] dir( String path) throws RemoteException, InfoNotFoundException;
	
	/**
	 * Devolve informacao sobre ficheiro.
	 */
	public FileInfo getFileInfo( String path, String name) throws RemoteException, InfoNotFoundException;
	
	/**
	 * Devolve informacao sobre ficheiro.
	 */
	public FileContent getFileContent( String path, String name) throws RemoteException, InfoNotFoundException, IOException;

}
