package net.kdt.pojavlaunch.modmanager.api;

import com.google.gson.annotations.SerializedName;

public class ModData {

    @SerializedName("platform")
    private final String platform;
    @SerializedName("name")
    private final String name;
    @SerializedName("slug")
    private final String slug;
    @SerializedName("id")
    private final String id;
    @SerializedName("iconUrl")
    private final String iconUrl;
    @SerializedName("url")
    private final String url;
    @SerializedName("filename")
    private final String filename;
    @SerializedName("isActive")
    private boolean isActive = true;

    public ModData(String platform, String name, String slug, String iconUrl, String id, String url, String filename) {
        this.platform = platform;
        this.name = name;
        this.slug = slug;
        this.id = id;
        this.iconUrl = iconUrl;
        this.url = url;
        this.filename = filename;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPlatform() {
        return platform;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getId() {
        return id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isActive() {
        return isActive;
    }
}