# PrettyListView 

[![](https://jitpack.io/v/Rohyme/PrettyListView.svg)]((https://jitpack.io/#Rohyme/PrettyListView))

 **B**ecause Life is too short for duplicating Recyclerview code, PrettylistView introduce a very simple generic adapter, pagination handling with customized views (Error,loading) and updating via diffUtils.  

Enough talking let's see how to use it .

## Installation

- #### Step 1. Add the JitPack repository to your build file 

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

- #### Step 2. Add the dependency

```groovy
 	dependencies {
	        implementation 'com.github.Rohyme:PrettyListView:2.0.0'
		}
```

###  Usage :

1. Get your view

```kotlin
 val prettyList =  findViewById<RecyclerView>(R.id.testListView)
```

1. Build your adapter using  

```kotlin
 val myAdapterBuilder = BaseListAdapter.with<TestModel>(prettyList)
```

1. Make instance of class implementing `MainHolderInterface<T>` to set your adapter specification in .

   ```kotlin
   val holderInterfaceInstance = object : MainHolderInterface<TestModel> {
         
          // Mandatory
         // return the instance of your list that will be used in your list view
       override fun getList(): ArrayList<TestModel?> {
            return testListData
       }
         
         // Mandatory
         // return the layout of your list view item
       override fun getView(type: Int): Int {
          return R.layout.test_list_item
       }
   
         // Mandatory
         // Here you can use your model item data for your list item view 
       override fun getViewData(holder: RecyclerView.ViewHolder, t: TestModel,             position: Int) {
           
        val listItemView = holder.itemView
        val listItemText = listItemView.findViewById<RecyclerView>(R.id.testListView)
        listItemText.text = t.text
      }
   
         // Optional 
         // For itemViewTypes 
       override fun getItemViewType(position: Int): Int {
                   return super.getItemViewType(position)
      }
   
   }
   
   ```

   > Note : Don't use (-30 , -31) as Item view types constants Because they are already taken for Loading & Error items of pagination



1. Then set your `holderInterface` instance to your adapter builder

   ```kotlin
   myAdapterBuilder.setAdapter(holderInterfaceInstance)
   ```


1. For using pagination

   ```kotlin
   val myPaginationBuilder = myAdapterBuilder.hasPaginated() //return PaginationBuilder instance
   ```

1. Configure your pagination 

   ```kotlin
   myPaginationBuilder.setErrorLayout(R.layout.error_layout)
                      .setLoadingLayout(R.layout.loading_layout)
   				   .setVisibleThreshold(5)
   ```

2. Get your final adapter 

   ```kotlin
   mAdapter = myAdapterBuilder.adapterDone()
   ```

3. Set list and pagination calls & callbacks : 

   -  `setListCallBacks` Used to notify if items count in recycler view changed :

     ```kotlin
     mAdapter.setListCallBacks(object : ListUtilsCallbacks<TestModel> {
         override fun onDataCountChanged(itemCount: Int) {
             Log.i("MyListView","list item count = $itemCount")
         }
     })
     ```

   - `setPaginationListener` Used for pagination callbacks :

     ```kotlin
     val paginationListener = object : ListPaginationListener {
         override fun onLoadMore(page: Int, totalItemCount: Int, listView: RecyclerView) {
            getlist(page)
         }
     
       //Optional
         override fun onError(errorView: View) {
             errorView.setOnClickListener {
                 retryGettingList()
             }
         }
     
         //Optional 
         //get the view of your loading view to make any action on it
         override fun onLoading(loadingView: View) {
             val progressText = view.findViewById<TextView>(R.id.progressText)
         	val progressText.text = "Loading new Item"
         }
     }
     ```

   - Pagination Calls 

     - `mAdapter.paginatedDataAdded()`  used when new paginated data added.

     -  `mAdapter.paginationDone()` used when there is no more items for pagination.

     - `mAdapter.paginationError()` used to show pagination error item.

     - `mAdapter.paginationNormalState()` used to hide loading or error pagination  item.

     - `mAdapter.paginationLoading()` used to show pagination loading item.

4. You have 3 ways to update your list 

   - You can make change to  `testListData` returned in the `mainHolderInstance` then call `mAdatper.updateList()`
     e.g :

     ```kotlin
     testListData.add(TestModel("new test"))
     mAdapter.update()
     ```

   - You can use Observable list instead of your list instance and any change happened in it will be updated automatically .
     e.g :

     ```kotlin
     // global field
     var testListData: ObservableArrayList<TestModel> = ObservableArrayList()
     
     // in your holderInstance 
     override fun getList(): ArrayList<TestModel?> {
                     return testListData
                 }
     
     // updating
     testListData.add(TestModel("new test in observable list"))
     ```

   - You can use update list with just passing a list from the same type in order not to change your original data in `testListData ` . It could be useful for filtering or searching.
     e.g:

     ```kotlin
      searchField.addTextChangedListener(object : TextWatcher {
                 override fun afterTextChanged(s: Editable?) {
                 }
                 override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                 }
     
                 override fun onTextChanged(s: CharSequence?, start: Int, before:Int, count: Int) {
                     mAdapter.updateList(ArrayList(testListData.filter {
                         it?.text?.contains(s.toString()) ?: false
                     }))
                 }
             })
     ```
      ### Credits :

      [**CodePathGuide**](https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView) 
     
      [**Mahmoud Abd Elaal**](https://github.com/MahmoudAbdelaalMahmoud)

     ### License 

     ##### ![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)


