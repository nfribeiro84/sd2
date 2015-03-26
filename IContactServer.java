

import java.rmi.*;
import java.util.*;
import java.io.*;


public interface IContactServer extends Remote
{
	/**
	 * Lista os nomes de servidores de ficheiros conhecidos pelo sistema.
	 */
	public String[] servers() throws RemoteException;

	/**
	 * Lista os endereços dos servidores de ficheiros com um dado nome.
	 */
	public String[] servers(String name) throws RemoteException;

	/**
	 * Adiciona um novo servidor à lista de servidores conhecidos.
	 */
	public boolean subscribe(String name) throws RemoteException, ServerExistsException;

	/**
	 * Resfresca o ultimo contacto do servidor de ficheiros na timetable.
	 */
	public boolean ping(String name) throws RemoteException;
	
}
