package org.elcata98.translator.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class StatsBean {

    private String ref;
    private Integer better;
    private Matcher[] matchers;
    private Anointment[] anointments;

    private StatsBean() {
    }

    public Anointment[] getAnointments() {
        return anointments;
    }

    public void setAnointments(Anointment[] anointments) {
        this.anointments = anointments;
    }

    public Integer getBetter() {
        return better;
    }

    public void setBetter(Integer better) {
        this.better = better;
    }

    public Matcher[] getMatchers() {
        return matchers;
    }

    @JsonIgnore
    public Stream<Matcher> getMatchersStream(){
        return Stream.of(matchers);
    }

    public void setMatchers(Matcher[] matchers) {
        this.matchers = matchers;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static final class Matcher {
        private String string;
        private Integer value;
        private Boolean negate;

        private Matcher() {
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Boolean getNegate() {
            return negate;
        }

        public void setNegate(Boolean negate) {
            this.negate = negate;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static final class Anointment{
        private Integer roll;
        private String oils;

        private Anointment() {
        }

        public String getOils() {
            return oils;
        }

        public void setOils(String oils) {
            this.oils = oils;
        }

        public Integer getRoll() {
            return roll;
        }

        public void setRoll(Integer roll) {
            this.roll = roll;
        }
    }
}
