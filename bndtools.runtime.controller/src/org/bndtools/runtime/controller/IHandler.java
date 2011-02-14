package org.bndtools.runtime.controller;

import java.io.InputStream;
import java.util.Properties;


public interface IHandler {
	/**
	 * Handle a GET request.
	 * 
	 * @param queryPath
	 *            The tokenized query path, excluding the prefix mapped to this
	 *            handler.
	 */
    IResponse handleGet(String[] queryPath, Properties params);

	/**
	 * Handle a POST request.
	 * 
	 * @param queryPath
	 *            The tokenized query path, excluding the prefix mapped to this
	 *            handler.
	 * @param uploads
	 *            A map (file name -> filesystem path) of uploaded files (may be
	 *            {@code null} or empty).
	 * @param content
	 *            the content stream of the request, if it could not be parsed
	 *            as form data (may be {@code null} or empty).
	 */
    IResponse handlePost(String[] queryPath, Properties params, Properties uploads, InputStream content);

	/**
	 * Handle a DELETE request
	 * 
	 * @param queryPath
	 *            The tokenized query path, excluding the prefix mapped to this
	 *            handler.
	 */
    IResponse handleDelete(String[] queryPath, Properties params);
    
    /**
	 * @param queryPath
	 *            The tokenized query path, excluding the prefix mapped to this
	 *            handler.
	 * @param uploads
	 *            A map (file name -> filesystem path) of uploaded files (may be
	 *            {@code null} or empty).
	 * @param content
	 *            the content stream of the request, if it could not be parsed
	 *            as form data (may be {@code null} or empty).
     */
    IResponse handlePut(String[] queryPath, Properties params, Properties uploads, InputStream content);

}
