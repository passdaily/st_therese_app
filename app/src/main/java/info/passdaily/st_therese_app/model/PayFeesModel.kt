package info.passdaily.saint_thomas_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PayFeesModel(
    @SerializedName("PayFeeId")
    var payFeeId: Int,
    @SerializedName("PayFeeLink")
    var payFeeLink: String,
    @SerializedName("PayFeeType")
    var payFeeType: String,
    @SerializedName("Status")
    var status: Int
)