package org.bndtools.runtime.controller.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class LogTracker extends ServiceTracker implements Log {
	
	private final Log console = new ConsoleLog();

    public LogTracker(final BundleContext context) {
        super(context, LogService.class.getName(), null);
    }

    public void log(ServiceReference sr, final int level, final String message, final Throwable exception) {
        final LogService log = (LogService) getService();
        if (log == null) {
        	console.log(sr, level, message, exception);
        } else {
        	log.log(null, level, message, exception);
        }
    }


}
