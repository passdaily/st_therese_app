package info.passdaily.st_therese_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ObjectiveExamResultModel(
    @SerializedName("OnlineExamResult")
    val onlineExamResult: OnlineExamResult,
    @SerializedName("TopperList")
    val topperList: List<Topper>
){
    @Keep
    data class OnlineExamResult(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CORRECT_ANSWER_COUNT")
        val cORRECTANSWERCOUNT: Int,
        @SerializedName("ELAPSED_TIME")
        val eLAPSEDTIME: Int,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("IS_AUTO_ENDED")
        val iSAUTOENDED: Int,
        @SerializedName("IS_PAUSED")
        val iSPAUSED: Int,
        @SerializedName("IS_SUBMITTED")
        val iSSUBMITTED: Int,
        @SerializedName("OEXAM_ATTEMPT_ID")
        val oEXAMATTEMPTID: Int,
        @SerializedName("OEXAM_DESCRIPTION")
        val oEXAMDESCRIPTION: String,
        @SerializedName("OEXAM_DURATION")
        val oEXAMDURATION: Int,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("PAUSED_COUNT")
        val pAUSEDCOUNT: Int,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_QUESTIONS")
        val tOTALQUESTIONS: Int
    )


    @Keep
    data class Topper(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CORRECT_ANSWER_COUNT")
        val cORRECTANSWERCOUNT: Int,
        @SerializedName("ELAPSED_TIME")
        val eLAPSEDTIME: Int,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("IS_AUTO_ENDED")
        val iSAUTOENDED: Int,
        @SerializedName("IS_PAUSED")
        val iSPAUSED: Int,
        @SerializedName("IS_SUBMITTED")
        val iSSUBMITTED: Int,
        @SerializedName("OEXAM_ATTEMPT_ID")
        val oEXAMATTEMPTID: Int,
        @SerializedName("OEXAM_DESCRIPTION")
        val oEXAMDESCRIPTION: String,
        @SerializedName("OEXAM_DURATION")
        val oEXAMDURATION: Int,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("PAUSED_COUNT")
        val pAUSEDCOUNT: Int,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_QUESTIONS")
        val tOTALQUESTIONS: Int
    )
}