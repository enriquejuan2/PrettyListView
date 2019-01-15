package com.tripl3dev.luffylist


import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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

        val adapterBuilder = BaseListAdapter.with<TestModel>(testListView).setAdapter(object : MainHolderInterface<TestModel> {
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
        })


        adapterBuilder.hasPaginated()
                .setErrorLayout(R.layout.error_layout)
                .setLoadingLayout(R.layout.loading_layout)
                .paginationDone()

        mAdapter = adapterBuilder.adapterDone()



        mAdapter.setPaginationListener(object : ListPaginationListener {
            override fun onLoadMore(page: Int, totalItemCount: Int, listView: RecyclerView) {
                if (page == 5) {
                    mAdapter.paginationError()
                    return
                }
                Handler().postDelayed({
                    testListData.addAll(getData())
                    count++
                    mAdapter.paginatedDataAdded()
                }, 1000)
            }

            override fun onError(errorView: View) {
                errorView.setOnClickListener {
                    mAdapter.paginationLoading()
                }
            }

            override fun onLoading(loadingView: View) {
                super.onLoading(loadingView)
            }
        })




        mAdapter.setListCallBacks(object : ListUtilsCallbacks<TestModel> {
            override fun onDataCountChanged(itemCount: Int) {
            }
        })


        testListData.addAll(getData())
        mAdapter.updateList()
        count++





        filterField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mAdapter.updateList(ArrayList(testListData.filter {
                    it?.text?.contains(s.toString()) ?: false
                }))
            }
        })


    }

    fun getData(): ArrayList<TestModel> {
        val tempList = ArrayList<TestModel>()
        for (i in 1..50) {
            tempList.add(TestModel(i * count, "Item Number $i page $count  "))
        }
        return tempList
    }


}

