package org.elcata98.translator.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ItemBean {

    private String name;
    private String refName;
    private String namespace;
    private String icon;
    private Unique unique;
    private Craftable craftable;
    private Armour armour;
    private Map map;

    private ItemBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Armour getArmour() {
        return armour;
    }

    public void setArmour(Armour armour) {
        this.armour = armour;
    }

    public Craftable getCraftable() {
        return craftable;
    }

    public void setCraftable(Craftable craftable) {
        this.craftable = craftable;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Unique getUnique() {
        return unique;
    }

    public void setUnique(Unique unique) {
        this.unique = unique;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static final class Unique {
        private String base;

        private Unique() {
        }

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static final class Craftable {
        private String category;
        private Boolean uniqueOnly;
        private Boolean corrupted;

        private Craftable() {
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Boolean getCorrupted() {
            return corrupted;
        }

        public void setCorrupted(Boolean corrupted) {
            this.corrupted = corrupted;
        }

        public Boolean getUniqueOnly() {
            return uniqueOnly;
        }

        public void setUniqueOnly(Boolean uniqueOnly) {
            this.uniqueOnly = uniqueOnly;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static final class Armour {
        private int[] ar;
        private int[] ev;
        private int[] es;

        private Armour() {
        }

        public int[] getAr() {
            return ar;
        }

        public void setAr(int[] ar) {
            this.ar = ar;
        }

        public int[] getEs() {
            return es;
        }

        public void setEs(int[] es) {
            this.es = es;
        }

        public int[] getEv() {
            return ev;
        }

        public void setEv(int[] ev) {
            this.ev = ev;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static final class Map {
        private String screenshot;

        private Map() {
        }

        public String getScreenshot() {
            return screenshot;
        }

        public void setScreenshot(String screenshot) {
            this.screenshot = screenshot;
        }
    }
}
