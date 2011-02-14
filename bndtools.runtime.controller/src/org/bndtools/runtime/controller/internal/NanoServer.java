package org.bndtools.runtime.controller.internal;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.bndtools.runtime.controller.AbstractServer;
import org.bndtools.runtime.controller.IResponse;
import org.osgi.service.log.LogService;

public class NanoServer extends AbstractServer {

    private static final String PREFIX_FILE = "/files/";
    
	private final int port;
	private final Log log;
	
	private NanoHTTPD httpd;
    
    public NanoServer(int port, Log log) {
		this.port = port;
		this.log = log;
    }

    public void start() throws IOException {
    	log.log(null, LogService.LOG_INFO, "Starting NanoHTTPD on port " + port, null);
        httpd = new NanoHTTPD(port) {
            public Response serve(String uri, String method, Properties header, Properties params, Properties files) {
                Response response;
                if (uri.startsWith(PREFIX_FILE)) {
                    response = super.serveFile("/" + uri.substring(PREFIX_FILE.length()), header, new File(System.getProperty("user.dir")), false);
                } else {
                    String[] path = tokenizePath(uri);

                    Properties uploads = null;
                    if (!files.isEmpty()) {
                        uploads = new Properties();
                        for (Enumeration names = files.propertyNames(); names.hasMoreElements(); ) {
                            String propertyName = (String) names.nextElement();

                            String fileName = params.getProperty(propertyName);
                            String filePath = files.getProperty(propertyName);
                            uploads.setProperty(fileName, filePath);
                        }
                    }

                    IResponse r = dispatch(method, path, params, uploads);

                    response = new NanoHTTPD.Response(r.getStatus(), r.getMimeType(), r.getData());
                    response.header = r.getHeaders();
                    response.addHeader("Content-Length", Long.toString(r.getContentLength()));
                }
                return response;
            };
        };
        printWarning();
    }

    private void printWarning() {
        System.err.println("********************************************************************\n"
                + "* WARNING: The NanoHTTPD connector for Bndtools Runtime Controller *\n"
                + "* is active. DO NOT USE THIS IN A PRODUCTION ENVIRONMENT!!         *\n"
                + "* IT IS NOT SECURE.                                                *\n"
                + "********************************************************************\n");
    }

    public void stop() throws IOException {
        httpd.stop();
        log.log(null, LogService.LOG_INFO, "Stopped NanoHTTPD", null);
    }

}
