package com.ievolutioned.iac.entity;

/**
 * Created by Daniel on 17/04/2017.
 */

public class Support {
    public abstract static class Category {
        public final static String NORMAL = "NORMAL";
        public final static String NO_SUPPORT = "SIN SUBSIDIO";
        public final static String EXTRA_TIME = "TIEMPO EXTRA";
        public final static String GUEST = "INVITADO";

        private final static String NORMAL_ID = "normal";
        private final static String NO_SUPPORT_ID = "no_support";
        private final static String EXTRA_TIME_ID = "extra_time";
        private final static String GUEST_ID = "guest";

        public static String getSupportCategoryId(final String category) {
            switch (category) {
                case NORMAL:
                    return NORMAL_ID;
                case NO_SUPPORT:
                    return NO_SUPPORT_ID;
                case EXTRA_TIME:
                    return EXTRA_TIME_ID;
                case GUEST:
                    return GUEST_ID;
                default:
                    return NO_SUPPORT_ID;
            }
        }

        public static String getSupportCategory(final String categoryId) {
            switch (categoryId) {
                case NORMAL_ID:
                    return NORMAL;
                case NO_SUPPORT_ID:
                    return NO_SUPPORT;
                case EXTRA_TIME_ID:
                    return EXTRA_TIME;
                default:
                    return NO_SUPPORT;
            }
        }
    }

    public abstract static class Type {
        public final static String FOOD = "COMIDA";
        public final static String BEVERAGE = "REFRESCO";
        public final static String WATER = "AGUA";

        private final static String FOOD_ID = "food";
        private final static String BEVERAGE_ID = "soda";
        private final static String WATER_ID = "water";

        public static String getSupportTypeId(final String type) {
            switch (type) {
                case FOOD:
                    return FOOD_ID;
                case BEVERAGE:
                    return BEVERAGE_ID;
                case WATER:
                    return WATER_ID;
                default:
                    return FOOD_ID;
            }
        }

        public static String getSupportType(final String typeId) {
            switch (typeId) {
                case FOOD_ID:
                    return FOOD;
                case BEVERAGE:
                    return BEVERAGE;
                case WATER:
                    return WATER;
                default:
                    return FOOD;
            }
        }
    }
}
