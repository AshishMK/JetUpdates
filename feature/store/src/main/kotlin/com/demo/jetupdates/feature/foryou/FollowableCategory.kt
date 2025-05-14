package com.demo.jetupdates.feature.foryou

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.demo.jetupdates.core.model.data.Category

class FollowableCategory(
    val category: Category,
    initialChecked:Boolean = false
){
    var isFollowed by mutableStateOf(initialChecked)
}