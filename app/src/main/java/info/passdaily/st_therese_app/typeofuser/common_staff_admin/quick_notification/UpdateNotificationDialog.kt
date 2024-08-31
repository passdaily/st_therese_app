package info.passdaily.st_therese_app.typeofuser.common_staff_admin.quick_notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.DialogCreateEnquiryBinding
import info.passdaily.st_therese_app.databinding.DialogCreateNotificationBinding
import info.passdaily.st_therese_app.lib.ProgressBarDialog
import info.passdaily.st_therese_app.lib.upload_progress.FileUploader
import info.passdaily.st_therese_app.model.*
import info.passdaily.st_therese_app.services.*
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayerStaff
import info.passdaily.st_therese_app.services.localDB.LocalDBHelper
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.leave.staff_leave.FileList
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.leave.staff_leave.LeaveStaffListener
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.leave.staff_leave.UpdateStaffLeaveDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.ArrayList
import java.util.HashMap

@Suppress("DEPRECATION")
class UpdateNotificationDialog : DialogFragment,NotificationListener {

    lateinit var notificationTabClicker: NotificationTabClicker

    companion object {
        var TAG = "UpdateNotificationDialog"
    }

    private var _binding: DialogCreateNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var quickNotificationViewModel: QuickNotificationViewModel

    lateinit var notificationList: NotificationStaffModel.Inbox

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

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
    var noDeleteImage = 0

    var jsonArrayList = ArrayList<FileList>()



    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null

    var buttonSubmit : AppCompatButton? =null

    var arrayListItems = ""

    var textViewNoFilesStudent : TextView? =null
    var recyclerViewItems : RecyclerView? =null
    lateinit var studyMaterialAdapter: StudyMaterialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleUpdate)
    }

    constructor(
        notificationTabClicker: NotificationTabClicker,
        notificationList: NotificationStaffModel.Inbox
    ) {
        this.notificationTabClicker = notificationTabClicker
        this.notificationList = notificationList
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        quickNotificationViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickNotificationViewModel::class.java]

        _binding = DialogCreateNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID
        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Update Notification"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        constraintLeave = binding.constraintLeave

        editTextTitle?.setText(notificationList.vIRTUALMAILTITLE)
        editTextDesc?.setText(notificationList.vIRTUALMAILCONTENT)

        textViewNoFilesStudent = binding.textViewNoFilesStudent
        recyclerViewItems  = binding.recyclerViewItems
        recyclerViewItems?.layoutManager = GridLayoutManager(requireActivity(), 4)

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setBackgroundResource(R.drawable.round_orage400)
        buttonSubmit?.setTextAppearance(
            requireActivity(),
            R.style.RoundedCornerButtonOrange400
        )


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


        buttonSubmit?.text = requireActivity().resources.getString(R.string.update)
        buttonSubmit?.setOnClickListener {
            if(quickNotificationViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLeave!!) &&
                quickNotificationViewModel.validateField(editTextDesc!!,"Description field cannot be empty",requireActivity(),constraintLeave!!)){
          //     String reply_url=Global.url+"InboxEdit/InboxSetById";
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

        staffLeaveFunction();
    }


    fun submitFile(details : String){
        //{
        //    "VIRTUAL_MAIL_TITLE":"Dummy",
        //    "VIRTUAL_MAIL_CONTENT": "Dummy Message with files",
        //    "VIRTUAL_MAIL_STATUS": 1,
        //    "VIRTUAL_MAIL_CREATED_BY":5,
        //    "SCHOOL_ID": 1,
        //    "VIRTUAL_MAIL_FILE": "xxx.mp4,vvv.mp3",
        //    "VIRTUAL_MAIL_ID":98
        //}
        var url = "Inbox/InboxMessageSet"

        val jsonObject = JSONObject()
        try {
            jsonObject.put("VIRTUAL_MAIL_ID", notificationList.vIRTUALMAILID)
            jsonObject.put("VIRTUAL_MAIL_TITLE", editTextTitle?.text.toString())
            jsonObject.put("VIRTUAL_MAIL_CONTENT", editTextDesc?.text.toString())
            jsonObject.put("VIRTUAL_MAIL_STATUS", "1")
            jsonObject.put("VIRTUAL_MAIL_CREATED_BY", adminId)
            jsonObject.put("SCHOOL_ID", schoolId)
            jsonObject.put("VIRTUAL_MAIL_FILE", details);
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        quickNotificationViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Log.i(TAG,"response $response")
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    notificationTabClicker.onSuccessMessage("Notification Updated successfully")
                                    cancelFrg()
                                }
                                Utils.resultFun(response) == "ERROR" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Notification Creation Failed", constraintLeave!!)
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Notification Already Exist", constraintLeave!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLeave!!)
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }




    fun staffLeaveFunction(){
        quickNotificationViewModel.getNotificationUpdate(notificationList.vIRTUALMAILID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var filesList= response.fileDetails as ArrayList<NotificationUpdateModel.FileDetail>
                            if (filesList.isNotEmpty()) {
                                noDeleteImage = 2
                                textViewNoFilesStudent?.visibility = View.GONE
                                recyclerViewItems?.visibility = View.VISIBLE

                                for (i in filesList.indices) {
                                    jsonArrayList.add(
                                        FileList(
                                            filesList[i].fILEID,
                                            filesList[i].fILENAME,
                                            "",
                                            "Json",
                                            noDeleteImage,
                                            filesList[i].fILENAME
                                        )
                                    )
                                }

                                fileNameList.addAll(jsonArrayList)
                                maxCountSelection = maxCount - fileNameList.size
//                                if(fileNameList.size == 10){
//                                    Utils.getSnackBar4K(requireActivity(),"You reached max limit 10",constraintLeave!!)
//                                    binding.constraintLayoutUpload.isEnabled = false
//                                }else{
//                                    binding.constraintLayoutUpload.isEnabled = true
//
//                                }
                                studyMaterialAdapter = StudyMaterialAdapter(
                                    this,
                                    this,
                                    fileNameList,
                                    requireActivity(),
                                    UpdateStaffLeaveDialog.TAG
                                )
                                recyclerViewItems?.adapter = studyMaterialAdapter

                            } else {
                                binding.constraintLayoutUpload.isEnabled = true
                                textViewNoFilesStudent?.visibility = View.VISIBLE
                                recyclerViewItems?.visibility = View.GONE
                            }

                            Log.i(UpdateStaffLeaveDialog.TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(UpdateStaffLeaveDialog.TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(UpdateStaffLeaveDialog.TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }

    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
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
            noDeleteImage = 1
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(UpdateStaffLeaveDialog.TAG, "data $data")

                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    val countPath = count + fileNameList.size
                    if (countPath > 10) {
                        Toast.makeText(requireActivity(), "You select more then $maxCount", Toast.LENGTH_SHORT)
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
                                    noDeleteImage,""
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
                            noDeleteImage,""
                        )
                    )
                }
                maxCountSelection = maxCount - fileNameList.size
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
                    UpdateStaffLeaveDialog.TAG
                )
                recyclerViewItems?.adapter = studyMaterialAdapter

            }

        }


    class StudyMaterialAdapter(
        var leaveStaffListener: NotificationListener,
        var updateStaffLeaveDialog: UpdateNotificationDialog,
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
                        .load(Global.event_url + "/Upload/VMailFile/" + path)
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
                        // .load(java.io.File(path))
                        .load(Global.event_url + "/Upload/VMailFile/" + path)
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



    override fun onDeleteClick(position: Int, fileList: FileList) {
        if (fileList.fILETYPE == "Json") {
            fileNameList.removeAt(position)
            //      jsonArrayList.removeAt(position)
            //Assignment/FileDelete?AssignmentFileId=
            deleteStudyFile("/Inbox/InboxFileDrop",fileList.fILEID)
        } else {
            fileNameList.removeAt(position)
        }
        maxCountSelection = maxCount - fileNameList.size
        studyMaterialAdapter.notifyDataSetChanged()
//        textViewSelect?.visibility = View.VISIBLE
//        constraintLayoutUpload?.visibility = View.VISIBLE
        if (fileNameList.size == 0) {
            textViewNoFilesStudent?.visibility = View.VISIBLE
            recyclerViewItems?.visibility = View.GONE

        }
    }


    fun deleteStudyFile(url: String,fILEID : Int) {
        // http://localhost:17842/ElixirApi/Inbox/InboxFileDrop?FileId=5&AdminId=10
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["FileId"] = fILEID
        paramsMap["AdminId"] = adminId
        //FileName
        quickNotificationViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "DELETED" -> {
                                    Log.i(UpdateStaffLeaveDialog.TAG,"Deleted Successfully")
                                    //     Utils.getSnackBarGreen(this, "Deleted Successfully", constraintLayout!!)
                                }
                                Utils.resultFun(response) == "DELETION FAILED" -> {
                                    Log.i(UpdateStaffLeaveDialog.TAG," File Deletion Failed")
                                    //     Utils.getSnackBarGreen(this, "Deleted Successfully", constraintLayout!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Log.i(UpdateStaffLeaveDialog.TAG,"Please try again after sometime")
                        }
                        Status.LOADING -> {
                            Log.i(UpdateStaffLeaveDialog.TAG,"loading")
                        }
                    }
                }
            })
    }

    override fun onFileUploadProgress(
        position: Int,
        fILEPATHName: String,
        textViewTitle: TextView,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
        var SERVER_URL = "Inbox/UploadFiles"

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(UpdateStaffLeaveDialog.TAG,"selectedFilePath $fILEPATHName");
        filesToUpload[0] = File(fILEPATHName)
        Log.i(UpdateStaffLeaveDialog.TAG,"filesToUpload $filesToUpload");

        showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "",object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                hideProgress(perProgressBar,textViewProgress)
                Log.i(UpdateStaffLeaveDialog.TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                hideProgress(perProgressBar,textViewProgress)
                for (i in responses.indices) {
                    Log.i(UpdateStaffLeaveDialog.TAG,"responses ${responses[i]}")
                    //val str = responses[i]
                    textViewProgress.visibility = View.GONE
                    perProgressBar.visibility = View.GONE
                    textViewTitle.visibility = View.VISIBLE
                    //   Log.i(TAG, "RESPONSE $i ${responses[i]}")
                    //submitFile(responses[i],fILETITLE,position)
                    // if ((position + 1) == fileNameList.size) {
                    //  arrayListItems += responses[i] + ","
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
        Log.i(UpdateStaffLeaveDialog.TAG,"showProgress $str")
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

}

interface NotificationListener{
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
