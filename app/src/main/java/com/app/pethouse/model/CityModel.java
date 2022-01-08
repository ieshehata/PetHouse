package com.app.pethouse.model;

public class CityModel {
    private String key;
    private String governorateKey;
    private String name = "";

    public CityModel() {
    }

    public CityModel(String key, String governorateKey, String name) {
        this.key = key;
        this.governorateKey = governorateKey;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGovernorateKey() {
        return governorateKey;
    }

    public void setGovernorateKey(String governorateKey) {
        this.governorateKey = governorateKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
