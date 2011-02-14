package org.bndtools.runtime.controller.internal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.bndtools.runtime.controller.DefaultResponse;
import org.bndtools.runtime.controller.IHandler;
import org.bndtools.runtime.controller.IResponse;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.log.LogService;

public class BundlesHandler implements IHandler {

    private final BundleContext context;
    private final Log log;

    public BundlesHandler(BundleContext context, Log log) {
        this.context = context;
        this.log = log;
    }

    public IResponse handleGet(String[] tokenizedPath, Properties params) {
        IResponse response;
        if (tokenizedPath.length == 0) {
            response = listBundles();
        } else {
            response = new DefaultResponse(IResponse.HTTP_INTERNALERROR, IResponse.MIME_PLAINTEXT, "Not implemented.");
        }
        return response;
    }

    public IResponse handlePost(String[] queryPath, Properties params, Properties uploads) {
        IResponse response;
        if (queryPath.length == 0) {
            try {
                String startProp = params.getProperty("start");
                boolean start = "true".equals(startProp) || "on".equals(startProp);

                List errors = installBundles(uploads, start);
                if (errors.isEmpty())
                    response = listBundles();
                else
                    response = listErrors(IResponse.HTTP_INTERNALERROR, errors);
            } catch (IllegalArgumentException e) {
                response = new DefaultResponse(IResponse.HTTP_BADREQUEST, IResponse.MIME_PLAINTEXT, e.getMessage());
            }
        } else {
            try {
                Bundle bundle = findBundle(queryPath[0]);
                if (queryPath.length == 1 && uploads != null && !uploads.isEmpty()) {
                    updateBundle(bundle, uploads);
                } else if (queryPath.length == 2 && "start".equals(queryPath[1])) {
                    bundle.start();
                } else if (queryPath.length == 2 && "stop".equals(queryPath[1])) {
                    bundle.stop();
                } else if (queryPath.length == 2 && "update".equals(queryPath[1])) {
                    bundle.update();
                }
                response = listBundles();
            } catch (NumberFormatException e) {
                response = new DefaultResponse(IResponse.HTTP_BADREQUEST, IResponse.MIME_PLAINTEXT, e.getMessage());
            } catch (FileNotFoundException e) {
                response = new DefaultResponse(IResponse.HTTP_INTERNALERROR, IResponse.MIME_PLAINTEXT, e.getMessage());
            } catch (BundleException e) {
                response = new DefaultResponse(IResponse.HTTP_INTERNALERROR, IResponse.MIME_PLAINTEXT, e.getMessage());
            } catch (IllegalArgumentException e) {
                response = new DefaultResponse(IResponse.HTTP_BADREQUEST, IResponse.MIME_PLAINTEXT, e.getMessage());
            }
        }

        return response;
    }

    /**
     * DELETE must be idempotent so we return status 200 even when a bundle
     * doesn't exist with the requested ID, since it may have already been
     * deleted. Only returns error 400 if the request formatting is invalid.
     *
     * @see org.bndtools.runtime.controller.IHandler#handleDelete(java.lang.String[])
     */
    public IResponse handleDelete(String[] queryPath, Properties params) {
        IResponse response;
        if (queryPath.length != 1) {
            response = new DefaultResponse(IResponse.HTTP_BADREQUEST, IResponse.MIME_PLAINTEXT, "Bad request format. Required: DELETE /bundles/<BUNDLE_ID>");
        } else {
            try {
                Bundle bundle = findBundle(queryPath[0]);
                bundle.uninstall();
                response = listBundles();
            } catch (NumberFormatException e) {
                response = new DefaultResponse(IResponse.HTTP_BADREQUEST, IResponse.MIME_PLAINTEXT, e.getMessage());
            } catch (IllegalArgumentException e) {
                log.log(null, LogService.LOG_INFO, "Ignoring error in BundlesHandler.handleDelete()", e);
                response = listBundles();
            } catch (BundleException e) {
                log.log(null, LogService.LOG_INFO, "Ignoring error in BundlesHandler.handleDelete()", e);
                response = listBundles();
            }
        }

        return response;
    }

    protected Bundle findBundle(String id) throws IllegalArgumentException, NumberFormatException {
        Bundle bundle = context.getBundle(Long.parseLong(id));
        if (bundle == null)
            throw new IllegalArgumentException("No such bundle: " + id);
        return bundle;
    }

    protected void updateBundle(Bundle bundle, Properties uploads) throws FileNotFoundException, BundleException, IllegalArgumentException {
        if (uploads == null || uploads.size() != 1)
            throw new IllegalArgumentException("Bundle update request must include exactly one uploaded file.");
        else {
            String name = (String) uploads.propertyNames().nextElement();
            String file = uploads.getProperty(name);
            bundle.update(new FileInputStream(file));
        }
    }

    protected List/*<Exception>*/ installBundles(Properties uploads, boolean start) throws IllegalArgumentException {
        if (uploads == null || uploads.isEmpty())
            throw new IllegalArgumentException("Bundle installation request must include at least one uploaded file.");
        List bundles = new LinkedList();
        List errors = new LinkedList();

        for (Enumeration names = uploads.propertyNames(); names.hasMoreElements(); ) {
            try {
                String name = (String) names.nextElement();
                Bundle bundle = context.installBundle("uploaded:" + name, new FileInputStream(uploads.getProperty(name)));
                bundles.add(bundle);
            } catch (FileNotFoundException e) {
                errors.add(e);
            } catch (BundleException e) {
                errors.add(e);
            }
        }

        if (start) {
            for (Iterator iter = bundles.iterator(); iter.hasNext(); ) {
                try {
                    Bundle bundle = (Bundle) iter.next();
                    bundle.start();
                } catch (BundleException e) {
                    errors.add(e);
                }
            }
        }

        return errors;
    }

    protected IResponse listBundles() {
        StringBuffer buffer = new StringBuffer();

        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            if (i > 0) buffer.append('\n');
            Bundle bundle = bundles[i];
            printBundleHeaderLine(bundle, buffer);
        }

        return new DefaultResponse(IResponse.HTTP_OK, IResponse.MIME_PLAINTEXT, buffer.toString());
    }

    private StringBuffer printBundleHeaderLine(Bundle bundle, StringBuffer buffer) {
        buffer.append(Long.toString(bundle.getBundleId())).append(',');
        buffer.append(bundle.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME)).append(',');
        buffer.append(bundle.getLocation()).append(',');
        buffer.append(printBundleState(bundle));

        return buffer;
    }

    private String printBundleState(Bundle bundle) {
        String result;
        switch (bundle.getState()) {
        case Bundle.UNINSTALLED:
            result = "UNINSTALLED";
            break;
        case Bundle.INSTALLED:
            result = "INSTALLED";
            break;
        case Bundle.RESOLVED:
            result = "RESOLVED";
            break;
        case Bundle.STARTING:
            result = "STARTING";
            if ("lazy".equals(bundle.getHeaders().get("Bundle-ActivationPolicy")))
                result += "/LAZY";
            break;
        case Bundle.ACTIVE:
            result = "ACTIVE";
            break;
        case Bundle.STOPPING:
            result = "STOPPING";
            break;
        default:
            result = "<<UNKNOWN>>";
            break;
        }
        return result;
    }

    protected IResponse listErrors(String status, List errors) {
        StringBuffer buffer = new StringBuffer();

        for(Iterator iter = errors.iterator(); iter.hasNext(); ) {
            Throwable error = (Throwable) iter.next();
            buffer.append(error.getMessage());
            if (iter.hasNext())
                buffer.append('\n');
        }

        return new DefaultResponse(status, IResponse.MIME_PLAINTEXT, buffer.toString());
    }
}
