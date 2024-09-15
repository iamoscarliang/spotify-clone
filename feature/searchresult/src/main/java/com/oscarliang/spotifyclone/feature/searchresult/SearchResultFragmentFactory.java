package com.oscarliang.spotifyclone.feature.searchresult;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SearchResultFragmentFactory {

    @NonNull
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MusicResultFragment();
            case 1:
                return new AlbumResultFragment();
            case 2:
                return new ArtistResultFragment();
            default:
                throw new IllegalArgumentException("Fragment not found!");
        }
    }

    public int getFragmentCount() {
        return 3;
    }

}
