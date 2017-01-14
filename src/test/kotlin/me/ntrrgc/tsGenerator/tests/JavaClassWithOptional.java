package me.ntrrgc.tsGenerator.tests;


import java.util.Optional;

public class JavaClassWithOptional {
    private String name;
    private String surname;

    public Optional<String> getSurname() {
        return Optional.ofNullable(surname);
    }

    public String getName() {
        return name;
    }
}