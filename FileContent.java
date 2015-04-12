import java.util.*;

public class FileContent implements java.io.Serializable
{
	private static final long serialVersionUID = -4498079336259690561L;

	public String name;
	public long length;
	public Date modified;
	public boolean isFile;
	public byte[] content;
	
	public FileContent( String name, long length, Date modified, boolean isFile, byte[] content) {
		this.name = name;
		this.length = length;
		this.modified = modified;
		this.isFile = isFile;
		this.content = content;
	}
	
	public String toString() {
		return "Name : " + name + "\nLength: " + length + "\nData modified: " + modified + "\nisFile : " + isFile + "\nContent : " + content; 
	}
}
