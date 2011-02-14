package org.bndtools.runtime.controller;

import java.io.IOException;

public interface IServer {
    void registerHandler(String prefix, IHandler handler);
    void unregisterHandler(String prefix);

    void start() throws IOException;
    void stop() throws IOException;
}
