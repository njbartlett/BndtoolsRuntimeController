package org.bndtools.runtime.controller.test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.osgi.framework.Constants;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class BundlesHandlerTest extends TestCase {

    private static final String HTTP = "http";
    private static final String LOCALHOST = "127.0.0.1";

    private final URI bundlesURI;

    public BundlesHandlerTest() throws URISyntaxException {
        bundlesURI = new URI(HTTP, null, LOCALHOST, 8081, "/bundles/", null, null);
    }

    public void testListBundles() throws Exception {
        StringWriter writer = new StringWriter();
        new ClientResource(bundlesURI).get().write(writer);

        List<BundleHeader> bundleHeaders = new ListOfBundlesParser(writer.toString()).parse();
        assertFalse(bundleHeaders.isEmpty());

        BundleHeader systemBundle = bundleHeaders.get(0);
        assertEquals(0L, systemBundle.getId());
        assertEquals(Constants.SYSTEM_BUNDLE_LOCATION, systemBundle.getLocation());
        assertEquals("ACTIVE", systemBundle.getState());
    }

    public void testInstall() throws ResourceException, IOException {
        File bundleFile = new File("testData/org.jquantlib_0.1.2.jar");
        Map<String,String> form = new HashMap<String, String>();
        form.put("comment", "this is a comment");
        Representation request = Utils.buildUploadRepresentation(form, bundleFile);

        StringWriter writer = new StringWriter();
        new ClientResource(bundlesURI).post(request).write(writer);

        List<BundleHeader> bundleHeaders = new ListOfBundlesParser(writer.toString()).parse();
        assertFalse(bundleHeaders.isEmpty());

        BundleHeader bundle = bundleHeaders.get(bundleHeaders.size() - 1);
        assertEquals("org.jquantlib", bundle.getBsn());
        assertEquals("uploaded:org.jquantlib_0.1.2.jar", bundle.getLocation());
        assertEquals("INSTALLED", bundle.getState());
    }

    public void testInstallWithStart() throws ResourceException, IOException {
        File bundleFile = new File("testData/org.joda.time_1.5.0.jar");
        Map<String,String> form = new HashMap<String, String>();
        form.put("start", "true");
        Representation request = Utils.buildUploadRepresentation(form, bundleFile);

        StringWriter writer = new StringWriter();
        new ClientResource(bundlesURI).post(request).write(writer);

        List<BundleHeader> bundleHeaders = new ListOfBundlesParser(writer.toString()).parse();
        assertFalse(bundleHeaders.isEmpty());

        BundleHeader bundle = bundleHeaders.get(bundleHeaders.size() - 1);
        assertEquals("org.joda.time", bundle.getBsn());
        assertEquals("uploaded:org.joda.time_1.5.0.jar", bundle.getLocation());
        assertEquals("ACTIVE", bundle.getState());
    }

    public void testDelete() throws ResourceException, IOException {
        StringWriter writer = new StringWriter();
        new ClientResource(bundlesURI).get().write(writer);

        List<BundleHeader> initialBundles = new ListOfBundlesParser(writer.toString()).parse();
        BundleHeader victim = Utils.findBundleByBSN("org.jquantlib", initialBundles);

        if (victim == null)
            fail("org.jquantlib bundle not installed, did previous test fail?");

        URI deleteURI = bundlesURI.resolve(Long.toString(victim.getId()));
        writer = new StringWriter();
        new ClientResource(deleteURI).delete().write(writer);

        List<BundleHeader> afterBundles = new ListOfBundlesParser(writer.toString()).parse();
        assertEquals(initialBundles.size() - 1, afterBundles.size());
    }

    public void testDeleteIdempotent() throws ResourceException, IOException {
        StringWriter writer = new StringWriter();
        new ClientResource(bundlesURI).get().write(writer);
        List<BundleHeader> initialBundles = new ListOfBundlesParser(writer.toString()).parse();

        URI deleteURI = bundlesURI.resolve("9999"); // shouldn't exist!
        writer = new StringWriter();
        new ClientResource(deleteURI).delete().write(writer);

        List<BundleHeader> afterBundles = new ListOfBundlesParser(writer.toString()).parse();
        assertEquals(initialBundles.size(), afterBundles.size());
    }

    public void testStopBundle() throws ResourceException, IOException {
        StringWriter writer = new StringWriter();
        new ClientResource(bundlesURI).get().write(writer);

        List<BundleHeader> initialBundles = new ListOfBundlesParser(writer.toString()).parse();
        BundleHeader victim = Utils.findBundleByBSN("org.joda.time", initialBundles);
        assertNotNull(victim);
        assertEquals("ACTIVE", victim.getState());

        writer = new StringWriter();
        URI requestURI = bundlesURI.resolve(Long.toString(victim.getId()) + "/stop");
        new ClientResource(requestURI).put(new EmptyRepresentation()).write(writer);

        List<BundleHeader> afterBundles = new ListOfBundlesParser(writer.toString()).parse();
        victim = Utils.findBundleByBSN("org.joda.time", afterBundles);
        assertNotNull(victim);
        assertEquals("RESOLVED", victim.getState());

    }

}
