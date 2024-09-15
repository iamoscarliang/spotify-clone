package com.oscarliang.spotifyclone.core.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oscarliang.spotifyclone.core.ui.R;
import com.oscarliang.spotifyclone.core.model.Category;
import com.oscarliang.spotifyclone.core.ui.databinding.LayoutCategoryItemBinding;

import java.util.Objects;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private final OnCategoryClickListener onCategoryClickListener;

    public CategoryAdapter(OnCategoryClickListener onCategoryClickListener) {
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return Objects.equals(oldItem.getName(), newItem.getName());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutCategoryItemBinding binding = LayoutCategoryItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CategoryViewHolder holder,
            int position
    ) {
        Category category = getItem(position);
        Glide.with(holder.itemView.getContext())
                .load(category.getImageUrl())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_error)
                .into(holder.binding.imageCategory);
        String color = category.getColor();
        if (color != null && !color.isEmpty()) {
            holder.binding.imageCategoryBg.setBackgroundColor(Color.parseColor(category.getColor()));
        }
        holder.binding.textCategory.setText(category.getName());
        holder.itemView.setOnClickListener(view -> onCategoryClickListener.onCategoryClick(category));
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final LayoutCategoryItemBinding binding;

        public CategoryViewHolder(LayoutCategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public interface OnCategoryClickListener {

        void onCategoryClick(Category category);

    }

}