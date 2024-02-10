package com.oscarliang.spotifyclone.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.LayoutPlaylistItemBinding;
import com.oscarliang.spotifyclone.domain.model.Playlist;

import java.util.List;
import java.util.Objects;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private final OnPlaylistClickListener mOnPlaylistClickListener;
    private final OnPlaylistMoreClickListener mOnPlaylistMoreClickListener;
    private final AsyncListDiffer<Playlist> mDiffer;

    public PlaylistAdapter(OnPlaylistClickListener onPlaylistClickListener,
                           OnPlaylistMoreClickListener onPlaylistMoreClickListener) {
        mOnPlaylistClickListener = onPlaylistClickListener;
        mOnPlaylistMoreClickListener = onPlaylistMoreClickListener;
        mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Playlist>() {
            @Override
            public boolean areItemsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutPlaylistItemBinding binding = LayoutPlaylistItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new PlaylistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(playlist.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.mBinding.imagePlaylist);
        holder.mBinding.textPlaylist.setText(playlist.getName());
        int musicCount = playlist.getMusicIds() != null ? playlist.getMusicIds().size() : 0;
        holder.mBinding.textMusicCount.setText(holder.itemView.getContext().getString(R.string.playlist_count,
                String.valueOf(musicCount)));
        holder.mBinding.btnMore.setOnClickListener(view -> mOnPlaylistMoreClickListener.onMoreClick(playlist));
        holder.itemView.setOnClickListener(view -> mOnPlaylistClickListener.onPlaylistClick(playlist));
        holder.itemView.setOnLongClickListener(view -> {
            mOnPlaylistMoreClickListener.onMoreClick(playlist);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Playlist> playlists) {
        mDiffer.submitList(playlists);
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private final LayoutPlaylistItemBinding mBinding;

        public PlaylistViewHolder(LayoutPlaylistItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface OnPlaylistClickListener {

        void onPlaylistClick(Playlist playlist);

    }

    public interface OnPlaylistMoreClickListener {

        void onMoreClick(Playlist playlist);

    }

}
