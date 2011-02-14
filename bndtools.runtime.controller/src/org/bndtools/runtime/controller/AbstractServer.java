package org.bndtools.runtime.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

public abstract class AbstractServer implements IServer {

    private Map handlers = new HashMap();

    public void registerHandler(String prefix, IHandler handler) {
        handlers.put(prefix, handler);
    }

    public void unregisterHandler(String prefix) {
        handlers.remove(prefix);
    }

    protected String[] tokenizePath(String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        String[] result = new String[tokenizer.countTokens()];

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            result[i++] = tokenizer.nextToken();
        }

        return result;
    }
    
    protected String detokenize(String[] tokenizedPath) {
    	StringBuffer buffer = new StringBuffer();
    	
    	for (int i = 0; i < tokenizedPath.length; i++) {
			buffer.append('/').append(tokenizedPath[i]);
		}
    	
    	return buffer.toString();
    }

    protected String[] drop(String[] array, int count) {
        if (array == null)
            throw new NullPointerException("null array");
        else if (array.length - count < 0)
            throw new IllegalArgumentException("not enough array elements");
        else {
            String[] copy = new String[array.length - count];
            System.arraycopy(array, count, copy, 0, copy.length);
            return copy;
        }
    }

    protected IResponse dispatch(String method, String[] tokenizedPath, Properties params, Properties uploads, InputStream content) {
        IResponse response;

    	if (tokenizedPath.length == 0 && "GET".equals(method)) {
        	// Handle "GET /" to return a list of path prefixes
            String commandList = listCommandsText();
            response = new DefaultResponse(IResponse.HTTP_OK, IResponse.MIME_PLAINTEXT, commandList);
    	} else if (tokenizedPath.length > 0) {
    		// Dispatch to a handler based on path prefix
            IHandler handler = (IHandler) handlers.get(tokenizedPath[0]);
            if (handler != null) {
                String[] queryPath = drop(tokenizedPath, 1);
                if ("GET".equals(method))
                    response = handler.handleGet(queryPath, params);
                else if ("POST".equals(method))
                    response = handler.handlePost(queryPath, params, uploads, content);
                else if ("DELETE".equals(method))
                    response = handler.handleDelete(queryPath, params);
                else if ("PUT".equals(method))
                	response = handler.handlePut(queryPath, params, uploads, content);
                else
                    response = new DefaultResponse(IResponse.HTTP_BADREQUEST, IResponse.MIME_PLAINTEXT, "Unsupported method: " + method);

                if (response == null)
                	response = DefaultResponse.createInternalError(null);
            } else {
                response = new DefaultResponse(IResponse.HTTP_NOTFOUND, IResponse.MIME_PLAINTEXT, "Not found: " + detokenize(tokenizedPath));
            }
        } else {
        	response = new DefaultResponse(IResponse.HTTP_BADREQUEST, IResponse.MIME_PLAINTEXT, "Invalid request.");
        }

        return response;
    }

    String listCommandsText() {
        StringBuffer buffer = new StringBuffer();
        for (Iterator iter = handlers.entrySet().iterator(); iter.hasNext(); ) {
            Entry entry = (Entry) iter.next();
            buffer.append(entry.getKey());
            buffer.append('\n');
        }
        String commandList = buffer.toString();
        return commandList;
    }

}
