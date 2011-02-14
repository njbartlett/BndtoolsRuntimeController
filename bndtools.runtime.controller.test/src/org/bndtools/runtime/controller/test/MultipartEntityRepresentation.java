package org.bndtools.runtime.controller.test;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.entity.mime.MultipartEntity;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

public class MultipartEntityRepresentation extends OutputRepresentation {

    private final MultipartEntity entity;

    public MultipartEntityRepresentation(MultipartEntity entity) {
        super(createMediaType(entity.getContentType()));
        this.entity = entity;
        setSize(entity.getContentLength());
    }

    private static MediaType createMediaType(Header contentType) {
        return new MediaType(contentType.getValue());
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        entity.writeTo(outputStream);
    }

}
