package org.bndtools.runtime.controller.test;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ConsoleLog implements LogService {
    public void log(final int level, final String message) {
        log(null, level, message, null);
    }

    public void log(final int level, final String message, final Throwable exception) {
        log(null, level, message, exception);
    }

    public void log(final ServiceReference sr, final int level, final String message) {
        log(sr, level, message, null);
    }

    public void log(final ServiceReference sr, final int level, final String message, final Throwable exception) {
        String formatted = formatMessage(sr, level, message, exception);
        if (level >= LogService.LOG_INFO)
            System.out.println(formatted);
        else
            System.err.println(formatted);
    }

    private String formatMessage(final ServiceReference sr, final int level, final String message, final Throwable exception) {
        final StringBuilder builder = new StringBuilder();

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
