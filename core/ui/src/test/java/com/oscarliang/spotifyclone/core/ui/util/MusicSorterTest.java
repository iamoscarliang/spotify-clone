package com.oscarliang.spotifyclone.core.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static java.util.Collections.emptyList;

import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.testing.util.TestUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class MusicSorterTest {

    @Test
    public void testEmpty() {
        List<Music> sorted = MusicSorter.sort(emptyList(), emptyList());
        assertNotNull(sorted);
        assertEquals(sorted.size(), 0);
    }

    @Test
    public void testEmptyMusics() {
        List<Music> sorted = MusicSorter.sort(emptyList(), Arrays.asList("0", "1", "2"));
        assertNotNull(sorted);
        assertEquals(sorted.size(), 0);
    }

    @Test
    public void testSort() {
        List<Music> sorted = MusicSorter.sort(
                TestUtil.createMusics(3, "", "foo"),
                Arrays.asList("2", "0", "1")
        );
        assertNotNull(sorted);
        assertEquals(sorted.size(), 3);
        assertEquals(sorted.get(0).getId(), "2");
        assertEquals(sorted.get(1).getId(), "0");
        assertEquals(sorted.get(2).getId(), "1");
    }

    @Test
    public void testSortWithMissingMusics() {
        List<Music> sorted = MusicSorter.sort(
                TestUtil.createMusics(2, "", "foo"),
                Arrays.asList("2", "1", "0")
        );
        assertNotNull(sorted);
        assertEquals(sorted.size(), 2);
        assertEquals(sorted.get(0).getId(), "1");
        assertEquals(sorted.get(1).getId(), "0");
    }

    @Test
    public void testSortWithMissingOrder() {
        List<Music> sorted = MusicSorter.sort(
                TestUtil.createMusics(3, "", "foo"),
                Arrays.asList("2", "0")
        );
        assertNotNull(sorted);
        assertEquals(sorted.size(), 2);
        assertEquals(sorted.get(0).getId(), "2");
        assertEquals(sorted.get(1).getId(), "0");
    }

    @Test
    public void testSortWithDuplicateMusics() {
        List<Music> sorted = MusicSorter.sort(
                Arrays.asList(
                        TestUtil.createMusic("0", "foo0"),
                        TestUtil.createMusic("0", "foo0"),
                        TestUtil.createMusic("1", "foo0"),
                        TestUtil.createMusic("2", "foo0")
                ),
                Arrays.asList("2", "1", "0")
        );
        assertNotNull(sorted);
        assertEquals(sorted.size(), 3);
        assertEquals(sorted.get(0).getId(), "2");
        assertEquals(sorted.get(1).getId(), "1");
        assertEquals(sorted.get(2).getId(), "0");
    }

    @Test
    public void testSortWithDuplicateOrder() {
        List<Music> sorted = MusicSorter.sort(
                TestUtil.createMusics(3, "", "foo"),
                Arrays.asList("2", "2", "1", "0")
        );
        assertNotNull(sorted);
        assertEquals(sorted.size(), 3);
        assertEquals(sorted.get(0).getId(), "2");
        assertEquals(sorted.get(1).getId(), "1");
        assertEquals(sorted.get(2).getId(), "0");
    }

}
