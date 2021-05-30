package com.appinbox.sdk.vm

import android.app.Application
import androidx.lifecycle.*
import com.appinbox.sdk.model.SdkAuth
import com.appinbox.sdk.repo.dao.Message
import com.appinbox.sdk.repo.dao.MessageDao
import com.appinbox.sdk.repo.dao.SdkDatabase
import com.appinbox.sdk.repo.svc.ApiBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class STATUS {
    LOADING, DONE, FAIL
}

class ListVM(application: Application) : AndroidViewModel(application){

    private val dao: MessageDao = SdkDatabase.getInstance(application.applicationContext).messageDao()
    private val status: MutableLiveData<STATUS> by lazy {
        MutableLiveData<STATUS>().also {
            it.value = STATUS.LOADING
        }
    }

    fun loadMsgs() {
        status.value = STATUS.LOADING
        ApiBuilder.getApi().getMessages(SdkAuth.appId, SdkAuth.appKey, SdkAuth.contact)
            .enqueue(object : Callback<List<Message>> {
                override fun onResponse(
                    call: Call<List<Message>>,
                    response: Response<List<Message>>
                ) {
                    if (response.body() != null) {
                        status.value = STATUS.DONE
                        viewModelScope.launch(Dispatchers.IO) {
                            dao.insertAll(response.body()!!)
                        }
                    } else {
                        status.value = STATUS.FAIL
                    }
                }

                override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                    status.value = STATUS.FAIL
                }
            })
    }

    fun getStatus(): LiveData<STATUS> {
        return status
    }

    fun getUsers(): LiveData<List<Message>> {
        loadMsgs()
        return dao.getAll()
    }
}