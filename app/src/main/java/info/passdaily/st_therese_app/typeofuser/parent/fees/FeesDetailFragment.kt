package info.passdaily.st_therese_app.typeofuser.parent.fees

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.saint_thomas_app.model.FeesDetailPaidModel
import info.passdaily.saint_thomas_app.model.FeesDetailsModel
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.FeesPaymentLayourBinding
import info.passdaily.st_therese_app.model.*
import info.passdaily.st_therese_app.services.Global
import info.passdaily.st_therese_app.services.Status
import info.passdaily.st_therese_app.services.Utils
import info.passdaily.st_therese_app.services.ViewModelFactory
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayer
import info.passdaily.st_therese_app.services.localDB.parent.StudentDBHelper
import java.text.NumberFormat
import java.util.*

@Suppress("DEPRECATION")
class FeesDetailFragment() : Fragment(),ItemClickListener {

    var TAG = "TrackFragment"
    lateinit var binding : FeesPaymentLayourBinding

    private lateinit var feesDetailViewModel: FeesDetailViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var recyclerViewFees : RecyclerView? = null
    var constraintEmpty: ConstraintLayout? =null
    var constraintBalance : ConstraintLayout? =null
    var imageViewEmpty: ImageView? =null
    var textEmpty : TextView? =null
    var textViewBalance : TextView? =null

    var shimmerViewContainer : ShimmerFrameLayout? =null
    var mContext : Context? =null


    var feesDetailsModel = ArrayList<FeesDetailsModel.FeesPaidDetail>()


    ///recyclerView for dialog
    var recyclerViewDialog  : RecyclerView? = null
    var textResults: TextView? =null

    lateinit var mAdapter : FeeDetailAdapter

    var  termfeepaid  = ArrayList<FeesDetailPaidModel.Termfeepaid>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG,"onAttach ")

    }

    fun refreshContent() {
        // Logic to refresh or update content
        Log.i(TAG,"refreshContent ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{


        feesDetailViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[FeesDetailViewModel::class.java]


        feesDetailsModel = Global.feesDetailsModel

        // Inflate the layout for this fragment
        binding = FeesPaymentLayourBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
     //   return inflater.inflate(R.layout.fragment_fees_detail, container, false)
        return binding.root
    }


    private fun initMethod() {
        //Fees/FeesPaidListGet?ClassId=1&AccademicId=8&StudentId=533&StudentRollNo=1
        feesDetailViewModel.getFeesDetails(CLASSID,ACADEMICID,STUDENTID,STUDENT_ROLL_NO)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"$resource");
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Global.feesDetailsModel = response.feesPaidDetails
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"ERROR ")
                        }
                        Status.LOADING -> {
                            Global.feesDetailsModel = ArrayList<FeesDetailsModel.FeesPaidDetail>()
                        }
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        constraintEmpty = binding.constraintEmpty
        constraintBalance = binding.constraintBalance
        textViewBalance = binding.textViewBalance
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(requireActivity())
            .load(R.drawable.ic_empty_state_assignment)
            .into(imageViewEmpty!!)
        textEmpty?.text = "No Fees Detail"


        recyclerViewFees = binding.recyclerView
        recyclerViewFees?.layoutManager =  LinearLayoutManager(requireActivity())

        shimmerViewContainer = binding.shimmerViewContainer

        shimmerViewContainer?.visibility = View.VISIBLE


        if(feesDetailsModel.isNotEmpty()){
            constraintEmpty?.visibility = View.GONE
            recyclerViewFees?.visibility = View.VISIBLE
            shimmerViewContainer?.visibility = View.GONE
           // constraintBalance?.visibility = View.GONE
            if(feesDetailsModel[0].bALANCESHOW.toLowerCase() == "yes") {
                constraintBalance?.visibility = View.VISIBLE
//                val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
//                val formattedNumber = numberFormat.format(feesDetailsModel[0].bALANCE)
                textViewBalance?.text = feesDetailsModel[0].bALANCE
            }
            if (isAdded) {
                recyclerViewFees?.adapter = FeesAdapter(this,feesDetailsModel, mContext!!)
            }
        }else{
            constraintEmpty?.visibility = View.VISIBLE
            recyclerViewFees?.visibility = View.GONE
            shimmerViewContainer?.visibility = View.GONE
            if (isAdded) {
                Glide.with(mContext!!)
                    .load(R.drawable.ic_empty_state_absent)
                    .into(imageViewEmpty!!)
            }
            textEmpty?.text =  requireActivity().resources.getString(R.string.no_results)
        }




        //initMethod()
    }

//    private fun initMethod() {
//        //Fees/FeesPaidListGet?ClassId=1&AccademicId=8&StudentId=533&StudentRollNo=1
//        feesDetailViewModel.getFeesDetails(CLASSID,ACADEMICID,STUDENTID,STUDENT_ROLL_NO)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            if(response.feesPaidDetails.isNotEmpty()){
//                                constraintEmpty?.visibility = View.GONE
//                                recyclerViewFees?.visibility = View.VISIBLE
//                                shimmerViewContainer?.visibility = View.GONE
//                                if (isAdded) {
//                                    recyclerViewFees?.adapter =
//                                        FeesAdapter(
//                                            response.feesPaidDetails,
//                                            mContext!!
//                                        )
//                                }
//                            }else{
//                                constraintEmpty?.visibility = View.VISIBLE
//                                recyclerViewFees?.visibility = View.GONE
//                                shimmerViewContainer?.visibility = View.GONE
//                                if (isAdded) {
//                                    Glide.with(mContext!!)
//                                        .load(R.drawable.ic_empty_state_absent)
//                                        .into(imageViewEmpty!!)
//                                }
//                                textEmpty?.text =  requireActivity().resources.getString(R.string.no_results)
//                            }
//                        }
//                        Status.ERROR -> {
//                            if (isAdded) {
//                                Glide.with(mContext!!)
//                                    .load(R.drawable.ic_no_internet)
//                                    .into(imageViewEmpty!!)
//                            }
//                            textEmpty?.text =  requireActivity().resources.getString(R.string.no_internet)
//                            constraintEmpty?.visibility = View.VISIBLE
//                            recyclerViewFees?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                        }
//                        Status.LOADING -> {
//                            if (isAdded) {
//                                Glide.with(mContext!!)
//                                    .load(R.drawable.ic_empty_state_absent)
//                                    .into(imageViewEmpty!!)
//                            }
//                            textEmpty?.text = requireActivity().resources.getString(R.string.loading)
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            recyclerViewFees?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.GONE
//                        }
//                    }
//                }
//            })
//    }


    class FeesAdapter(val itemClickListener: ItemClickListener,
                      var feesDetailsModel : ArrayList<FeesDetailsModel.FeesPaidDetail>, var mContext: Context) :
        RecyclerView.Adapter<FeesAdapter.ViewHolder>() {
        var receiptDate = ""
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textViewRollNo : TextView = view.findViewById(R.id.textViewRollNo)

            var textViewFeesType : TextView = view.findViewById(R.id.textViewFeesType)

            var textPaidAmount: TextView = view.findViewById(R.id.textPaidAmount)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)

            var textViewPaymentMode: TextView = view.findViewById(R.id.textViewPaymentMode)

            var buttonDownload : AppCompatButton = view.findViewById(R.id.buttonDownload)
            var buttonDownloadReceipt : AppCompatButton = view.findViewById(R.id.buttonDownloadReceipt)

            var constraintLayoutDown : ConstraintLayout = view.findViewById(R.id.constraintLayoutDown)


            //var textBalance : TextView = view.findViewById(R.id.textBalance)
            //var textPaymentMode : TextView = view.findViewById(R.id.textPaymentMode)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.fees_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if(!feesDetailsModel[position].rECEIPTDATE.isNullOrBlank()) {
                val date1: Array<String> = feesDetailsModel[position].rECEIPTDATE.split("T".toRegex()).toTypedArray()
                val date = Utils.longconversion(date1[0] + " " + date1[1])
                receiptDate = Utils.formattedDateTime(date)
            }
            val format: NumberFormat = NumberFormat.getCurrencyInstance()

            holder.textViewDate.text = receiptDate
            // holder.textPaidAmount.text = format.format(feesDetailsModel[position].rECEIPTTOTAL)
            holder.textPaidAmount.text = "${feesDetailsModel[position].rECEIPTTOTAL}"
            holder.textStudentName.text = feesDetailsModel[position].sTUDENTFNAME
            holder.textViewRollNo.text = "Roll.No : ${feesDetailsModel[position].rOLLNUMBER}"
            // holder.textViewClass.text = feesDetailsModel[position].sTUDENTFNAME
            // holder.textAccedamic.text = feesDetailsModel[position].aCCADEMICTIME

            holder.textViewFeesType.text = feesDetailsModel[position].fEETYPE

            holder.textViewPaymentMode.text = "Payment Mode : ${feesDetailsModel[position].pAYMENTMODE}"

            if(feesDetailsModel[position].dOWNLOADSHOW.toLowerCase() == "yes"){
                //download
                holder.constraintLayoutDown.visibility = View.VISIBLE
                holder.buttonDownload.visibility = View.VISIBLE
            }

            if(feesDetailsModel[position].rEDIRECTSHOW.toLowerCase() == "yes"){
                //dialog
                holder.constraintLayoutDown.visibility = View.VISIBLE
                holder.buttonDownloadReceipt.visibility = View.VISIBLE
            }



            holder.buttonDownload.setOnClickListener {
                itemClickListener.onDownloadClick(feesDetailsModel[position])
            }

            holder.buttonDownloadReceipt.setOnClickListener {
                itemClickListener.onReceiptDownload(feesDetailsModel[position])
            }

        }

        override fun getItemCount(): Int {
            return feesDetailsModel.size
        }

    }

    override fun onDownloadClick(feesPaidDetail: FeesDetailsModel.FeesPaidDetail) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("${feesPaidDetail.dOWNLOADLINK}${feesPaidDetail.rECEIPTID}")
        startActivity(i)
    }

    override fun onReceiptDownload(feesPaidDetail: FeesDetailsModel.FeesPaidDetail) {
        var dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_fee_details)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        recyclerViewDialog = dialog.findViewById(R.id.recyclerViewDialog) as RecyclerView
        recyclerViewDialog?.layoutManager =  LinearLayoutManager(requireActivity())

        textResults = dialog.findViewById(R.id.textResults) as TextView

        var imageViewClose = dialog.findViewById(R.id.imageViewClose) as ImageView

        imageViewClose.setOnClickListener {

            dialog.dismiss()
        }

        feesDetailMethod(feesPaidDetail)

        dialog.show()
    }


    fun feesDetailMethod(feesPaidDetail: FeesDetailsModel.FeesPaidDetail) {
        feesDetailViewModel.getFeesPaidDetails(feesPaidDetail.sTUDENTID,feesPaidDetail.rECEIPTID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            termfeepaid = response.termfeepaidList as ArrayList<FeesDetailPaidModel.Termfeepaid>
                            if(termfeepaid.isNotEmpty()){
                                recyclerViewDialog?.visibility = View.VISIBLE
                                textResults?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter = FeeDetailAdapter(
                                        termfeepaid,
                                        requireActivity(),
                                        TAG
                                    )
                                    recyclerViewDialog?.adapter = mAdapter
                                }
                            }else{
                                recyclerViewDialog?.visibility = View.GONE
                                textResults?.visibility = View.VISIBLE
                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }

                        }Status.ERROR -> {
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getMeetingAttendedReport ERROR")
                        }Status.LOADING -> {

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getMeetingAttendedReport LOADING")
                        }
                    }
                }
            })
    }



    class FeeDetailAdapter(
        var termfeepaid: ArrayList<FeesDetailPaidModel.Termfeepaid>,
        var mContext: Context, var TAG: String)
        : RecyclerView.Adapter<FeeDetailAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewFeesName: TextView = view.findViewById(R.id.textViewFeesName)
            var textViewAmount : TextView = view.findViewById(R.id.textViewAcademic)
            var textViewTerms : TextView = view.findViewById(R.id.textViewFeesType)

        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fees_paid_details_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


            holder.textViewFeesName.text = termfeepaid[position].feeTitle
            holder.textViewTerms.text = termfeepaid[position].termTitle
            holder.textViewAmount.text = "${termfeepaid[position].totalAmount}"

        }

        override fun getItemCount(): Int {
            return termfeepaid.size
        }

    }


}

interface ItemClickListener{
    fun onDownloadClick(feesPaidDetail: FeesDetailsModel.FeesPaidDetail)
    fun onReceiptDownload(feesPaidDetail: FeesDetailsModel.FeesPaidDetail)

}