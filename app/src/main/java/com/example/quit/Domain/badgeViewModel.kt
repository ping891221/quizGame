package com.example.quit.Domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quit.badgeRepository.badgeRepository

class badgeViewModel : ViewModel(){
    private val repository : badgeRepository
    private val _allBadges = MutableLiveData<List<badgeDomain>>()
    val allBadges : LiveData<List<badgeDomain>> = _allBadges
    //獲得變數
    init {
        repository = badgeRepository().getInstance()
        repository.loadBadges(_allBadges)
    }
}