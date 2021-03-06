
package ws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "FileServerWSService", targetNamespace = "http://ws.srv/", wsdlLocation = "http://localhost:8080/FileServerWs?wsdl")
public class FileServerWSService
    extends Service
{

    private final static URL FILESERVERWSSERVICE_WSDL_LOCATION;
    private final static WebServiceException FILESERVERWSSERVICE_EXCEPTION;
    private final static QName FILESERVERWSSERVICE_QNAME = new QName("http://ws.srv/", "FileServerWSService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8080/FileServerWs?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        FILESERVERWSSERVICE_WSDL_LOCATION = url;
        FILESERVERWSSERVICE_EXCEPTION = e;
    }

    public FileServerWSService() {
        super(__getWsdlLocation(), FILESERVERWSSERVICE_QNAME);
    }

    public FileServerWSService(WebServiceFeature... features) {
        super(__getWsdlLocation(), FILESERVERWSSERVICE_QNAME, features);
    }

    public FileServerWSService(URL wsdlLocation) {
        super(wsdlLocation, FILESERVERWSSERVICE_QNAME);
    }

    public FileServerWSService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, FILESERVERWSSERVICE_QNAME, features);
    }

    public FileServerWSService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FileServerWSService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns FileServerWS
     */
    @WebEndpoint(name = "FileServerWSPort")
    public FileServerWS getFileServerWSPort() {
        return super.getPort(new QName("http://ws.srv/", "FileServerWSPort"), FileServerWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns FileServerWS
     */
    @WebEndpoint(name = "FileServerWSPort")
    public FileServerWS getFileServerWSPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://ws.srv/", "FileServerWSPort"), FileServerWS.class, features);
    }

    private static URL __getWsdlLocation() {
        if (FILESERVERWSSERVICE_EXCEPTION!= null) {
            throw FILESERVERWSSERVICE_EXCEPTION;
        }
        return FILESERVERWSSERVICE_WSDL_LOCATION;
    }

}
