package com.example.goalguru

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoadingViewModel: ViewModel() {
    private val _isDataLoaded = MutableLiveData<Boolean>()
    val isDataLoaded: LiveData<Boolean> = _isDataLoaded

    fun setDataLoaded(isLoaded: Boolean) {
        _isDataLoaded.value = isLoaded
    }
}