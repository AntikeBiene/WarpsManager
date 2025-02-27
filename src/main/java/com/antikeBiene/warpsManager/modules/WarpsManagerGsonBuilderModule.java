package com.antikeBiene.warpsManager.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heroslender.helpers.LocationAdapter;
import org.bukkit.Location;

public class WarpsManagerGsonBuilderModule {

    public static Gson gson() {
        return new GsonBuilder()
                    .registerTypeAdapter(Location.class, new LocationAdapter())
                    .setPrettyPrinting()
                    .create();
    }
}
