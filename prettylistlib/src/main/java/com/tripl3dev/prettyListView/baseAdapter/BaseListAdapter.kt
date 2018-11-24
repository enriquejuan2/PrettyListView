package com.tripl3dev.prettyListView.baseAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.os.Handler
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tripl3dev.prettyListView.pagination.EndlessRecyclerViewScrollListener
import com.tripl3dev.prettyListView.pagination.ListPaginationListener
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class BaseListAdapter<T>(private val adapterBuilder: AdapterBuilder<T>) : RecyclerView.Adapter<GenericHolder>() {
    private val LOADING_STATE = -30
    private val ERROR_STATE = -31
    private val NORMAL_STATE = -32

    private var mHolderInterface: MainHolderInterface<T> = adapterBuilder.holder
    private var mContext: Context = adapterBuilder.listView.context
    private var originalList: ArrayList<T?> = ArrayList()
    private fun newList() = mHolderInterface.getList()
    lateinit var listCallBack: ListUtilsCallbacks<T>
    private val isPaginated = false
    private lateinit var paginationListener: ListPaginationListener
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    private var paginationState = NORMAL_STATE


    companion object {
        fun <T> with(listView: RecyclerView): AdapterBuilder<T> {
            return AdapterBuilder(listView)
        }
    }

    init {
        adapterBuilder.listView.adapter = this
        setPaginationLogic()
        if (mHolderInterface.getList() is ObservableArrayList) {
            (mHolderInterface.getList() as ObservableArrayList<T?>).addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
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
        val v: View = if (adapterBuilder.isPaginated() && viewType == LOADING_STATE) {
            LayoutInflater.from(mContext).inflate(adapterBuilder.paginationBuilder!!.loadingLayoutRes, parent, false)
        } else if (adapterBuilder.isPaginated() && viewType == ERROR_STATE) {
            LayoutInflater.from(mContext).inflate(adapterBuilder.paginationBuilder!!.errorLayoutRes, parent, false)
        } else {
            LayoutInflater.from(mContext).inflate(mHolderInterface.getView(viewType), parent, false)
        }
        return GenericHolder(v)
    }

    override fun getItemCount(): Int {
//        return if(paginationState == LOADING_STATE || paginationState == ERROR_STATE){
//            getCurrentGenerictList().size-1
//        }else{
//        }
        return getCurrentGenerictList().size
    }

    override fun getItemViewType(position: Int): Int {
        return if (originalList[position] == null && paginationState == LOADING_STATE) {
            LOADING_STATE
        } else if (originalList[position] == null && paginationState == ERROR_STATE) {
            ERROR_STATE
        } else {
            mHolderInterface.getItemViewType(position)
        }

    }

    override fun onBindViewHolder(holder: GenericHolder, position: Int) {
        when (holder.itemViewType) {
            LOADING_STATE -> {
                paginationListener.onLoading(holder.itemView)
            }

            ERROR_STATE -> {
                paginationListener.onError(holder.itemView)
            }

            else -> {
                mHolderInterface.getViewData(holder, getCurrentGenerictList()[position]!!, position)
            }
        }

    }

    private var itemsCount: Int = 0

    /**
     * the list set in the adabter and also figure out if there is any change in item count of adapter
     */
    fun getCurrentGenerictList(): List<T?> {
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


    /**
     * set Scroll Listener to List if it has pagination
     */

    private fun setPaginationLogic() {
        if (!adapterBuilder.isPaginated()) return
        scrollListener = object : EndlessRecyclerViewScrollListener(adapterBuilder.paginationBuilder!!.visibleThreshold, adapterBuilder.listView.layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (paginationState != ERROR_STATE && paginationState != LOADING_STATE) {
                    originalList.add(null)
                    notifyItemInserted(originalList.size - 1)
                    paginationState = LOADING_STATE

                    paginationListener.onLoadMore(page, totalItemsCount, view)
                }
            }
        }

        adapterBuilder.listView.addOnScrollListener(scrollListener)
    }


    fun setPaginationListener(paginationListener: ListPaginationListener) {
        this.paginationListener = paginationListener
    }

    fun resetPagination() {
        if (!adapterBuilder.isPaginated()) return
        scrollListener.resetState()
    }

    fun paginationLoaded() {
        if (!adapterBuilder.isPaginated()) return
        paginationState = NORMAL_STATE
        if (originalList[originalList.size - 1] == null) {
            originalList.removeAt(originalList.size - 1)
//                notifyDataSetChanged()
        }

    }

    fun paginationError() {
        if (!adapterBuilder.isPaginated()) return
        paginationState = ERROR_STATE
        originalList.add(null)
        notifyItemInserted(originalList.size - 1)
    }

    fun paginationDone() {
        if (!adapterBuilder.isPaginated()) return
        paginationState = NORMAL_STATE
        if (originalList[originalList.size - 1] == null) {
            originalList.removeAt(originalList.size - 1)
        }
        stopPagination()


    }

    private fun stopPagination() {
        if (!adapterBuilder.isPaginated()) return
        scrollListener.stopLoading = true
    }

}
