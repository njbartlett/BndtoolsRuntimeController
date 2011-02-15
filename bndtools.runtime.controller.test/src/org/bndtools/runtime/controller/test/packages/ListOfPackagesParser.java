package org.bndtools.runtime.controller.test.packages;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ListOfPackagesParser {
    private final String data;

    public ListOfPackagesParser(String data) {
        this.data = data;
    }

    public List<PackageHeader> parse() {
        List<PackageHeader> result = new ArrayList<PackageHeader>();

        StringTokenizer lines = new StringTokenizer(data, "\n");
        while (lines.hasMoreTokens()) {
            String line = lines.nextToken();
            StringTokenizer fields = new StringTokenizer(line, ",");
            if (fields.countTokens() != 8)
                throw new IllegalArgumentException("Incorrect number of fields in ListOfPackages");

            PackageHeader pkg = new PackageHeader(fields.nextToken(), fields.nextToken(), Long.parseLong(fields.nextToken()), fields.nextToken(), fields.nextToken(), fields.nextToken(), Integer.parseInt(fields.nextToken()), Boolean.parseBoolean(fields.nextToken()));
            result.add(pkg);
        }

        return result;
    }
}
