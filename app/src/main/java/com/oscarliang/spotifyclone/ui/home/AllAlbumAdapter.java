package com.oscarliang.spotifyclone.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.LayoutAllAlbumItemBinding;
import com.oscarliang.spotifyclone.domain.model.Album;

import java.util.List;
import java.util.Objects;

public class AllAlbumAdapter extends RecyclerView.Adapter<AllAlbumAdapter.AllAlbumViewHolder> {

    private final onAlbumClickListener mOnAlbumClickListener;

    private final AsyncListDiffer<Album> mDiffer;

    public AllAlbumAdapter(onAlbumClickListener onAlbumClickListener) {
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
    public AllAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAllAlbumItemBinding binding = LayoutAllAlbumItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AllAlbumViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllAlbumViewHolder holder, int position) {
        Album album = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(album.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.mBinding.imageAlbum);
        holder.mBinding.textAlbum.setText(album.getTitle());
        holder.mBinding.textArtist.setText(album.getArtist());
        holder.itemView.setOnClickListener(view -> mOnAlbumClickListener.onAlbumClick(album));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Album> albums) {
        mDiffer.submitList(albums);
    }

    public static class AllAlbumViewHolder extends RecyclerView.ViewHolder {

        private final LayoutAllAlbumItemBinding mBinding;

        public AllAlbumViewHolder(LayoutAllAlbumItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface onAlbumClickListener {

        void onAlbumClick(Album album);

    }

}
