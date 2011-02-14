package org.bndtools.runtime.controller.test;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        context.registerService(LogService.class.getName(), new ConsoleLog(), null);
    }

    public void stop(BundleContext context) throws Exception {
    }

}
