package net.kdt.pojavlaunch.modmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.kdt.pojavlaunch.api.ModData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class State {
    private final String workDir;
    @SerializedName("instances")
    private final List<Instance> instances = new ArrayList<>();

    public State(String workDir) {
        this.workDir = workDir;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public Instance getInstance(String name) {
        for (Instance instance : instances) {
            if (instance.name.equals(name)) return instance;
        }
        return null;
    }

    public void addInstance(Instance instance) {
        instances.add(instance);
    }

    public void save() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(workDir + "/mods.json");
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        byte[] buf = gson.toJson(this).getBytes();
        out.write(buf, 0, buf.length);
        out.close();
    }

    public static class Instance {
        @SerializedName("name")
        private String name;
        @SerializedName("gameVersion")
        private String gameVersion;
        @SerializedName("fabricLoaderVersion")
        private String fabricLoaderVersion;
        @SerializedName("mods")
        private final List<ModData> mods = new ArrayList<>();

        public void setName(String name) {
            this.name = name;
        }

        public void setGameVersion(String gameVersion) {
            this.gameVersion = gameVersion;
        }

        public void setFabricLoaderVersion(String fabricLoaderVersion) {
            this.fabricLoaderVersion = fabricLoaderVersion;
        }

        public void addMod(ModData modData) {
            this.mods.add(modData);
        }

        public String getName() {
            return name;
        }

        public String getGameVersion() {
            return gameVersion;
        }

        public String getFabricLoaderVersion() {
            return fabricLoaderVersion;
        }

        public List<ModData> getMods() {
            return mods;
        }
    }
}
