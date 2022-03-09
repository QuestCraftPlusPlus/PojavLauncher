package net.kdt.pojavlaunch.api;

import android.util.Log;
import com.google.gson.annotations.SerializedName;
import net.kdt.pojavlaunch.fragments.ModsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.List;

public class Modrinth {

    private static final String BASE_URL = "https://api.modrinth.com/v2/";
    private static Retrofit retrofit;

    public static Retrofit getClient(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface ModrinthProjectInf {
        @GET("project/{slug}")
        Call<ModrinthProject> getProject(@Path("slug") String slug);
    }

    public interface ModrinthVersionsInf {
        @GET("project/{slug}/version")
        Call<List<ModrinthVersion>> getVersions(@Path("slug") String slug);
    }

    public interface ModrinthSearchInf {
        @GET("search")
        Call<ModrinthSearchResult> searchMods(@Query("limit") int limit);
    }

    public static class ModrinthProject {
        @SerializedName("title")
        private String title;
        @SerializedName("icon_url")
        private String iconUrl;

        public String getTitle() {
            return title;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }

    public static class ModrinthVersion {
        @SerializedName("id")
        private String id;
        @SerializedName("loaders")
        private List<String> loaders;
        @SerializedName("game_versions")
        private List<String> gameVersions;
        @SerializedName("files")
        private List<ModrinthFile> files;

        public String getId() {
            return id;
        }

        public List<String> getLoaders() {
            return loaders;
        }

        public List<String> getGameVersions() {
            return gameVersions;
        }

        public List<ModrinthFile> getFiles() {
            return files;
        }

        public static class ModrinthFile {
            @SerializedName("url")
            private String url;
            @SerializedName("filename")
            private String filename;

            public String getUrl() {
                return url;
            }

            public String getFilename() {
                return filename;
            }
        }
    }

    public static class ModrinthSearchResult {
        @SerializedName("hits")
        private List<ModResult> hits;

        public List<ModResult> getHits() {
            return hits;
        }
    }

    public static ModData getModData(String slug, String gameVersion) throws IOException {
        ModrinthProjectInf projectInf = getClient().create(ModrinthProjectInf.class);
        ModrinthProject project = projectInf.getProject(slug).execute().body();

        ModrinthVersionsInf versionsInf = getClient().create(ModrinthVersionsInf.class);
        List<ModrinthVersion> versions = versionsInf.getVersions(slug).execute().body();

        if (project == null || versions == null) {
            return null;
        }

        for (ModrinthVersion modVersion : versions) {
            for (String loader : modVersion.getLoaders()) {
                if (loader.equals("fabric")) {
                    for (String modGameVersion : modVersion.getGameVersions()) {
                        if (modGameVersion.equals(gameVersion)) {
                            ModrinthVersion.ModrinthFile file = modVersion.getFiles().get(0);
                            return new ModData("modrinth",
                                    project.getTitle(),
                                    modVersion.getId(),
                                    project.getIconUrl(),
                                    file.getUrl(),
                                    file.getFilename()
                                    );
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void addProjectsToRecycler(ModsFragment.ModAPIAdapter adapter, String version, int offset, String query) {
        ModrinthSearchInf searchInf = getClient().create(ModrinthSearchInf.class);
        Call<ModrinthSearchResult> call = searchInf.searchMods(50);

        call.enqueue(new Callback<ModrinthSearchResult>() {
            @Override
            public void onResponse(Call<ModrinthSearchResult> call, Response<ModrinthSearchResult> response) {
                ModrinthSearchResult mods = response.body();
                if (mods != null) {
                    adapter.addMods(mods);
                }
            }

            @Override
            public void onFailure(Call<ModrinthSearchResult> call, Throwable t) {
                Log.d("MODRINTH", String.valueOf(t));
            }
        });
    }
}
