package org.bndtools.runtime.controller.internal;

import org.osgi.framework.ServiceReference;

/**
 * A tiny abstraction to avoid direct dependency on OSGi LogService.
 * @author Neil Bartlett
O */
public interface Log {
	void log(ServiceReference sr, int level, String message, Throwable error);
}
