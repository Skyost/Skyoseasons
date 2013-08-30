package com.skyost.seasons.api;

import java.util.Map;

import com.google.common.collect.Maps;

public enum Season {

    /**
     * Represent the spring
     */
    SPRING("spring"),
    /**
     * Represent the summer
     */
    SUMMER("summer"),
    /**
     * Represent the autumn
     */
    AUTUMN("autumn"),
    /**
     * Represent the winter
     */
    WINTER("winter");

    private final String name;
    private final static Map<String, Season> BY_NAME = Maps.newHashMap();
    
    Season(final String name) {
        this.name = name;
    }

    /**
     * Get the name of the season
     * @return the name of the season
     */
    public String getName() {
        return name;
    }

    /**
     * Get the Season from a specified name
     * @param name
     * @return the season from the specified name
     */
    public static Season fromName(final String name) {
        return BY_NAME.get(name.toLowerCase());
    }

    /**
     * Get the previous season.
     * 
     * @return SPRING If the previous season is spring,
     *         SUMMER If the previous season is summer,
     *         AUTUMN If the previous season is autumn or 
     *         WINTER If the previous season is winter. 
     */
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

    /**
     * Get the next season.
     * 
     * @return SPRING If the last season is spring,
     *         SUMMER If the next season is summer,
     *         AUTUMN If the next season is autumn or 
     *         WINTER If the next season is winter.
     */
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
