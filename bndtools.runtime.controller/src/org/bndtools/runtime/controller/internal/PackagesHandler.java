package org.bndtools.runtime.controller.internal;

import java.io.InputStream;
import java.util.Properties;

import org.bndtools.runtime.controller.DefaultResponse;
import org.bndtools.runtime.controller.IHandler;
import org.bndtools.runtime.controller.IResponse;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

class PackagesHandler implements IHandler {
	
	private final BundleContext context;
	private final ServiceTracker pkgAdminTracker;

	PackagesHandler(BundleContext context, ServiceTracker pkgAdminTracker) {
		this.context = context;
		this.pkgAdminTracker = pkgAdminTracker;
	}

	public IResponse handleGet(String[] queryPath, Properties params) {
		IResponse response;
		
		if (queryPath.length == 0) {
			response = listAllPackages();
		} else {
			response = DefaultResponse.createBadRequestError(null);
		}
		
		return response;
	}

	public IResponse handlePost(String[] queryPath, Properties params, Properties uploads, InputStream content) {
		// TODO Auto-generated method stub
		return null;
	}

	public IResponse handleDelete(String[] queryPath, Properties params) {
		// TODO Auto-generated method stub
		return null;
	}

	public IResponse handlePut(String[] queryPath, Properties params, Properties uploads, InputStream content) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private PackageAdmin getPkgAdmin() {
		return (PackageAdmin) pkgAdminTracker.getService();
	}

	private IResponse listAllPackages() {
		StringBuffer buffer = new StringBuffer();
		ExportedPackage[] packages = getPkgAdmin().getExportedPackages((Bundle) null);
		for (int i = 0; i < packages.length; i++) {
			if (i > 0)
				buffer.append('\n');
			printPackageHeaderLine(packages[i], buffer);
		}
		return new DefaultResponse(IResponse.HTTP_OK, IResponse.MIME_PLAINTEXT, buffer.toString());
	}

	private StringBuffer printPackageHeaderLine(ExportedPackage pkg, StringBuffer buffer) {
		Bundle exporter = pkg.getExportingBundle();
		
		String exporterVersion;
		if (exporter != null) {
			exporterVersion = (String) exporter.getHeaders().get(Constants.BUNDLE_VERSION);
			if (exporterVersion == null) exporterVersion = "0.0.0";
		} else {
			exporterVersion = "";
		}
		
		Bundle[] importers = pkg.getImportingBundles();
		
		buffer.append(pkg.getName()).append(',');
		buffer.append(pkg.getVersion()).append(',');
		buffer.append(exporter != null ? exporter.getBundleId() : -1L).append(',');
		buffer.append(exporter != null ? exporter.getLocation() : "").append(',');
		buffer.append(exporter != null ? exporter.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME) : "").append(',');
		buffer.append(exporterVersion).append(',');
		buffer.append(importers != null ? importers.length : 0).append(',');
		buffer.append(pkg.isRemovalPending());
		
		return buffer;
	}

}
