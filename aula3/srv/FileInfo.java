package aula3.srv;

import java.util.*;

public class FileInfo implements java.io.Serializable
{
	public String name;
	public long length;
	public Date modified;
	public boolean isFile;
	
	FileInfo() {
	}

	public FileInfo( String name, long length, Date modified, boolean isFile) {
		this.name = name;
		this.length = length;
		this.modified = modified;
		this.isFile = isFile;
	}
	
	public String toString() {
		return "Name : " + name + "\nLength: " + length + "\nData modified: " + modified + "\nisFile : " + isFile; 
	}
}
