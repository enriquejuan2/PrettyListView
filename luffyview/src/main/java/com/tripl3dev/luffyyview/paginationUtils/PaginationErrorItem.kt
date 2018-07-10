package com.tripl3dev.luffyyview.paginationUtils

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.alexbykov.nopaginate.callback.OnRepeatListener
import ru.alexbykov.nopaginate.item.ErrorItem

class PaginationErrorItem(@LayoutRes val errorLayout: Int) : ErrorItem {

    private var paginatCallBack: PaginateErrorListener? = null

    constructor(@LayoutRes errorLayout: Int, paginatCallBack: PaginateErrorListener) : this(errorLayout) {
        this.paginatCallBack = paginatCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(errorLayout, parent, false)
        return CustomErrorViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, onRepeatListener: OnRepeatListener?) {
        val view = holder as CustomErrorViewHolder
        paginatCallBack?.getErrorView(view.itemView)

    }

    interface PaginateErrorListener {
        fun getErrorView(errorView: View)
    }

    class CustomErrorViewHolder(v: View) : RecyclerView.ViewHolder(v)
}