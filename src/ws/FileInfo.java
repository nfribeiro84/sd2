
package ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for fileInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fileInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="isFile" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="childrenFiles" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="childrenDirectories" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="md5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileInfo", propOrder = {
    "name",
    "length",
    "modified",
    "isFile",
    "childrenFiles",
    "childrenDirectories",
    "md5"
})
public class FileInfo {

    protected String name;
    protected long length;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modified;
    protected boolean isFile;
    protected int childrenFiles;
    protected int childrenDirectories;
    protected String md5;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the length property.
     * 
     */
    public long getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     */
    public void setLength(long value) {
        this.length = value;
    }

    /**
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModified(XMLGregorianCalendar value) {
        this.modified = value;
    }

    /**
     * Gets the value of the isFile property.
     * 
     */
    public boolean isIsFile() {
        return isFile;
    }

    /**
     * Sets the value of the isFile property.
     * 
     */
    public void setIsFile(boolean value) {
        this.isFile = value;
    }

    /**
     * Gets the value of the childrenFiles property.
     * 
     */
    public int getChildrenFiles() {
        return childrenFiles;
    }

    /**
     * Sets the value of the childrenFiles property.
     * 
     */
    public void setChildrenFiles(int value) {
        this.childrenFiles = value;
    }

    /**
     * Gets the value of the childrenDirectories property.
     * 
     */
    public int getChildrenDirectories() {
        return childrenDirectories;
    }

    /**
     * Sets the value of the childrenDirectories property.
     * 
     */
    public void setChildrenDirectories(int value) {
        this.childrenDirectories = value;
    }

    /**
     * Gets the value of the md5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Sets the value of the md5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMd5(String value) {
        this.md5 = value;
    }

}
