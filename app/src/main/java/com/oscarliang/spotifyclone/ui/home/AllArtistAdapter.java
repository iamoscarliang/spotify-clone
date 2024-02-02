package com.oscarliang.spotifyclone.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.databinding.LayoutAllArtistItemBinding;
import com.oscarliang.spotifyclone.domain.model.Artist;

import java.util.List;
import java.util.Objects;

public class AllArtistAdapter extends RecyclerView.Adapter<AllArtistAdapter.AllArtistViewHolder> {

    private final onArtistClickListener monArtistClickListener;

    private final AsyncListDiffer<Artist> mDiffer;

    public AllArtistAdapter(onArtistClickListener onArtistClickListener) {
        monArtistClickListener = onArtistClickListener;
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
    public AllArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAllArtistItemBinding binding = LayoutAllArtistItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AllArtistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllArtistViewHolder holder, int position) {
        Artist artist = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView.getContext())
                .load(artist.getImageUrl())
                .placeholder(R.drawable.ic_artist)
                .error(R.drawable.ic_artist)
                .into(holder.mBinding.imageArtist);
        holder.mBinding.textArtist.setText(artist.getName());
        holder.itemView.setOnClickListener(view -> monArtistClickListener.onArtistClick(artist));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Artist> artists) {
        mDiffer.submitList(artists);
    }

    public static class AllArtistViewHolder extends RecyclerView.ViewHolder {

        private final LayoutAllArtistItemBinding mBinding;

        public AllArtistViewHolder(LayoutAllArtistItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface onArtistClickListener {

        void onArtistClick(Artist artist);

    }

}
