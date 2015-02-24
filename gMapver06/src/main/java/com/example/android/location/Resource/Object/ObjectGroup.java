package com.example.android.location.Resource.Object;

import java.util.HashMap;

/**
 * Created by Adisorn on 2/14/2015.
 */
public class ObjectGroup {
    private HashMap<String,ObjectDetail> objectDetailList;
    private String[] keyList;
    String mainKey;

    public ObjectGroup(String[] keyList, String mainKey) {
        this.keyList = keyList;
        this.mainKey = mainKey;
    }

    public ObjectGroup(String[] keyList) {
        this.keyList = keyList;
    }

    public HashMap<String, ObjectDetail> getObjectDetailList() {
        return objectDetailList;
    }

    public void setObjectDetailList(HashMap<String, ObjectDetail> objectDetailList) {
        this.objectDetailList = objectDetailList;
    }

    public String getMainKey() {
        return mainKey;
    }

    public void setMainKey(String mainKey) {
        this.mainKey = mainKey;
    }

    public String[] getKeyList() {
        return keyList;
    }

    public void setKeyList(String[] keyList) {
        this.keyList = keyList;
    }
}
