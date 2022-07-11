package com.example.practice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.regex.Pattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val validDob: MutableLiveData<Boolean> = MutableLiveData()
    val validPan: MutableLiveData<Boolean> = MutableLiveData()

    fun checkValidPan(panCardNumber: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}"
            val p = Pattern.compile(regex)
            if (panCardNumber.isEmpty()) {
                validPan.postValue(false)
            } else {
                validPan.postValue(p.matcher(panCardNumber).matches())
            }
        }

    }

    fun checkValidDob(dob: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val regex = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((?:19|20)[0-9][0-9])"
            val p = Pattern.compile(regex)
            validDob.postValue(p.matcher(dob).matches())
        }


    }
}
