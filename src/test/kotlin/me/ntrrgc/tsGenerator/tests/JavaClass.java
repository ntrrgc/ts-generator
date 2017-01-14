package me.ntrrgc.tsGenerator.tests;

public class JavaClass {
    private String name;
    private int[] results;
    private boolean finished;
    private char[][] multidimensional;

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

    public boolean isFinished() {
        return finished;
    }

    public char[][] getMultidimensional() {
        return multidimensional;
    }

    public void setMultidimensional(char[][] multidimensional) {
        this.multidimensional = multidimensional;
    }
}
