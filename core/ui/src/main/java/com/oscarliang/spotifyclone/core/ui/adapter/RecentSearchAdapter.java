package com.oscarliang.spotifyclone.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.oscarliang.spotifyclone.core.model.RecentSearch;
import com.oscarliang.spotifyclone.core.ui.databinding.LayoutRecentSearchItemBinding;

import java.util.Objects;

public class RecentSearchAdapter extends ListAdapter<RecentSearch, RecentSearchAdapter.RecentSearchViewHolder> {

    private final OnRecentSearchClickListener onRecentSearchClickListener;

    public RecentSearchAdapter(OnRecentSearchClickListener onRecentSearchClickListener) {
        super(new DiffUtil.ItemCallback<RecentSearch>() {
            @Override
            public boolean areItemsTheSame(@NonNull RecentSearch oldItem, @NonNull RecentSearch newItem) {
                return Objects.equals(oldItem.getQuery(), newItem.getQuery());
            }

            @Override
            public boolean areContentsTheSame(@NonNull RecentSearch oldItem, @NonNull RecentSearch newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
        this.onRecentSearchClickListener = onRecentSearchClickListener;
    }

    @NonNull
    @Override
    public RecentSearchViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutRecentSearchItemBinding binding = LayoutRecentSearchItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecentSearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecentSearchViewHolder holder,
            int position
    ) {
        RecentSearch recentSearch = getItem(position);
        holder.binding.textRecentSearchQuery.setText(recentSearch.getQuery());
        holder.itemView.setOnClickListener(view -> onRecentSearchClickListener.onRecentSearchClick(recentSearch));
    }

    public static class RecentSearchViewHolder extends RecyclerView.ViewHolder {

        private final LayoutRecentSearchItemBinding binding;

        public RecentSearchViewHolder(LayoutRecentSearchItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface OnRecentSearchClickListener {

        void onRecentSearchClick(RecentSearch recentSearch);

    }

}