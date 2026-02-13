package com.example.movieapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CinemaViewModel @Inject constructor() : ViewModel() {
    // כרגע הוא יכול להישאר ריק, אלא אם תרצי להוסיף לוגיקה לבדיקת הרשאות מיקום
}