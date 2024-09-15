package com.oscarliang.spotifyclone.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.core.model.Album;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.databinding.LayoutAlbumItemBinding;

import java.util.Objects;

public class AlbumAdapter extends ListAdapter<Album, AlbumAdapter.AlbumViewHolder> {

    private final OnAlbumClickListener onAlbumClickListener;

    public AlbumAdapter(OnAlbumClickListener onAlbumClickListener) {
        super(new DiffUtil.ItemCallback<Album>() {
            @Override
            public boolean areItemsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
        this.onAlbumClickListener = onAlbumClickListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutAlbumItemBinding binding = LayoutAlbumItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AlbumViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull AlbumViewHolder holder,
            int position
    ) {
        Album album = getItem(position);
        Glide.with(holder.itemView.getContext())
                .load(album.getImageUrl())
                .placeholder(R.drawable.ic_album)
                .error(R.drawable.ic_error)
                .into(holder.binding.imageAlbum);
        holder.binding.textAlbum.setText(album.getTitle());
        holder.binding.textArtist.setText(album.getArtist());
        holder.binding.textYear.setText(album.getYear());
        holder.itemView.setOnClickListener(view -> onAlbumClickListener.onAlbumClick(album));
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        private final LayoutAlbumItemBinding binding;

        public AlbumViewHolder(LayoutAlbumItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface OnAlbumClickListener {

        void onAlbumClick(Album album);

    }

}