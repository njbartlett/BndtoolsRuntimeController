package org.bndtools.runtime.controller.internal;

import java.io.PrintStream;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ConsoleLog implements Log {
	
	public void log(ServiceReference sr, int level, String message, Throwable error) {
		PrintStream stream = level > LogService.LOG_WARNING ? System.out : System.err;
		stream.println(formatMessage(sr, level, message, error));
	}
	
    private String formatMessage(ServiceReference sr, final int level, final String message, final Throwable exception) {
        final StringBuffer builder = new StringBuffer();

        switch (level) {
        case LogService.LOG_DEBUG:
            builder.append("DEBUG");
            break;
        case LogService.LOG_INFO:
            builder.append("INFO");
            break;
        case LogService.LOG_WARNING:
            builder.append("WARNING");
            break;
        case LogService.LOG_ERROR:
            builder.append("ERROR");
            break;
        default:
            builder.append("UNKNOWN");
        }

        builder.append(": ").append(message);
        if (exception != null) {
            builder.append(": ").append(exception.getClass().getName()).append(": ").append(exception.getLocalizedMessage());
        }

        return builder.toString();
    }
}
