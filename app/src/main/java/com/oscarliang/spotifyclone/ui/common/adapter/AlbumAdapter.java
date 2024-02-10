package com.oscarliang.spotifyclone.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.LayoutAlbumItemBinding;
import com.oscarliang.spotifyclone.domain.model.Album;

import java.util.List;
import java.util.Objects;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private final OnAlbumClickListener mOnAlbumClickListener;
    private final AsyncListDiffer<Album> mDiffer;

    public AlbumAdapter(OnAlbumClickListener onAlbumClickListener) {
        mOnAlbumClickListener = onAlbumClickListener;
        mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Album>() {
            @Override
            public boolean areItemsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAlbumItemBinding binding = LayoutAlbumItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AlbumViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(album.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.mBinding.imageAlbum);
        holder.mBinding.textAlbum.setText(album.getTitle());
        holder.mBinding.textArtist.setText(album.getArtist());
        holder.mBinding.textYear.setText(holder.itemView.getContext().getString(R.string.album_year, album.getYear()));
        holder.itemView.setOnClickListener(view -> mOnAlbumClickListener.onAlbumClick(album));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Album> albums) {
        mDiffer.submitList(albums);
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        private final LayoutAlbumItemBinding mBinding;

        public AlbumViewHolder(LayoutAlbumItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface OnAlbumClickListener {

        void onAlbumClick(Album album);

    }

}
