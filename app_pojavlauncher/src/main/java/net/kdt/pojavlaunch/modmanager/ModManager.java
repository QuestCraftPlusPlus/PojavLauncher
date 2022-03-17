package net.kdt.pojavlaunch.modmanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kdt.pojavlaunch.BaseLauncherActivity;
import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.fragments.ModsFragment;
import net.kdt.pojavlaunch.modmanager.State.Instance;
import net.kdt.pojavlaunch.modmanager.api.Fabric;
import net.kdt.pojavlaunch.modmanager.api.ModData;
import net.kdt.pojavlaunch.modmanager.api.Modrinth;
import net.kdt.pojavlaunch.tasks.RefreshVersionListTask;
import net.kdt.pojavlaunch.utils.DownloadUtils;
import net.kdt.pojavlaunch.utils.UiUitls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ModManager {

    public static final String workDir = Tools.DIR_GAME_NEW + "/modmanager";
    public static State state = new State();
    private static JsonObject modCompats = new JsonObject();
    private static final ArrayList<String> currentDownloadSlugs = new ArrayList<>();
    private static boolean saveStateCalled = false;

    public static void init(BaseLauncherActivity activity) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    File path = new File(workDir);
                    if (!path.exists()) path.mkdir();

                    File modsJson = new File(workDir + "/mods.json");
                    if (!modsJson.exists()) {
                        String gameVersion = Tools.getCompatibleVersions("releases").get(0);
                        String flVersion = Fabric.getLatestLoaderVersion();
                        Fabric.install(gameVersion, flVersion);

                        String profileName = String.format("%s-%s-%s", "fabric-loader", flVersion, gameVersion);
                        Instance instance = new Instance();
                        instance.setName("QuestCraft-" + gameVersion);
                        instance.setGameVersion(gameVersion);
                        instance.setFabricLoaderVersion(profileName);
                        state.addInstance(instance);
                        Tools.write(modsJson.getPath(), Tools.GLOBAL_GSON.toJson(state)); //Cant use save state cause async issues
                    } else state = Tools.GLOBAL_GSON.fromJson(Tools.read(modsJson.getPath()), net.kdt.pojavlaunch.modmanager.State.class);

                    //Load instance versions
                    for (Instance instance : state.getInstances()) {
                        JMinecraftVersionList.Version version = Tools.GLOBAL_GSON.fromJson(Tools.read(Tools.DIR_HOME_VERSION + "/" + instance.getFabricLoaderVersion() + "/" + instance.getFabricLoaderVersion() + ".json"), JMinecraftVersionList.Version.class);
                        version.name = instance.getName();
                        version.arguments.jvm.add("-Dfabric.addMods=" + workDir + "/" + instance.getName());
                        activity.mVersionList.versions.add(version);
                    }
                    new RefreshVersionListTask(activity).execute();

                    InputStream stream = PojavApplication.assetManager.open("jsons/mod-compat.json");
                    modCompats = Tools.GLOBAL_GSON.fromJson(Tools.read(stream), JsonObject.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static String getModCompat(String slug) {
        JsonElement compatLevel = modCompats.get(slug);
        if (compatLevel != null) return compatLevel.getAsString();
        return "Untested";
    }

    //Will only save the state if there is nothing currently happening
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
                //fabric-loader-0.13.3-1.18.2/fabric-loader-0.13.3-1.18.2.json
                //fabric-loader-0.13.3-1.18.2/fabric-loader-0.13.3-1.18.2.json
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

    public static void createInstance(BaseLauncherActivity activity, String name, String gameVersion) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    String flVersion = Fabric.getLatestLoaderVersion();
                    Fabric.install(gameVersion, flVersion);

                    String profileName = String.format("%s-%s-%s", "fabric-loader", flVersion, gameVersion);
                    Instance instance = new Instance();
                    instance.setName(name);
                    instance.setGameVersion(gameVersion);
                    instance.setFabricLoaderVersion(profileName);
                    state.addInstance(instance);
                    saveState();

                    JMinecraftVersionList.Version version = Tools.GLOBAL_GSON.fromJson(Tools.read(Tools.DIR_HOME_VERSION + "/" + profileName + "/" + profileName + ".json"), JMinecraftVersionList.Version.class);
                    version.name = name;
                    version.arguments.jvm.add("-Dfabric.addMods=" + workDir + "/" + name);
                    activity.mVersionList.versions.add(version);
                    new RefreshVersionListTask(activity).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static void addMod(ModsFragment.InstalledModAdapter adapter, String instanceName, String slug, String gameVersion) throws IOException {
        Thread thread = new Thread() {
            public void run() {
                currentDownloadSlugs.add(slug);
                File path = new File(workDir + "/instances/" + instanceName);
                if (!path.exists()) path.mkdir();

                try {
                    ModData modData = Modrinth.getModData(slug, gameVersion);
                    if (modData == null) return;

                    //No duplicate mods allowed
                    Instance instance = state.getInstance(instanceName);
                    for (ModData mod : instance.getMods()) {
                        if (mod.getName().equals(modData.getName())) return;
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
                    if (modData.getSlug().equals(slug)) continue;
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
        };
        thread.start();
    }

    public static ArrayList<ModData> listInstalledMods(String instanceName) {
        return (ArrayList<ModData>) state.getInstance(instanceName).getMods();
    }
}