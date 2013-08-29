package com.skyost.seasons.api;

import java.util.Map;

import com.google.common.collect.Maps;

public enum Season {

    SPRING("spring"),
    SUMMER("summer"),
    AUTUMN("autumn"),
    WINTER("winter");

    private final String name;
    private final static Map<String, Season> BY_NAME = Maps.newHashMap();
    
    Season(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Season fromName(final String name) {
        return BY_NAME.get(name.toLowerCase());
    }

    static {
        for (Season season : values()) {
            BY_NAME.put(season.name, season);
        }
    }
}
