package com.troiryan.modverify.common;

public class Mod {
    
    private String modId, version, combinedString;

    // keep only alphanumeric, dash, underscore, plus, and dots
    private static String getFilteredString(String str) {
        return str.replaceAll("[^\\w\\-\\+.]", "");
    }

    public Mod(String modId, String version) {
        this.modId = getFilteredString(modId);
        this.version = getFilteredString(version);
        this.combinedString = this.modId + "_" + this.version;
    }

    public String getModID() {
        return this.modId;
    }

    public String getVersion() {
        return this.version;
    }

    public String getCombinedString() {
        return this.combinedString;
    }

    @Override
    public int hashCode() {
        return this.combinedString.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.combinedString.equals((String) ((Mod) obj).getCombinedString());
    }
}
