package net.kdt.pojavlaunch.modmanager;

import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.fragments.ModsFragment;
import net.kdt.pojavlaunch.modmanager.api.ModData;
import net.kdt.pojavlaunch.modmanager.api.Modrinth;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import net.kdt.pojavlaunch.modmanager.State.Instance;
import net.kdt.pojavlaunch.utils.UiUitls;

public class ModManager {

    private static final String workDir = Tools.DIR_GAME_NEW + "/modmanager";
    private static State state = new State();
    private static JsonObject modCompats = new JsonObject();
    private static final ArrayList<String> currentDownloadSlugs = new ArrayList<>();
    private static boolean saveStateCalled = false;

    public static void init() throws IOException {
        File path = new File(workDir);
        if (!path.exists()) {
            path.mkdir();
        }

        File modsJson = new File(workDir + "/mods.json");
        if (!modsJson.exists()) {
            State.Instance instance = new State.Instance();
            instance.setName("test");
            instance.setGameVersion("1.18.1");
            instance.setFabricLoaderVersion("0.13.3");
            state.addInstance(instance);
            saveState();
        } else {
            state = Tools.GLOBAL_GSON.fromJson(Tools.read(modsJson.getPath()), State.class);
        }

        //Read mod compat json
        InputStream stream = PojavApplication.assetManager.open("jsons/mod-compat.json");
        modCompats = Tools.GLOBAL_GSON.fromJson(Tools.read(stream), JsonObject.class);
    }

    public static String getModCompat(String slug) {
        JsonElement compatLevel = modCompats.get(slug);
        if (compatLevel != null) return compatLevel.getAsString();
        return "Untested";
    }

    //Only save the state if there is nothing currently happening
    public static void saveState() {
        Thread thread = new Thread() {
            public void run() {
                while (currentDownloadSlugs.size() > 0) {
                    synchronized (state) {
                        try {
                            state.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    Tools.write(workDir + "/mods.json", Tools.GLOBAL_GSON.toJson(state));
                    saveStateCalled = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        if (!saveStateCalled) {
            saveStateCalled = true;
            thread.start();
        }
    }

    public static boolean isDownloading(String slug) {
        return currentDownloadSlugs.contains(slug);
    }

    public static void addMod(ModsFragment.InstalledModAdapter adapter, String instanceName, String slug, String gameVersion) throws IOException {
        Thread thread = new Thread() {
            public void run() {
                currentDownloadSlugs.add(slug);
                File path = new File(workDir + "/instances/" + instanceName);
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

                    //Must run on ui thread or it crashes. Idk why it works without this over in Modrinth.java
                    UiUitls.runOnUI(() -> adapter.addMod(modData));

                    DownloadUtils.downloadFile(modData.getUrl(), new File(path.getPath() + "/" + modData.getFilename()));
                    instance.addMod(modData);
                    currentDownloadSlugs.remove(slug);

                    saveState();
                    synchronized (state) {
                        state.notifyAll();
                    }
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

                        File path = new File(workDir + "/instances/" + instanceName);
                        for (File modJar : path.listFiles()) {
                            if (modJar.getName().replace(".disabled", "").equals(modData.getFilename())) {
                                try {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        Path source = Paths.get(modJar.getPath());
                                        Files.move(source, source.resolveSibling(modData.getFilename() + suffix));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        saveState();
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