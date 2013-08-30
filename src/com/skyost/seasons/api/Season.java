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

    public Season getPrevious() {
        switch(this) {
        case SPRING:
            return WINTER;
        case SUMMER:
            return SPRING;
        case AUTUMN:
            return SUMMER;
        case WINTER:
            return AUTUMN;
        default:
            return null;
        }
    }

    public Season getNext() {
        switch(this) {
        case SPRING:
            return SUMMER;
        case SUMMER:
            return AUTUMN;
        case AUTUMN:
            return WINTER;
        case WINTER:
            return SPRING;
        default:
            return null;
        }
    }

    static {
        for (Season season : values()) {
            BY_NAME.put(season.name, season);
        }
    }
}
