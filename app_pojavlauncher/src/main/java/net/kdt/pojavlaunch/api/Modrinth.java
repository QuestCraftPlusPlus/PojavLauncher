package net.kdt.pojavlaunch.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Modrinth {

    private static final String MODRINTH_API = "https://api.modrinth.com/v2";

    public static ModData getModData(String slug, String gameVersion) throws IOException {
        JsonElement response = HttpClient.get(MODRINTH_API + "/project/" + slug);
        if (response == null) return null;
        JsonObject project = response.getAsJsonObject();

        JsonElement response1 = HttpClient.get(MODRINTH_API + "/project/" + slug + "/version");
        if (response1 == null) return null;
        JsonArray versions = response1.getAsJsonArray();

        for (JsonElement element : versions) {
            JsonObject modVersion = element.getAsJsonObject();

            for (JsonElement element1 : modVersion.getAsJsonArray("loaders")) {
                if (Objects.equals(element1.getAsString(), "fabric")) {
                    for (JsonElement element2 : modVersion.getAsJsonArray("game_versions")) {
                        if (element2.getAsString().equals(gameVersion)) {
                            JsonObject modFile = modVersion.getAsJsonArray("files").get(0).getAsJsonObject();

                            return new ModData("modrinth",
                                    project.get("title").getAsString(),
                                    modVersion.get("id").getAsString(),
                                    modFile.get("url").getAsString(),
                                    modFile.get("filename").getAsString()
                                    );
                        }
                    }
                }
            }
        }
        return null;
    }

    public static ArrayList<ModResult> searchMods(String version, int offset, String query) throws IOException {
        String url = MODRINTH_API + "/search/?facets=[[\"categories:fabric\"],[\"versions:" + version + "\"],[\"project_type:mod\"]]&offset=" + offset + "&query=" + query;
        JsonElement response = HttpClient.get(url);
        if (response == null) return null;
        JsonArray mods = response.getAsJsonObject().getAsJsonArray("hits");

        ArrayList<ModResult> modResults = new ArrayList<>();
        for (JsonElement element : mods) {
            JsonObject mod = element.getAsJsonObject();

            modResults.add(new ModResult(mod.get("title").getAsString(),
                    mod.get("slug").getAsString(),
                    mod.get("author").getAsString(),
                    mod.get("description").getAsString(),
                    mod.get("downloads").getAsInt(),
                    mod.get("icon_url").getAsString()
                    ));
        }
        return modResults;
    }
}
