package com.oilpalm3f.gradingapp.datasync.helpers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager<T> {

    private static final String LOG_TAG = DataManager.class.getName();

    private static final DataManager instance = new DataManager();

    public static final String USER_DETAILS = "user_details";
    public static final String USER_VILLAGES = "user_villages";

    public static final String USER_ACTIVITY_RIGHTS = "user_activity_rights";

    private final Map<String, T> dataMap = new ConcurrentHashMap<>();

    public static DataManager getInstance() {
        return instance;
    }

    public synchronized void addData(final String type, final T data) {
        dataMap.put(type, data);
    }

    public synchronized T getDataFromManager(final String type) {
        return dataMap.get(type);
    }

    public void deleteData(final String type) {
        if (dataMap.get(type) != null) {
            dataMap.remove(type);
        }
    }
}
