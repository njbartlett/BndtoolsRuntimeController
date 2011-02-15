package org.bndtools.runtime.controller.test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.osgi.framework.Constants;
import org.restlet.data.MediaType;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class BundlesHandlerTest extends TestCase {

    private static final String HTTP = "http";
    private static final String LOCALHOST = "127.0.0.1";

    private final URI bundlesURI;

    public BundlesHandlerTest() throws URISyntaxException {
        bundlesURI = new URI(HTTP, null, LOCALHOST, 2605, "/bundles/", null, null);
    }

    public void testListBundles() throws Exception {
        List<BundleHeader> bundleHeaders = getBundleList();
        assertFalse(bundleHeaders.isEmpty());

        BundleHeader systemBundle = bundleHeaders.get(0);
        assertEquals(0L, systemBundle.getId());
        assertEquals(Constants.SYSTEM_BUNDLE_LOCATION, systemBundle.getLocation());
        assertEquals("ACTIVE", systemBundle.getState());
    }
    
    public void testInstall() throws ResourceException, IOException {
    	// Build upload representation
        File bundleFile = new File("testData/org.jquantlib_0.1.2.jar");
        Map<String,String> form = new HashMap<String, String>();
        form.put("comment", "this is a comment");
        Representation request = Utils.buildUploadRepresentation(form, bundleFile);

        // Post it and read result list of bundles
        StringWriter writer = new StringWriter();
        new ClientResource(bundlesURI).post(request).write(writer);
        List<BundleHeader> bundleHeaders = new ListOfBundlesParser(writer.toString()).parse();
        assertFalse(bundleHeaders.isEmpty());

        BundleHeader lastBundle = bundleHeaders.get(bundleHeaders.size() - 1);
        assertEquals("org.jquantlib", lastBundle.getBsn());
        assertEquals("uploaded:org.jquantlib_0.1.2.jar", lastBundle.getLocation());
        assertEquals("INSTALLED", lastBundle.getState());
    }

    public void testBundleDetails() throws Exception {
		List<BundleHeader> initialBundles = getBundleList();
        BundleHeader victim = Utils.findBundleByBSN("org.jquantlib", initialBundles);
        if (victim == null)
            fail("org.jquantlib bundle not installed, did previous test fail?");
        
        // Request bundle properties
        Properties props = getBundleDetails(victim.getId());
        
        // Check it
        assertEquals("INSTALLED", props.getProperty("state"));
        assertEquals("uploaded:org.jquantlib_0.1.2.jar", props.getProperty("location"));
        assertEquals("org.jquantlib", props.getProperty("Bundle-SymbolicName"));
        assertEquals("0.1.2", props.getProperty("Bundle-Version"));
        assertEquals(Long.toString(victim.getId()), props.getProperty("id"));
    }

	public void testInstallWithStart() throws ResourceException, IOException {
    	// Build upload representation
        File bundleFile = new File("testData/org.joda.time_1.5.0.jar");
        Map<String,String> form = new HashMap<String, String>();
        form.put("start", "true");
        Representation request = Utils.buildUploadRepresentation(form, bundleFile);

        // Post it and read list of bundles result
        StringWriter writer = new StringWriter();
        new ClientResource(bundlesURI).post(request).write(writer);
        List<BundleHeader> bundleHeaders = new ListOfBundlesParser(writer.toString()).parse();
        assertFalse(bundleHeaders.isEmpty());

        BundleHeader lastBundle = bundleHeaders.get(bundleHeaders.size() - 1);
        assertEquals("org.joda.time", lastBundle.getBsn());
        assertEquals("uploaded:org.joda.time_1.5.0.jar", lastBundle.getLocation());
        assertEquals("ACTIVE", lastBundle.getState());
    }

    public void testDelete() throws ResourceException, IOException {
        StringWriter writer;

        // Find the bundle to delete
        List<BundleHeader> initialBundles = getBundleList();
        BundleHeader victim = Utils.findBundleByBSN("org.jquantlib", initialBundles);
        if (victim == null)
            fail("org.jquantlib bundle not installed, did previous test fail?");

        // Delete it and read list of bundles result
        URI deleteURI = bundlesURI.resolve(Long.toString(victim.getId()));
        writer = new StringWriter();
        new ClientResource(deleteURI).delete().write(writer);
        List<BundleHeader> afterBundles = new ListOfBundlesParser(writer.toString()).parse();
        assertEquals(initialBundles.size() - 1, afterBundles.size());
    }

    public void testDeleteIdempotent() throws ResourceException, IOException {
        StringWriter writer;
        
        // Delete a non-existing bundle ID
        List<BundleHeader> initialBundles = getBundleList();
        URI deleteURI = bundlesURI.resolve("9999");
        writer = new StringWriter();
        new ClientResource(deleteURI).delete().write(writer);
        List<BundleHeader> afterBundles = new ListOfBundlesParser(writer.toString()).parse();
        
        assertEquals(initialBundles.size(), afterBundles.size());
    }

    public void testStopStartBundle() throws ResourceException, IOException {
    	StringWriter writer;

        List<BundleHeader> initialBundles = getBundleList();
        BundleHeader victim = Utils.findBundleByBSN("org.joda.time", initialBundles);
        assertNotNull(victim);
        assertEquals("ACTIVE", victim.getState());

        // Stop the bundle and check state
        writer = new StringWriter();
        URI requestURI = bundlesURI.resolve(Long.toString(victim.getId()) + "/stop");
        new ClientResource(requestURI).put(new EmptyRepresentation()).write(writer);

        List<BundleHeader> afterBundles = new ListOfBundlesParser(writer.toString()).parse();
        victim = Utils.findBundleByBSN("org.joda.time", afterBundles);
        assertNotNull(victim);
        assertEquals("RESOLVED", victim.getState());
        
        // Start the bundle again and check state 
        writer = new StringWriter();
        requestURI = bundlesURI.resolve(Long.toString(victim.getId()) + "/start");
        new ClientResource(requestURI).put(new EmptyRepresentation()).write(writer);

        afterBundles = new ListOfBundlesParser(writer.toString()).parse();
        victim = Utils.findBundleByBSN("org.joda.time", afterBundles);
        assertNotNull(victim);
        assertEquals("ACTIVE", victim.getState());
    }
    
    public void testUpdate() throws IOException {
    	// Find the bundle to update
    	List<BundleHeader> bundleList = getBundleList();
    	BundleHeader victim = Utils.findBundleByBSN("org.joda.time", bundleList);
    	
    	// Get lastModified property
    	Properties bundleProps = getBundleDetails(victim.getId());
    	long timestampBefore = Long.parseLong(bundleProps.getProperty("lastModified"));
        assertEquals("1.5.0", bundleProps.getProperty("Bundle-Version"));
    	
    	// Build upload representation
    	File bundleFile = new File("testData/joda-time-1.6.2.jar");
    	FileRepresentation upload = new FileRepresentation(bundleFile, MediaType.APPLICATION_OCTET_STREAM);
    	
    	// PUT it
    	URI requestURI = bundlesURI.resolve(Long.toString(victim.getId()));
    	new ClientResource(requestURI).put(upload);
    	
    	// Get properties again
    	bundleProps = getBundleDetails(victim.getId());
    	long timestampAfter = Long.parseLong(bundleProps.getProperty("lastModified"));
    	assertTrue(timestampAfter > timestampBefore);
    	assertEquals("joda-time", bundleProps.getProperty("Bundle-SymbolicName"));
        assertEquals("1.6.2", bundleProps.getProperty("Bundle-Version"));
    }

	private List<BundleHeader> getBundleList() throws IOException {
		StringWriter writer = new StringWriter();
		new ClientResource(bundlesURI).get().write(writer);
		List<BundleHeader> initialBundles = new ListOfBundlesParser(writer.toString()).parse();
		return initialBundles;
	}

	private Properties getBundleDetails(long id) throws IOException {
		StringWriter writer = new StringWriter();
	    URI requestURI = bundlesURI.resolve(Long.toString(id));
	    new ClientResource(requestURI).get().write(writer);
	    Properties props = new Properties();
	    props.load(new StringReader(writer.toString()));
		return props;
	}
    
}
