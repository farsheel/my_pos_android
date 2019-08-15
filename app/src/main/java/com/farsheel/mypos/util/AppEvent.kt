package com.farsheel.mypos.util

sealed class AppEvent {
    object TokenExpired: AppEvent()
}