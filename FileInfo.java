

import java.util.*;

public class FileInfo implements java.io.Serializable
{
	private static final long serialVersionUID = -4498079336259690561L;

	public String name;
	public long length;
	public Date modified;
	public boolean isFile;
	public int childrenFiles;
	public int childrenDirectories;
	
	public FileInfo( String name, long length, Date modified, boolean isFile, int childrenDirectories, int childrenFiles) 
	{
		this.name = name;
		this.length = length;
		this.modified = modified;
		this.isFile = isFile;
		this.childrenFiles = childrenFiles;
		this.childrenDirectories = childrenDirectories;
	}

	/**
	*		WS contructor
	*/
	public FileInfo( String name, long length, Date modified, boolean isFile) {
		this.name = name;
		this.length = length;
		this.modified = modified;
		this.isFile = isFile;
	}
	
	public String toString() 
	{
		if(this.isFile)
			return "Name : " + name + "\nLength: " + length + "\nData modified: " + modified + "\nisFile : " + isFile;
		return "Name : " + name + "\nData modified: " + modified + "\nisFile : " + isFile + "\nNumero de Directorios: "+childrenDirectories+"\nNumero de Ficheiros: "+childrenFiles;
	}
}
