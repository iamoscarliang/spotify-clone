package com.oscarliang.spotifyclone.core.data.mapper;

import com.oscarliang.spotifyclone.core.database.model.RecentSearchEntity;
import com.oscarliang.spotifyclone.core.model.RecentSearch;

public class RecentSearchMapper {

    public static RecentSearch map(RecentSearchEntity entity) {
        return new RecentSearch(
                entity.getQuery(),
                entity.getQueriedTime()
        );
    }

    public static RecentSearchEntity map(RecentSearch model) {
        return new RecentSearchEntity(
                model.getQuery(),
                model.getQueriedTime()
        );
    }

}