package info.passdaily.st_therese_app.typeofuser.parent.fees

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.saint_thomas_app.model.PayFeesModel
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.FragmentPayFeesBinding
import info.passdaily.st_therese_app.model.DescriptiveExamListStaffModel
import info.passdaily.st_therese_app.services.Global
import info.passdaily.st_therese_app.services.ViewModelFactory
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayer
import info.passdaily.st_therese_app.services.localDB.parent.StudentDBHelper



class PayFeesFragment(var feesInitFragmentListener : FeesInitFragmentListener, var payFeesModel: PayFeesModel?, var currentItem : Int) : Fragment(),PayFeesListener {

    var TAG = "PayFeesFragment"
    lateinit var binding : FragmentPayFeesBinding

    private lateinit var feesDetailViewModel: FeesDetailViewModel



    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.
    var mContext : Context? =null

    var constraintEmpty: ConstraintLayout? =null
    var imageViewEmpty: ImageView? =null
    var textEmpty : TextView? =null

    var shimmerViewContainer : ShimmerFrameLayout? =null


    var constraintLayoutWebView : ConstraintLayout? =null
    var constraintLayoutNative : ConstraintLayout? =null
    var constraintLayoutBrowser : ConstraintLayout? =null

    var buttonBrowser : AppCompatButton? =null

    var webView : WebView? =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(webView != null){
            webView?.clearCache(true)
            webView?.clearHistory()
            webView?.reload()
        }
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG,"onAttach ")

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        feesDetailViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[FeesDetailViewModel::class.java]

        // Inflate the layout for this fragment
        binding = FragmentPayFeesBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
        //   return inflater.inflate(R.layout.fragment_fees_detail, container, false)
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


        ///empty constraint
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(requireActivity())
            .load(R.drawable.ic_empty_state_assignment)
            .into(imageViewEmpty!!)
        textEmpty?.text = "No Fees Detail"


        //shimmer
        shimmerViewContainer = binding.shimmerViewContainer


        //webview
        constraintLayoutWebView = binding.constraintLayoutWebView


        webView = binding.webView

        // app native
        constraintLayoutNative = binding.constraintLayoutNative


        //Browser
        constraintLayoutBrowser = binding.constraintLayoutBrowser

        buttonBrowser = binding.buttonBrowser
        buttonBrowser?.visibility = View.GONE

        Log.i(TAG,"payFeesModel ${payFeesModel?.payFeeLink}");
        Log.i(TAG,"payFeesModel ${payFeesModel?.payFeeType}");

        Log.i(TAG,"tabController ${Global.tabController}");

        if(payFeesModel?.payFeeType?.toLowerCase() == "app"){
            shimmerViewContainer?.visibility = View.GONE
            constraintLayoutWebView?.visibility = View.GONE
            constraintLayoutBrowser?.visibility = View.GONE
            constraintLayoutNative?.visibility = View.VISIBLE
            constraintEmpty?.visibility = View.GONE


        }else if(payFeesModel?.payFeeType?.toLowerCase() == "webview"){
            shimmerViewContainer?.visibility = View.GONE
            constraintLayoutWebView?.visibility = View.VISIBLE
            constraintLayoutBrowser?.visibility = View.GONE
            constraintLayoutNative?.visibility = View.GONE
            constraintEmpty?.visibility = View.GONE

        }else if(payFeesModel?.payFeeType?.toLowerCase() == "browser"){
            shimmerViewContainer?.visibility = View.GONE
            constraintLayoutWebView?.visibility = View.GONE
            constraintLayoutBrowser?.visibility = View.VISIBLE
            constraintLayoutNative?.visibility = View.GONE
            constraintEmpty?.visibility = View.GONE


//            buttonBrowser?.setOnClickListener {
//            if(Global.tabController == 0) {
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse("${payFeesModel?.payFeeLink}$STUDENTID")
//                startActivity(i)
//            }
//            }

        }else if(payFeesModel?.payFeeType?.toLowerCase() == "comingsoon"){

            shimmerViewContainer?.visibility = View.GONE
            constraintLayoutWebView?.visibility = View.GONE
            constraintLayoutBrowser?.visibility = View.GONE
            constraintLayoutNative?.visibility = View.GONE
            constraintEmpty?.visibility = View.VISIBLE

            Glide.with(requireActivity())
                .load(R.drawable.ic_empty_state_library)
                .into(imageViewEmpty!!)

            textEmpty?.text = "${payFeesModel?.payFeeLink}"
        }

    }

    fun refreshContent() {
        // Logic to refresh or update content
        Log.i(TAG,"refreshContent ")

        if(payFeesModel?.payFeeType?.toLowerCase() == "browser"){
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("${payFeesModel?.payFeeLink}$STUDENTID")
            startActivity(i)

        }else if(payFeesModel?.payFeeType?.toLowerCase() == "webview"){
            val dialog1 = PayFeesWebViewDialog(this, payFeesModel!!)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, PayFeesWebViewDialog.TAG)
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        webView?.goBack()
    }

    override fun onWebViewClicker(message: String) {
        feesInitFragmentListener.onBackPressed(message);
    }
}

interface PayFeesListener{
    fun onWebViewClicker(message : String)
}