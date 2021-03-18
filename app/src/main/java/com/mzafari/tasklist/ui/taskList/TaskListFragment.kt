package com.mzafari.tasklist.ui.taskList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mzafari.tasklist.MainActivity
import com.mzafari.tasklist.MainApplication
import com.mzafari.tasklist.R
import com.mzafari.tasklist.model.Page
import com.mzafari.tasklist.model.Task
import com.mzafari.tasklist.model.TaskStatus
import com.mzafari.tasklist.ui.SharedViewModel
import com.mzafari.tasklist.ui.taskForm.TaskFormFragment

/**
 * Main fragment for diplay all,done,onprogress task list
 */
class TaskListFragment: Fragment(),TaskListListener {


    //init model with custom factory
    private val model: SharedViewModel by activityViewModels()

    //views
    private var type:String = TaskStatus.all
    private var noTaskLayout:ConstraintLayout? = null
    private var recyclerView:RecyclerView? = null

    //list adapter
    private var adapter: TaskAdapter? = null


    /**
     * check for passed arguments
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let{
            type = it.getString(typeField)?:TaskStatus.all
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_task_list,container,false)
        noTaskLayout = root.findViewById(R.id.noTaskLayout)
        recyclerView = root.findViewById(R.id.recyclerView)

        adapter = TaskAdapter(requireContext(),this)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())


        when(type){
            TaskStatus.done -> {
                model.doneTasks.observe(viewLifecycleOwner){
                    setList(it)
                }
            }
            TaskStatus.onProgress -> {
                model.onProgressTasks.observe(viewLifecycleOwner){
                    setList(it)
                }
            }
            else -> {
                model.allTasks.observe(viewLifecycleOwner){
                    setList(it)
                }
            }
        }



        return root
    }

    /**
     * handle edit icon click in task list
     */
    override fun onTaskEditClicked(view:View, task: Task) {
        (requireActivity() as MainActivity).showFragment(Page(TaskFormFragment.tagEdit,null,task))
    }

    /**
     * handle delete icon click in task list
     */
    override fun onTaskDeleteClicked(task: Task) {
        //display dialog to confirm delete action
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(resources.getString(R.string.delete))
            .setMessage(resources.getString(R.string.areSureToDelete))

            .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                model.deleteTask(task)
                dialog.dismiss()

            }
            .show()
    }

    /**
     * set list data from data comes from model livedata
     */
    private fun setList(list:List<Task>){
        Log.e(tag,"setList ${list.size}")
        for(t in list){
            Log.e(tag, "setList: ${t.name} ${t.status}" )
        }

        if(list.size==0){
            noTaskLayout?.visibility = View.VISIBLE
        }else{
            noTaskLayout?.visibility = View.GONE
        }
        adapter?.submitList(list)


    }

    override fun onDestroyView() {
        super.onDestroyView()

        //null all views to prevent memory leak
        recyclerView = null
        noTaskLayout = null
    }


    companion object{
        //different tag for distinguish different list types
        val tag = TaskListFragment::class.java.simpleName
        val tagDone = TaskListFragment::class.java.simpleName+"Done"
        val tagOnProgress = TaskListFragment::class.java.simpleName+"OnProgress"

        //specify argument fields
        const val typeField = "type"

        @JvmStatic
        fun newInstance(type:String) =
            TaskListFragment().apply {
                arguments = Bundle().apply {
                    putString(typeField, type)
                }
            }

    }
}