package info.passdaily.saint_thomas_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class FeesDetailPaidModel(
    @SerializedName("TermfeepaidList")
    var termfeepaidList: List<Termfeepaid>
) {
    @Keep
    data class Termfeepaid(
        @SerializedName("DiscountAmount")
        var discountAmount: Int,
        @SerializedName("FeeId")
        var feeId: Int,
        @SerializedName("FeeTitle")
        var feeTitle: String,
        @SerializedName("FineAmount")
        var fineAmount: Int,
        @SerializedName("GrandTotal")
        var grandTotal: Int,
        @SerializedName("ReceiptDetailsId")
        var receiptDetailsId: Int,
        @SerializedName("ReceiptId")
        var receiptId: Int,
        @SerializedName("ScholarshipAmount")
        var scholarshipAmount: Int,
        @SerializedName("TermId")
        var termId: Int,
        @SerializedName("TermTitle")
        var termTitle: String,
        @SerializedName("TotalAmount")
        var totalAmount: Int
    )
}