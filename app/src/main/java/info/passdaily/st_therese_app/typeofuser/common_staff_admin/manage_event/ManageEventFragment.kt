package info.passdaily.st_therese_app.typeofuser.common_staff_admin.manage_event

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.databinding.FragmentManageEventBinding
import info.passdaily.st_therese_app.lib.ExoPlayerActivity
import info.passdaily.st_therese_app.lib.video.Video_Activity
import info.passdaily.st_therese_app.model.*
import info.passdaily.st_therese_app.services.Global
import info.passdaily.st_therese_app.services.Status
import info.passdaily.st_therese_app.services.ViewModelFactory
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayerStaff
import info.passdaily.st_therese_app.services.localDB.LocalDBHelper
import info.passdaily.st_therese_app.typeofuser.parent.online_video.YouTubeIframePlayer
//import info.passdaily.st_therese_app.typeofuser.parent.online_video.YouTubePlayerActivity
import info.passdaily.st_therese_app.typeofuser.staff.ToolBarClickListener

@Suppress("DEPRECATION")
class ManageEventFragment : Fragment(),EventClickListener {

    var TAG = "ManageEventFragment"
    private lateinit var manageEventViewModel: ManageEventViewModel
    private var _binding: FragmentManageEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0

    var aCCADEMICID = 0

    var getEventList = ArrayList<EventListModel.Event>()

    var constraintLayoutContent: ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo: RecyclerView? = null

    var spinnerAcademic: AppCompatSpinner? = null

    var toolBarClickListener: ToolBarClickListener? = null

    lateinit var mAdapter : EventAdapter


    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        } catch (e: Exception) {
            Log.i(TAG, "Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Manage Event")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID

        manageEventViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageEventViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManageEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        var textViewTitle = binding.textView32
//        textViewTitle.visibility = View.GONE

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer


        recyclerViewVideo = binding.recyclerViewEvent
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


//        spinnerAcademic?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long
//            ) {
//                aCCADEMICID = getYears[position].aCCADEMICID
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }



//        initFunction()


        binding.buttonSubmit.visibility = View.GONE


        getEventList()
    }




    fun getEventList() {

//        manageEventViewModel.getManageEvents(adminId)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//
//                            shimmerViewContainer?.visibility = View.GONE
//                            val response = resource.data?.body()!!
//                            getEventList = response.eventList
//                            if (getEventList.isNotEmpty()) {
//                                recyclerViewVideo?.visibility = View.VISIBLE
//                                constraintEmpty?.visibility = View.GONE
//                                if (isAdded) {
//                                    mAdapter = EventAdapter(
//                                        this,
//                                        getEventList,
//                                        requireActivity(), TAG
//                                    )
//                                    recyclerViewVideo!!.adapter = mAdapter
//                                }
//                            } else {
//                                recyclerViewVideo?.visibility = View.GONE
//                                constraintEmpty?.visibility = View.VISIBLE
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_progress_report)
//                                    .into(imageViewEmpty!!)
//
//                                textEmpty?.text = resources.getString(R.string.no_results)
//                            }
//                            Log.i(TAG, "getSubjectList SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            constraintEmpty?.visibility = View.VISIBLE
//                            recyclerViewVideo?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            Glide.with(this)
//                                .load(R.drawable.ic_no_internet)
//                                .into(imageViewEmpty!!)
//                            textEmpty?.text = resources.getString(R.string.no_internet)
//                            Log.i(TAG, "getSubjectList ERROR")
//                        }
//                        Status.LOADING -> {
//                            recyclerViewVideo?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            getEventList = ArrayList<EventListModel.Event>()
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text = resources.getString(R.string.loading)
//                            Log.i(TAG, "getSubjectList LOADING")
//                        }
//                    }
//                }
//            })
    }

    class EventAdapter(
        var eventClickListener: EventClickListener,
        var eventList: ArrayList<EventListModel.Event>,
        var context: Context,var TAG : String
    ) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewEvent: ImageView = view.findViewById(R.id.imageViewEvent)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.manage_event_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


            Glide.with(context)
                .load(Global.event_url+"/EventFile/"+eventList[position].eVENTFILE)
                .apply(
                    RequestOptions.centerCropTransform()
                        .dontAnimate() //   .override(imageSize, imageSize)
                        .placeholder(R.drawable.ic_image_view)
                )
                .thumbnail(0.5f)
                .into(holder.imageViewEvent)

            holder.itemView.setOnClickListener {
                eventClickListener.onViewEvent(eventList[position])
            }

        }

        override fun getItemCount(): Int {
            return eventList.size
        }

    }

    override fun onViewEvent(event: EventListModel.Event) {
        Log.i(TAG,"event ${event.eVENTLINKFILE}")
        if(event.eVENTTYPE == 2){
            val intent = Intent(requireActivity(), ExoPlayerActivity::class.java)
            intent.putExtra("ALBUM_TITLE", "")
            intent.putExtra("ALBUM_FILE", Global.event_url+"/EventFile/"+event.eVENTLINKFILE)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivity(intent)
        }
        else if(event.eVENTTYPE == 3){
            val split2 = event.eVENTLINKFILE.split("=").toTypedArray()
            val intent = Intent(mContext, YouTubeIframePlayer::class.java)
            intent.putExtra("youTubeLink", split2[1])
            intent.putExtra("YOUTUBE_ID", event.eVENTID)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivity(intent)
        }
    }

}

interface EventClickListener {
    fun onViewEvent(event : EventListModel.Event)
}