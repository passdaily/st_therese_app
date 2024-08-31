package info.passdaily.st_therese_app.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class EventListModel(
    @SerializedName("EventList")
    val eventList: ArrayList<Event>
) {
    @Keep
    data class Event(                      /////////////////"EVENT_ID": 110,
        @SerializedName("ACCADEMIC_ID")                      /////////////////"ACCADEMIC_ID": 10,
        val aCCADEMICID: Int,                      /////////////////"EVENT_TITLE": "test",
        @SerializedName("ADMIN_ID")                      /////////////////"EVENT_DESCRIPTION": "testing",
        val aDMINID: Int,                      /////////////////"EVENT_FILE": "ce345185-cddb-4b0f-a87d-73204cccf52bIMG-20230929-WA0003.jpg",
        @SerializedName("EVENT_DATE")                      /////////////////"EVENT_DATE": "2023-10-24T20:11:48.08",
        val eVENTDATE: String,                      /////////////////"ADMIN_ID": 1,
        @SerializedName("EVENT_DESCRIPTION")                      /////////////////"EVENT_STATUS": 1,
        val eVENTDESCRIPTION: String,                      /////////////////"EVENT_TYPE": 2,
        @SerializedName("EVENT_FILE")                      /////////////////"EVENT_LINK_FILE": "c44462d0-0400-4987-9334-3a5d72feb960VID-20231018-WA0021.mp4",
        val eVENTFILE: String,                      /////////////////"SCHOOL_ID": 1
        @SerializedName("EVENT_ID")                      /////////////////
        val eVENTID: Int,                      /////////////////
        @SerializedName("EVENT_LINK_FILE")                      /////////////////
        val eVENTLINKFILE: String,                      /////////////////
        @SerializedName("EVENT_STATUS")                      /////////////////
        val eVENTSTATUS: Int,                      /////////////////
        @SerializedName("EVENT_TITLE")
        val eVENTTITLE: String,
        @SerializedName("EVENT_TYPE")
        val eVENTTYPE: Int,
        @SerializedName("SCHOOL_ID")
        val sCHOOLID: Int,
    )
}