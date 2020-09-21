package com.example.scoutingplatform;

import android.provider.BaseColumns;

public class RecyclerContainerdp {
    private RecyclerContainerdp(){}


    public static final class RecyclerEntry implements BaseColumns {
        public static final String TABLE_NAME = "cap_table";
        public static final String COLDESCRIPTION =  "PestDescription";
        public static final String COLGENDER =  "Gender";
        public static final String COLPOS =  "PestLocation";
        public static final String COLSEV =  "Severity";
        public static final String COLQUAN =  "Quantity";
        public static final String COLTIME =  "Timestamp";
        public static final String COLID =  "ID";

    }
}
