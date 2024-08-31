package info.passdaily.st_therese_app.typeofuser.common_staff_admin.quick_message

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.FragmentQuickNotificationBinding
import info.passdaily.st_therese_app.model.*
import info.passdaily.st_therese_app.services.Global
import info.passdaily.st_therese_app.services.Status
import info.passdaily.st_therese_app.services.Utils
import info.passdaily.st_therese_app.services.ViewModelFactory
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayerStaff
import info.passdaily.st_therese_app.services.localDB.LocalDBHelper
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.manage_assignment.AssignmentStaffFragment
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.object_exam.CreateObjectiveExam
import info.passdaily.st_therese_app.typeofuser.common_staff_admin.quick_notification.NotificationSentTabFragment
import info.passdaily.st_therese_app.typeofuser.staff.ToolBarClickListener
import java.util.ArrayList

@Suppress("DEPRECATION")
class QuickMessageFragment : Fragment(),MessageClickListener {

    var TAG = "QuickMessageFragment"
    private lateinit var quickMessageViewModel: QuickMessageViewModel
    private var _binding: FragmentQuickNotificationBinding? = null
    private val binding get() = _binding!!
    var staffId = 0

    private lateinit var localDBHelper : LocalDBHelper
    var toolBarClickListener : ToolBarClickListener? = null

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null


    var fab : FloatingActionButton? = null

    var mContext : Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(mContext ==null){
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        }catch(e : Exception){
            Log.i(TAG,"Exception $e")
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Quick Message")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        staffId = user[0].ADMIN_ID


        // Inflate the layout for this fragment
        quickMessageViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickMessageViewModel::class.java]

        _binding = FragmentQuickNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        constraintLayoutContent = binding.constraintLayoutContent
        shimmerViewContainer= binding.shimmerViewContainer


        viewPager = binding.pager
        tabLayout = binding.tabLayout

        fab = binding.fab
        fab?.visibility = View.VISIBLE
        fab?.setOnClickListener {
            val dialog1 = CreateMessageDialog(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateMessageDialog.TAG)
        }

        initFunction()

    }

    private fun initFunction() {
        quickMessageViewModel.getMessageListStaff(staffId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Global.messageList = response.messageList as ArrayList<MessageListModel.Message>
                            getNotificationSentDetails()

                            Log.i(TAG,"initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            constraintLayoutContent?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            Global.messageList = ArrayList<MessageListModel.Message>()
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })

    }

    private fun getNotificationSentDetails() {
        quickMessageViewModel.getMessageDeliveryListStaff(staffId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Global.messageDeliveryList = response.messageDeliveryList as ArrayList<MessageDeliveryListModel.SmsDelivery>
                            getTabResult()

                            Log.i(TAG,"getNotificationSentDetails SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getNotificationSentDetails ERROR")
                        }
                        Status.LOADING -> {
                            Global.messageDeliveryList = ArrayList<MessageDeliveryListModel.SmsDelivery>()
                            Log.i(TAG,"getNotificationSentDetails LOADING")
                        }
                    }
                }
            })
    }


    private fun getTabResult() {

        val adapter = Global.MyPagerAdapter(childFragmentManager)
        adapter.addFragment(
            MessageTabFragment(this), resources.getString(R.string.message_template)
        )
        adapter.addFragment(MessageDeliveryTabFragment(), resources.getString(R.string.message_delivery))
        // adapter.addFragment(new DMKOfficial(), "Tweets");
        constraintLayoutContent?.visibility = View.VISIBLE
        shimmerViewContainer?.visibility = View.GONE
        viewPager?.adapter = adapter
        viewPager?.currentItem = 0
        tabLayout?.setupWithViewPager(viewPager)

        viewPager?.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tabLayout
            )
        )
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }

    override fun onCreateClick(message: String) {
        Log.i(TAG,"onCreateClick")
        initFunction()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onFailedMessage(message: String) {
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

}

interface MessageClickListener {
    fun onCreateClick(message: String)
    fun onFailedMessage(message: String)
}