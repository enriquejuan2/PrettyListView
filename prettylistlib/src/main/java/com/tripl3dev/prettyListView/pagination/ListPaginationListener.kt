package com.tripl3dev.prettyListView.pagination

import android.support.v7.widget.RecyclerView
import android.view.View

interface ListPaginationListener {
    fun onLoadMore(page: Int, totalItemCount: Int, listView: RecyclerView)
    fun onError(errorView: View){}
    fun onLoading(loadingView:View){}
}