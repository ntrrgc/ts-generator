package me.ntrrgc.tsGenerator.tests;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JavaClassWithNullables {
    @Nonnull
    private String name;

    @Nonnull
    private int[] results;

    @Nullable
    private int[] nextResults;

    JavaClassWithNullables(@Nonnull String name, @Nonnull int[] results, @Nullable int[] nextResults) {
        this.name = name;
        this.results = results;
        this.nextResults = nextResults;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    public int[] getResults() {
        return results;
    }

    public void setResults(@Nonnull int[] results) {
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
