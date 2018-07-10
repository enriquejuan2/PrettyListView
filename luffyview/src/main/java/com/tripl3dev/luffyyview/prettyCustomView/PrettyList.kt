package com.tripl3dev.luffyyview.prettyCustomView

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.tripl3dev.luffyyview.R
import ru.alexbykov.nopaginate.callback.OnLoadMoreListener
import ru.alexbykov.nopaginate.item.ErrorItem
import ru.alexbykov.nopaginate.item.LoadingItem
import ru.alexbykov.nopaginate.paginate.NoPaginate
import ru.alexbykov.nopaginate.paginate.NoPaginateBuilder
import java.util.logging.Handler
import kotlin.math.log

class PrettyList : FrameLayout {

    constructor(context: Context) : super(context) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setAttributes(attrs)
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setAttributes(attrs)
        initLayout()
    }

    private var hasSwapToRefresh = false
    private lateinit var paginate: NoPaginate
    private var defaultFirstPage = 1
    private var currentPage: Int = defaultFirstPage
    private lateinit var loadMoreListener: OnLoadMoreListener
    private lateinit var refreshListener: SwipeRefreshLayout.OnRefreshListener
    val listView: RecyclerView = RecyclerView(context)
    private lateinit var swipeView: SwipeRefreshLayout

    private lateinit var mLoadingItem: LoadingItem

    private lateinit var mErrorItem: ErrorItem

    private var mPaginationThreshold: Int = -1


    fun setAttributes(attributes: AttributeSet?) {
        val attrs = context.theme.obtainStyledAttributes(attributes, R.styleable.prettyList, 0, 0)
        try {
            hasSwapToRefresh = attrs.getBoolean(R.styleable.prettyList_hasSwapToRefresh, false)
        } finally {
            attrs.recycle()
        }
    }

    fun initLayout() {
        listView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        if (hasSwapToRefresh) {
            swipeView = SwipeRefreshLayout(context)
            swipeView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            swipeView.addView(listView)
            this.addView(swipeView)
            swipeView.setOnRefreshListener {
                if (this::refreshListener.isInitialized) {
                    refreshListener.onRefresh()
                }
            }
        } else {
            this.addView(listView)
        }

    }

    /**
     * Intializing Paginating and setting on Load more Listener
     *
     * @param listener : OnLoadMoreListener called on LoadMore
     *
     */
    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        if (listView.adapter == null) {
            throw Throwable("Please use PrettyList.setOnLoadMoreListener() After setting the adapter to your list")
        }
        this.loadMoreListener = listener
    }


    fun commitPagination() {
        val builder: NoPaginateBuilder = NoPaginate.with(listView).setOnLoadMoreListener {
            Log.e("OnLoadMore", "Current page  is $currentPage")
            loadMoreListener.onLoadMore(currentPage)
            currentPage++
        }
        if (this::mErrorItem.isInitialized)
            builder.setCustomErrorItem(mErrorItem)
        if (this::mLoadingItem.isInitialized)
            builder.setCustomLoadingItem(mLoadingItem)
        if (mPaginationThreshold != -1)
            builder.setLoadingTriggerThreshold(mPaginationThreshold)

        paginate = builder.build()

    }

    /**
     * setPagination error item
     *@param errorItem : setError Item to be used in pagination
     */
    fun setPaginationErrorItem(errorItem: ErrorItem) {
        mErrorItem = errorItem
    }

    /**
     * setPagination loading item
     * @param loadingItem : setLoading item to be used in pagination
     */
    fun setPaginationLoadingItem(loadingItem: LoadingItem) {
        mLoadingItem = loadingItem
    }

    /**
     * @param paginationThreshold : number of items from the end of the list that will trigger loadMore callback
     */
    fun setPaginationThreShold(paginationThreshold: Int) {
        mPaginationThreshold = paginationThreshold
    }


    fun resetPagination() {
        currentPage = defaultFirstPage
        if (this::paginate.isInitialized) {
            paginate.unbind()
        }
        commitPagination()
    }

    /**
     * set first index page
     */
    fun setFirstPage(firstPage: Int) {
        this.defaultFirstPage = firstPage
    }

    val paginatingNotIntializedError = "Pagination isn't intialized , Please intialize it by using PrettyList.setOnLoadMoreListener()"

    /**
     *show Pagination Loading
     *
     */
    fun showLoading(show: Boolean) {
        if (this::paginate.isInitialized) {
            android.os.Handler().post {
                paginate.showLoading(show)
            }
        } else {
            Log.e("PrettyList", paginatingNotIntializedError)
        }
    }

    /**
     *show Pagination Error
     *
     */
    fun showError(show: Boolean) {
        if (this::paginate.isInitialized) {
            android.os.Handler().post {
                paginate.showError(show)
            }
        } else {
            Log.e("PrettyList", paginatingNotIntializedError)
        }
    }

    fun paginationFinished() {
        paginate.setNoMoreItems(true)
    }

    /**
     *set No moreItems for Pagination
     *
     *@param noMore if true will stop pagination
     *
     */
    fun setNoMoreItems(noMore: Boolean) {
        if (this::paginate.isInitialized) {
            paginate.setNoMoreItems(noMore)
        } else {
            Log.e("PrettyList", paginatingNotIntializedError)
        }
    }


    /**
     * used on Destroy of Activity or fragment to unbind pagination
     *
     */
    fun onDestroyPrettyList() {
        if (this::paginate.isInitialized) {
            paginate.unbind()
        }
    }

    /**
     *  setting on Swipe to refresh Listener
     * @param listener : OnRefreshListener called when you swipe to refresh
     */
    fun setOnRefreshListener(listener: SwipeRefreshLayout.OnRefreshListener) {
        this.refreshListener = listener
    }

    /**
     * Stop swipe to refresh loading
     *
     */
    fun stopRefreshing() {
        if (this::swipeView.isInitialized) {
            if (swipeView.isRefreshing) {
                swipeView.isRefreshing = false
            }
        }
    }


    /**
     *get Swipe view
     */
    fun getSwipeView(): SwipeRefreshLayout? {
        if (this::swipeView.isInitialized) {
            return swipeView
        } else {
            throw Throwable("Please Add app:hasSwapToRefresh to add swipeView to pretty List")
        }
    }

    fun getPaginationInstance(): NoPaginate? {

        if (this::paginate.isInitialized) {
            return paginate
        } else {
            throw Throwable("You have to intialize pagination first by using PrettyList.setOnLoadMoreListener() ")
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore(currentPage: Int)
    }
}


