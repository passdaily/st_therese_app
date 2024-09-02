package info.passdaily.saint_thomas_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class FeesDetailsModel(
    @SerializedName("FeesPaidDetails")
    val feesPaidDetails: ArrayList<FeesPaidDetail>
) {
    @Keep
    data class FeesPaidDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("RECEIPT_DATE")
        val rECEIPTDATE: String,
        @SerializedName("RECEIPT_ID")
        val rECEIPTID: Int,
        @SerializedName("RECEIPT_TOTAL")
        val rECEIPTTOTAL: Int,
        @SerializedName("ROLL_NUMBER")
        val rOLLNUMBER: Int,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,


        @SerializedName("PAYMENT_MODE")
        val pAYMENTMODE: String,
        @SerializedName("FEE_TYPE")
        val fEETYPE: String,
        @SerializedName("BALANCE")
        val bALANCE: String,
        @SerializedName("DOWNLOAD_LINK")
        val dOWNLOADLINK: String,
        @SerializedName("BALANCE_SHOW")
        val bALANCESHOW: String,
        @SerializedName("DETAILS_SHOW")
        val dETAILSSHOW: String,
        @SerializedName("DOWNLOAD_SHOW")
        val dOWNLOADSHOW: String,
        @SerializedName("REDIRECT_SHOW")
        val rEDIRECTSHOW: String,
    )
}