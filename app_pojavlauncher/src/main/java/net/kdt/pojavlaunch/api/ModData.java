package net.kdt.pojavlaunch.api;

public class ModData {

    private final String platform;
    private final String name;
    private final String id;
    private final String url;
    private final String filename;

    public ModData(String platform, String name, String id, String url, String filename) {
        this.platform = platform;
        this.name = name;
        this.id = id;
        this.url = url;
        this.filename = filename;
    }

    public String getPlatform() {
        return platform;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }
}