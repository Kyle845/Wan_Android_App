package com.example.lib_framework.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerViewAdapter<T,B : ViewBinding> : RecyclerView.Adapter<BaseViewHolder>() {

    private var data: MutableList<T> = mutableListOf()

    private lateinit var mHeaderLayout: LinearLayout
    private lateinit var mFooterLayout: LinearLayout

    companion object {
        const val HEADER_VIEW = 0x10000111
        const val FOOTER_VIEW = 0x10000222
    }

    var onItemClickListener: ((view: View, position: Int) -> Unit)? = null

    var onItemLongClickListener: ((view: View, position: Int) -> Boolean) = {view, position -> false}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val baseViewHolder: BaseViewHolder
        when(viewType) {
            HEADER_VIEW -> {
                val headerParent: ViewParent? = mHeaderLayout.parent
                if (headerParent is ViewGroup) {
                    headerParent.removeView(mHeaderLayout)
                }
                baseViewHolder = BaseViewHolder(mHeaderLayout)
            }
            FOOTER_VIEW -> {
                val headParent: ViewParent? = mFooterLayout.parent
                if (headParent is ViewGroup) {
                    headParent.removeView(mFooterLayout)
                }
                baseViewHolder = BaseViewHolder(mFooterLayout)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                baseViewHolder = onCreateDefViewHolder(layoutInflater, parent, viewType)
                bindViewClickListener(baseViewHolder)
            }
        }
        return baseViewHolder
    }
    protected open fun bindViewClickListener(holder: BaseViewHolder) {
        onItemClickListener?.let {
            holder.itemView.setOnClickListener { v ->
                var position = holder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                position -= headerLayoutCount
                it.invoke(holder.itemView, position)
            }
        }
        onItemLongClickListener?.let {
            holder.itemView.setOnLongClickListener { v ->
                var position = holder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnLongClickListener false
                }
                position -= headerLayoutCount
                it.invoke(holder.itemView, position)
            }
        }
    }

    /**
     * 子类实现创建自定义ViewHolder，父类提供了LayoutInflater
     */
    protected open fun onCreateDefViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return BaseBindViewHolder(getViewBinding(layoutInflater, parent, viewType))
    }

    abstract fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): B

}