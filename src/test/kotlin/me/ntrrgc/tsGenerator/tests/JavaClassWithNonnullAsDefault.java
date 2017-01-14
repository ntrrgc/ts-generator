package me.ntrrgc.tsGenerator.tests;


import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JavaClassWithNonnullAsDefault {
    private String name;

    private int[] results;

    @Nullable
    private int[] nextResults;

    JavaClassWithNonnullAsDefault(String name, int[] results, @Nullable int[] nextResults) {
        this.name = name;
        this.results = results;
        this.nextResults = nextResults;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int[] getResults() {
        return results;
    }

    public void setResults(int[] results) {
        this.results = results;
    }

    @Nullable
    public int[] getNextResults() {
        return nextResults;
    }

    public void setNextResults(@Nullable int[] nextResults) {
        this.nextResults = nextResults;
    }
}
