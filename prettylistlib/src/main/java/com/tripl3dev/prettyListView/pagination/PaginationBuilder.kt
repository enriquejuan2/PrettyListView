package com.tripl3dev.prettyListView.pagination

import android.support.annotation.IntegerRes
import android.support.annotation.LayoutRes
import com.tripl3dev.prettyListView.R
import com.tripl3dev.prettyListView.baseAdapter.AdapterBuilder

class PaginationBuilder<T>internal constructor(val adapterBuilder: AdapterBuilder<T>) {
    internal var isPaginated = false
    internal var visibleThreshold = 5

    @LayoutRes
    internal var loadingLayoutRes : Int = R.layout.default_pagination_loading

    @LayoutRes
    internal var errorLayoutRes : Int = R.layout.default_pagination_error

    fun setVisibleThreshold(visibleThreshold: Int) :PaginationBuilder<T>{
        this.visibleThreshold = visibleThreshold
        return this
    }


    fun setErrorLayout(@LayoutRes errorLayoutRes : Int):PaginationBuilder<T>{
        this.errorLayoutRes  = errorLayoutRes
        return this
    }

    fun setLoadingLayout(@LayoutRes loadingLayoutRes: Int):PaginationBuilder<T>{
        this.loadingLayoutRes = loadingLayoutRes
        return this
    }


     fun donePagination() :AdapterBuilder<T>{
        adapterBuilder.paginationBuilder = this
        return adapterBuilder
    }

}