package info.passdaily.st_therese_app.typeofuser.parent.leave_enquires

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.DialogCreateLeaveBinding
import info.passdaily.st_therese_app.services.Global
import info.passdaily.st_therese_app.services.Status
import info.passdaily.st_therese_app.services.Utils
import info.passdaily.st_therese_app.services.ViewModelFactory
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayer
import info.passdaily.st_therese_app.services.localDB.parent.StudentDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class CreateLeaveDialog : DialogFragment {

    lateinit var leaveClickListener: LeaveClickListener

    companion object {
        var TAG = "CreateLeaveDialog"
    }

    private var _binding: DialogCreateLeaveBinding? = null
    private val binding get() = _binding!!

    private lateinit var leaveEnquiryViewModel: LeaveEnquiryViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var fromStr = ""
    var toStr = ""

    var fromDateCompare = ""
    var toDateCompare = ""

    var toolbar : Toolbar? = null

    var leaveFromDate : TextInputEditText? =null
    var leaveEndDate : TextInputEditText? =null
    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null

    var buttonSubmit : AppCompatButton? =null

    var constraintLeave : ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    constructor(leaveClickListener: LeaveClickListener) {
        this.leaveClickListener = leaveClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        leaveEnquiryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LeaveEnquiryViewModel::class.java]

        _binding = DialogCreateLeaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Create Leave"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        leaveFromDate = binding.leaveFromDate
        leaveEndDate = binding.leaveEndDate

        leaveFromDate?.inputType = InputType.TYPE_NULL
        leaveFromDate?.keyListener = null
        leaveEndDate?.inputType = InputType.TYPE_NULL
        leaveEndDate?.keyListener = null

        leaveFromDate!!.isClickable = true;
        leaveEndDate!!.isClickable = true;

        constraintLeave  = binding.constraintLeave

        //        et_leave_fromdate.requestFocus();
        leaveFromDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(leaveFromDate?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    fromStr = "$year-$s-$dayOfMonth"
                    if(toStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            leaveFromDate?.setText(Utils.dateformat(fromStr))
                        }else{
                            Toast.makeText(activity, "Give Valid Date", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        leaveFromDate?.setText(Utils.dateformat(fromStr))
                    }


                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("From Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }


        leaveEndDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(leaveEndDate?.windowToken, 0)

            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    toStr = "$year-$s-$dayOfMonth"
                    if(fromStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            leaveEndDate?.setText(Utils.dateformat(toStr))
                        }else{
                            Toast.makeText(activity, "Give Valid Date", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(activity, "Give Start Date", Toast.LENGTH_SHORT).show()
                    }
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("To Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);


//            val buttonbackground: Button = mDatePicker3.getButton(DialogInterface.BUTTON_NEGATIVE)
//            buttonbackground.setTextColor(Color.BLACK)
//
//            val buttonbackground1: Button = mDatePicker3.getButton(DialogInterface.BUTTON_POSITIVE)
//            buttonbackground1.setTextColor(requireActivity().resources.getColor(R.color.green_400))
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {
            if(leaveEnquiryViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLeave!!) &&
                leaveEnquiryViewModel.validateField(editTextDesc!!,"Description field cannot be empty",requireActivity(),constraintLeave!!) &&
                leaveEnquiryViewModel.validateField(leaveFromDate!!,"Give Start Date",requireActivity(),constraintLeave!!) &&
                leaveEnquiryViewModel.validateField(leaveEndDate!!,"Give End Date",requireActivity(),constraintLeave!!)){


                val url = "Leave/LeaveRequest"

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("ACCADEMIC_ID", ACADEMICID)
                    jsonObject.put("CLASS_ID", CLASSID)
                    jsonObject.put("STUDENT_ID", STUDENTID)
                    jsonObject.put("STUDENT_ROLL_NUMBER", STUDENT_ROLL_NO)
                    jsonObject.put("LEAVE_SUBJECT", editTextTitle?.text.toString())
                    jsonObject.put("LEAVE_DESCRIPTION", editTextDesc?.text.toString())
                    jsonObject.put("LEAVE_FROM_DATE", fromStr)
                    jsonObject.put("LEAVE_TO_DATE", toStr)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                leaveEnquiryViewModel.getCommonPostFun(url,accademicRe)
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    cancelFrg()
                                    leaveClickListener.onCreateClick("Leave request submitted")
                                }
                                Status.ERROR -> {
                                    Toast.makeText(
                                        activity,
                                        "Please try again after sometime",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                Status.LOADING -> {
                                    Log.i(TAG,"loading")
                                }
                            }
                        }
                    })

            }

        }

        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }

    }


    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }


    }



}