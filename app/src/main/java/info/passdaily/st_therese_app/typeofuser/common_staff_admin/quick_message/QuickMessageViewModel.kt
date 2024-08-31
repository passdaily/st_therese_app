package info.passdaily.st_therese_app.typeofuser.common_staff_admin.quick_message

import android.content.Context
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.st_therese_app.MainRepository
import info.passdaily.st_therese_app.services.Resource
import info.passdaily.st_therese_app.services.Utils
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class QuickMessageViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "QuickMessageViewModel"
    fun getYearClassExam(adminId: Int,schoolId :Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getYearClassExam(adminId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getMessageListStaff(staffId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getMessageListStaff(staffId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getMessageDeliveryListStaff(staffId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getMessageDeliveryListStaff(staffId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun validateField(edtField: TextInputEditText, message: String, context: Context, constraintLayout : ConstraintLayout): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField.text.toString())) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            true
        }
    }

    ////create enquiry
    fun getCommonPostFun(url : String,accademicRe: RequestBody?) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getCommonPostFun(url,accademicRe)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getCommonGetFuntion(url : String,map : HashMap<String?, Int>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getCommonGetFuntion(url,map)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    ////Student List for Staff
    fun getStudentListStaff(accademicId: Int,classId : Int,schoolId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStudentListStaff(accademicId,classId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    //    ////Student List for Staff
    fun getStaffListStaff(accademicId: Int,schoolId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStaffListStaff(accademicId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    //getGroupStudentList
    fun getGroupStudentList(aCCADEMICID: Int,groupId: Int,schoolId :Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getGroupStudentList(aCCADEMICID,groupId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    ///
    //get Group List
    fun getGroupListForStudentDelete(url: String,aCCADEMICID: Int,schoolId:Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getGroupListForStudentDelete(url, aCCADEMICID,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    ////Student List for Staff
    fun getPublicGroupMember(aCCADEMICID: Int,groupId: Int,schoolId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getPublicGroupMember(aCCADEMICID,groupId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    //    ////Student List for Staff
    fun getPtaListGet(accademicId: Int,schoolId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getPtaListGet(accademicId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    //    ////Student List for Staff
    fun getConveyorListStaff(accademicId: Int,schoolId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getConveyorListStaff(accademicId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    //    ////Student List for Staff
    fun getClassBySuperAdmin(accademicId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getClassBySuperAdmin(accademicId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    //    ////Student List for Staff
    fun getClassSectionBySuperAdmin(accademicId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getClassSectionBySuperAdmin(accademicId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    //apiHelper
    //    ////Student List for Staff
    fun getClassByTeacher(accademicId: Int,adminId : Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getClassByTeacher(accademicId,adminId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

//////// Class Division
    fun getSendMessageClassWise(url : String,aCCADEMICID: Int,cLASSID: Int,messageId : Int, adminId: Int)  = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getSendMessageClassWise(url,aCCADEMICID,cLASSID,messageId,adminId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    //////// Class Wise
    fun getSendClassSectionWise(url : String,aCCADEMICID: Int,cLASSID: Int,messageId : Int, adminId: Int)  = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getSendClassSectionWise(url,aCCADEMICID,cLASSID,messageId,adminId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    /////class by teacher staff side
    fun getSendMessageClassByTeacher(classId: Int,accademicId: Int,messageId: Int,adminId : Int)  = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getSendMessageClassByTeacher(classId,accademicId,messageId,adminId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}