package com.tripl3dev.prettyListView.baseAdapter

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.*
import kotlin.collections.ArrayList


class BaseListAdapter<T>(holderInterface: MainHolderInterface<T>, context: Context) : RecyclerView.Adapter<GenericHolder>() {
    var mHolderInterface: MainHolderInterface<T> = holderInterface
    var mContext: Context = context
    var originalList: ArrayList<T> = ArrayList()
    var newList: ArrayList<T>? = null
        get() = mHolderInterface.getListCopy()
    lateinit var listCallBack: ListUtilsCallbacks<T>


    //set List Callbacks , it has only one method for now to observe if items count changed

    fun setListCallBacks(utilsCallbacks: ListUtilsCallbacks<T>) {
        this.listCallBack = utilsCallbacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericHolder {
        val v: View = LayoutInflater.from(mContext).inflate(mHolderInterface.getView(viewType), parent, false)
        return GenericHolder(v)
    }

    override fun getItemCount(): Int {
        return getCurrentGenerictList().size
    }

    override fun getItemViewType(position: Int): Int {
        return mHolderInterface.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: GenericHolder, position: Int) {
        mHolderInterface.getViewData(holder, getCurrentGenerictList()[position], position)
    }

    private var itemsCount: Int = 0

    /**
     * the list set in the adabter and also figure out if there is any change in item count of adapter
     */
    fun getCurrentGenerictList(): List<T> {
        if (itemsCount != originalList.size) {
            itemsCount = originalList.size
            if (this::listCallBack.isInitialized) {
                listCallBack.onDataCountChanged(itemCount)
            }
        }
        return originalList
    }

    var contentAreTheSame: MyDiffUtil.ContentsAreTheSame<T>? = null

    /**
     * set Item contents check
     * @param contentsTheSame -> Callback provide the new items and old items and return if there is any different between them
     */
    fun setItemsTheSameCheck(contentsTheSame: MyDiffUtil.ContentsAreTheSame<T>) {
        this.contentAreTheSame = contentsTheSame
    }

    /**
     * update the adapter list with a nother list
     * @param newList -> the updated list
     */
    fun updateList(list: ArrayList<T>) {

        val diffResult: DiffUtil.DiffResult = if (contentAreTheSame == null) {
            DiffUtil.calculateDiff(MyDiffUtil(originalList, list))
        } else {
            DiffUtil.calculateDiff(MyDiffUtil(originalList, list, contentAreTheSame!!))
        }
        originalList.clear()
        if (list.isNotEmpty())
            originalList.addAll(list)
        android.os.Handler().post {
            diffResult.dispatchUpdatesTo(this)
        }
    }


    /**
     * update the adapter list with list instance from Holderinterface callback
     */
    fun updateList() {
        if (newList == null) {
            throw Throwable("Please Override getListCopy() and return a non nullable list")
        }
        val diffResult: DiffUtil.DiffResult = if (contentAreTheSame == null) {
            DiffUtil.calculateDiff(MyDiffUtil(originalList, newList!!))
        } else {
            DiffUtil.calculateDiff(MyDiffUtil(originalList, newList!!, contentAreTheSame!!))
        }
        originalList.clear()
        if (newList!!.isNotEmpty())
            originalList.addAll(newList!!)
        android.os.Handler().post {
            diffResult.dispatchUpdatesTo(this)
        }
    }



    fun notifyChanges() {
        if (newList == null) {
            throw Throwable("Please Override getListCopy() and return a non nullable list")
        }
        originalList.clear()
        originalList.addAll(newList!!)
        notifyDataSetChanged()
    }


    fun notifyChanges(list: ArrayList<T>) {
        originalList.clear()
        originalList.addAll(list)
        notifyDataSetChanged()
    }

}
