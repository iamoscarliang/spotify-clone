package com.oscarliang.spotifyclone.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.core.model.Playlist;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.databinding.LayoutPlaylistSelectItemBinding;

import java.util.Objects;

public class PlaylistSelectAdapter extends ListAdapter<Playlist, PlaylistSelectAdapter.PlaylistSelectViewHolder> {

    private final OnPlaylistClickListener onPlaylistClickListener;

    public PlaylistSelectAdapter(OnPlaylistClickListener onPlaylistClickListener) {
        super(new DiffUtil.ItemCallback<Playlist>() {
            @Override
            public boolean areItemsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
        this.onPlaylistClickListener = onPlaylistClickListener;
    }

    @NonNull
    @Override
    public PlaylistSelectViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutPlaylistSelectItemBinding binding = LayoutPlaylistSelectItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlaylistSelectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PlaylistSelectViewHolder holder,
            int position
    ) {
        Playlist playlist = getItem(position);
        Glide.with(holder.itemView.getContext())
                .load(playlist.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.binding.imagePlaylist);
        holder.binding.textPlaylist.setText(playlist.getName());
        int count = playlist.getMusicIds() != null ? playlist.getMusicIds().size() : 0;
        holder.binding.textMusicCount.setText(String.valueOf(count));
        holder.itemView.setOnClickListener(view -> onPlaylistClickListener.onPlaylistClick(playlist));
    }

    public static class PlaylistSelectViewHolder extends RecyclerView.ViewHolder {

        private final LayoutPlaylistSelectItemBinding binding;

        public PlaylistSelectViewHolder(LayoutPlaylistSelectItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface OnPlaylistClickListener {

        void onPlaylistClick(Playlist playlist);

    }

}