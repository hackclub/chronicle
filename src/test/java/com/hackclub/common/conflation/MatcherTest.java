package com.hackclub.common.conflation;

import com.hackclub.common.Utils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class MatcherTest {

    @Test
    void basics() {
        Set<From> fromSet = new HashSet<>();
        From from1 = new From("test1", "test1@test.com", 16);
        From from2 = new From("test2", "test2@test.com", 17);
        fromSet.add(from1);
        fromSet.add(from2);
        Set<To> toSet = new HashSet<>();
        To to1 = new To("test1", "test1@test.com", 16);
        To to2 = new To("test2", "test2@test.com", 17);
        toSet.add(to1);
        toSet.add(to2);

        HashMap<From, To> expectedResults = new HashMap<>();
        expectedResults.put(from1, to1);
        expectedResults.put(from2, to2);

        Set<MatchResult<From, To>> results = new Matcher<>(fromSet, toSet, createBasicScorer(0)).getResults();
        Assertions.assertEquals(results.size(), 2);
        for (Iterator<MatchResult<From, To>> it = results.iterator(); it.hasNext(); ) {
            MatchResult<From, To> match = it.next();
            Assertions.assertEquals(expectedResults.get(match.getFrom()), match.getTo());
            Assertions.assertEquals(1.0, match.getScore());
        }
    }

    @Test
    void testSlightDifferences() {
        Set<From> fromSet = new HashSet<>();
        From from1 = new From("test1", "test1@test.com", 15);
        From from2 = new From("test2", "test2@test.com", 17);
        fromSet.add(from1);
        fromSet.add(from2);
        Set<To> toSet = new HashSet<>();
        To to1 = new To("test1", "test1@test.com", 16);
        To to2 = new To("test2", "test2@test.com", 18);
        toSet.add(to1);
        toSet.add(to2);

        HashMap<From, To> expectedResults = new HashMap<>();
        expectedResults.put(from1, to1);
        expectedResults.put(from2, to2);

        Set<MatchResult<From, To>> results = new Matcher<>(fromSet, toSet, createBasicScorer(0)).getResults();
        Assertions.assertEquals(results.size(), 2);
        for (Iterator<MatchResult<From, To>> it = results.iterator(); it.hasNext(); ) {
            MatchResult<From, To> match = it.next();
            Assertions.assertEquals(expectedResults.get(match.getFrom()), match.getTo());
            Assertions.assertNotEquals(1.0, match.getScore());
        }
    }

    @Test
    void testMassiveDifferences() {
        Set<From> fromSet = new HashSet<>();
        From from1 = new From("test1", "test1@wefweftest.com", 15);
        From from2 = new From("test2", "test2@test.com", 14);
        fromSet.add(from1);
        fromSet.add(from2);
        Set<To> toSet = new HashSet<>();
        To to1 = new To("test1", "test1@test.com", 20);
        To to2 = new To("test2", "test2@twefwefest.com", 18);
        toSet.add(to1);
        toSet.add(to2);

        HashMap<From, To> expectedResults = new HashMap<>();
        expectedResults.put(from1, to1);
        expectedResults.put(from2, to2);

        Set<MatchResult<From, To>> results = new Matcher<>(fromSet, toSet, createBasicScorer(0)).getResults();
        Assertions.assertEquals(0, results.size());
    }


    @NotNull
    private static MatchScorer<From, To> createBasicScorer(float threshold) {
        return new MatchScorer<>() {
            @Override
            public double score(From from, To to) {
                int textDiff = Utils.levenshtein(from.name, to.name);
                int ageDiff = Math.abs(from.age - to.age);
                double ret = 1.0f - (textDiff + ageDiff);
                return ret;
            }

            @Override
            public double getThreshold() {
                return threshold;
            }
        };
    }

    private static class From {
        private String name;
        private String emailAddress;
        private int age;

        public From(String name, String emailAddress, int age) {
            this.name = name;
            this.emailAddress = emailAddress;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public int getAge() {
            return age;
        }
    }

    private static class To {
        private String name;
        private String emailAddress;
        private int age;

        public To(String name, String emailAddress, int age) {
            this.name = name;
            this.emailAddress = emailAddress;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public int getAge() {
            return age;
        }
    }
}