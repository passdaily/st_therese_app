package info.passdaily.st_therese_app.services.retrofit

interface ApiCallBack<T> {

    fun  onFailure(error : String)
    fun  onError(error : String)
    fun  onSuccess(response : T)
}