package com.valenpatel.paisefy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.databinding.SampleCategoryItemBinding
import com.valenpatel.paisefy.model.Category

class CategoryAdapter(val context: Context, val categories: List<Category>, val categoryClickListener: CategoryClickListener) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
        //custom interface to get category on click
        interface CategoryClickListener {
            fun onCategoryClick(category: Category)
        }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SampleCategoryItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_category_item, parent, false))
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryText.setText(category.categoryName)
        holder.binding.categoryIcon.setImageResource(category.categoryImage)
        holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(category.categoryColor))

        holder.itemView.setOnClickListener {
            categoryClickListener.onCategoryClick(category)
        }
    }
}