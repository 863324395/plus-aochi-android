package com.tym.shortvideo.filter.helper;

/**
 * 滤镜类型枚举
 *
 * @author Created by jz on 2017/5/2 16:57
 */
public enum MagicFilterType {
    NONE,
    FAIRYTALE,
    SUNRISE,
    SUNSET,
    WHITECAT,
    BLACKCAT,
    BEAUTY,
    SKINWHITEN,
    HEALTHY,
    SWEETS,
    ROMANCE,
    SAKURA,
    WARM,
    ANTIQUE,
    NOSTALGIA,
    CALM,
    LATTE,
    TENDER,
    COOL,
    EMERALD,
    EVERGREEN,
    CRAYON,
    SKETCH,
    AMARO,
    BRANNAN,
    BROOKLYN,
    EARLYBIRD,
    FREUD,
    HEFE,
    HUDSON,
    INKWELL,
    KEVIN,
    LOMO,
    N1977,
    NASHVILLE,
    PIXAR,
    RISE,
    SIERRA,
    SUTRO,
    TOASTER2,
    VALENCIA,
    WALDEN,
    XPROII,
    //image adjust
    CONTRAST,
    BRIGHTNESS,
    EXPOSURE,
    HUE,
    SATURATION,
    SHARPEN("ss"),
    WHITENORREDDEN,
    IMAGE_ADJUST;

    MagicFilterType() {
    }

    private String value;

    MagicFilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}