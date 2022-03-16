package net.kdt.pojavlaunch.modmanager.api;

import android.os.Build;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.installer.LoaderVersion;
import net.fabricmc.installer.client.ClientInstaller;
import net.fabricmc.installer.util.InstallerProgress;
import net.kdt.pojavlaunch.Tools;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Fabric {

    private static final String BASE_URL = "https://meta.fabricmc.net/v2/";
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

    public interface FabricLoaderVersionsInf {
        @GET("versions/loader")
        Call<List<Version>> getVersions();
    }

    public static class Version {
        @SerializedName("version")
        public String version;
        @SerializedName("stable")
        public boolean stable;
    }

    public static String getLatestLoaderVersion() throws IOException {
        FabricLoaderVersionsInf inf = getClient().create(FabricLoaderVersionsInf.class);
        List<Version> versions = inf.getVersions().execute().body();
        if (versions != null) {
            for (Version version : versions) {
                if (version.stable) return version.version;
            }
        }
        return null;
    }

    //Won't do anything if version is already installed
    public static void install(String gameVersion, String loaderVersion) {
        try {
            String profileName = String.format("%s-%s-%s", "fabric-loader", loaderVersion, gameVersion);
            if (new File(Tools.DIR_HOME_VERSION + "/versions/" + profileName + "/" + profileName + ".jar").exists()) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ClientInstaller.install(Paths.get(Tools.DIR_GAME_NEW), gameVersion, new LoaderVersion(loaderVersion), new InstallerProgress() {
                    @Override
                    public void updateProgress(String s) {
                        Log.d("FABRIC", s);
                    }

                    @Override
                    public void error(Throwable throwable) {
                        Log.d("FABRIC", throwable.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}