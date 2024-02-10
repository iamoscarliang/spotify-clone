package com.oscarliang.spotifyclone.ui.album;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.util.EspressoTestUtil.withCollapsibleToolbarTitle;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaMetadata;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.domain.model.Album;
import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.ui.MainViewModel;
import com.oscarliang.spotifyclone.util.Event;
import com.oscarliang.spotifyclone.util.RecyclerViewMatcher;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.util.TestUtil;
import com.oscarliang.spotifyclone.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AlbumFragmentTest {

    private AlbumViewModel mAlbumViewModel;
    private MainViewModel mMainViewModel;

    private final MutableLiveData<Resource<Album>> mAlbum = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Music>>> mMusics = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddToPlaylistState = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Playlist>>> mAddToNewPlaylistState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<>();

    @Before
    public void init() {
        mAlbumViewModel = mock(AlbumViewModel.class);
        mMainViewModel = mock(MainViewModel.class);
        when(mAlbumViewModel.getAlbum()).thenReturn(mAlbum);
        when(mAlbumViewModel.getMusics()).thenReturn(mMusics);
        when(mMainViewModel.getAddToPlaylistState()).thenReturn(mAddToPlaylistState);
        when(mMainViewModel.getAddToNewPlaylistState()).thenReturn(mAddToNewPlaylistState);
        when(mMainViewModel.getPlaying()).thenReturn(mIsPlaying);

        FragmentScenario.launchInContainer(AlbumFragment.class, null, new FragmentFactory() {
            @NonNull
            @Override
            public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                AlbumFragment fragment = new AlbumFragment();
                fragment.mFactory = ViewModelUtil.createFor(Arrays.asList(mAlbumViewModel, mMainViewModel));
                fragment.mAlbumId = "foo";
                return fragment;
            }
        });
    }

    @Test
    public void testLoading() {
        mAlbum.postValue(Resource.loading(null));
        mMusics.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_album)).check(matches(isDisplayed()));
        onView(withId(R.id.shimmer_layout_music)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mAlbum.postValue(Resource.success(TestUtil.createAlbum("foo", "bar", "FOO", "BAR")));
        onView(isAssignableFrom(CollapsingToolbarLayout.class)).
                check(matches(withCollapsibleToolbarTitle(is("bar"))));
        onView(withId(R.id.text_album_artist)).check(matches(withText("FOO")));
        onView(withId(R.id.text_album_year)).check(matches(withText(getString(R.string.album_year, "BAR"))));
        onView(withId(R.id.shimmer_layout_album)).check(matches(not(isDisplayed())));

        mMusics.postValue(Resource.success(Arrays.asList(TestUtil.createMusic("foo", "bar"))));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_music)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mAlbum.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_album)).check(matches(isDisplayed()));
        onView(withId(R.id.shimmer_layout_music)).check(matches(isDisplayed()));
        onView(withId(R.id.layout_loading_state_album)).check(matches(isDisplayed()));
        onView(withId(R.id.text_message)).check(matches(withText("Failed to load")));
    }

    @Test
    public void testAddToPlaylist() {
        mAddToPlaylistState.postValue(new Event<>(Resource.success(TestUtil.createPlaylist("foo", "bar"))));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(getString(R.string.playlist_add, "bar"))));
    }

    @Test
    public void testErrorAddToPlaylist() {
        mAddToPlaylistState.postValue(new Event<>(Resource.error("Failed to add", null)));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to add")));
    }

    @Test
    public void testAddToNewPlaylist() {
        mAddToNewPlaylistState.postValue(new Event<>(Resource.success(TestUtil.createPlaylist("foo", "bar"))));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(getString(R.string.playlist_add, "bar"))));
    }

    @Test
    public void testErrorAddToNewPlaylist() {
        mAddToNewPlaylistState.postValue(new Event<>(Resource.error("Failed to add", null)));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to add")));
    }

    @Test
    public void testPlaying() {
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setTitle("foo")
                .build();
        when(mMainViewModel.getPlaylistMetadata()).thenReturn(metadata);
        mIsPlaying.postValue(false);
        onView(withId(R.id.fab_play)).check(matches(withTagValue(equalTo(R.drawable.ic_play))));
        mIsPlaying.postValue(true);
        onView(withId(R.id.fab_play)).check(matches(withTagValue(equalTo(R.drawable.ic_pause))));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_music);
    }

    private String getString(@StringRes int id, Object... args) {
        return ApplicationProvider.getApplicationContext().getString(id, args);
    }

}
