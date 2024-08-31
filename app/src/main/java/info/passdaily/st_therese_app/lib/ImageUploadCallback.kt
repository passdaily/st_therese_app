package info.passdaily.st_therese_app.lib

interface ImageUploadCallback {
    fun onProgressUpdate(percentage: Int)
    fun onError(message: String?)
    fun onSuccess(message: String?)
}