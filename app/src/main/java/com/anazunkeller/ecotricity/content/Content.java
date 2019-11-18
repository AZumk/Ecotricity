package com.anazunkeller.ecotricity.content;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Content {

    public static final ArrayList<RegionData> ITEMS = new ArrayList<>();


    public static void addItem(RegionData item) {
        ITEMS.add(item);
    }


    /**
     * Class that saves each item's data
     */
    public static class RegionData {

        public final String id;
        public final String dnoRegion;
        public final String shortName;
        public final String intensityForecast;
        public final String intensityIndex;

        public RegionData(String id, String dnoRegion, String shortName, String intensityForecast, String intensityIndex){
            this.id = id;
            this.dnoRegion = dnoRegion;
            this.shortName = shortName;
            this.intensityForecast = intensityForecast;
            this.intensityIndex = intensityIndex;
        }

    }
}
