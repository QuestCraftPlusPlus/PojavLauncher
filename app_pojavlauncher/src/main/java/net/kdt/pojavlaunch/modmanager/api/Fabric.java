package net.kdt.pojavlaunch.modmanager.api;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

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

    public interface FabricGameVersionsInf {
        @GET("versions/game")
        Call<List<GameVersion>> getVersions();
    }

    public static class GameVersion {
        @SerializedName("version")
        public String version;
        @SerializedName("stable")
        public String stable;
    }
}
