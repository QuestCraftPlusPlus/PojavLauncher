package net.kdt.pojavlaunch.modmanager;

import com.google.gson.annotations.SerializedName;
import net.kdt.pojavlaunch.modmanager.api.ModData;

import java.util.ArrayList;
import java.util.List;

public class State {
    @SerializedName("instances")
    private final List<Instance> instances = new ArrayList<>();

    public List<Instance> getInstances() {
        return instances;
    }

    public Instance getInstance(String name) {
        for (Instance instance : instances) {
            if (instance.name.equalsIgnoreCase(name)) return instance;
        }
        return null;
    }

    public void addInstance(Instance instance) {
        instances.add(instance);
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
