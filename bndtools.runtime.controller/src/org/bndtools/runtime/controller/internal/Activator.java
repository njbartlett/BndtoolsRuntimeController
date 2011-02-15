package org.bndtools.runtime.controller.internal;

import org.bndtools.runtime.controller.IServer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {


    private static final int DEFAULT_NANO_PORT = 2604;
	private static final String PROP_NANO_PORT = "bndtools.runtime.controller.nanoPort";
    
    // Handler Prefixes
    private static final String PREFIX_BUNDLES = "bundles";
	private static final String PREFIX_PACKAGES = "packages";

    private IServer server;

    public void start(BundleContext context) throws Exception {
    	ServiceTracker pkgAdminTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
    	pkgAdminTracker.open();
    	
    	// Use LogService if available, otherwise console logging
    	Log log;
    	try {
			ClassLoader myLoader = Activator.class.getClassLoader();
			myLoader.loadClass("org.osgi.service.log.LogService");
			LogTracker logTracker = new LogTracker(context);
			logTracker.open();
			log = logTracker;
		} catch (ClassNotFoundException e) {
			log = new ConsoleLog();
		}

		// Setup the server
		String nanoPortStr = context.getProperty(PROP_NANO_PORT);
		if (nanoPortStr != null) {
			int nanoPort = Integer.parseInt(nanoPortStr);
			server = new NanoServer(nanoPort, log);
		} else {
			server = new NanoServer(DEFAULT_NANO_PORT, log);
		} // TODO: additional server types e.g. HttpServic

        server.registerHandler(PREFIX_BUNDLES, new BundlesHandler(context, log));
        server.registerHandler(PREFIX_PACKAGES, new PackagesHandler(context, pkgAdminTracker));
        server.start();
    }

    public void stop(BundleContext context) throws Exception {
        server.stop();
    }

}
