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
import com.oscarliang.spotifyclone.core.ui.databinding.LayoutPlaylistItemBinding;

import java.util.Objects;

public class PlaylistAdapter extends ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder> {

    private final OnPlaylistClickListener onPlaylistClickListener;
    private final OnPlaylistMoreClickListener onPlaylistMoreClickListener;

    public PlaylistAdapter(
            OnPlaylistClickListener onPlaylistClickListener,
            OnPlaylistMoreClickListener onPlaylistMoreClickListener
    ) {
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
        this.onPlaylistMoreClickListener = onPlaylistMoreClickListener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutPlaylistItemBinding binding = LayoutPlaylistItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlaylistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PlaylistViewHolder holder,
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
        holder.binding.btnMore.setOnClickListener(view -> onPlaylistMoreClickListener.onMoreClick(playlist));
        holder.itemView.setOnClickListener(view -> onPlaylistClickListener.onPlaylistClick(playlist));
        holder.itemView.setOnLongClickListener(view -> {
            onPlaylistMoreClickListener.onMoreClick(playlist);
            return true;
        });
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private final LayoutPlaylistItemBinding binding;

        public PlaylistViewHolder(LayoutPlaylistItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface OnPlaylistClickListener {

        void onPlaylistClick(Playlist playlist);

    }

    public interface OnPlaylistMoreClickListener {

        void onMoreClick(Playlist playlist);

    }

}