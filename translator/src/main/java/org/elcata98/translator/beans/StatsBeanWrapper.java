package org.elcata98.translator.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StatsBeanWrapper {

    private StatsBean[] stats;

    public StatsBeanWrapper() {
    }

    public StatsBean[] getStats() {
        return stats;
    }

    public void setStats(StatsBean[] stats) {
        this.stats = stats;
    }
}
