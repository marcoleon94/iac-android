package com.ievolutioned.iac.entity;

/**
 * Created by Daniel on 17/04/2017.
 */

public class Support {
    public abstract static class Category {
        public final static String NORMAL = "NORMAL";
        public final static String NO_SUPPORT = "SIN SUBSIDIO";
        public final static String EXTRA_TIME = "TIEMPO EXTRA";

        public static String getSupportCategoryId(final String category) {
            switch (category) {
                case NORMAL:
                    return "normal";
                case NO_SUPPORT:
                    return "no_support";
                case EXTRA_TIME:
                    return "extra_time";
                default:
                    return "no_support";
            }
        }
    }

    public abstract static class Type {
        public final static String FOOD = "COMIDA";
        public final static String BEVERAGE = "REFRESCO";
        public final static String WATER = "AGUA";

        public static String getSupportType(final String type) {
            switch (type) {
                case FOOD:
                    return "food";
                case BEVERAGE:
                    return "soda";
                case WATER:
                    return "water";
                default:
                    return "food";
            }
        }
    }
}
