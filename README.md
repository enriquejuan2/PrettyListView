# Luffy View
 Powerful recycler view  with the ability to  swapToRefresh  and  pagination using 
 [NoPaginate library](https://github.com/NoNews/NoPaginate) with few number of code lines  .   Lets see How to use it :
 
# Installation
- #### Step 1. Add the JitPack repository to your build file 
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 ```
 
- #### Step 2. Add the dependency
 ```
 	dependencies {
	        implementation 'com.github.Tripl3Dev:LuffyListView:1.0.0'
	        }
  ```
  
  
  # Usage 
 #### 1. In XML View
```xml 
        <com.tripl3dev.luffyyview.prettyCustomView.PrettyList
        android:id="@+id/testListView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />
```
To Add SwapToRefresh to your view you have to add 
```xml
        <com.tripl3dev.luffyyview.prettyCustomView.PrettyList
        app:hasSwapToRefresh="true" />
```
 
 #### 2. In Activity or Fragment
- Get your view
 ```kotlin
        val luffyView =  findViewById<PrettyList>(R.id.testListView)
```
- Intialize adapter instance 
```kotlin
    mAdapter = BaseListAdapter(object :MainHolderInterface<TestModel>{
            //Mandatory
            override fun getView(type: Int): Int {
            return  R.layout.your_list_item_layout
            }
            //Mandatory
            override fun getViewData(holder: RecyclerView.ViewHolder
            , currentItem: TestModel, position: Int) {
                //yourItemView = holder.itemView
            }
            override fun getListCopy(): java.util.ArrayList<TestModel>? {
                return YourList
            }
            override fun getItemViewType(position: Int): Int {
                return YourItemTypes
            }
        },yourContext)
```
 - set up List
 1. you can get your list view then set its layout and other configuration u need
```kotlin
var myListView = luffyView.listView
myListView.layoutManager = LinearLayoutManager(this)
myListView.listView.setHasFixedSize(true)
myListView.listView.adapter = mAdapter
```
2. Adding data to your list and notifyList
You have three ways to notify list modification
-- First : if you implement fun getListCopy(): java.util.ArrayList<TestModel>? 
        then all what you need to do is to modify the list instance you return then 
        ```
            mAdapter.updateList()
        ```
-- Second : if you don't then u have to call
        ```
        mAdapter.updateList(yourNewListAfterModification)
        ```
-- Third : You can use ```mAdapter.notify..()``` Methods


 - Set pagination config and callback to your list
1. setOnLoadMoreListener
```kotlin 
luffyView.setOnLoadMoreListener(object : PrettyList.OnLoadMoreListener {
            override fun onLoadMore(currentPage: Int) {
            // Load More Data Here
            }
        })
```
2. set customErrorView to be displayed on pagination error
```kotlin
  luffyView.setPaginationErrorItem(PaginationErrorItem(R.layout.your_pagination_error_layout, object : PaginationErrorItem.PaginateErrorListener {
            override fun getErrorView(errorView: View) {
                // Get you your error view to make any action on it
                val button = errorView.findViewById<Button>(R.id.errorBut)
                button.setOnClickListener{retry()}
                }
            }
        }))
```
3. setCustomLoadingView to be displayed on loading
```kotlin
  luffyView.setPaginationLoadingItem(PaginationLoadingItem(R.layout.paginate_loading_custom_layout, object : PaginationLoadingItem.PaginateLoading {
            override fun getLoadingView(loadingView: View) {
                // Get you your loading view to make any action on it
                }
        }))
```
4. (very Important) Finally you have to commit pagination ,it wont work without commit ```luffyView.commitPagination()```
5. There are some methods for pagination to load, StopLoading, showError, hideError, setNoMoreItems  and setFirstPage index(Should use before setting pagination default = 1 )
```kotlin
luffyView.showError(show: Boolean)
luffyView.showLoading(show: Boolean) 
luffyView.setNoMoreItems(noMoreItems: Boolean)
setFirstPage(firstPage: Int)
```
6. You can reset pagination and set currentPage to the default ```luffyView.resetPagination()```
7. You should call onDestroyPrettyList()  in the onDestroy of activity or fragment to avoid memory leak
```kotlin
  override fun onDestroy() {
        super.onDestroy()
        luffyView.onDestroyPrettyList()
    }
```


# Credits
1. [NoPaginate](https://github.com/NoNews/NoPaginate)
