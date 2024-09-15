package com.oscarliang.spotifyclone.feature.player;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.feature.player.databinding.LayoutMiniPlayerItemBinding;

import java.util.Objects;

public class MiniPlayerAdapter extends ListAdapter<Music, MiniPlayerAdapter.MiniPlayerViewHolder> {

    private final OnMiniPlayerClickListener onMiniPlayerClickListener;

    public MiniPlayerAdapter(OnMiniPlayerClickListener onMiniPlayerClickListener) {
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
        this.onMiniPlayerClickListener = onMiniPlayerClickListener;
    }

    @NonNull
    @Override
    public MiniPlayerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutMiniPlayerItemBinding binding = LayoutMiniPlayerItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MiniPlayerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MiniPlayerViewHolder holder,
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
        holder.itemView.setOnClickListener(view -> onMiniPlayerClickListener.onMiniPlayerClick());
    }

    public static class MiniPlayerViewHolder extends RecyclerView.ViewHolder {

        private final LayoutMiniPlayerItemBinding binding;

        public MiniPlayerViewHolder(LayoutMiniPlayerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface OnMiniPlayerClickListener {

        void onMiniPlayerClick();

    }

}