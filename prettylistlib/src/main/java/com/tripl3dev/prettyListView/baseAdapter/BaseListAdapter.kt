package com.tripl3dev.prettyListView.baseAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class BaseListAdapter<T>(context: Context, holderInterface: MainHolderInterface<T>) : RecyclerView.Adapter<GenericHolder>() {
    var mHolderInterface: MainHolderInterface<T> = holderInterface
    var mContext: Context = context
    var originalList: ArrayList<T> = ArrayList()
    fun newList() = mHolderInterface.getList()
    lateinit var listCallBack: ListUtilsCallbacks<T>

    companion object {
        fun <D> with(listView: RecyclerView): AdapterBuilder<D> {
            return AdapterBuilder(listView)
        }
    }

    init {
        if (mHolderInterface.getList() is ObservableArrayList) {
            (mHolderInterface.getList() as ObservableArrayList<T>).addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
                override fun onItemRangeRemoved(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                    updateList()
                }

                override fun onItemRangeMoved(sender: ObservableList<T>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    updateList()
                }

                override fun onItemRangeInserted(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                    updateList()
                }

                override fun onItemRangeChanged(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
                    updateList()
                }

                override fun onChanged(sender: ObservableList<T>?) {
                    updateList()
                }

            })
        }
    }


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


    private var contentAreTheSame: MyDiffUtil.ContentsAreTheSame<T>? = null

    /**
     * set Item contents check
     * @param contentsTheSame -> Callback provide the new items and old items and return if there is any different between them
     */
    fun setDiffUtils(contentsTheSame: MyDiffUtil.ContentsAreTheSame<T>) {
        this.contentAreTheSame = contentsTheSame
    }

    /**
     * update the adapter list with list instance from Holderinterface callback
     */
    @SuppressLint("CheckResult")
    fun updateList() {

        Flowable.just(newList())
                .map {
                    if (contentAreTheSame == null) {
                        DiffUtil.calculateDiff(MyDiffUtil(originalList, it))
                    } else {
                        DiffUtil.calculateDiff(MyDiffUtil(originalList, it, contentAreTheSame!!))
                    }
                }.doOnNext {
                    originalList = ArrayList(newList())
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.dispatchUpdatesTo(this)
                }

    }

}
