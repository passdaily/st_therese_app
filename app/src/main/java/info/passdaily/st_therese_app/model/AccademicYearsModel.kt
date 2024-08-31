package info.passdaily.st_therese_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AccademicYearsModel(
    @SerializedName("AccademicYears")
    val accademicYears: List<AccademicYear>
){
    @Keep
    data class AccademicYear(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_STATUS")
        val aCCADEMICSTATUS: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String
    )
}