package org.bndtools.runtime.controller.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public class Utils {
    public static Representation buildUploadRepresentation(Map<String, String> formFields, File... files) {
        MultipartEntity multipartEntity = new MultipartEntity();

        int i = 0;
        if (formFields != null) {
            for (Entry<String,String> entry : formFields.entrySet()) {
                try {
                    FormBodyPart formPart = new FormBodyPart(entry.getKey(), new StringBody(entry.getValue()));
                    multipartEntity.addPart(formPart);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (File file : files) {
            FileBody fileBody = new FileBody(file, MediaType.APPLICATION_OCTET_STREAM.getName());
            multipartEntity.addPart(Integer.toString(i++), fileBody);
        }
        return new MultipartEntityRepresentation(multipartEntity);
    }

    public static BundleHeader findBundleByBSN(String bsn, Collection<BundleHeader> bundles) {
        BundleHeader match = null;
        for (BundleHeader header : bundles) {
            if (bsn.equals(header.getBsn())) {
                match = header;
                break;
            }
        }
        return match;
    }


}
