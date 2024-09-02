package info.passdaily.st_therese_app.typeofuser.parent.fees

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import info.passdaily.st_therese_app.R

class PaymentAppListAdapter(
    private val context: Context,
    private val appNames: List<CharSequence>,
    private val appIcons: List<android.graphics.drawable.Drawable>
) : BaseAdapter() {

    override fun getCount(): Int = appNames.size

    override fun getItem(position: Int): Any = appNames[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_payment_app, parent, false)
        val appNameTextView: TextView = view.findViewById(R.id.app_name)
        val appIconImageView: ImageView = view.findViewById(R.id.app_icon)

        appNameTextView.text = appNames[position]
        appIconImageView.setImageDrawable(appIcons[position])

        return view
    }
}