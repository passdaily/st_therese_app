package info.passdaily.st_therese_app.typeofuser.parent.fees

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import info.passdaily.saint_thomas_app.model.PayFeesModel
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.DialogPayFeesDialogBinding
import info.passdaily.st_therese_app.services.Global
import info.passdaily.st_therese_app.services.Utils
import info.passdaily.st_therese_app.services.localDB.parent.StudentDBHelper


@Suppress("DEPRECATION")
class PayFeesWebViewDialog : DialogFragment {

    lateinit var payFeesListener: PayFeesListener


    companion object {
        var TAG = "PayFeesWebViewDialog"
    }

    private lateinit var payFeesModel: PayFeesModel

    private var _binding: DialogPayFeesDialogBinding? = null
    private val binding get() = _binding!!

    var STUDENTID = 0
    var toolbar : Toolbar? = null
    var webView : WebView? =null

    constructor(payFeesListener: PayFeesListener, payFeesModel: PayFeesModel
    ) {
        this.payFeesListener = payFeesListener
        this.payFeesModel = payFeesModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                val builder = AlertDialog.Builder(requireActivity())
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to close?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, which ->
                        payFeesListener.onWebViewClicker("")
                        cancelFrg()
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, id -> //  Action for 'NO' Button
                        dialog.dismiss()
                    }
                //  builder.show()
                val dialog = builder.create()
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
                    //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
                }
                dialog.show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)


        _binding = DialogPayFeesDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID


        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar?.title = "Payment"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to close?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, which ->
                    payFeesListener.onWebViewClicker("")
                    cancelFrg()
                    dialog.dismiss()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id -> //  Action for 'NO' Button
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
                //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
            }
            dialog.show()

        }

        webView = binding.webView


        // Configure WebView settings
        val webSettings: WebSettings? = webView?.settings
        webSettings?.javaScriptEnabled = true // Enable JavaScript if needed
        webSettings?.domStorageEnabled = true // Enable DOM storage
        webView?.setBackgroundColor(0x00FFFFFF);

        // Set WebViewClient to handle page loading and errors
        webView?.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?, handler: SslErrorHandler?, error: SslError?
            ) {
                handler?.proceed() // Ignore SSL certificate errors (Not recommended for production)
            }

            override fun onReceivedError(
                view: WebView?, errorCode: Int, description: String?, failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("WebViewError", "Error: $description")
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.startsWith("upi:")) {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                        startActivity(intent)
                    openPaymentApp(requireActivity(), url)
                    return true
                }
                return false // Let WebView handle URL loading
            }
        }
        Log.i(TAG,"payFeesModel ${payFeesModel.payFeeLink}$STUDENTID");
        // Load a URL
        webView?.loadUrl("${payFeesModel.payFeeLink}$STUDENTID")


        Utils.setStatusBarColor(requireActivity())
    }


    fun openPaymentApp(context: Context, upiUri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(upiUri)
        val paymentApps = context.packageManager.queryIntentActivities(intent, 0)
        val paymentAppNames = paymentApps.map { it.loadLabel(context.packageManager) }
        val paymentAppIcons = paymentApps.map { it.loadIcon(context.packageManager) }

        if (paymentApps.isNotEmpty()) {
            val adapter = PaymentAppListAdapter(context,paymentAppNames, paymentAppIcons)
            val dialog = AlertDialog.Builder(context)
                .setTitle("UPI Apps")
                .setAdapter(adapter) { _, which ->
                    val chosenPaymentApp = paymentApps[which]
                    val chosenPaymentAppName = chosenPaymentApp.loadLabel(context.packageManager)
                    val chosenPaymentAppPackage = chosenPaymentApp.activityInfo.packageName
                    intent.setPackage(chosenPaymentAppPackage)
                    startActivity(intent)
                }
                .create()
            dialog.show()
        } else {
            // Handle the case where no UPI apps are available
            Toast.makeText(context, "No UPI apps installed", Toast.LENGTH_LONG).show()
        }

    }


    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(PayFeesWebViewDialog.TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }
}