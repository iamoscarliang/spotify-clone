package com.oscarliang.spotifyclone.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.oscarliang.spotifyclone.domain.model.Music;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class MusicSorterTest {

    @Test
    public void empty() {
        List<Music> sorted = MusicSorter.sort(Collections.emptyList(), Collections.emptyList());
        assertNotNull(sorted);
        assertEquals(sorted.size(), 0);
    }

    @Test
    public void emptyData() {
        List<Music> sorted = MusicSorter.sort(Collections.emptyList(), Arrays.asList("foo", "bar"));
        assertNotNull(sorted);
        assertEquals(sorted.size(), 0);
    }

    @Test
    public void basic() {
        List<Music> data = new ArrayList<>();
        data.add(new Music("a", null, null, null, null, null, null, null));
        data.add(new Music("b", null, null, null, null, null, null, null));
        data.add(new Music("c", null, null, null, null, null, null, null));
        List<Music> sorted = MusicSorter.sort(data, Arrays.asList("c", "a", "b"));
        assertNotNull(sorted);
        assertEquals(sorted.size(), 3);
        assertEquals(sorted.get(0).getId(), "c");
        assertEquals(sorted.get(1).getId(), "a");
        assertEquals(sorted.get(2).getId(), "b");
    }

    @Test
    public void missingData() {
        List<Music> data = new ArrayList<>();
        data.add(new Music("a", null, null, null, null, null, null, null));
        data.add(new Music("b", null, null, null, null, null, null, null));
        List<Music> sorted = MusicSorter.sort(data, Arrays.asList("c", "b", "a"));
        assertNotNull(sorted);
        assertEquals(sorted.size(), 2);
        assertEquals(sorted.get(0).getId(), "b");
        assertEquals(sorted.get(1).getId(), "a");
    }

    @Test
    public void missingOrder() {
        List<Music> data = new ArrayList<>();
        data.add(new Music("a", null, null, null, null, null, null, null));
        data.add(new Music("b", null, null, null, null, null, null, null));
        data.add(new Music("c", null, null, null, null, null, null, null));
        List<Music> sorted = MusicSorter.sort(data, Arrays.asList("c", "a"));
        assertNotNull(sorted);
        assertEquals(sorted.size(), 2);
        assertEquals(sorted.get(0).getId(), "c");
        assertEquals(sorted.get(1).getId(), "a");
    }

    @Test
    public void duplicateData() {
        List<Music> data = new ArrayList<>();
        data.add(new Music("a", null, null, null, null, null, null, null));
        data.add(new Music("a", null, null, null, null, null, null, null));
        data.add(new Music("b", null, null, null, null, null, null, null));
        data.add(new Music("c", null, null, null, null, null, null, null));
        List<Music> sorted = MusicSorter.sort(data, Arrays.asList("c", "b", "a"));
        assertNotNull(sorted);
        assertEquals(sorted.size(), 3);
        assertEquals(sorted.get(0).getId(), "c");
        assertEquals(sorted.get(1).getId(), "b");
        assertEquals(sorted.get(2).getId(), "a");
    }

    @Test
    public void duplicateOrder() {
        List<Music> data = new ArrayList<>();
        data.add(new Music("a", null, null, null, null, null, null, null));
        data.add(new Music("b", null, null, null, null, null, null, null));
        data.add(new Music("c", null, null, null, null, null, null, null));
        List<Music> sorted = MusicSorter.sort(data, Arrays.asList("c", "c", "b", "a"));
        assertNotNull(sorted);
        assertEquals(sorted.size(), 3);
        assertEquals(sorted.get(0).getId(), "c");
        assertEquals(sorted.get(1).getId(), "b");
        assertEquals(sorted.get(2).getId(), "a");
    }

}
