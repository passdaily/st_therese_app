package info.passdaily.st_therese_app.landingpage.firstpage

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.st_therese_app.R
import info.passdaily.st_therese_app.landingpage.firstpage.viewmodel.FaqViewModel
import info.passdaily.st_therese_app.model.*
import info.passdaily.st_therese_app.services.Global
import info.passdaily.st_therese_app.services.Status
import info.passdaily.st_therese_app.services.Utils
import info.passdaily.st_therese_app.services.ViewModelFactory
import info.passdaily.st_therese_app.services.client_manager.ApiClient
import info.passdaily.st_therese_app.services.client_manager.NetworkLayer

@Suppress("DEPRECATION")
class AboutUsFragment : Fragment() {

    var imageBackPress :ImageView? =null
    var recyclerViewFAQ : RecyclerView? =null
    private lateinit var faqViewModel: FaqViewModel

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var constraintLayout : ConstraintLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        faqViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[FaqViewModel::class.java]
        return inflater.inflate(R.layout.about_us_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Global.screenState = "landingpage"

        constraintEmpty =view.findViewById(R.id.constraintEmpty) as ConstraintLayout
        imageViewEmpty = view.findViewById(R.id.imageViewEmpty) as ImageView
        textEmpty =view.findViewById(R.id.textEmpty) as TextView

        constraintLayout = view.findViewById(R.id.constraintLayout) as ConstraintLayout

        imageBackPress = view.findViewById(R.id.imageBackPress) as ImageView
        imageBackPress?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, FirstScreenFragment()
            ).commit()
        }

 //       faqViewModel.getFaqItems(0,1)
        recyclerViewFAQ = view.findViewById(R.id.recyclerViewFAQ);
        recyclerViewFAQ?.layoutManager  = LinearLayoutManager(requireActivity())
        initView()

        if(!Utils.isOnline(requireActivity())){
            Utils.getSnackBar4K(requireActivity(),requireActivity().resources.getString(R.string.no_internet),constraintLayout)
        }
    }

    private fun initView() {
//        faqViewModel.getfaqModelObservable()
//            .observe(requireActivity(), {
//                if (it != null) {
//                    faqItems = it.AboutusList
//                    recyclerViewFAQ?.adapter = FAQAdapter(requireActivity(), faqItems!!)
//                }
//            })

        val studentId = 0
        val aboutType = 1
        faqViewModel.getFaqItems(studentId,aboutType)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var aboutUsList = response.aboutusList
                            if(aboutUsList.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                recyclerViewFAQ?.visibility = View.VISIBLE
                                if (isAdded) {
                                    recyclerViewFAQ?.adapter = FaqFragment.FAQAdapter(
                                        requireActivity(),
                                        response.aboutusList
                                    )
                                }
                            }else{
                                recyclerViewFAQ?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_state_notification)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewFAQ?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i("TAG", "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewFAQ?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_empty_state_notification)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i("TAG", "message ${resource.message}")
                        }
                    }
                }
            })
    }

    class FAQAdapter(context: Context, var faqItems : List<AboutusListModel.Aboutus>)
        : RecyclerView.Adapter<FAQAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.about_us_adapter,
                parent,
                false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: FAQAdapter.ViewHolder, position: Int) {
            holder.textTitle.text = faqItems[position].aBTFAQTITLE
            holder.textDescription.text = faqItems[position].aBTFAQDESCRIPTION
        }

        override fun getItemCount(): Int {
            return faqItems.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textTitle: TextView = itemView.findViewById(R.id.textDesc)
            var textDescription: TextView = itemView.findViewById(R.id.textDate)
        }

    }

}