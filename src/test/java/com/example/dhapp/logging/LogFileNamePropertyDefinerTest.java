package com.example.dhapp.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * log4j2-spring.xml のログファイル名に使うホスト IP とランダム文字列の確認。
 */
class LogFileNamePropertyDefinerTest {

    @Test
    void hostIpIsIpv4WithHyphenSeparators() {
        String value = new HostIpPropertyDefiner().getPropertyValue();

        assertTrue(value.matches("\\d{1,3}-\\d{1,3}-\\d{1,3}-\\d{1,3}"), value);
        assertEquals("10-0-1-23", HostIpPropertyDefiner.toFileNameToken("10.0.1.23"));
    }

    @Test
    void randomStringUsesAlphanumericsOfConfiguredLength() {
        RandomStringPropertyDefiner definer = new RandomStringPropertyDefiner();

        String defaultValue = definer.getPropertyValue();
        assertTrue(defaultValue.matches("[a-zA-Z0-9]{10}"), defaultValue);

        definer.setLength(20);
        String value = definer.getPropertyValue();
        assertTrue(value.matches("[a-zA-Z0-9]{20}"), value);
        assertNotEquals(value, definer.getPropertyValue());
    }

    @Test
    void randomStringUsesConfiguredCharacters() {
        RandomStringPropertyDefiner definer = new RandomStringPropertyDefiner();
        definer.setLength(17);
        definer.setCharacters("0123456789");

        String value = definer.getPropertyValue();
        assertTrue(value.matches("[0-9]{17}"), value);
        assertNotEquals(value, definer.getPropertyValue());
    }

    @Test
    void dhLogLookupCachesHostIpAndRandomToken() {
        DhLogLookup lookup = new DhLogLookup();

        String host = lookup.lookup(null, "hostip");
        assertTrue(host.matches("\\d{1,3}-\\d{1,3}-\\d{1,3}-\\d{1,3}"), host);
        assertEquals(host, lookup.lookup(null, "hostip"));

        String token = lookup.lookup(null, "rand:20:alnum");
        assertTrue(token.matches("[a-zA-Z0-9]{20}"), token);
        assertEquals(token, lookup.lookup(null, "rand:20:alnum"));

        String digits = lookup.lookup(null, "rand:17:digits");
        assertTrue(digits.matches("[0-9]{17}"), digits);
        assertEquals(digits, lookup.lookup(null, "rand:17:digits"));
    }
}
