package com.appinbox.sdk.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appinbox.sdk.model.Message
import com.appinbox.sdk.svc.ApiBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class STATUS {
    LOADING, DONE, FAIL
}

class ListVM :ViewModel(){
    private var appId: String? = null
    private var appKey: String? = null
    private var contact: String? = null
    private val status: MutableLiveData<STATUS> by lazy {
        MutableLiveData<STATUS>().also {
            it.value = STATUS.LOADING
        }
    }

    private val msgs: MutableLiveData<List<Message>> by lazy {
        MutableLiveData<List<Message>>().also {
            loadMsgs()
        }
    }

    fun init(appId: String?, appKey: String?, contact: String?) {
        this.appId = appId
        this.appKey = appKey
        this.contact = contact
    }

    fun loadMsgs() {
        status.value = STATUS.LOADING
        ApiBuilder.getApi().getMessages(appId, appKey, contact)
            .enqueue(object : Callback<List<Message>> {
                override fun onResponse(
                    call: Call<List<Message>>,
                    response: Response<List<Message>>
                ) {
                    if (response.body() != null) {
                        status.value = STATUS.DONE
                        msgs.value = response.body()
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
        return msgs
    }
}