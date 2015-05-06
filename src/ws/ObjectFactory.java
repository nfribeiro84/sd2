
package ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetFileContentResponse_QNAME = new QName("http://ws.srv/", "getFileContentResponse");
    private final static QName _Cp_QNAME = new QName("http://ws.srv/", "cp");
    private final static QName _Dir_QNAME = new QName("http://ws.srv/", "dir");
    private final static QName _GetFileContent_QNAME = new QName("http://ws.srv/", "getFileContent");
    private final static QName _CpResponse_QNAME = new QName("http://ws.srv/", "cpResponse");
    private final static QName _SetAsPrimary_QNAME = new QName("http://ws.srv/", "setAsPrimary");
    private final static QName _SetAsPrimaryResponse_QNAME = new QName("http://ws.srv/", "setAsPrimaryResponse");
    private final static QName _Rmfile_QNAME = new QName("http://ws.srv/", "rmfile");
    private final static QName _DirResponse_QNAME = new QName("http://ws.srv/", "dirResponse");
    private final static QName _GetFileInfoResponse_QNAME = new QName("http://ws.srv/", "getFileInfoResponse");
    private final static QName _GetFileInfo_QNAME = new QName("http://ws.srv/", "getFileInfo");
    private final static QName _IOException_QNAME = new QName("http://ws.srv/", "IOException");
    private final static QName _RmdirResponse_QNAME = new QName("http://ws.srv/", "rmdirResponse");
    private final static QName _MkdirResponse_QNAME = new QName("http://ws.srv/", "mkdirResponse");
    private final static QName _CreateFileResponse_QNAME = new QName("http://ws.srv/", "createFileResponse");
    private final static QName _RunResponse_QNAME = new QName("http://ws.srv/", "runResponse");
    private final static QName _Rmdir_QNAME = new QName("http://ws.srv/", "rmdir");
    private final static QName _Mkdir_QNAME = new QName("http://ws.srv/", "mkdir");
    private final static QName _Run_QNAME = new QName("http://ws.srv/", "run");
    private final static QName _CreateFile_QNAME = new QName("http://ws.srv/", "createFile");
    private final static QName _RmfileResponse_QNAME = new QName("http://ws.srv/", "rmfileResponse");
    private final static QName _InfoNotFoundException_QNAME = new QName("http://ws.srv/", "InfoNotFoundException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetFileInfo }
     * 
     */
    public GetFileInfo createGetFileInfo() {
        return new GetFileInfo();
    }

    /**
     * Create an instance of {@link IOException }
     * 
     */
    public IOException createIOException() {
        return new IOException();
    }

    /**
     * Create an instance of {@link RmdirResponse }
     * 
     */
    public RmdirResponse createRmdirResponse() {
        return new RmdirResponse();
    }

    /**
     * Create an instance of {@link CreateFileResponse }
     * 
     */
    public CreateFileResponse createCreateFileResponse() {
        return new CreateFileResponse();
    }

    /**
     * Create an instance of {@link MkdirResponse }
     * 
     */
    public MkdirResponse createMkdirResponse() {
        return new MkdirResponse();
    }

    /**
     * Create an instance of {@link RunResponse }
     * 
     */
    public RunResponse createRunResponse() {
        return new RunResponse();
    }

    /**
     * Create an instance of {@link Rmdir }
     * 
     */
    public Rmdir createRmdir() {
        return new Rmdir();
    }

    /**
     * Create an instance of {@link Mkdir }
     * 
     */
    public Mkdir createMkdir() {
        return new Mkdir();
    }

    /**
     * Create an instance of {@link Run }
     * 
     */
    public Run createRun() {
        return new Run();
    }

    /**
     * Create an instance of {@link CreateFile }
     * 
     */
    public CreateFile createCreateFile() {
        return new CreateFile();
    }

    /**
     * Create an instance of {@link RmfileResponse }
     * 
     */
    public RmfileResponse createRmfileResponse() {
        return new RmfileResponse();
    }

    /**
     * Create an instance of {@link InfoNotFoundException }
     * 
     */
    public InfoNotFoundException createInfoNotFoundException() {
        return new InfoNotFoundException();
    }

    /**
     * Create an instance of {@link GetFileContentResponse }
     * 
     */
    public GetFileContentResponse createGetFileContentResponse() {
        return new GetFileContentResponse();
    }

    /**
     * Create an instance of {@link Cp }
     * 
     */
    public Cp createCp() {
        return new Cp();
    }

    /**
     * Create an instance of {@link Dir }
     * 
     */
    public Dir createDir() {
        return new Dir();
    }

    /**
     * Create an instance of {@link SetAsPrimary }
     * 
     */
    public SetAsPrimary createSetAsPrimary() {
        return new SetAsPrimary();
    }

    /**
     * Create an instance of {@link CpResponse }
     * 
     */
    public CpResponse createCpResponse() {
        return new CpResponse();
    }

    /**
     * Create an instance of {@link GetFileContent }
     * 
     */
    public GetFileContent createGetFileContent() {
        return new GetFileContent();
    }

    /**
     * Create an instance of {@link SetAsPrimaryResponse }
     * 
     */
    public SetAsPrimaryResponse createSetAsPrimaryResponse() {
        return new SetAsPrimaryResponse();
    }

    /**
     * Create an instance of {@link Rmfile }
     * 
     */
    public Rmfile createRmfile() {
        return new Rmfile();
    }

    /**
     * Create an instance of {@link DirResponse }
     * 
     */
    public DirResponse createDirResponse() {
        return new DirResponse();
    }

    /**
     * Create an instance of {@link GetFileInfoResponse }
     * 
     */
    public GetFileInfoResponse createGetFileInfoResponse() {
        return new GetFileInfoResponse();
    }

    /**
     * Create an instance of {@link FileContent }
     * 
     */
    public FileContent createFileContent() {
        return new FileContent();
    }

    /**
     * Create an instance of {@link FileInfo }
     * 
     */
    public FileInfo createFileInfo() {
        return new FileInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFileContentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "getFileContentResponse")
    public JAXBElement<GetFileContentResponse> createGetFileContentResponse(GetFileContentResponse value) {
        return new JAXBElement<GetFileContentResponse>(_GetFileContentResponse_QNAME, GetFileContentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Cp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "cp")
    public JAXBElement<Cp> createCp(Cp value) {
        return new JAXBElement<Cp>(_Cp_QNAME, Cp.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Dir }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "dir")
    public JAXBElement<Dir> createDir(Dir value) {
        return new JAXBElement<Dir>(_Dir_QNAME, Dir.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFileContent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "getFileContent")
    public JAXBElement<GetFileContent> createGetFileContent(GetFileContent value) {
        return new JAXBElement<GetFileContent>(_GetFileContent_QNAME, GetFileContent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CpResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "cpResponse")
    public JAXBElement<CpResponse> createCpResponse(CpResponse value) {
        return new JAXBElement<CpResponse>(_CpResponse_QNAME, CpResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAsPrimary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "setAsPrimary")
    public JAXBElement<SetAsPrimary> createSetAsPrimary(SetAsPrimary value) {
        return new JAXBElement<SetAsPrimary>(_SetAsPrimary_QNAME, SetAsPrimary.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAsPrimaryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "setAsPrimaryResponse")
    public JAXBElement<SetAsPrimaryResponse> createSetAsPrimaryResponse(SetAsPrimaryResponse value) {
        return new JAXBElement<SetAsPrimaryResponse>(_SetAsPrimaryResponse_QNAME, SetAsPrimaryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Rmfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "rmfile")
    public JAXBElement<Rmfile> createRmfile(Rmfile value) {
        return new JAXBElement<Rmfile>(_Rmfile_QNAME, Rmfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "dirResponse")
    public JAXBElement<DirResponse> createDirResponse(DirResponse value) {
        return new JAXBElement<DirResponse>(_DirResponse_QNAME, DirResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFileInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "getFileInfoResponse")
    public JAXBElement<GetFileInfoResponse> createGetFileInfoResponse(GetFileInfoResponse value) {
        return new JAXBElement<GetFileInfoResponse>(_GetFileInfoResponse_QNAME, GetFileInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFileInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "getFileInfo")
    public JAXBElement<GetFileInfo> createGetFileInfo(GetFileInfo value) {
        return new JAXBElement<GetFileInfo>(_GetFileInfo_QNAME, GetFileInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IOException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "IOException")
    public JAXBElement<IOException> createIOException(IOException value) {
        return new JAXBElement<IOException>(_IOException_QNAME, IOException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RmdirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "rmdirResponse")
    public JAXBElement<RmdirResponse> createRmdirResponse(RmdirResponse value) {
        return new JAXBElement<RmdirResponse>(_RmdirResponse_QNAME, RmdirResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MkdirResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "mkdirResponse")
    public JAXBElement<MkdirResponse> createMkdirResponse(MkdirResponse value) {
        return new JAXBElement<MkdirResponse>(_MkdirResponse_QNAME, MkdirResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "createFileResponse")
    public JAXBElement<CreateFileResponse> createCreateFileResponse(CreateFileResponse value) {
        return new JAXBElement<CreateFileResponse>(_CreateFileResponse_QNAME, CreateFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RunResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "runResponse")
    public JAXBElement<RunResponse> createRunResponse(RunResponse value) {
        return new JAXBElement<RunResponse>(_RunResponse_QNAME, RunResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Rmdir }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "rmdir")
    public JAXBElement<Rmdir> createRmdir(Rmdir value) {
        return new JAXBElement<Rmdir>(_Rmdir_QNAME, Rmdir.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mkdir }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "mkdir")
    public JAXBElement<Mkdir> createMkdir(Mkdir value) {
        return new JAXBElement<Mkdir>(_Mkdir_QNAME, Mkdir.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Run }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "run")
    public JAXBElement<Run> createRun(Run value) {
        return new JAXBElement<Run>(_Run_QNAME, Run.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "createFile")
    public JAXBElement<CreateFile> createCreateFile(CreateFile value) {
        return new JAXBElement<CreateFile>(_CreateFile_QNAME, CreateFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RmfileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "rmfileResponse")
    public JAXBElement<RmfileResponse> createRmfileResponse(RmfileResponse value) {
        return new JAXBElement<RmfileResponse>(_RmfileResponse_QNAME, RmfileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.srv/", name = "InfoNotFoundException")
    public JAXBElement<InfoNotFoundException> createInfoNotFoundException(InfoNotFoundException value) {
        return new JAXBElement<InfoNotFoundException>(_InfoNotFoundException_QNAME, InfoNotFoundException.class, null, value);
    }

}
