package com.tripl3dev.prettyListView.paginationUtils

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.alexbykov.nopaginate.item.LoadingItem

class PaginationLoadingItem(@LayoutRes private val loadingLayout: Int) : LoadingItem {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val view = holder as CustomErrorViewHolder
        paginatCallBack?.getLoadingView(view.itemView)
    }

    var paginatCallBack: PaginateLoading? = null

    constructor(@LayoutRes loadingLayout: Int, paginatCallBack: PaginateLoading?) : this(loadingLayout) {
        this.paginatCallBack = paginatCallBack
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(loadingLayout, parent, false)
        return CustomErrorViewHolder(v)
    }


    interface PaginateLoading {
        fun getLoadingView(loadingView: View)
    }

    class CustomErrorViewHolder(v: View) : RecyclerView.ViewHolder(v)
}