package com.oscarliang.spotifyclone.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.LayoutMusicItemBinding;
import com.oscarliang.spotifyclone.domain.model.Music;

import java.util.List;
import java.util.Objects;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private final OnMusicClickListener mOnMusicClickListener;
    private final OnMusicMoreClickListener mOnMusicMoreClickListener;
    private final AsyncListDiffer<Music> mDiffer;

    public MusicAdapter(OnMusicClickListener onMusicClickListener,
                        OnMusicMoreClickListener onMusicMoreClickListener) {
        mOnMusicClickListener = onMusicClickListener;
        mOnMusicMoreClickListener = onMusicMoreClickListener;
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
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutMusicItemBinding binding = LayoutMusicItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MusicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(music.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.mBinding.imageMusic);
        holder.mBinding.textTitle.setText(music.getTitle());
        holder.mBinding.textArtist.setText(music.getArtist());
        holder.mBinding.btnMore.setOnClickListener(view -> mOnMusicMoreClickListener.onMoreClick(music));
        holder.itemView.setOnClickListener(view -> mOnMusicClickListener.onMusicClick(music));
        holder.itemView.setOnLongClickListener(view -> {
            mOnMusicMoreClickListener.onMoreClick(music);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Music> musics) {
        mDiffer.submitList(musics);
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {

        private final LayoutMusicItemBinding mBinding;

        public MusicViewHolder(LayoutMusicItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface OnMusicClickListener {

        void onMusicClick(Music music);

    }

    public interface OnMusicMoreClickListener {

        void onMoreClick(Music music);

    }

}
