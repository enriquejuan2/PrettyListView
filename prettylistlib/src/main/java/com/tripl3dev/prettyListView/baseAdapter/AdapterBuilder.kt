package com.tripl3dev.prettyListView.baseAdapter

import android.support.v7.widget.RecyclerView

class AdapterBuilder<T>(private val listView: RecyclerView) {
    fun setAdapter(holderInterface: MainHolderInterface<T>): BaseListAdapter<T> {
        return BaseListAdapter<T>(listView.context, holderInterface)
    }
}