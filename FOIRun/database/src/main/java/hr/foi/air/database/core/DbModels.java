package hr.foi.air.database.core;

public enum DbModels {
    ;
    public static final String PRIMARY_KEY = "_id";
    public enum LOCATION {
        ;
        public static final String TABLE = "location";
        public static final String ACTIVITY_ID = "activity_id";
        public static final String LAP = "lap";
        public static final String TYPE = "type";
        public static final String TIME = "time"; // in milliseconds since epoch
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String ACCURANCY = "accurancy";
        public static final String ALTITUDE = "altitude";
        public static final String SPEED = "speed";
        public static final String BEARING = "bearing";
        public static final String HR = "hr";
        public static final String CADENCE = "cadence";

        public static final int TYPE_START = 1;
        public static final int TYPE_END = 2;
        public static final int TYPE_GPS = 3;
        public static final int TYPE_PAUSE = 4;
        public static final int TYPE_RESUME = 5;
        public static final int TYPE_DISCARD = 6;
    }

    public enum LOGIN {
        ;
        public static final String TABLE = "user";
        public static final String EMAIL = "email";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String NAME = "name";
    }
}
