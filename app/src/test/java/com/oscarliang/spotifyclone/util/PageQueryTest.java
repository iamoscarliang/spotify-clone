package com.oscarliang.spotifyclone.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PageQueryTest {

    @Test
    public void nullQuery() {
        PageQuery query = new PageQuery(null, 10, 1);
        assertTrue(query.isEmpty());
    }

    @Test
    public void emptyQuery() {
        PageQuery query = new PageQuery("", 10, 1);
        assertTrue(query.isEmpty());
    }

    @Test
    public void testEqual() {
        PageQuery query = new PageQuery("foo", 10, 1);
        assertEquals(query, new PageQuery("foo", 10, 1));
    }

}
