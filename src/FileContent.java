import java.util.*;
import javax.xml.datatype.XMLGregorianCalendar;

public class FileContent implements java.io.Serializable
{
	private static final long serialVersionUID = -4498079336259690561L;

	public String name;
	public long length;
	public Date modified;
	public boolean isFile;
	public byte[] content;
	
	public FileContent( ) {
	}
	
	public FileContent( String name, long length, Date modified, boolean isFile, byte[] content) {
		this.name = name;
		this.length = length;
		this.modified = modified;
		this.isFile = isFile;
		this.content = content;
	}
	

	public static Date toDate(XMLGregorianCalendar calendar){
	 if(calendar == null) {
     return null;
	 }
	 return calendar.toGregorianCalendar().getTime();
	}

	public byte[] getContent() {
		return this.content;
	}


	public String toString() {
		return "Name : " + name + "\nLength: " + length + "\nData modified: " + modified + "\nisFile : " + isFile + "\nContent : " + content; 
	}
}
