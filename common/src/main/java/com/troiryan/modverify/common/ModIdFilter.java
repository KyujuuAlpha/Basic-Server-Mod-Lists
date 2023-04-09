package com.troiryan.modverify.common;

public class ModIdFilter {

    private static enum MatchType {
        EXACT, PREFIX, SUFFIX
    };

    private MatchType type; 
    private String modId;

    public ModIdFilter(String modId) {
        if (modId.startsWith("*")) {
            this.modId = modId.substring(1, modId.length());
            this.type = MatchType.PREFIX;
        } else if (modId.endsWith("*")) {
            this.modId = modId.substring(0, modId.length() - 1);
            this.type = MatchType.SUFFIX;
        } else {
            this.modId = modId;
            this.type = MatchType.EXACT;
        }   
    }

    public boolean match(String matchModId) {
        switch (this.type) {
            case PREFIX:
                return matchModId.endsWith(this.modId);
            case SUFFIX:
                return matchModId.startsWith(this.modId);
            case EXACT:
                return matchModId.equals(this.modId);
        }
        return false;
    }
}
