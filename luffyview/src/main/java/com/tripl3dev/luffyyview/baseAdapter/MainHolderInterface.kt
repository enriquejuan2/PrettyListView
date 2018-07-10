package com.tripl3dev.luffyyview.baseAdapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import java.util.*

interface MainHolderInterface<T> {
    @LayoutRes
    fun getView(type:Int): Int

    fun getListCopy(): ArrayList<T>?{return null}

    fun getViewData(holder: RecyclerView.ViewHolder,t:T,position: Int = 0)

     fun getItemViewType(position: Int): Int{
        return 0
    }

}