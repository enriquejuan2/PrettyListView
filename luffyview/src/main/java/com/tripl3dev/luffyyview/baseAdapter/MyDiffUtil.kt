package com.tripl3dev.luffyyview.baseAdapter

import android.support.v7.util.DiffUtil
import java.util.*

class MyDiffUtil<T>(val oldList: ArrayList<T>, val newList: ArrayList<T> ) : DiffUtil.Callback() {
   companion object {
       const  val EQUALS =-1
       const val NOT_EQUALS=-2
       const val NOT_DEFINED =-3
   }

    constructor(oldList: ArrayList<T>, newList: ArrayList<T> , ss : ContentsAreTheSame<T>):this (oldList,newList){
        setItemsTheSame(ss)
    }
    private lateinit var itemsTheSame: ContentsAreTheSame<T>
    fun setItemsTheSame(itemsTheSame: ContentsAreTheSame<T>) {
        this.itemsTheSame = itemsTheSame
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return if (this::itemsTheSame.isInitialized) {
            if (itemsTheSame.areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])!= NOT_DEFINED){
                itemsTheSame.areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition]) == EQUALS
            }else{
                itemsTheSame.areItemsHaveSameContent(oldList[oldItemPosition], newList[newItemPosition])
            }
        }else{
            oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return if (this::itemsTheSame.isInitialized) {
            itemsTheSame.areItemsHaveSameContent(oldList[oldItemPosition], newList[newItemPosition])
        }else{
            true
        }
    }


    interface ContentsAreTheSame<T> {
        fun areItemsHaveSameContent(oldItem: T, newItem: T): Boolean
        fun  areItemsTheSame(oldItem: T, newItem: T):Int{return NOT_DEFINED
        }
    }
}
