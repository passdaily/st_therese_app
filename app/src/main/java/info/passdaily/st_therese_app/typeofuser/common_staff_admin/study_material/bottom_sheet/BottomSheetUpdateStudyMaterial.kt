package info.passdaily.st_therese_app.typeofuser.common_staff_admin.study_material.bottom_sheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.*
import info.passdaily.st_therese_app.model.*
import info.passdaily.st_therese_app.services.Status
import info.passdaily.st_therese_app.services.Utils
import info.passdaily.st_therese_app.services.ViewModelFactory
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayerStaff
import info.passdaily.st_therese_app.services.localDB.LocalDBHelper
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.study_material.MaterialListener
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.study_material.StudyMaterialStaffViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

@Suppress("DEPRECATION")
class BottomSheetUpdateStudyMaterial : BottomSheetDialogFragment {

    private var _binding: BottomSheetCreateStudyMaterialBinding? = null
    private val binding get() = _binding!!


    lateinit var materialListener: MaterialListener


    private lateinit var studyMaterialStaffViewModel: StudyMaterialStaffViewModel
    lateinit var materialList: MaterialListStaffModel.Material

    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var adminId = 0
    var schoolId =0
    var typeStr =""

    var toolbar : Toolbar? = null
    var constraintLayoutContent : ConstraintLayout? = null

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var spinnerSubject: AppCompatSpinner? = null

    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null



    var buttonSubmit : AppCompatButton? =null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()
    private lateinit var localDBHelper : LocalDBHelper

    var type = arrayOf("Unpublished", "Published")

    constructor()

    constructor(materialListener: MaterialListener,getYears:
    ArrayList<GetYearClassExamModel.Year>,getClass :
    ArrayList<GetYearClassExamModel.Class>,getSubject : ArrayList<SubjectsModel.Subject>,
                materialList: MaterialListStaffModel.Material){
        this.materialListener = materialListener
        this.getYears = getYears
        this.getClass = getClass
        this.getSubject = getSubject
        this.materialList = materialList

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
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
        schoolId = user[0].SCHOOL_ID

        studyMaterialStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StudyMaterialStaffViewModel::class.java]

        _binding = BottomSheetCreateStudyMaterialBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerSubject = binding.spinnerSubject
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc

        binding.textViewTitle.text = "Update Study Material"

        editTextTitle?.setText(materialList.sTUDYMETERIALTITLE)
        editTextDesc?.setText(materialList.sTUDYMETERIALDESCRIPTION)

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

        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                cLASSID = getClass[position].cLASSID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                sUBJECTID = getSubject[position].sUBJECTID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        var aCCADEMIC_ID = 0
        var years = Array(getYears.size){""}
        for (i in getYears.indices) {
            years[i] = getYears[i].aCCADEMICTIME
            if (materialList.aCCADEMICID == getYears[i].aCCADEMICID) {
                aCCADEMIC_ID = i
            }
        }
        if (spinnerAcademic != null) {
            val adapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                years
            )
            spinnerAcademic?.adapter = adapter
        }
        spinnerAcademic?.setSelection(aCCADEMIC_ID)

        var cLASS_ID = 0
        var classX = Array(getClass.size){""}
        for (i in getClass.indices) {
            classX[i] = getClass[i].cLASSNAME
            if (materialList.cLASSID == getClass[i].cLASSID) {
                cLASS_ID = i
            }
        }
        if (spinnerClass != null) {
            val adapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                classX
            )
            spinnerClass?.adapter = adapter
        }
        spinnerClass?.setSelection(cLASS_ID)

        var sUBJECT_ID = 0
        var subject = Array(getSubject.size){""}
        if(subject.isNotEmpty()){
            for (i in getSubject.indices) {
                subject[i] = getSubject[i].sUBJECTNAME
                if (materialList.sUBJECTID == getSubject[i].sUBJECTID) {
                    sUBJECT_ID = i
                }
            }

        }
        if (spinnerSubject != null) {
            val adapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                subject
            )
            spinnerSubject?.adapter = adapter
        }
        spinnerSubject?.setSelection(sUBJECT_ID)

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setBackgroundResource(R.drawable.round_orage400)
        buttonSubmit?.setTextAppearance(
            requireActivity(),
            R.style.RoundedCornerButtonOrange400
        )
        buttonSubmit?.text = requireActivity().resources.getString(R.string.update_studymaterial)
        buttonSubmit?.setOnClickListener {
            if (materialListener.OnValidation(editTextTitle!!, "Title field cannot be empty")
                && materialListener.OnValidation(editTextDesc!!, "Description field cannot be empty")
            ) {

                val url = "StudyMaterial/StudyMaterialUpdate"
                val jsonObject = JSONObject()
                try {
                    //postParam.put("ACCADEMIC_ID",s_aid);
                    //        postParam.put("CLASS_ID",scid);
                    //        postParam.put("SUBJECT_ID",ssid);
                    //        postParam.put("STUDY_METERIAL_TITLE",input_exam_title.getText().toString());
                    //        postParam.put("STUDY_METERIAL_DESCRIPTION",input_exam_description.getText().toString());
                    //        postParam.put("ADMIN_ID",Global.Admin_id);
                    jsonObject.put("SCHOOL_ID", schoolId)
                    jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
                    jsonObject.put("CLASS_ID", cLASSID)
                    jsonObject.put("SUBJECT_ID", sUBJECTID)
                    jsonObject.put("STUDY_METERIAL_TITLE", editTextTitle?.text.toString())
                    jsonObject.put("STUDY_METERIAL_DESCRIPTION", editTextDesc?.text.toString())
                    jsonObject.put("ADMIN_ID", adminId)
                    jsonObject.put("STUDY_METERIAL_ID", materialList.sTUDYMETERIALID)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG, "jsonObject $jsonObject")
                val accademicRe =
                    jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                studyMaterialStaffViewModel.getCommonPostFun(url, accademicRe)
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val response = resource.data?.body()!!
                                    when {
                                        Utils.resultFun(response) != "0" -> {
                                            materialListener.onCreateClick("Study Material Created Successfully",2)
                                        }
                                        Utils.resultFun(response) == "1" -> {
                                            materialListener.onFailedClick("Study Material Creation Failed")
//                                            Utils.getSnackBar4K(
//                                                requireActivity(),
//                                                "Study Material Creation Failed",
//                                                constraintLayoutContent!!
//                                            )
                                        }
                                    }
                                }
                                Status.ERROR -> {
                                    materialListener.onFailedClick("Please try again after sometime")
//                                    Utils.getSnackBar4K(
//                                        requireActivity(),
//                                        "Please try again after sometime",
//                                        constraintLayoutContent!!
//                                    )
                                }
                                Status.LOADING -> {
                                    Log.i(TAG, "loading")
                                }
                            }
                        }
                    })

            }

        }
    }



    companion object {
        var TAG = "BottomSheetFragment"
    }
}