package com.oscarliang.spotifyclone.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.LayoutMusicBarItemBinding;
import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.List;
import java.util.Objects;

public class MusicBarAdapter extends RecyclerView.Adapter<MusicBarAdapter.MusicBarViewHolder> {

    private final OnMusicBarClickListener mOnMusicBarClickListener;
    private final AsyncListDiffer<Music> mDiffer;

    public MusicBarAdapter(OnMusicBarClickListener onMusicBarClickListener) {
        mOnMusicBarClickListener = onMusicBarClickListener;
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
    public MusicBarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutMusicBarItemBinding binding = LayoutMusicBarItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MusicBarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicBarViewHolder holder, int position) {
        Music music = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(music.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.mBinding.imageMusic);
        holder.mBinding.textTitle.setText(music.getTitle());
        holder.mBinding.textArtist.setText(music.getArtist());
        holder.itemView.setOnClickListener(view -> mOnMusicBarClickListener.onMusicBarClick(music));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Music> musics) {
        mDiffer.submitList(musics);
    }

    public static class MusicBarViewHolder extends RecyclerView.ViewHolder {

        private final LayoutMusicBarItemBinding mBinding;

        public MusicBarViewHolder(LayoutMusicBarItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface OnMusicBarClickListener {

        void onMusicBarClick(Music music);

    }

}
