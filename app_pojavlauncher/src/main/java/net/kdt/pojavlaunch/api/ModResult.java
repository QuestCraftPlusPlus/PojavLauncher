package net.kdt.pojavlaunch.api;

public class ModResult {

    private final String title;
    private final String slug;
    private final String author;
    private final String description;
    private final int downloads;
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
