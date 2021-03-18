package com.mzafari.tasklist.ui.taskForm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.google.android.material.card.MaterialCardView
import com.mzafari.tasklist.MainApplication
import com.mzafari.tasklist.R
import com.mzafari.tasklist.model.Task
import com.mzafari.tasklist.model.TaskStatus
import com.mzafari.tasklist.ui.SharedViewModel
import com.mzafari.tasklist.utils.Utils
import java.util.*


class TaskFormFragment: Fragment() {
    //init model with custom factory
    private val model: SharedViewModel by activityViewModels()

    //this is for edit
    private var task:Task? = null

    private var selectedDeadline:String? = null

    //views
    private var taskCardView:MaterialCardView? = null
    private var closeIcon: ImageButton? = null
    private var nameEditText: EditText? = null
    private var deadlineCalendarView: CalendarView? = null
    private var statusRadioGroup: RadioGroup? = null
    private var onProgressRadio: RadioButton? = null
    private var doneRadio: RadioButton? = null
    private var saveButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //get task for edit
        arguments?.let{
            task = it.getParcelable(taskField)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_task_form,container,false)
        taskCardView = root.findViewById(R.id.new_task_card_view)
        nameEditText = root.findViewById(R.id.name_edit_text)
        deadlineCalendarView = root.findViewById(R.id.deadline_calendar_view)
        deadlineCalendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
            var m = "${month+1}"
            if(month<9){
                m = "0${m}"
            }
            var d = "${dayOfMonth}"
            if(dayOfMonth<10){
                d = "0${d}"
            }
            selectedDeadline = "${year}-${m}-${d}"
        }
        saveButton = root.findViewById(R.id.save_icon)
        saveButton?.setOnClickListener{
            saveTask()
        }
        closeIcon = root.findViewById(R.id.close_icon)
        closeIcon?.setOnClickListener{
            requireActivity().onBackPressed()
        }
        statusRadioGroup = root.findViewById(R.id.status_radio_group)
        onProgressRadio = root.findViewById(R.id.onprogress)
        doneRadio = root.findViewById(R.id.done)


        //fill the form in case of edit
        fillForm()

        postponeEnterTransition();

        return root
    }



    /**
     * Fill the form if task id is passed
     */
    fun fillForm(){
        if(task==null){
            return
        }
        nameEditText?.setText(task!!.name)
        selectedDeadline = task!!.deadline

        //set selected date in calendarview
        val dateArr = selectedDeadline!!.split("-")
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = dateArr[0].toInt()
        calendar[Calendar.MONTH] = dateArr[1].toInt()-1 //because months start from zero
        calendar[Calendar.DAY_OF_MONTH] = dateArr[2].toInt()
        deadlineCalendarView?.setDate(calendar.timeInMillis,true,true)

        //set the radio button
        if(task!!.status.equals(TaskStatus.done)){
            doneRadio?.isChecked = true
        }else{
            onProgressRadio?.isChecked = true
        }

    }

    /**
     * Save Task to database
     * if task is passed to fragment then update the record, otherwise insert new record
     */
    private var isSavingTask = false
    fun saveTask(){
        //validation
        if(!validateForm()){
            return
        }
        //check saving already on progress in case double click on save button
        if(isSavingTask){
            return
        }
        isSavingTask = true
        if(selectedDeadline==null){
            selectedDeadline = Utils.dateToString( Date(deadlineCalendarView?.date!!))
        }
        var selectedStatus = TaskStatus.onProgress
        if(statusRadioGroup?.checkedRadioButtonId == R.id.done){
            selectedStatus = TaskStatus.done
        }

        //check for edit
        if(task == null) {
            task = Task(
                null,
                nameEditText!!.text.toString(),
                selectedDeadline!!,
                selectedStatus
            )
            model.insertTask(task!!)
        }else{
            task!!.name = nameEditText!!.text.toString()
            task!!.deadline = selectedDeadline!!
            task!!.status = selectedStatus
            model.updateTask(task!!)
        }

        isSavingTask = false

        //hide keyboard after saving done
        hideKeyboard()

        //goto where we came from
        requireActivity().onBackPressed()

    }

    /**
     * validate all forms input and set the error in case of error
     */
    fun validateForm():Boolean{
        var result = true
        if(nameEditText?.text?.trim()?.length == 0){
            nameEditText?.error = getString(R.string.nameIsRequired)
            result = false
        }else{
            nameEditText?.error = null
        }
        return result
    }

    /**
     * hide keyboard
     */
    fun hideKeyboard(){
        // Only runs if there is a view that is currently focused
        requireActivity().currentFocus?.let { view ->

            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //set all views to null and prevent known memory leak issue reported by Google
        task = null
        taskCardView = null
        closeIcon = null
        nameEditText = null
        deadlineCalendarView = null
        statusRadioGroup = null
        onProgressRadio = null
        doneRadio = null
        saveButton = null

    }

    companion object{
        val tag = TaskFormFragment::class.java.simpleName
        val tagEdit = TaskFormFragment::class.java.simpleName+"Edit"

        //specify argument fields
        const val taskField = "task"

        @JvmStatic
        fun newInstance(task:Task? = null) =
            TaskFormFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(taskField, task)
                }
            }
    }

}