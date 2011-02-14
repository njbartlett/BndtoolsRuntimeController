package org.bndtools.runtime.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class DefaultResponse implements IResponse {

    private final String status;
    private final String mimeType;
    private final InputStream data;
    private final long contentLength;
    private final Properties headers = new Properties();

    public static IResponse createRedirect(String url) {
        return new DefaultResponse(HTTP_REDIRECT, MIME_HTML, "<html><body>Redirected: <a href=\"" + url + "\">" + url + "</a></body></html>");
    }
    
    public static IResponse createInternalError(Throwable t) {
    	return new DefaultResponse(HTTP_INTERNALERROR, MIME_PLAINTEXT, t != null ? t.getMessage() : "Internal error");
    }
    
    public static IResponse createBadRequestError(String message) {
    	return new DefaultResponse(HTTP_BADREQUEST, MIME_PLAINTEXT, message != null ? message : "Invalid request");
    }

    public DefaultResponse(String status, String mimeType, String textData) {
        this.status = status;
        this.mimeType = mimeType;

        try {
            byte[] bytes = textData.getBytes("UTF-8");
            this.data = new ByteArrayInputStream(bytes);
            this.contentLength = bytes.length;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultResponse(String status, String mimeType, InputStream data, long contentLength) {
        this.status = status;
        this.mimeType = mimeType;
        this.data = data;
        this.contentLength = contentLength;
    }

    public String getStatus() {
        return status;
    }

    public String getMimeType() {
        return mimeType;
    }

    public InputStream getData() {
        return data;
    }

    public long getContentLength() {
        return contentLength;
    }

    public Properties getHeaders() {
        return headers;
    }

}
