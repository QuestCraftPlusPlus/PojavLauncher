package net.kdt.pojavlaunch.modmanager.api;

import com.google.gson.annotations.SerializedName;

public class ModResult {

    @SerializedName("title")
    private final String title;
    @SerializedName("slug")
    private final String slug;
    @SerializedName("author")
    private final String author;
    @SerializedName("description")
    private final String description;
    @SerializedName("downloads")
    private final int downloads;
    @SerializedName("icon_url")
    private final String iconUrl;

    public ModResult(String title, String slug, String author, String description, int downloads, String iconUrl) {
        this.title = title;
        this.slug = slug;
        this.author = author;
        this.description = description;
        this.downloads = downloads;
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public int getDownloads() {
        return downloads;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
