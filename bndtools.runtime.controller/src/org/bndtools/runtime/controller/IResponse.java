package org.bndtools.runtime.controller;

import java.io.InputStream;
import java.util.Properties;

public interface IResponse {

    public static final String HTTP_OK = "200 OK";
    public static final String HTTP_REDIRECT = "301 Moved Permanently";
    public static final String HTTP_FORBIDDEN = "403 Forbidden";
    public static final String HTTP_NOTFOUND = "404 Not Found";
    public static final String HTTP_BADREQUEST = "400 Bad Request";
    public static final String HTTP_INTERNALERROR = "500 Internal IServer Error";
    public static final String HTTP_NOTIMPLEMENTED = "501 Not Implemented";

    public static final String MIME_PLAINTEXT = "text/plain";
    public static final String MIME_HTML = "text/html";
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    public static final String MIME_XML = "text/xml";

    String getStatus();
    String getMimeType();
    Properties getHeaders();
    InputStream getData();
    long getContentLength();

}
