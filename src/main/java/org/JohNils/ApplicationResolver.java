package org.JohNils;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class ApplicationResolver {
    private static final List<String> IconEXTENSIONS = List.of(".png", ".svg", ".xpm");
    private static final List<String> BASE_ICON_DIRS = List.of(
            "/usr/share/icons/",
            "/usr/share/pixmaps/"
    );
    private static final List<Path> BASE_APP_DIRS = List.of(
            Paths.get("/usr/share/applications"),
            Paths.get(System.getProperty("user.home") + "/.local/share/applications")
    );

    private static final Map<String, File> iconFileCache = new HashMap<>();
    private static final Map<String, Icon> iconCache = new HashMap<>();
    private static final List<String[]> appCache = new ArrayList<>();

    public static void buildCaches() {
        buildIconCache();
        buildAppCache();

        iconFileCache.clear();
    }

    public static void buildIconCache() {
        for (String baseDir : BASE_ICON_DIRS) {
            File base = new File(baseDir);
            if (base.exists()) {
                scanDirRecursive(base);
            }
        }
    }
    public static void buildAppCache() {
        for (Path dir : BASE_APP_DIRS) {
            try (Stream<Path> files = Files.walk(dir)) {
                files.filter(p -> p.toString().endsWith(".desktop")).forEach(file -> {
                    try {
                        List<String> lines = Files.readAllLines(file);
                        String name = null;
                        String exec = null;
                        String icon = null;
                        String keywords = null;
                        String categories = null;
                        for (String line : lines) {
                            if (name != null && exec != null && icon != null && keywords != null) break;

                            if (line.startsWith("Name=")) {
                                name = line.substring(5);
                            }
                            if (line.startsWith("Exec=")) {
                                exec = line.substring(5);
                            }
                            if (line.startsWith("Icon=")) {
                                icon = line.substring(5);
                            }
                            if (line.startsWith("Keywords=")) {
                                keywords = line.substring("Keywords=".length());
                            }
                            if (line.startsWith("Categories=")) {
                                keywords = line.substring("Categories=".length());
                            }
                        }

                        appCache.add(new String[]{name,icon,exec,keywords, categories});

                        iconCache.put(icon,getIcon(iconFileCache.get(icon)));

                    } catch (IOException ignored) {}
                });
            } catch (IOException ignored) {}
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
                for (String ext : IconEXTENSIONS) {
                    if (name.endsWith(ext)) {
                        String iconName = name.substring(0, name.length() - ext.length());


                        iconFileCache.put(iconName, file);
                    }
                }
            }
        }
    }

    private static Icon getIcon(File file) {
        if (file != null) {
            String[] names = file.getName().split("\\.");
            if (names[names.length - 1].equals("svg")) {
                BufferedImage svgImage = null;
                try {
                    svgImage = renderSVGToImage(file.getAbsolutePath(), 64, 64);
                } catch (Exception ignored) {
                    return null;
                }
                return new ImageIcon(svgImage);
            }

            BufferedImage image = new BufferedImage(64,64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            try {
                g.drawImage(ImageIO.read(file),0,0,64,64,null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            g.dispose();

            return new ImageIcon(image);
        } else {
            return new ImageIcon(Main.window.search); // fallback
        }
    }
    private static BufferedImage renderSVGToImage(String svgFilePath, int width, int height) throws IOException, Exception {
        FileInputStream svgStream = new FileInputStream(svgFilePath);
        InputStream fixedStream = sanitizeSvgVersion(svgStream);
        TranscoderInput input = new TranscoderInput(fixedStream);

        class BufferedImageTranscoder extends ImageTranscoder {
            private BufferedImage img = null;

            @Override
            public BufferedImage createImage(int w, int h) {
                return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            }

            @Override
            public void writeImage(BufferedImage bufferedImage, TranscoderOutput output) {
                this.img = bufferedImage;
            }

            public BufferedImage getBufferedImage() {
                return img;
            }
        }

        BufferedImageTranscoder t = new BufferedImageTranscoder();
        t.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
        t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);
        t.transcode(input, null);

        return t.getBufferedImage();
    }
    private static InputStream sanitizeSvgVersion(InputStream svgInputStream) throws IOException {
        String content = new String(svgInputStream.readAllBytes(), StandardCharsets.UTF_8);
        // Remove version="1" attribute entirely
        content = content.replaceAll("version=\"1\"", "");
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    public static Icon resolveIcon(String iconName) {
        return iconCache.get(iconName);
    }

    public static ArrayList<String[]> resolveApplication(String query) {
        ArrayList<String[]> matches = new ArrayList<>();

        for (String[] app: appCache) {
            if (app[0] != null && app[0].toLowerCase().contains(query.toLowerCase()) || (app[3] != null && app[3].toLowerCase().contains(query.toLowerCase()) || (app[4] != null && app[4].toLowerCase().contains(query.toLowerCase())))) {
                if (app[0] != null && app[0].toLowerCase().startsWith(query.toLowerCase())) {
                    matches.addFirst(app);
                }
                else {
                    matches.add(app);
                }
            }

        }

        return matches;
    }
}
