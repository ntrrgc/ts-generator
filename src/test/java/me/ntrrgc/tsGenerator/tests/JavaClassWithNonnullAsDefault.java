/*
 * Copyright 2017 Alicia Boya García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
