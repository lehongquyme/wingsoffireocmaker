package com.example.wingsoffireocmaker.ui.home.suggestionviewmodel

import androidx.lifecycle.ViewModel
import com.example.wingsoffireocmaker.data.model.SuggestionModel

class SuggestionViewModel : ViewModel() {
    val randomList = ArrayList<SuggestionModel>()
    //-----------------------------------------------------------------------------------------------------------------

    suspend fun updateRandomList(suggestionModel: SuggestionModel){
        randomList.add(suggestionModel)
    }


}