package com.oscarliang.spotifyclone.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.LayoutArtistItemBinding;
import com.oscarliang.spotifyclone.domain.model.Artist;

import java.util.List;
import java.util.Objects;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final OnArtistClickListener mOnArtistClickListener;
    private final AsyncListDiffer<Artist> mDiffer;

    public ArtistAdapter(OnArtistClickListener onArtistClickListener) {
        mOnArtistClickListener = onArtistClickListener;
        mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Artist>() {
            @Override
            public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutArtistItemBinding binding = LayoutArtistItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ArtistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(artist.getImageUrl())
                .placeholder(R.drawable.ic_artist)
                .error(R.drawable.ic_artist)
                .into(holder.mBinding.imageArtist);
        holder.mBinding.textArtist.setText(artist.getName());
        holder.itemView.setOnClickListener(view -> mOnArtistClickListener.onArtistClick(artist));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Artist> artists) {
        mDiffer.submitList(artists);
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {

        private final LayoutArtistItemBinding mBinding;

        public ArtistViewHolder(LayoutArtistItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface OnArtistClickListener {

        void onArtistClick(Artist artist);

    }

}
