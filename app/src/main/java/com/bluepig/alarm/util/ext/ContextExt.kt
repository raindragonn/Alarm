package com.bluepig.alarm.util.ext

import android.app.AlarmManager
import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.HttpDataSource.HttpDataSourceException
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.result.InitialException
import com.bluepig.alarm.domain.result.NotSelectAlarmMedia
import com.bluepig.alarm.domain.result.SearchQueryEmptyException
import com.bluepig.alarm.util.logger.BpLogger
import java.net.SocketTimeoutException
import java.net.UnknownHostException

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

val Context.audioManager: AudioManager
    get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

val Context.vibrator: Vibrator
    get() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

@get:UnstableApi
val Context.userAgent
    get() = Util.getUserAgent(this, getString(R.string.app_name))

fun Context.showErrorToast(throwable: Throwable?, moreAction: (() -> Unit)? = null) {
    val errorTextId = when (throwable) {
        is SearchQueryEmptyException -> R.string.toast_error_search_query_empty
        is HttpDataSourceException, is SocketTimeoutException, is UnknownHostException -> R.string.toast_error_network
        is NotSelectAlarmMedia -> R.string.toast_error_not_select_alarm_media
        is InitialException -> return
        else -> R.string.toast_error_basic
    }
    throwable?.let(BpLogger::logException)
    Toast.makeText(this, getString(errorTextId), Toast.LENGTH_SHORT).show()
    moreAction?.invoke()
}

val Context.isConnectedToInternet: Boolean
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return networkCapabilities.isConnectedToInternet()
    }

private fun NetworkCapabilities.isConnectedToInternet(): Boolean {
    return (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
}