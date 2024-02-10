package com.oscarliang.spotifyclone.ui.search;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.oscarliang.spotifyclone.databinding.LayoutCategoryItemBinding;
import com.oscarliang.spotifyclone.domain.model.Category;

import java.util.List;
import java.util.Objects;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final OnCategoryClickListener mOnCategoryClickListener;
    private final AsyncListDiffer<Category> mDiffer;

    public CategoryAdapter(OnCategoryClickListener onCategoryClickListener) {
        mOnCategoryClickListener = onCategoryClickListener;
        mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return Objects.equals(oldItem.getName(), newItem.getName());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutCategoryItemBinding binding = LayoutCategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mDiffer.getCurrentList().get(position);
        if (category.getColor() != null) {
            holder.mBinding.imageCategory.setBackgroundColor(Color.parseColor(category.getColor()));
        }
        holder.mBinding.textCategory.setText(category.getName());
        holder.itemView.setOnClickListener(view -> mOnCategoryClickListener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Category> categories) {
        mDiffer.submitList(categories);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final LayoutCategoryItemBinding mBinding;

        public CategoryViewHolder(LayoutCategoryItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface OnCategoryClickListener {

        void onCategoryClick(Category category);

    }

}
