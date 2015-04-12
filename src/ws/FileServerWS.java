
package ws;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "FileServerWS", targetNamespace = "http://ws.srv/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface FileServerWS {


    /**
     * 
     */
    @WebMethod
    @RequestWrapper(localName = "run", targetNamespace = "http://ws.srv/", className = "ws.Run")
    @ResponseWrapper(localName = "runResponse", targetNamespace = "http://ws.srv/", className = "ws.RunResponse")
    @Action(input = "http://ws.srv/FileServerWS/runRequest", output = "http://ws.srv/FileServerWS/runResponse")
    public void run();

    /**
     * 
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "mkdir", targetNamespace = "http://ws.srv/", className = "ws.Mkdir")
    @ResponseWrapper(localName = "mkdirResponse", targetNamespace = "http://ws.srv/", className = "ws.MkdirResponse")
    @Action(input = "http://ws.srv/FileServerWS/mkdirRequest", output = "http://ws.srv/FileServerWS/mkdirResponse")
    public boolean mkdir(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "rmdir", targetNamespace = "http://ws.srv/", className = "ws.Rmdir")
    @ResponseWrapper(localName = "rmdirResponse", targetNamespace = "http://ws.srv/", className = "ws.RmdirResponse")
    @Action(input = "http://ws.srv/FileServerWS/rmdirRequest", output = "http://ws.srv/FileServerWS/rmdirResponse")
    public boolean rmdir(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns boolean
     * @throws IOException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "cp", targetNamespace = "http://ws.srv/", className = "ws.Cp")
    @ResponseWrapper(localName = "cpResponse", targetNamespace = "http://ws.srv/", className = "ws.CpResponse")
    @Action(input = "http://ws.srv/FileServerWS/cpRequest", output = "http://ws.srv/FileServerWS/cpResponse", fault = {
        @FaultAction(className = IOException_Exception.class, value = "http://ws.srv/FileServerWS/cp/Fault/IOException")
    })
    public boolean cp(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1)
        throws IOException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "rmfile", targetNamespace = "http://ws.srv/", className = "ws.Rmfile")
    @ResponseWrapper(localName = "rmfileResponse", targetNamespace = "http://ws.srv/", className = "ws.RmfileResponse")
    @Action(input = "http://ws.srv/FileServerWS/rmfileRequest", output = "http://ws.srv/FileServerWS/rmfileResponse")
    public boolean rmfile(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns ws.FileInfo
     * @throws InfoNotFoundException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getFileInfo", targetNamespace = "http://ws.srv/", className = "ws.GetFileInfo")
    @ResponseWrapper(localName = "getFileInfoResponse", targetNamespace = "http://ws.srv/", className = "ws.GetFileInfoResponse")
    @Action(input = "http://ws.srv/FileServerWS/getFileInfoRequest", output = "http://ws.srv/FileServerWS/getFileInfoResponse", fault = {
        @FaultAction(className = InfoNotFoundException_Exception.class, value = "http://ws.srv/FileServerWS/getFileInfo/Fault/InfoNotFoundException")
    })
    public FileInfo getFileInfo(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws InfoNotFoundException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns ws.FileContent
     * @throws IOException_Exception
     * @throws InfoNotFoundException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getFileContent", targetNamespace = "http://ws.srv/", className = "ws.GetFileContent")
    @ResponseWrapper(localName = "getFileContentResponse", targetNamespace = "http://ws.srv/", className = "ws.GetFileContentResponse")
    @Action(input = "http://ws.srv/FileServerWS/getFileContentRequest", output = "http://ws.srv/FileServerWS/getFileContentResponse", fault = {
        @FaultAction(className = InfoNotFoundException_Exception.class, value = "http://ws.srv/FileServerWS/getFileContent/Fault/InfoNotFoundException"),
        @FaultAction(className = IOException_Exception.class, value = "http://ws.srv/FileServerWS/getFileContent/Fault/IOException")
    })
    public FileContent getFileContent(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws IOException_Exception, InfoNotFoundException_Exception
    ;

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns boolean
     * @throws InfoNotFoundException_Exception
     * @throws IOException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createFile", targetNamespace = "http://ws.srv/", className = "ws.CreateFile")
    @ResponseWrapper(localName = "createFileResponse", targetNamespace = "http://ws.srv/", className = "ws.CreateFileResponse")
    @Action(input = "http://ws.srv/FileServerWS/createFileRequest", output = "http://ws.srv/FileServerWS/createFileResponse", fault = {
        @FaultAction(className = InfoNotFoundException_Exception.class, value = "http://ws.srv/FileServerWS/createFile/Fault/InfoNotFoundException"),
        @FaultAction(className = IOException_Exception.class, value = "http://ws.srv/FileServerWS/createFile/Fault/IOException")
    })
    public boolean createFile(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        FileContent arg1)
        throws IOException_Exception, InfoNotFoundException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns java.util.List<java.lang.String>
     * @throws InfoNotFoundException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "dir", targetNamespace = "http://ws.srv/", className = "ws.Dir")
    @ResponseWrapper(localName = "dirResponse", targetNamespace = "http://ws.srv/", className = "ws.DirResponse")
    @Action(input = "http://ws.srv/FileServerWS/dirRequest", output = "http://ws.srv/FileServerWS/dirResponse", fault = {
        @FaultAction(className = InfoNotFoundException_Exception.class, value = "http://ws.srv/FileServerWS/dir/Fault/InfoNotFoundException")
    })
    public List<String> dir(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws InfoNotFoundException_Exception
    ;

}