package net.kdt.pojavlaunch.modmanager.api;

import android.util.Log;
import androidx.annotation.Keep;
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
        public String title;
        @SerializedName("slug")
        public String slug;
        @SerializedName("icon_url")
        public String iconUrl;
    }

    public static class ModrinthVersion {
        @SerializedName("id")
        public String id;
        @SerializedName("loaders")
        public List<String> loaders;
        @SerializedName("game_versions")
        public List<String> gameVersions;
        @SerializedName("files")
        public List<ModrinthFile> files;

        public static class ModrinthFile {
            @SerializedName("url")
            public String url;
            @SerializedName("filename")
            public String filename;
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
            for (String loader : modVersion.loaders) {
                if (loader.equals("fabric")) {
                    for (String modGameVersion : modVersion.gameVersions) {
                        if (modGameVersion.equals(gameVersion)) {
                            ModrinthVersion.ModrinthFile file = modVersion.files.get(0);
                            return new ModData("modrinth",
                                    project.title,
                                    project.slug,
                                    project.iconUrl,
                                    modVersion.id,
                                    file.url,
                                    file.filename
                                    );
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void addProjectsToRecycler(ModsFragment.APIModAdapter adapter, String version, int offset, String query) {
        ModrinthSearchInf searchInf = getClient().create(ModrinthSearchInf.class);
        searchInf.searchMods(50).enqueue(new Callback<ModrinthSearchResult>() {
            @Override
            public void onResponse(Call<ModrinthSearchResult> call, Response<ModrinthSearchResult> response) {
                ModrinthSearchResult mods = response.body();
                if (mods != null) adapter.addMods(mods);
            }

            @Override
            public void onFailure(Call<ModrinthSearchResult> call, Throwable t) {
                Log.d("MODRINTH", String.valueOf(t));
            }
        });
    }
}