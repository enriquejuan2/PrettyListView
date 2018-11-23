package com.tripl3dev.prettyListView.pagination

import android.support.v7.widget.RecyclerView

interface ListPaginationListener {
    fun onLoadMore(page:Int , totalItemCount:Int , listView :RecyclerView)
}