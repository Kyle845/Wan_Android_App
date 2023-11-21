package com.example.lib_network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lib_framework.log.LogUtil
import com.example.lib_network.error.ApiException
import com.example.lib_network.error.ERROR
import com.example.lib_network.error.ExceptionHandler
import com.example.lib_network.flow.requestFlow
import com.example.lib_network.response.BaseResponse
import com.sum.network.callback.IApiErrorCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

open class BaseViewModel : ViewModel(){
    fun launchUI(errorBlock: (Int?, String?) -> Unit, responseBlock: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            safeApiCall(errorBlock = errorBlock, responseBlock)
        }
    }

    suspend fun <T> safeApiCall(
        errorBlock: suspend (Int?, String?) -> Unit,
        responseBlock: suspend () -> T?
    ): T? {
        try {
            return responseBlock()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            val exception = ExceptionHandler.handleException(e)
            errorBlock(exception.errCode, exception.errMsg)
        }
        return null
    }

    fun <T> launchUIWithResult(
        responseBlock: suspend () -> BaseResponse<T>?,
        errorCall: IApiErrorCallback?,
        successBlock: (T?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val result = safeApiCallWithResult(errorCall = errorCall, responseBlock)
            successBlock(result)
        }
    }

    suspend fun <T> safeApiCallWithResult(
        errorCall: IApiErrorCallback?,
        responseBlock: suspend () -> BaseResponse<T>?
    ): T? {
        try {
            val response = withContext(Dispatchers.IO) {
                withTimeout(10 * 1000) {
                    responseBlock()
                }
            } ?: return null

            if (response.isFailed()) {
                throw  ApiException(response.errorCode, response.errorMsg)
            }
            return response.data
        } catch (e : Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            val exception = ExceptionHandler.handleException(e)
            if (ERROR.UNLOGIN.code == exception.errCode) {
                errorCall?.onLoginFail(exception.errCode, exception.errMsg)
            } else {
                errorCall?.onError(exception.errCode, exception.errMsg)
            }
        }
        return null
    }

    fun <T> launchFlow(
        errorCall: IApiErrorCallback? = null,
        requestCall: suspend () -> BaseResponse<T>?,
        showLoading: ((Boolean) -> Unit)? = null,
        successBlock:(T?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val data = requestFlow(errorBlock = {code, error ->
                if (ERROR.UNLOGIN.code == code) {
                    errorCall?.onLoginFail(code, error)
                } else {
                    errorCall?.onError(code, error)
                }
            }, requestCall, showLoading)
        }
    }
}