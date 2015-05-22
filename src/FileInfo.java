import java.util.*;
import javax.xml.datatype.XMLGregorianCalendar;


public class FileInfo implements java.io.Serializable
{
	private static final long serialVersionUID = -4498079336259690561L;

	public String name;
	public long length;
	public Date modified;
	public boolean isFile;
	public int childrenFiles;
	public int childrenDirectories;
	public String md5;
	
	
	public FileInfo( String name, long length, Date modified, boolean isFile, int childrenDirectories, int childrenFiles, String md5) 
	{
		this.name = name;
		this.length = length;
		this.modified = modified;
		this.isFile = isFile;
		this.childrenFiles = childrenFiles;
		this.childrenDirectories = childrenDirectories;
		this.md5 = md5;
	}

	/**
	*		WS contructor
	*/
	public FileInfo( String name, long length, Date modified, boolean isFile, String md5) {
		this.name = name;
		this.length = length;
		this.modified = modified;
		this.isFile = isFile;
		this.md5 = md5;
	}


	public static Date toDate(XMLGregorianCalendar calendar){
	 if(calendar == null) {
     return null;
	 }
	 return calendar.toGregorianCalendar().getTime();
	}
	
	public String toString() 
	{
		if(this.isFile)
			return "Name : " + name + "\nLength: " + length + "\nData modified: " + modified + "\nisFile : " + isFile;
		return "Name : " + name + "\nData modified: " + modified + "\nisFile : " + isFile + "\nNumero de Directorios: "+childrenDirectories+"\nNumero de Ficheiros: "+childrenFiles;
	}
}
