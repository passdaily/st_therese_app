package info.passdaily.st_therese_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DescriptiveExamModel(
    @SerializedName("OnlineExamList")
    val onlineExamList: List<OnlineExam>
){
    @Keep
    data class OnlineExam(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ALLOWED_PAUSE")
        val aLLOWEDPAUSE: Int,
        @SerializedName("ATTEMPTED_ON")
        val aTTEMPTEDON: Any,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("EXAM_ATTEMPT_ID")
        val eXAMATTEMPTID: Int,
        @SerializedName("EXAM_ATTEMPT_STATUS")
        val eXAMATTEMPTSTATUS: Int,
        @SerializedName("EXAM_DESCRIPTION")
        val eXAMDESCRIPTION: String,
        @SerializedName("EXAM_DURATION")
        val eXAMDURATION: Int,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("EXAM_KEY")
        val eXAMKEY: Any,
        @SerializedName("EXAM_NAME")
        val eXAMNAME: String,
        @SerializedName("IS_AUTO_ENDED")
        val iSAUTOENDED: Int,
        @SerializedName("IS_SUBMITTED")
        val iSSUBMITTED: Int,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: String,
        @SerializedName("PAUSED_COUNT")
        val pAUSEDCOUNT: Any,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TIME_NOW")
        val tIMENOW: String,
        @SerializedName("TOTAL_ANSWERED_QUESTIONS")
        val tOTALANSWEREDQUESTIONS: Any,
        @SerializedName("TOTAL_OUTOFF_MARK")
        val tOTALOUTOFFMARK: Int,
        @SerializedName("TOTAL_QUESTION")
        val tOTALQUESTION: Int
    )
}