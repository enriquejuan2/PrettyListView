package com.tripl3dev.luffylist


import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.tripl3dev.luffylist.databinding.TestListItemBinding
import com.tripl3dev.prettyListView.baseAdapter.*
import com.tripl3dev.prettyListView.pagination.ListPaginationListener
import com.tripl3dev.prettyListView.pagination.WrapContentLinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var mAdapter: BaseListAdapter<TestModel>
    var testListData: ArrayList<TestModel?> = ArrayList()

    var list: ObservableArrayList<TestModel> = ObservableArrayList()

    var count = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testListView.layoutManager = WrapContentLinearLayoutManager(this)
        mAdapter = BaseListAdapter.with<TestModel>(testListView).setAdapter(object : MainHolderInterface<TestModel> {
            override fun getList(): ArrayList<TestModel?> {
                return testListData
            }

            override fun getView(type: Int): Int {
                return R.layout.test_list_item
            }

            override fun getViewData(holder: RecyclerView.ViewHolder, t: TestModel, position: Int) {
                val itemBinding = DataBindingUtil.bind<TestListItemBinding>(holder.itemView)
                itemBinding!!.listItemText.text = t.text
            }
        }).hasPaginated()
                .donePagination()
                .adapterDone()

        mAdapter.setPaginationListener(object : ListPaginationListener {
            override fun onLoadMore(page: Int, totalItemCount: Int, listView: RecyclerView) {
                Log.e("PageCount","page Count is $page")
                Handler().postDelayed({
                    val tempList = ArrayList<TestModel>()
                    for (i in 1..20) {
                        tempList.add(TestModel(i * count, "Item Number $i page $count  "))
                    }
                    count++
                    testListData.addAll(tempList)

                        mAdapter.paginationLoaded()

                    mAdapter.updateList()

                },1000)
             }
        })



        mAdapter.setListCallBacks(object:ListUtilsCallbacks<TestModel>{
            override fun onDataCountChanged(itemCount: Int) {
                Toast.makeText(this@MainActivity,"Items Count become =  $itemCount",Toast.LENGTH_SHORT).show()
            }
        })
        fun getNewId() = testListData[testListData.size - 1]!!.id + 1
        addMore.setOnClickListener {
          val tempList = ArrayList<TestModel>()
            for (i in 1..20) {
                tempList.add(TestModel(i * count, "Item Number $i page $count  "))
            }
            count++
            testListData.addAll(tempList)
            mAdapter.updateList()
        }

        clearBut.setOnClickListener {
            testListData.clear()
            mAdapter.updateList()
        }
        addToIndex.setOnClickListener {
            val newItemText = inputToIndex.text.toString()
            val i = index.text.toString().toInt()
            testListData.add(i, TestModel(getNewId(), newItemText))
            mAdapter.updateList()
        }

        addFirstBut.setOnClickListener {
            val newItemText = addFirstOrLast.text.toString()
            testListData.add(0, TestModel(getNewId(), newItemText))
            mAdapter.updateList()
        }

        addLastBut.setOnClickListener {
            val newItemText = addFirstOrLast.text.toString()
            testListData.add(testListData.size, TestModel(getNewId(), newItemText))
            mAdapter.updateList()
        }

        changeIndex.setOnClickListener {
            val newItemText = inputToIndex.text.toString()
            val i = index.text.toString().toInt()
            testListData[i] = TestModel(testListData[i]!!.id, newItemText)
            mAdapter.updateList()
        }
    }

}

