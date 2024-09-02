package info.passdaily.st_therese_app.typeofuser.parent.fees

import android.util.Log
import androidx.lifecycle.*
import info.passdaily.saint_thomas_app.model.PayFeesModel
import info.passdaily.st_therese_app.MainRepository
import info.passdaily.st_therese_app.model.*
import info.passdaily.st_therese_app.services.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response

class FeesDetailViewModel(private val mainRepository: MainRepository) : ViewModel() {


    var TAG = "FeesDetailViewModel"


    fun getFeesDetails(CLASSID: Int, ACADEMICID: Int, STUDENTID: Int, STUDENT_ROLL_NO: Int) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainRepository.getFeesDetails(
                            CLASSID,
                            ACADEMICID,
                            STUDENTID,
                            STUDENT_ROLL_NO
                        )
                    )
                )
            } catch (exception: Exception) {
                Log.i(TAG, "exception $exception")
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getFeesPaidDetails(STUDENTID: Int, ReceiptId: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getFeesPaidDetails(STUDENTID, ReceiptId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    //http://holyapp.passdaily.in/PassDailyParentsApi/Fees/PayFeeStupGet?StudentId=741&ClassId=55
    fun getPayFeesDetails(STUDENTID: Int, ClassId: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getPayFeesDetails(STUDENTID, ClassId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


//    fun loadData(STUDENTID: Int, ClassId: Int): PayFeesModel {
//        viewModelScope.launch {
//            var response = mainRepository.getPayFeesDetails(STUDENTID, ClassId)
//            // Update UI with the response or handle any other logic
//        }
//        return response;
//    }

    private val _payFeesDetails = MutableLiveData<PayFeesModel>()
    val payFeesDetails: LiveData<PayFeesModel> get() = _payFeesDetails

    fun loadData(STUDENTID: Int, ClassId: Int) {
        viewModelScope.launch {
            val response = mainRepository.getPayFeesDetails(STUDENTID, ClassId)
            Log.i("message","${response.message()}");
            _payFeesDetails.postValue(response.body())
        }
    }

}

