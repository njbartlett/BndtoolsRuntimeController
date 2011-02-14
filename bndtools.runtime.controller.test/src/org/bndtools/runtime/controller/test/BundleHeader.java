package org.bndtools.runtime.controller.test;

public class BundleHeader {
    private final long id;
    private final String bsn;
    private final String location;
    private final String state;

    public BundleHeader(long id, String bsn, String location, String state) {
        this.id = id;
        this.bsn = bsn;
        this.location = location;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public String getBsn() {
        return bsn;
    }

    public String getLocation() {
        return location;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "BundleHeader [id=" + id + ", bsn=" + bsn + ", location=" + location + ", state=" + state + "]";
    }

}
