# PrettyListView 
### Not Working for now (Under Modifications) 
 Powerful recycler view  with the ability to  swapToRefresh  and  pagination using 
 [NoPaginate library](https://github.com/NoNews/NoPaginate) with few number of code lines  and updating via diffUtils.   Lets see How to use it :
 
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
	        implementation 'com.github.Tripl3Dev:PrettyListView:1.0.0'
		}
  ```
  
  
  # Usage 
 #### 1. In XML View
```xml 
        <com.tripl3dev.prettyListView.PrettyList
        android:id="@+id/testListView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />
```
To Add SwapToRefresh to your view you have to add 
```xml
        <com.tripl3dev.prettyListView.PrettyList
        app:hasSwapToRefresh="true" />
```
 
 #### 2. In Activity or Fragment
- Get your view
 ```kotlin
        val prettyList =  findViewById<PrettyList>(R.id.testListView)
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
var myListView = prettyList.listView
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

