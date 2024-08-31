package info.passdaily.st_therese_app.typeofuser.parent.annual_report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.st_therese_app.MainRepository
import info.passdaily.st_therese_app.services.Resource
import kotlinx.coroutines.Dispatchers

class AnnualReportViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "AnnualReportViewModel"

    fun getAnnualReport(STUDENT_ID : Int,ACADEMICID : Int) = liveData(
        Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAnnualReport(STUDENT_ID,ACADEMICID)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}