package com.oscarliang.spotifyclone.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.model.Artist;
import com.oscarliang.spotifyclone.core.ui.databinding.LayoutArtistCardItemBinding;

import java.util.Objects;

public class ArtistCardAdapter extends ListAdapter<Artist, ArtistCardAdapter.AllArtistViewHolder> {

    private final onArtistClickListener onArtistClickListener;

    public ArtistCardAdapter(onArtistClickListener onArtistClickListener) {
        super(new DiffUtil.ItemCallback<Artist>() {
            @Override
            public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
        this.onArtistClickListener = onArtistClickListener;
    }

    @NonNull
    @Override
    public AllArtistViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutArtistCardItemBinding binding = LayoutArtistCardItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AllArtistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull AllArtistViewHolder holder,
            int position
    ) {
        Artist artist = getItem(position);
        Glide.with(holder.itemView.getContext())
                .load(artist.getImageUrl())
                .placeholder(R.drawable.ic_artist)
                .error(R.drawable.ic_error)
                .into(holder.binding.imageArtist);
        holder.binding.textArtist.setText(artist.getName());
        holder.itemView.setOnClickListener(view -> onArtistClickListener.onArtistClick(artist));
    }

    public static class AllArtistViewHolder extends RecyclerView.ViewHolder {

        private final LayoutArtistCardItemBinding binding;

        public AllArtistViewHolder(LayoutArtistCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface onArtistClickListener {

        void onArtistClick(Artist artist);

    }

}