package info.passdaily.st_therese_app.typeofuser.parent.leave_enquires

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.FragmentLibraryBinding
import info.passdaily.st_therese_app.model.*
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

@Suppress("DEPRECATION")
class EnquiryFragment : Fragment(), LeaveClickListener {

    var TAG = "LeaveFragment"

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var recyclerViewLeaveList: RecyclerView? = null

    var constraintLayout : ConstraintLayout? = null

    private lateinit var leaveEnquiryViewModel: LeaveEnquiryViewModel

    var fab: ExtendedFloatingActionButton? = null

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var shimmerViewContainer : ShimmerFrameLayout? =null

    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG, "onAttach ")

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 12

        Global.screenState = "landingpage"
        // Inflate the layout for this fragment
        leaveEnquiryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LeaveEnquiryViewModel::class.java]

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root

//        leaveEnquiryViewModel = ViewModelProvider(this).get(LeaveEnquiryViewModel::class.java)
//        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        constraintLayout = binding.constraintLayout

        var textViewTitle = binding.textView32
        textViewTitle.text = "Enquiry"
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        if (isAdded) {
            Glide.with(mContext!!)
                .load(R.drawable.ic_empty_state_assignment)
                .into(imageViewEmpty!!)
        }
        textEmpty?.text = "No Enquiry"

        fab = binding.fab
        fab?.visibility = View.VISIBLE
        fab?.setOnClickListener {
            val dialog1 = CreateEnquiryDialog(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateEnquiryDialog.TAG)

    }

        //  {
        //        	"ACCADEMIC_ID":"7",
        //             "CLASS_ID":"2",
        //             "STUDENT_ID":"1",
        //             "STUDENT_ROLL_NUMBER":"1",
        //             "QUERY_SUBMITTED_DATE":"",
        //
        //        }



//        leaveEnquiryViewModel.getEnquiryList(jsonObject)



        recyclerViewLeaveList = binding.recyclerView
        recyclerViewLeaveList?.layoutManager = LinearLayoutManager(requireActivity())

        shimmerViewContainer = binding.shimmerViewContainer
        intiFunction()
    }

    //    {
//        "ACCADEMIC_ID":"7",
//        "CLASS_ID":"2",
//        "STUDENT_ID":"1",
//        "STUDENT_ROLL_NUMBER":"1",
//        "LEAVE_SUBMITTED_DATE":"",
//        "TODate":""
//
//    }

    private fun intiFunction() {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("ACCADEMIC_ID", ACADEMICID)
            jsonObject.put("CLASS_ID", CLASSID)
            jsonObject.put("STUDENT_ID", STUDENTID)
            jsonObject.put("STUDENT_ROLL_NUMBER", STUDENT_ROLL_NO)
            jsonObject.put("QUERY_SUBMITTED_DATE", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.i(TAG,"jsonObject ${jsonObject.toString()}")

        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        leaveEnquiryViewModel.getEnquiryList(accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(response.enquiryList.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                recyclerViewLeaveList?.visibility = View.VISIBLE
                                if (isAdded) {
                                    recyclerViewLeaveList?.adapter =
                                        LeaveAdapter(this,response.enquiryList, requireActivity())
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewLeaveList?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text = requireActivity().resources.getString(R.string.no_results)
                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewLeaveList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text = requireActivity().resources.getString(R.string.no_internet)
                        }
                        Status.LOADING -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_empty_state_absent)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
                            shimmerViewContainer?.visibility = View.VISIBLE
                            recyclerViewLeaveList?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                        }
                    }
                }
            })
    }

    class LeaveAdapter(
        var leaveClickListener: LeaveClickListener,
        var leaveDetails: List<EnquiryListModel.Enquiry>,
        var context: Context
    ) : RecyclerView.Adapter<LeaveAdapter.ViewHolder>() {
        var leaveSubmitDate = ""

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textLeaveTitle: TextView = view.findViewById(R.id.textLeaveTitle)
            var textLeaveDesc: TextView = view.findViewById(R.id.textLeaveDesc)

            var textViewDate: TextView = view.findViewById(R.id.textViewDate)

            var cardViewStatus: CardView = view.findViewById(R.id.cardViewStatus)
            val textViewStatus: TextView = view.findViewById(R.id.textViewStatus)

            var imageViewEdit  : ImageView = view.findViewById(R.id.imageViewEdit)

            var textViewReplied: TextView = view.findViewById(R.id.textViewReplied)
            var textView50 : TextView = view.findViewById(R.id.textView50)
            var viewReply : View = view.findViewById(R.id.viewReply)
            var textViewTime  : TextView = view.findViewById(R.id.textViewTime)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.enquiry_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textLeaveTitle.text = leaveDetails[position].qUERYSUBJECT
            holder.textLeaveDesc.text = leaveDetails[position].qUERYDESCRIPTION
            var query = leaveDetails[position].qUERYSTATUS
            when (query) {
                0 -> {
                    holder.textViewStatus.text = "Submitted"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_chemistry))
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_chemistry))
                    holder.textViewReplied.visibility = View.GONE
                    holder.textView50.visibility = View.GONE
                    holder.viewReply.visibility = View.GONE
                    holder.textViewTime.visibility = View.GONE
                }
                1 -> {
                    holder.textViewStatus.text = "Replied"
                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.green_400))
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.green_100))
                    holder.textViewReplied.visibility = View.VISIBLE
                    holder.textView50.visibility = View.VISIBLE
                    holder.viewReply.visibility = View.VISIBLE
                    holder.textViewTime.visibility = View.VISIBLE
                    holder.textViewReplied.text = leaveDetails[position].qUERYREPLY

                    if (!leaveDetails[position].qUERYREPLYEDDATE.isNullOrBlank()) {
                        val date1: Array<String> =
                            leaveDetails[position].qUERYREPLYEDDATE.split("T".toRegex()).toTypedArray()
                        val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                        holder.textViewTime.text = Utils.formattedDateTime(ddddd)
                    }
                }

            }
            if (!leaveDetails[position].qUERYSUBMITTEDDATE.isNullOrBlank()) {
                val date1: Array<String> =
                    leaveDetails[position].qUERYSUBMITTEDDATE.split("T".toRegex()).toTypedArray()
                val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                leaveSubmitDate = Utils.formattedDateTime(ddddd)
            }
            holder.textViewDate.text = leaveSubmitDate

            holder.imageViewEdit.setOnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewEdit)
                popupMenu.menuInflater.inflate(R.menu.menu_video, popupMenu.menu)
                popupMenu.menu.findItem(R.id.menu_edit).isVisible = true
                popupMenu.menu.findItem(R.id.menu_video).title = "Delete"
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            leaveClickListener.onUpdateClick(
                                "",
                                "",
                                leaveDetails[position].qUERYSUBJECT,
                                leaveDetails[position].qUERYDESCRIPTION,
                                leaveDetails[position].qUERYID
                            )
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { dialog, _ ->
                                    leaveClickListener.onDeleteClick(leaveDetails[position].qUERYID)
                                    dialog.dismiss()
                                }
                                .setNegativeButton(
                                    "No"
                                ) { dialog, _ -> //  Action for 'NO' Button
                                    dialog.cancel()
                                }
                            //Creating dialog box
                            val alert = builder.create()
                            //Setting the title manually
                            alert.setTitle("Logout?")
                            alert.show()
                            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                            buttonbackground.setTextColor(Color.BLACK)

                            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                            buttonbackground1.setTextColor(Color.BLACK)
                            true
                        }
                        else -> false
                    }
                }

            }


        }

        override fun getItemCount(): Int {
            return leaveDetails.size
        }

    }

    override fun onCreateClick(message : String) {
        Log.i(TAG,"onFinishClick")
        intiFunction()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayout!!)
    }

    override fun onUpdateClick(fromDate : String,
                               toDate : String,
                               title : String,
                               description : String,
                               leaveId: Int) {
        val dialog1 = UpdateEnquiryDialog(this,fromDate,
            toDate,title,description,leaveId)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateEnquiryDialog.TAG)
    }
    override fun onDeleteClick(leaveId: Int) {

        val url = "Enquiry/EnquiryDelete"
        leaveEnquiryViewModel.getDeleteEnquiryFun(url,leaveId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            intiFunction()
                            Utils.getSnackBarGreen(requireActivity(),"Delete Successfully",constraintLayout!!)
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
    }

}