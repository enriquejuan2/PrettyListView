package com.tripl3dev.luffylist


import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.tripl3dev.luffylist.databinding.TestListItemBinding
import com.tripl3dev.luffyyview.baseAdapter.BaseListAdapter
import com.tripl3dev.luffyyview.baseAdapter.ListUtilsCallbacks
import com.tripl3dev.luffyyview.baseAdapter.MainHolderInterface
import com.tripl3dev.luffyyview.paginationUtils.PaginationErrorItem
import com.tripl3dev.luffyyview.paginationUtils.PaginationLoadingItem
import com.tripl3dev.luffyyview.prettyCustomView.PrettyList
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainHolderInterface<TestModel> {
    var list: ArrayList<TestModel> = ArrayList()
    lateinit var mAdapter: BaseListAdapter<TestModel>

    override fun getView(type: Int): Int {
        return R.layout.test_list_item
    }



    override fun getListCopy(): ArrayList<TestModel> {
        return list
    }

    override fun getViewData(holder: RecyclerView.ViewHolder, t: TestModel, position: Int) {
        val itemBindig = DataBindingUtil.bind<TestListItemBinding>(holder.itemView)
        itemBindig?.listItemText?.text = t.string

        itemBindig!!.root.setOnLongClickListener {
            list.removeAt(position)
            mAdapter.updateList()
            return@setOnLongClickListener true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpList()
    }

    fun setUpList() {
        //intialize your adapter it takes two params
        mAdapter = BaseListAdapter(this, this)

        // You can get RecyclerView instance by : yourLuffyView.listView
        testListView.listView.layoutManager = LinearLayoutManager(this)
        testListView.listView.setHasFixedSize(true)
        testListView.listView.adapter = mAdapter


        // setOnLoadMore to your list
        testListView.setOnLoadMoreListener(object : PrettyList.OnLoadMoreListener {
            override fun onLoadMore(currentPage: Int) {
                testListView.showLoading(true)
                Handler().postDelayed({
                    addDummyData(currentPage)
                },500)

            }
        })

        // set custom error view for Pagination you can just set the view or set the view and call back returns your view to make any action on it .
        testListView.setPaginationErrorItem(PaginationErrorItem(R.layout.pagination_error_custom_layout, object : PaginationErrorItem.PaginateErrorListener {
            override fun getErrorView(errorView: View) {
                val button = errorView.findViewById<Button>(R.id.errorBut)
                button.setOnClickListener {
                    Toast.makeText(this@MainActivity, "Retrying .... ", Toast.LENGTH_SHORT).show()
                }
            }
        }))

        // set custom loading view for Pagination you can just set the view or set the view and call back returns your view to make any action on it .
        testListView.setPaginationLoadingItem(PaginationLoadingItem(R.layout.pagination_loading_custom_layout))



        // You must commit pagination to get it working in your list
        testListView.commitPagination()




        //onRefreshListener callback for Swap to refresh
        testListView.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            Handler().postDelayed({
                Handler().postDelayed({
                clearList()
                }, 100)
                Toast.makeText(this@MainActivity, "Retrying .... ", Toast.LENGTH_SHORT).show()

                // You can use luffyListView.stopRefreshing to stop loading indicator of swap to refresh
                testListView.stopRefreshing()

            }, 300)
        })

        // Adapter list callback to get on Data item count changed
        mAdapter.setListCallBacks(object : ListUtilsCallbacks<TestModel> {
            override fun onDataCountChanged(itemCount: Int) {
                Toast.makeText(this@MainActivity, "Count  equals  : $itemCount", Toast.LENGTH_SHORT).show()
            }
        })


        filterField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // you can temporary change your list By providing the adapter with the new temporary list without changing original list
                mAdapter.updateList(ArrayList(list.filter {
                    it.string.contains(s.toString())
                }))
            }
        })
    }

    var i = 0
    var x  = 0
    fun addDummyData(currentPage :Int) {

        if (currentPage == 8) {
            testListView.showError(true)
        }else {
            for (x in i..i + 10) {
                this.x =x
                list.add(TestModel(x, "Element No $x"))
            }
            i += 10
            //update data according to the list provided with onListCopy()
            mAdapter.updateList()
            testListView.showLoading(false)
        }

    }


    fun addMoreData(view:View) {
        addDummyData(1)
    }

    fun clearData(view:View) {
        clearList()
    }
    fun clearList(){
        list.clear()
        i = 0
        // You can reset your pagination data and set current page to the default value by resetPagination() method
        testListView.resetPagination()
        mAdapter.updateList()
    }


    fun addToIndex(view: View){
        when {
            index.text.isNullOrEmpty() -> Toast.makeText(this@MainActivity,"Enter Index where you want to add the item",Toast.LENGTH_SHORT).show()
            inputToIndex.text.isNullOrEmpty() -> Toast.makeText(this@MainActivity,"Enter the item text",Toast.LENGTH_SHORT).show()
            else -> {
                x++
                list.add(index.text.toString().toInt(),TestModel(x,inputToIndex.text.toString()))
                mAdapter.updateList()
                index.text.clear()
                inputToIndex.text.clear()
            }
        }
    }
    fun addLast(view: View){
     addLastOrFirst(true)
    }

    fun addFirst(view: View){
        addLastOrFirst(false)
    }

    fun addLastOrFirst(islast:Boolean){
        if(addFirstOrLast.text.isNullOrEmpty()){
            Toast.makeText(this@MainActivity,"Enter the item text",Toast.LENGTH_SHORT).show()
        }else{
            x++
            if (islast) {
                list.add(list.size, TestModel(x,addFirstOrLast.text.toString()))
            }else{
                list.add(0, TestModel(x,addFirstOrLast.text.toString()))
            }
            addFirstOrLast.text.clear()
            mAdapter.updateList()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // You should call that on destroy to avoid memoryLeak
        testListView.onDestroyPrettyList()
    }


}

