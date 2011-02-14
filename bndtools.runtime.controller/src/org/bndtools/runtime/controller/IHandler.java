package org.bndtools.runtime.controller;

import java.util.Properties;


public interface IHandler {
    /**
     * Handle a GET request.
     * @param queryPath The tokenized query path, excluding the prefix mapped to this handler.
     */
    IResponse handleGet(String[] queryPath, Properties params);

    /**
     * Handle a POST request.
     * @param queryPath The tokenized query path, excluding the prefix mapped to this handler.
     * @param uploads A map (file name -> filesystem path) of uploaded files (may be {@code null} or empty).
     */
    IResponse handlePost(String[] queryPath, Properties params, Properties uploads);

    /**
     * Handle a DELETE request
     * @param queryPath The tokenized query path, excluding the prefix mapped to this handler.
     */
    IResponse handleDelete(String[] queryPath, Properties params);

}
