package info.passdaily.st_therese_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MarkRegisterKGToIVModel(
    @SerializedName("Marks")
    val markList: List<Mark>
){
    @Keep
    data class Mark(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("MARK_GRADE")
        val mARKGRADE: String,
        @SerializedName("OUTOFF_MARK")
        val oUTOFFMARK: String,
        @SerializedName("PASS_MARK")
        val pASSMARK: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("TOTAL_MARK")
        var tOTALMARK: String,

        var updated : Boolean = false
    )
}