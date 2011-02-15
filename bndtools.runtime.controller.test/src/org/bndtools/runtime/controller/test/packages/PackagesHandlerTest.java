package org.bndtools.runtime.controller.test.packages;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import junit.framework.TestCase;

public class PackagesHandlerTest extends TestCase {

	private final URI packagesURI;
	
	public PackagesHandlerTest() throws URISyntaxException {
		packagesURI = new URI("http", null, "127.0.0.1", 2605, "/packages/", null, null);
	}
	
	public void testListPackages() throws Exception {
		List<PackageHeader> packages = getPackageList();
		
		assertNotNull(packages);
		assertTrue(packages.size() > 0);
	}

	private List<PackageHeader> getPackageList() throws ResourceException, IOException {
		StringWriter writer = new StringWriter();
		new ClientResource(packagesURI).get().write(writer);
		return new ListOfPackagesParser(writer.toString()).parse();
	}
	
	
}
