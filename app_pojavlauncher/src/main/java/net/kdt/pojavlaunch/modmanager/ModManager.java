package net.kdt.pojavlaunch.modmanager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.api.ModData;
import net.kdt.pojavlaunch.api.Modrinth;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.*;
import java.util.ArrayList;

public class ModManager {

    private static final String workDir = Tools.DIR_GAME_NEW + "/modmanager";
    private static final File mods = new File(workDir + "mods.json");

    public static void init() throws IOException {
        File path = new File(workDir);
        if (!path.exists()) {
            path.mkdir();
        }

        //Create mods.json if it doesn't exist
        if (mods.createNewFile()) {
            FileWriter writer = new FileWriter(mods);
            JsonObject modsJson = new JsonObject();

            //Temp test data
            JsonObject instances = new JsonObject();
            JsonObject instance = new JsonObject();
            instance.addProperty("gameVersion", "1.18.1");
            instance.addProperty("fabricLoaderVersion", "0.13.3");
            instance.add("mods", new JsonArray());
            instance.add("test", instance);
            modsJson.add("instances", instances);

            writer.write(String.valueOf(instances));
            writer.close();
        }
    }

    public static void addMod(String instanceName, String gameVersion, String slug) throws IOException {
        File path = new File(workDir + "/" + instanceName);
        if (!path.exists()) {
            path.mkdir();
        }

        ModData modData = Modrinth.getModData(slug, gameVersion);
        DownloadUtils.downloadFile(modData.getUrl(), new File(path.getPath() + "/" + modData.getFilename()));
        Gson gson = new Gson();
        FileReader reader = new FileReader(mods);
        JsonObject modsJson = gson.fromJson(reader, JsonObject.class);
        reader.close();

        //parse data for mods.json - Probably a better way to do this with beans but ill do it later
        JsonObject instances = modsJson.get("instances").getAsJsonObject();
        JsonObject instance = instances.getAsJsonObject().get(instanceName).getAsJsonObject();
        JsonArray instanceMods = instance.getAsJsonArray("mods");
        JsonObject mod = new JsonObject();
        mod.addProperty("platform", modData.getPlatform());
        mod.addProperty("name", modData.getName());
        mod.addProperty("id", modData.getId());
        mod.addProperty("filename", modData.getFilename());
        instanceMods.add(mod);
        instance.add("mods", instanceMods);
        instances.add(instanceName, instance);
        modsJson.add("instances", instances);

        FileWriter writer = new FileWriter(mods);
        writer.write(String.valueOf(modsJson));
        writer.close();
    }
    
    //Convert to use beans later
    public static ArrayList<ModData> listMods(String instanceName) throws FileNotFoundException {
        Gson gson = new Gson();
        FileReader reader = new FileReader(mods);
        JsonObject modsJson = gson.fromJson(reader, JsonObject.class);
        JsonObject instance = modsJson.get("instances").getAsJsonObject().getAsJsonObject().get(instanceName).getAsJsonObject();

        ArrayList<ModData> instanceMods = new ArrayList<>();
        for (JsonElement element : instance.getAsJsonArray("mods")) {
            JsonObject mod = element.getAsJsonObject();
            instanceMods.add(new ModData(
                    mod.get("platform").getAsString(),
                    mod.get("name").getAsString(),
                    mod.get("id").getAsString(),
                    mod.get("url").getAsString(),
                    mod.get("filename").getAsString()
            ));
        }
        return instanceMods;
    }
}
