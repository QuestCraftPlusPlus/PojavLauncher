package net.kdt.pojavlaunch.modmanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modmanager.api.ModData;
import net.kdt.pojavlaunch.modmanager.api.Modrinth;
import net.kdt.pojavlaunch.fragments.ModsFragment;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.*;
import java.util.ArrayList;

import net.kdt.pojavlaunch.modmanager.State.Instance;

public class ModManager {

    private static final String workDir = Tools.DIR_GAME_NEW + "/modmanager";
    private static State state;
    private static JsonObject modCompats = new JsonObject();
    private static final ArrayList<String> currentDownloadSlugs = new ArrayList<>();

    public static void init() throws IOException {
        File path = new File(workDir);
        if (!path.exists()) {
            path.mkdir();
        }

        state = new State(workDir);
        State.Instance instance = new State.Instance();
        instance.setName("test");
        instance.setGameVersion("1.18.1");
        instance.setFabricLoaderVersion("0.13.3");
        state.addInstance(instance);
        //saveState();

        //Read mod compat json
        InputStream stream = PojavApplication.assetManager.open("jsons/mod-compat.json");
        byte[] buffer = new byte[stream.available()];
        stream.read(buffer);
        modCompats = Tools.GLOBAL_GSON.fromJson(new String(buffer), JsonObject.class);
    }

    public static String getModCompat(String slug) {
        JsonElement compatLevel = modCompats.get(slug);
        if (compatLevel != null) return compatLevel.getAsString();
        return "Untested";
    }

    public static void saveState() {
        try {
            state.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDownloading(String slug) {
        return currentDownloadSlugs.contains(slug);
    }

    public static void addMod(ModsFragment.InstalledModAdapter adapter, String instanceName, String slug, String gameVersion) throws IOException {
        Thread thread = new Thread() {
            public void run() {
                currentDownloadSlugs.add(slug);
                File path = new File(workDir + "/" + instanceName);
                if (!path.exists()) {
                    path.mkdir();
                }

                try {
                    ModData modData = Modrinth.getModData(slug, gameVersion);
                    if (modData == null) {
                        return;
                    }

                    //No duplicate mods allowed
                    Instance instance = state.getInstance(instanceName);
                    for (ModData mod : instance.getMods()) {
                        if (mod.getName().equals(modData.getName())) {
                            return;
                        }
                    }

                    DownloadUtils.downloadFile(modData.getUrl(), new File(path.getPath() + "/" + modData.getFilename()));
                    instance.addMod(modData);
                    adapter.addMod(modData);
                    currentDownloadSlugs.remove(slug);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static void setModActive(String instanceName, String slug, boolean active) {
        Thread thread = new Thread() {
            public void run() {
                Instance instance = state.getInstance(instanceName);
                for (ModData modData : instance.getMods()) {
                    if (modData.getSlug().equals(slug)) {
                        modData.setActive(active);

                        String suffix = "";
                        if (!active) suffix = ".disabled";

                        File path = new File(workDir + "/" + instanceName);
                        for (File modJar : path.listFiles()) {
                            if (modJar.getName().replace(".disabled", "").equals(modData.getFilename())) {
                                modJar.renameTo(new File(modData.getFilename() + suffix));
                            }
                        }
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    public static ArrayList<ModData> listInstalledMods(String instanceName) {
        return (ArrayList<ModData>) state.getInstance(instanceName).getMods();
    }
}