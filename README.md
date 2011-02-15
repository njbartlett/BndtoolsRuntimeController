Bndtools Runtime Controller
===========================

The OSGi bundle `bndtools.runtime.controller` creates a control channel for inspecting and manipulating an OSGi runtime. Clients can communicate with the controller using an HTTP REST-style protocol.

The controller was designed for use by [Bndtools](http://bndtools.org) but it is completely independent from both Bndtools and Eclipse, and can be used in a standalone OSGi runtime and driven by any type of client.

Dependencies
============

The runtime controller is designed to be as lightweight as possible, and has no mandatory dependencies aside from the JRE and OSGi. It runs on Java 1.4 or higher and OSGi R4.0 or higher. The bundle size is 53K including full source code.

Server Mode
===========

The default mode uses an embedded HTTP server based on [NanoHTTPD](http://elonen.iki.fi/code/nanohttpd/). The default port number is 2604; this can be overridden by setting the property `bndtools.runtime.controller.nanoPort`, e.g. via system properties:

	java -Dbndtools.runtime.controller.nanoPort=9999 -jar felix.jar

or via your framework's configuration file (`config.ini`, `config.properties` etc.).

Alternatively the controller can be configured to register itself with the OSGi Http Service, if there is one available. **NOT YET IMPLEMENTED**

SECURITY WARNING
================

**DO NOT DEPLOY THE CONTROLLER IN A PRODUCTION ENVIRONMENT!!** It is inherently insecure, especially in the NanoHTTPD server mode, and is intended only for development and debugging purposes.

Using the controller with the OSGi Http Service *can* be secure, but only if the Http Service itself is correctly configured.

API Reference
=============

List Bundles
------------

Request format: `GET /bundles/`

Lists the bundles. Response is one line per bundle, each line containing the following fields in order, separated by commas:

* Bundle ID
* Bundle Symbolic Name
* Location
* State: INSTALLED, RESOLVED, STARTING, STARTING/LAZY, ACTIVE, STOPPING, or UNINSTALLED.


Install New Bundles
-------------------

Request format: `POST /bundles/`

Installs new bundle(s). NB do not use to update existing bundles.

The request content must be a MIME "multipart/form-data" compatible with [RFC2388](http://www.ietf.org/rfc/rfc2388.txt). If a form field named `start` is added to the request with the value `on` or `true` then the installed bundles will additionally be started.

Returns: 200, list of bundles, *OR* 500, list of errors

Bundle Details
--------------

Request format: `GET /bundles/<BUNDLE_ID>`

Prints verbose details about the specified bundle. The returned text is in Java Properties format, suitable for parsing with the `java.util.Properties` class. It shall contain:

* `id`: the bundle ID;
* `state`: INSTALLED, RESOLVED, STARTING, STARTING/LAZY, ACTIVE, STOPPING, or UNINSTALLED;
* `location`: the bundle location;
* `lastModified`: the last modified time of the bundle, as number of milliseconds since 1 January 1970 00:00:00 UTC;

In addition it contains copies of each static bundle header, i.e., all manifest entries

Returns: 200, bundle details *OR* 404, if bundle ID unknown.

Update Bundle From Supplied Data
--------------------------------

Request format: `PUT /bundles/<BUNDLE_ID>`

Updates the specified bundle using new data, which must be sent by the client in the request content as type "application/octet-stream".

Returns: 200, list of bundles *OR* 500, list of errors

Start/Stop a Bundle
-------------------

Request format: `PUT /bundles/<BUNDLE_ID>/start`

*OR*

Request format: `PUT /bundles/<BUNDLE_ID>/stop`

Starts/stops the specified bundle. This is an idempotent request, i.e. starting an already started bundle has no effect and stopping an already stopped bundle has no effect.

Returns: 200, list of bundles *OR* 404, if bundle ID unknown, *OR* 500 on error

Update Bundle From Persisted Location
-------------------------------------

Request format: `PUT /bundles/<BUNDLE_ID>/update`

Updates the bundle from its original persisted location. NB this only works for bundles that were not originally installed by the controller.

Returns: 200, list of bundles *OR* 404, if bundle ID unknown, *OR* 500 on error

Uninstall Bundle
----------------

Request format: `DELETE /bundles/<BUNDLE_ID>`

Uninstalled the specified bundle. If the bundle ID is unknown or the bundle was already uninstalled then this request still returns successfully, due to the idempotency requirements of the DELETE verb.

Returns: 200, list of bundles, *OR* 400 only on improperly formatted request.

List of Packages
---------------

Request format: `GET /packages/`

List of packages exported by all bundles. Response is one line per package, each line containing the following fields in order, separated by commas:

* Package name;
* Package version;
* Exporting bundle ID (may be empty if package is stale);
* Exporting bundle location (may be empty if package is stale);
* Exporting bundle symbolic name (may be empty if package is stale);
* Exporting bundle version (may be empty if package is stale);
* Number of importing bundles;
* Whether the package is pending removal (true/false).

Planned Features
================

The following features are planned:

* Integration with the OSGi HttpService.
* Requests for querying services.
* Requests for querying and creating/updating configurations (Config Admin).
* ...

Licence
=======

This project is licensed under the [Eclipse Public License v1.0](http://www.eclipse.org/legal/epl-v10.html).
