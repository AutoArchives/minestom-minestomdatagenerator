package net.minestom.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.jar.JarFile;

public class ResourceUtils {

    private ResourceUtils() {}

    /**
     * Get a resource listing from within a jar file
     * @param clazz The class to use the {@link ClassLoader} of
     * @param path The path within the classpath to list resources from
     * @return An array containing the path of all resources within the specified directory,
     *         relative to it and including any nested directories
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        var dirURL = clazz.getClassLoader().getResource(path);

        // walk the directory in case path is just a regular file
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            var root = Path.of(dirURL.toURI());
            try (var files = Files.walk(root)) {
                return files.filter(Files::isRegularFile)
                        .map(file -> root.relativize(file).toString())
                        .toArray(String[]::new);
            }
        }

        if (dirURL == null) {
            var me = clazz.getName().replace(".", "/")+".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        // dirURL should not be null at this point
        assert dirURL != null;

        if (dirURL.getProtocol().equals("jar")) {
            // strip out jar file from dirURL
            var jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));

            try (var jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                // get all files within the jar
                var entries = jar.entries();
                var result = new HashSet<String>();

                while(entries.hasMoreElements()) {
                    var name = entries.nextElement().getName();
                    if (name.startsWith(path) && !name.endsWith("/")) {
                        result.add(name.substring(path.length()));
                    }
                }

                return result.toArray(new String[result.size()]);
            }
        }

        throw new UnsupportedOperationException("Unable to list files for URL " + dirURL);
    }

}
