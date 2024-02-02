package com.oscarliang.spotifyclone.ui.playlistedit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.LayoutMusicEditItemBinding;
import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MusicEditAdapter extends RecyclerView.Adapter<MusicEditAdapter.MusicEditViewHolder> {

    private final AsyncListDiffer<Music> mDiffer;

    public MusicEditAdapter() {
        mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Music>() {
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
    public MusicEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutMusicEditItemBinding binding = LayoutMusicEditItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MusicEditViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicEditViewHolder holder, int position) {
        Music music = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(music.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.mBinding.imageMusic);
        holder.mBinding.textTitle.setText(music.getTitle());
        holder.mBinding.textArtist.setText(music.getArtist());
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public List<Music> getList() {
        return mDiffer.getCurrentList();
    }

    public void submitList(List<Music> musics) {
        mDiffer.submitList(musics);
    }

    public void addToList(Music music, int position) {
        if (position < 0 || position > mDiffer.getCurrentList().size()) {
            return;
        }
        List<Music> musics = new ArrayList<>(mDiffer.getCurrentList());
        musics.add(position, music);
        mDiffer.submitList(musics);
    }

    public Music removeFromList(int position) {
        if (position < 0 || position > mDiffer.getCurrentList().size() - 1) {
            return null;
        }
        List<Music> musics = new ArrayList<>(mDiffer.getCurrentList());
        Music music = musics.remove(position);
        mDiffer.submitList(musics);
        return music;
    }

    public void swapBetweenList(int fromPosition, int toPosition) {
        if (fromPosition < 0 || toPosition > mDiffer.getCurrentList().size() - 1) {
            return;
        }
        List<Music> musics = new ArrayList<>(mDiffer.getCurrentList());
        Collections.swap(musics, fromPosition, toPosition);
        mDiffer.submitList(musics);
    }

    public static class MusicEditViewHolder extends RecyclerView.ViewHolder {

        private final LayoutMusicEditItemBinding mBinding;

        public MusicEditViewHolder(LayoutMusicEditItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

}
