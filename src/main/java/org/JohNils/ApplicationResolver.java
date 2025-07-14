package org.JohNils;

import java.io.File;
import java.util.*;

public class ApplicationResolver {
    private static final List<String> EXTENSIONS = List.of(".png", ".svg", ".xpm");
    private static final List<String> BASE_DIRS = List.of(
            "/usr/share/icons/",
            "/usr/share/pixmaps/"
    );

    private static final Map<String, File> iconCache = new HashMap<>();

    public static void buildIconCache() {
        for (String baseDir : BASE_DIRS) {
            File base = new File(baseDir);
            if (base.exists()) {
                scanDirRecursive(base);
            }
        }
    }

    private static void scanDirRecursive(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirRecursive(file);
            } else {
                String name = file.getName();
                for (String ext : EXTENSIONS) {
                    if (name.endsWith(ext)) {
                        String iconName = name.substring(0, name.length() - ext.length());
                        iconCache.put(iconName, file);
                    }
                }
            }
        }
    }

    public static File resolveIcon(String iconName) {
        return iconCache.get(iconName);
    }
}
