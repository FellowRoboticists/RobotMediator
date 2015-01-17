package com.naiveroboticist.robotmediator;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCamelCaseOneWord() {
        assertEquals("Word", StringUtils.camelCase("wOrD"));
    }

    @Test
    public void testCamelCaseTwoWords() {
        assertEquals("WordUp", StringUtils.camelCase("wOrD_UP"));
    }

    @Test
    public void testCamelCaseFourWords() {
        assertEquals("WordToYourMother", StringUtils.camelCase("wOrD_to_YOUR_moTHer"));
    }

}
