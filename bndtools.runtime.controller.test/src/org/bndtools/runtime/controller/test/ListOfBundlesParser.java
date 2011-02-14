package org.bndtools.runtime.controller.test;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ListOfBundlesParser {
    private final String data;

    public ListOfBundlesParser(String data) {
        this.data = data;
    }

    public List<BundleHeader> parse() {
        List<BundleHeader> result = new ArrayList<BundleHeader>();

        StringTokenizer lines = new StringTokenizer(data, "\n");
        while (lines.hasMoreTokens()) {
            String line = lines.nextToken();
            StringTokenizer fields = new StringTokenizer(line, ",");
            if (fields.countTokens() != 4)
                throw new IllegalArgumentException("Incorrect number of fields in ListOfBundles");

            BundleHeader bundle = new BundleHeader(Long.parseLong(fields.nextToken()), fields.nextToken(), fields.nextToken(), fields.nextToken());
            result.add(bundle);
        }

        return result;
    }
}
