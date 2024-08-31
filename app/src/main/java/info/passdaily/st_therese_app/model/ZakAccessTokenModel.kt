package info.passdaily.st_therese_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ZakAccessTokenModel(
    @SerializedName("token")
    var token: String
)