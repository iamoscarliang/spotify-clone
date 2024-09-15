package com.oscarliang.spotifyclone.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.ui.databinding.LayoutMusicItemBinding;

import java.util.Objects;

public class MusicAdapter extends ListAdapter<Music, MusicAdapter.MusicViewHolder> {

    private final OnMusicClickListener onMusicClickListener;
    private final OnMusicMoreClickListener onMusicMoreClickListener;

    public MusicAdapter(
            OnMusicClickListener onMusicClickListener,
            OnMusicMoreClickListener onMusicMoreClickListener
    ) {
        super(new DiffUtil.ItemCallback<Music>() {
            @Override
            public boolean areItemsTheSame(@NonNull Music oldItem, @NonNull Music newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Music oldItem, @NonNull Music newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
        this.onMusicClickListener = onMusicClickListener;
        this.onMusicMoreClickListener = onMusicMoreClickListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutMusicItemBinding binding = LayoutMusicItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MusicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MusicViewHolder holder,
            int position
    ) {
        Music music = getItem(position);
        Glide.with(holder.itemView.getContext())
                .load(music.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_error)
                .into(holder.binding.imageMusic);
        holder.binding.textTitle.setText(music.getTitle());
        holder.binding.textArtist.setText(music.getArtist());
        holder.binding.btnMore.setOnClickListener(view -> onMusicMoreClickListener.onMoreClick(music));
        holder.itemView.setOnClickListener(view -> onMusicClickListener.onMusicClick(music));
        holder.itemView.setOnLongClickListener(view -> {
            onMusicMoreClickListener.onMoreClick(music);
            return true;
        });
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {

        private final LayoutMusicItemBinding binding;

        public MusicViewHolder(LayoutMusicItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface OnMusicClickListener {

        void onMusicClick(Music music);

    }

    public interface OnMusicMoreClickListener {

        void onMoreClick(Music music);

    }

}