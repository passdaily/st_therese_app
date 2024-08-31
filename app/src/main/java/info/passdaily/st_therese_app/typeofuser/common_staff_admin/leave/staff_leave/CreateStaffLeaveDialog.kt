package info.passdaily.st_therese_app.typeofuser.common_staff_admin.leave.staff_leave

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.DialogCreateLeaveBinding
import info.passdaily.st_therese_app.databinding.DialogCreateStaffLeaveBinding
import info.passdaily.st_therese_app.lib.ProgressBarDialog
import info.passdaily.st_therese_app.lib.upload_progress.FileUploader
import info.passdaily.st_therese_app.model.CustomImageModel
import info.passdaily.st_therese_app.model.GetYearClassExamModel
import info.passdaily.st_therese_app.model.LeaveStaffListModel
import info.passdaily.st_therese_app.services.*
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayerStaff
import info.passdaily.st_therese_app.services.localDB.LocalDBHelper
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.info_manage_student.student_info.DialogCreateStudentInfo
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.leave.LeaveViewModel
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.manage_assignment.AssignmentCreateListener
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class CreateStaffLeaveDialog : DialogFragment,LeaveStaffListener {


    lateinit var staffDetailsListener: StaffDetailsListener
    companion object {
        var TAG = "CreateStaffLeaveDialog"
    }
    var arrayListItems = ""

    private lateinit var leaveViewModel: LeaveViewModel

    private var _binding: DialogCreateStaffLeaveBinding? = null
    private val binding get() = _binding!!

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var STUDENTID = 0
    var CLASSID = 0
    var aCCADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var fromStr = ""
    var toStr = ""

    var fromDateCompare = ""
    var toDateCompare = ""

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private var readPermission = false
    private var writePermission = false
    var maxCount = 10
    var maxCountSelection = 10

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var adminRole = 0

    var fileNameList = ArrayList<FileList>()
    var dummyFileName = ArrayList<String>()

    var imageSelection = ""

    var toolbar : Toolbar? = null

    var spinnerAcademic: AppCompatSpinner? = null
    var leaveFromDate : TextInputEditText? =null
    var leaveEndDate : TextInputEditText? =null
    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null

    var recyclerViewItems : RecyclerView? =null
    var textViewNoFilesStudent : TextView? =null

    lateinit var studyMaterialAdapter: StudyMaterialAdapter

    var buttonSubmit : AppCompatButton? =null

    var constraintLeave : ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    constructor()
    constructor(
        staffDetailsListener: StaffDetailsListener,
    ) {
        this.staffDetailsListener = staffDetailsListener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID

        // Inflate the layout for this fragment
        leaveViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[LeaveViewModel::class.java]

        _binding = DialogCreateStaffLeaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Create Staff Leave"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        spinnerAcademic = binding.spinnerAcademic
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

        textViewNoFilesStudent = binding.textViewNoFilesStudent
        recyclerViewItems  = binding.recyclerViewItems
        recyclerViewItems?.layoutManager = GridLayoutManager(requireActivity(), 4)


        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

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
         //   mDatePicker3.datePicker.minDate = Date().time
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
          //  mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);


//            val buttonbackground: Button = mDatePicker3.getButton(DialogInterface.BUTTON_NEGATIVE)
//            buttonbackground.setTextColor(Color.BLACK)
//
//            val buttonbackground1: Button = mDatePicker3.getButton(DialogInterface.BUTTON_POSITIVE)
//            buttonbackground1.setTextColor(requireActivity().resources.getColor(R.color.green_400))
        }


        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermission


            }



        binding.constraintLayoutUpload.setOnClickListener {

            if (requestPermission()) {
                mimeTypeFun("*/*", Intent.ACTION_GET_CONTENT)
            }
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {

            if(leaveViewModel.validateField(editTextTitle!!,"Enter Date Of Birth",requireActivity(),constraintLeave!!)
                && leaveViewModel.validateField(editTextDesc!!,"Description field cannot be empty",requireActivity(),constraintLeave!!)
                && leaveViewModel.validateField(leaveFromDate!!,"Give Start Date",requireActivity(),constraintLeave!!)
                && leaveViewModel.validateField(leaveEndDate!!,"Give End Date",requireActivity(),constraintLeave!!)
            ){
                arrayListItems = ""
                for(i in fileNameList.indices){
                    if(fileNameList[i].fILETYPE == "Uploaded"){
                        //  fileNameList[i].fILETYPE = "Json"
                        arrayListItems += fileNameList[i].fILEUPLOADED+","
                    }else{
                        arrayListItems = ""
                    }
                }
                Log.i(UpdateStaffLeaveDialog.TAG,"arrayListItems $arrayListItems")

                if (arrayListItems.isNotEmpty()) {
                   submitFile(Utils.removeLastChar(arrayListItems))
                } else {
                    submitFile("")
                }
            }
        }

        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }

        initFunction()

    }


    private fun initFunction() {
        leaveViewModel.getYearClassExam(adminId,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size) { "" }
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }

                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }


    private fun submitFile(details: String) {
        // http://meridianstaff.passdaily.in/ElixirApi/Staff/StaffLeaveRequest
        //
        //{
        //    "ACCADEMIC_ID": 10,
        //
        //    "ADMIN_ID": 1,
        //
        //    "LEAVE_SUBJECT": "Request for 5 days leave",
        //
        //    "LEAVE_DESCRIPTION": "Due to fever i need five days leave",
        //
        //    "LEAVE_FROM_DATE": "09/28/2023",
        //
        //    "LEAVE_TO_DATE": "09/30/2023",
        //
        //    "FILE_NAME": "aaa.png,gggg.jpeg,hhhhh.pdf",
        //
        //    "SCHOOL_ID": 1

        val url = "Staff/StaffLeaveRequest"
        val jsonObject = JSONObject()
        try {

            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("ADMIN_ID", adminId)

            jsonObject.put("LEAVE_SUBJECT", editTextTitle?.text.toString())
            jsonObject.put("LEAVE_DESCRIPTION", editTextDesc?.text.toString())

            jsonObject.put("LEAVE_FROM_DATE",  Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", leaveFromDate!!.text.toString()))
            jsonObject.put("LEAVE_TO_DATE",  Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", leaveEndDate!!.text.toString()))
            jsonObject.put("FILE_NAME", details);
            jsonObject.put("SCHOOL_ID", schoolId)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(CreateAssignmentDialog.TAG, "jsonObject $jsonObject")
        val accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        leaveViewModel.getCommonPostFun(url, accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
//                                    Utils.getSnackBarGreen(
//                                        requireActivity(),
//                                        "Leave Created Successfully",
//                                        binding.constraintLeave
//                                    )
                                    arrayListItems = ""
                                    cancelFrg()
                                    staffDetailsListener.onCreateClick("Leave Created Successfully")
                                }
                                Utils.resultFun(response) == "EXIST" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Leave Already Exist",
                                        binding.constraintLeave
                                    )
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Leave Creation Failed",
                                        binding.constraintLeave
                                    )
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(
                                requireActivity(),
                                "Please try again after sometime",
                                binding.constraintLeave
                            )
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG, "loading")
                        }
                    }
                }
            })

    }



    fun mimeTypeFun(mimeTypes: String, actionOpenDocument: String) {
        if (fileNameList.size < maxCount) {
            maxCountSelection = maxCount - fileNameList.size
            Toast.makeText(requireActivity(), "Select $maxCountSelection ", Toast.LENGTH_SHORT)
                .show()

            val intent = Intent(actionOpenDocument); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = mimeTypes;
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startForResult.launch(intent)
        } else {
            Utils.getSnackBar4K(
                requireActivity(),
                "Maximum Count Reached",
                constraintLeave
            )
        }
    }



    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(TAG, "data $data")

                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    val countPath = count + fileNameList.size
                    if (countPath > 10) {
                        Toast.makeText(
                            requireActivity(),
                            "You select more then $maxCount",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
//                        fileNameList.addAll(jsonArrayList)
                        for (i in 0 until count) {
                            val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                            dummyFileName.add(imageUri!!.toString())
                            fileNameList.add(
                                FileList(
                                    0,
                                    imageUri.toString(),
                                    "",
                                    "Local",
                                    0,
                                    ""
                                )
                            )
                        }
                    }
                    //     imageAdapter.addSelectedImages(selectedPaths)
                }

                //If single image selected
                else if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    dummyFileName.add(imageUri!!.toString())
                    fileNameList.add(
                        FileList(
                            0,
                            imageUri.toString(),
                            "",
                            "Local",
                            0,
                            ""
                        )
                    )
                }
                if (fileNameList.size == 10) {
                    Utils.getSnackBar4K(requireActivity(),"You reached max limit $maxCount",constraintLeave!!)
                  //  binding.constraintLayoutUpload.isEnabled = false
                } else {
                  //  binding.constraintLayoutUpload.isEnabled = true
                }
                textViewNoFilesStudent?.visibility = View.GONE
                recyclerViewItems?.visibility = View.VISIBLE
                studyMaterialAdapter = StudyMaterialAdapter(
                    this,
                    this,
                    fileNameList,
                    requireActivity(),
                    TAG
                )
                recyclerViewItems?.adapter = studyMaterialAdapter
            }

        }



    class StudyMaterialAdapter(
        var leaveStaffListener: LeaveStaffListener,
        var createStaffLeaveDialog: CreateStaffLeaveDialog,
        var materialList: ArrayList<FileList>,
        var context: Context, var TAG: String
    ) : RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder>() {
        var downLoadPos = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById(R.id.imageView)
            var imageViewOther: ImageView = view.findViewById(R.id.imageViewOther)
            var imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)

            var perProgressBar : CircularProgressIndicator = view.findViewById(R.id.perProgressBar)
            var textViewProgress : TextView = view.findViewById(R.id.textViewProgress)
            var textViewTitle  : TextView = view.findViewById(R.id.textViewTitle)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.assignment_student_attach_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.textViewFileName.text = materialList[position].fILETITLE
            holder.textViewTitle.visibility = View.GONE
            if (materialList[position].fILETITLE.isNotEmpty()) {
                holder.textViewTitle.text = materialList[position].fILETITLE
            } else {
                holder.textViewTitle.text = "Uploaded"
            }

            if (materialList[position].fILETYPE == "Json") {
                holder.textViewTitle.visibility  =  View.GONE
                holder.perProgressBar.visibility  =  View.GONE
                holder.textViewProgress.visibility  =  View.GONE

                val path: String = materialList[position].fILENAME
                Log.i(TAG,"path $path")
                val mFile = File(path)
                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //  .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_word)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") ||
                    mFile.toString().contains(".PDF")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file
                    Glide.with(context)
                        .load(File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //     .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_pdf)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_power_point)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //  .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_excel)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {

                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(Global.event_url + "/AssignmentFile/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_gallery)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageView)
                }
                else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_text)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                }
                else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() // .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_voice)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                }
                else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_video_library)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                }
            }

            else if (materialList[position].fILETYPE == "Local") {
                val path: String = materialList[position].fILENAME
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
                Log.i(TAG, "mFile $mFile")

                materialList[position].fILETYPE = "Uploaded"
                holder.perProgressBar.visibility  =  View.VISIBLE
                holder.textViewProgress.visibility  =  View.VISIBLE//,materialList[position].fILENAME
                leaveStaffListener.onFileUploadProgress(position,mFile!!,holder.textViewTitle, holder.perProgressBar,holder.textViewProgress)

                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_word)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") || mFile.toString().contains(".PDF")) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file

                    Glide.with(context)
                        .load(R.drawable.ic_file_pdf)
                        .into(holder.imageViewOther)

                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_power_point)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(R.drawable.ic_file_excel)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {
                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(materialList[position].fILENAME)
                        .into(holder.imageView)
                } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_text)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_voice)
                        .into(holder.imageViewOther)
                } else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
//                    Glide.with(context)
//                        .load(R.drawable.ic_video_library)
//                        .into(holder.imageViewOther)
                    try {
                        val thumb = ThumbnailUtils.createVideoThumbnail(
                            mFile,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        holder.imageViewOther.setImageBitmap(thumb)
                    } catch (e: java.lang.Exception) {
                        Log.i("TAG", "Exception $e")
                    }
                }
            }

            else if (materialList[position].fILETYPE == "Uploaded") {
                holder.textViewTitle.visibility  =  View.VISIBLE
                holder.textViewTitle.text = "Uploaded"
                holder.perProgressBar.visibility  =  View.GONE
                holder.textViewProgress.visibility  =  View.GONE
                val path: String = materialList[position].fILENAME
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
                Log.i(TAG, "mFile $mFile")

                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_word)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") || mFile.toString().contains(".PDF")) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file

                    Glide.with(context)
                        .load(R.drawable.ic_file_pdf)
                        .into(holder.imageViewOther)

                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_power_point)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(R.drawable.ic_file_excel)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {
                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(materialList[position].fILENAME)
                        .into(holder.imageView)
                } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_text)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_voice)
                        .into(holder.imageViewOther)
                } else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
//                    Glide.with(context)
//                        .load(R.drawable.ic_file_video)
//                        .into(holder.imageViewOther)

                    try {
                        val thumb = ThumbnailUtils.createVideoThumbnail(
                            mFile!!,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        holder.imageViewOther.setImageBitmap(thumb)
                    } catch (e: java.lang.Exception) {
                        Log.i("TAG", "Exception $e")
                    }
                }
            }





            holder.imageViewDelete.setBackgroundResource(R.drawable.ic_file_close_icon)
            holder.imageViewDelete.setOnClickListener {
//                holder.constraintDownload.visibility = View.GONE
//                holder.textViewPercentage.visibility = View.GONE
//                PRDownloader.cancel(downLoadPos)
                leaveStaffListener.onDeleteClick(position, materialList[position])
            }
        }

        override fun getItemCount(): Int {
            return materialList.size
        }

    }





    fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }


    }


    fun requestPermission(): Boolean {

        var hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        }else {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }


        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermission = hasReadPermission
        writePermission = hasWritePermission || minSdk29

        val permissions = readPermission && writePermission


        val permissionToRequests = mutableListOf<String>()
        if (!writePermission) {
            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!readPermission) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            }else {
                permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }


    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

    override fun onDeleteClick(position: Int, fileList: FileList) {
        fileNameList.removeAt(position)
        studyMaterialAdapter.notifyDataSetChanged()
        if (fileNameList.size == 0) {
            textViewNoFilesStudent?.visibility = View.VISIBLE
            recyclerViewItems?.visibility = View.GONE

        }
    }

    override fun onFileUploadProgress(
        position: Int, fILEPATHName: String,
        textViewTitle: TextView,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView){
        var SERVER_URL = "Staff/UploadMedicalDocuments"

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $fILEPATHName");
        filesToUpload[0] = File(fILEPATHName)
        Log.i(TAG,"filesToUpload $filesToUpload");

        showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "",object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                hideProgress(perProgressBar,textViewProgress)
                for (i in responses.indices) {
                    Log.i(TAG,"responses ${responses[i]}")
                    //val str = responses[i]
                    textViewProgress.visibility = View.GONE
                    perProgressBar.visibility = View.GONE
                    textViewTitle.visibility = View.VISIBLE
                    //   Log.i(TAG, "RESPONSE $i ${responses[i]}")
                    //submitFile(responses[i],fILETITLE,position)
                    // if ((position + 1) == fileNameList.size) {
                   // arrayListItems += responses[i] + ","
                    fileNameList[position].fILEUPLOADED = responses[i]
                    //arrayListItems += responses[i]+","
                    // }

                }
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
              //  Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
    }


    fun updateProgress(
        progress: Int,
        title: String?,
        msg: String?,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
  //      Log.i(TAG,"updateProgress $progress")
//        perProgressBar.setTitle(title)
//        perProgressBar.setMessage(msg)
        textViewProgress.text = "$progress %"
        perProgressBar.progress = progress
    }

    fun showProgress(str: String?, perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
        Log.i(TAG,"showProgress $str")
        try {
            //perProgressBar.setCancelable(false)
            // perProgressBar.setTitle("Please wait")
            //perProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            perProgressBar.max = 100 // Progress Dialog Max Value
            // perProgressBar.setMessage(str)
//            if (perProgressBar.isShowing) perProgressBar.dismiss()
//            perProgressBar.show()
        } catch (e: java.lang.Exception) {
        }
    }

    fun hideProgress(perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
        try {
         //   Log.i(TAG,"hideProgress")
            // if (perProgressBar.isShowing) perProgressBar.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onStop() {
        super.onStop()
        Global.albumImageList =  ArrayList<CustomImageModel>()
    }

}



class FileList(val fILEID : Int,var fILENAME: String,var fILETITLE : String, var fILETYPE: String,val fILEDELETE: Int,var fILEUPLOADED: String )

interface LeaveStaffListener{
    fun onDeleteClick(
        position: Int,
        fileList: FileList
    )

    fun onFileUploadProgress(
        position: Int, fILEPATHName: String,
        textViewTitle: TextView,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView)

}
