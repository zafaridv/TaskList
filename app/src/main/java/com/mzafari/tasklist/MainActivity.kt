package com.mzafari.tasklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mzafari.tasklist.model.Page
import com.mzafari.tasklist.model.Task
import com.mzafari.tasklist.model.TaskStatus
import com.mzafari.tasklist.ui.SharedViewModel
import com.mzafari.tasklist.ui.taskForm.TaskFormFragment
import com.mzafari.tasklist.ui.taskList.TaskListFragment
import kotlinx.coroutines.*
import java.lang.Runnable

/**
 * Main and only activity of the app
 */
class MainActivity : AppCompatActivity() {
    private val model: SharedViewModel by viewModels{
        SharedViewModel.SharedViewModelFactory((application as MainApplication).repository)
    }
    private var bottomNav:BottomNavigationView?=null
    private var addButton: FloatingActionButton?=null
    private var topBar: ConstraintLayout?=null

    //handle navbottomview and prevent from re-call showfragment method in onseletitem
    private var isBackPressed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init views
        topBar = findViewById(R.id.topBar)
        addButton = findViewById(R.id.fab)
        addButton?.setOnClickListener{
            openNewTaskForm()
        }

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav?.setOnNavigationItemSelectedListener {
            handleBottomNavClick(it)
        }
        bottomNav?.setOnNavigationItemReselectedListener {
            handleBottomNavClick(it)
        }


        //observe page to watch page changes and display proper page(fragment)
        model.page.observe(this){
            if(it==null){
                replaceFragment(Page(TaskListFragment.tag))
            }else {
                showFragment(it)
            }
        }

    }

    fun handleBottomNavClick(menuItem: MenuItem):Boolean{
        //check if this called after backpress
        if(isBackPressed) {
            isBackPressed = false
            return true
        }else{
            when (menuItem.itemId) {
                R.id.all -> showFragment(Page(TaskListFragment.tag))
                R.id.done -> showFragment(Page(TaskListFragment.tagDone, TaskListFragment.tag))
                else -> showFragment(
                    Page(
                        TaskListFragment.tagOnProgress,
                        TaskListFragment.tag
                    )
                )
            }
        }
        return true
    }


    /**
     * Customize onbackpress to handle fragments in the stack
     */
    var onExit = false
    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0){
            if(!onExit) {
                Toast.makeText(this, getString(R.string.pressBackExit), Toast.LENGTH_SHORT).show()
                onExit = true
                CoroutineScope(Dispatchers.IO).launch{
                    delay(2000)
                    onExit = false
                }
            }else{
                finish();
            }
        }else{
            //var lastTag = model.stack.value!!.last().tag
            var lastTag = supportFragmentManager.fragments.last().tag
            supportFragmentManager.commit {

                var lastFragment = supportFragmentManager.findFragmentByTag(lastTag)
                if(lastFragment!=null){
                    remove(lastFragment)
                }
            }
            supportFragmentManager.popBackStackImmediate()

            lastTag = supportFragmentManager.fragments.last().tag
            supportFragmentManager.commit {
                val lastFragment = supportFragmentManager.findFragmentByTag(lastTag)
                if(lastFragment!=null){
                    show(lastFragment)
                }
                when(lastTag){
                    TaskFormFragment.tag,
                    TaskFormFragment.tagEdit -> {
                        hideTopBarBottomBar()
                    }
                    else -> showTopBarBottomBar()
                }

                when(lastTag){
                    TaskListFragment.tag -> {
                        isBackPressed = true
                        bottomNav?.selectedItemId = R.id.all
                    }
                    TaskListFragment.tagDone -> {
                        isBackPressed = true
                        bottomNav?.selectedItemId = R.id.done
                    }
                    TaskListFragment.tagOnProgress -> {
                        isBackPressed = true
                        bottomNav?.selectedItemId = R.id.onprogress
                    }
                    else -> {}
                }
            }
        }
    }

    /**
     * instantiate the fragment with the data, based on tag
     * @return Fragment: corresponding fragment and TaskListFragment as default
     */
    fun getFragmentByTag(tag: String, data: Any?):Fragment{
        return when(tag){

            TaskListFragment.tagDone -> TaskListFragment.newInstance(TaskStatus.done)
            TaskListFragment.tagOnProgress -> TaskListFragment.newInstance(TaskStatus.onProgress)
            TaskFormFragment.tag -> TaskFormFragment.newInstance()
            TaskFormFragment.tagEdit -> TaskFormFragment.newInstance(data as Task)
            else -> TaskListFragment.newInstance(TaskStatus.all)
        }
    }

    /**
     * handle showing fragment as the main navigation method
     * Adroid navigation has limitation in add/show framgent so we use the customized one
     * this will handle add and show operation based on provided fragment tag and backstack entry data
     */
    fun showFragment(page: Page){
        Log.e(tag,"show:${page.tag}")
        val ft = supportFragmentManager.beginTransaction()
        when(page.tag){
            TaskFormFragment.tag -> {
                //ft.addSharedElement(addButton!!, "newTaskFormTransition")
                hideTopBarBottomBar()
            }
            TaskFormFragment.tagEdit -> {
                hideTopBarBottomBar()
            }
            else -> showTopBarBottomBar()
        }
        var fragment = supportFragmentManager.findFragmentByTag(page.tag)
        if(fragment == null){
            fragment = getFragmentByTag(page.tag, page.data)
            ft.add(
                R.id.myNavHostFragment,
                fragment,
                page.tag
            )
            ft.addToBackStack(null)

        }else{
            //check for home page list
            if(page.tag.equals(TaskListFragment.tag)){
                //remove all in stack
                if(supportFragmentManager.backStackEntryCount >0) {
                    for (i in supportFragmentManager.backStackEntryCount downTo 1){
                        supportFragmentManager.popBackStackImmediate()
                    }
                }
            }

            //show the new one
            ft.show(fragment)
        }
        ft.commit()
    }

    /**
     * clear the backstack entry
     * and replace frame layout with a fragment that specified by tag in Page object
     */
    fun replaceFragment(page: Page){
        //remove all in stack
        if(supportFragmentManager.backStackEntryCount >0) {
            for (i in supportFragmentManager.backStackEntryCount downTo 1){
                supportFragmentManager.popBackStackImmediate()
            }
        }

        //replace the container
        val fragment = getFragmentByTag(page.tag, page.data)
        supportFragmentManager.commit {
            replace(
                R.id.myNavHostFragment,
                fragment,
                page.tag
            )
        }
    }



    fun openNewTaskForm(){
        showFragment(Page(TaskFormFragment.tag))
    }


    fun hideTopBarBottomBar(){
        topBar?.visibility = View.GONE
        bottomNav?.visibility = View.GONE
    }

    fun showTopBarBottomBar(){
        topBar?.visibility = View.VISIBLE
        bottomNav?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        //prevent memory leak
        bottomNav = null
        addButton = null
        topBar = null
    }


    companion object{
        val tag = MainActivity::class.java.simpleName
    }
}