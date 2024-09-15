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
import com.oscarliang.spotifyclone.core.ui.databinding.LayoutMusicEditItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MusicEditAdapter extends ListAdapter<Music, MusicEditAdapter.MusicEditViewHolder> {

    public MusicEditAdapter() {
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
    }

    @NonNull
    @Override
    public MusicEditViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutMusicEditItemBinding binding = LayoutMusicEditItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MusicEditViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MusicEditViewHolder holder,
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
    }

    public void addMusic(Music music, int position) {
        if (position < 0 || position > getCurrentList().size()) {
            return;
        }
        List<Music> musics = new ArrayList<>(getCurrentList());
        musics.add(position, music);
        submitList(musics);
    }

    public Music removeMusic(int position) {
        if (position < 0 || position > getCurrentList().size() - 1) {
            return null;
        }
        List<Music> musics = new ArrayList<>(getCurrentList());
        Music music = musics.remove(position);
        submitList(musics);
        return music;
    }

    public void swapMusics(int fromPosition, int toPosition) {
        if (fromPosition < 0 || toPosition > getCurrentList().size() - 1) {
            return;
        }
        List<Music> musics = new ArrayList<>(getCurrentList());
        Collections.swap(musics, fromPosition, toPosition);
        submitList(musics);
    }

    public static class MusicEditViewHolder extends RecyclerView.ViewHolder {

        private final LayoutMusicEditItemBinding binding;

        public MusicEditViewHolder(LayoutMusicEditItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}