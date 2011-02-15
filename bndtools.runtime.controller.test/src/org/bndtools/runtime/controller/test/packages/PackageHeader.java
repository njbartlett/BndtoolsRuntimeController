package org.bndtools.runtime.controller.test.packages;

public class PackageHeader {
	
	private final String name;
	private final String version;
	private final long exporterId;
	private final String exporterLocation;
	private final String exporterBsn;
	private final String exporterVersion;
	private final int importerCount;
	private final boolean pendingRemoval;

	public PackageHeader(String name, String version, long exporterId, String exporterLocation, String exporterBsn, String exporterVersion, int importerCount, boolean pendingRemoval) {
		this.name = name;
		this.version = version;
		this.exporterId = exporterId;
		this.exporterLocation = exporterLocation;
		this.exporterBsn = exporterBsn;
		this.exporterVersion = exporterVersion;
		this.importerCount = importerCount;
		this.pendingRemoval = pendingRemoval;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public long getExporterId() {
		return exporterId;
	}
	
	public String getExporterLocation() {
		return exporterLocation;
	}

	public String getExporterBsn() {
		return exporterBsn;
	}

	public String getExporterVersion() {
		return exporterVersion;
	}

	public int getImporterCount() {
		return importerCount;
	}

	public boolean isPendingRemoval() {
		return pendingRemoval;
	}

	@Override
	public String toString() {
		return "PackageHeader [name=" + name + ", version=" + version
				+ ", exporterId=" + exporterId + ", exporter=" + exporterBsn
				+ "-" + exporterVersion + ", importerCount=" + importerCount
				+ ", pendingRemoval=" + pendingRemoval + "]";
	}	
	
}
