package com.example.theweather.ui.alarm.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.theweather.MyConstants
import com.example.theweather.R
import com.example.theweather.db.FavouriteLocationsLocalDataSource
import com.example.theweather.db.RoomDataBase
import com.example.theweather.model.Reposatory
import com.example.theweather.network.NetWorkService
import com.example.theweather.network.RemoteDataSource
import com.example.theweather.network.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlarmBroadCastReciver : BroadcastReceiver() {
    var temp: Int = 0
    var latitude: Double? = null
    var longitude: Double? = null

    companion object {
        const val CHANNEL_ID = "MyChannel"
        const val NOTIFICATION_ID = 200
        const val ACTION_DISMISS = "com.example.weatherwise.DISMISS_ALERT"
        private var Myringtone: Ringtone? = null
    }
    override fun onReceive(context: Context, intent: Intent) {
        latitude = intent.extras?.getDouble("lat")?.toDouble()
        longitude = intent.extras?.getDouble("long")?.toDouble()
        Log.d("TAG", "onReceive: fel alarm braod castReciver  long &lat  $latitude $longitude")
        when (intent.action) {
            ACTION_DISMISS -> closeMyAlert(context)
            else -> CoroutineScope(Dispatchers.IO).launch {
                val response =  Reposatory.getInstance(
                    RemoteDataSource.getInstance(RetrofitHelper.retrofitInstance.create(
                        NetWorkService::class.java)),
                    FavouriteLocationsLocalDataSource(RoomDataBase.getInstance(context).getAllFavLoacations())
                ).getWitherOfTheDay(latitude!!, longitude!!, "en") // el mafrod a check fel shared prefrenece 3al lo8a

                response.catch {
                    Toast.makeText(context.applicationContext, "Failure", Toast.LENGTH_SHORT).show()
                }
                    .collect {
                    withContext(Dispatchers.Main) {
                        temp = it.main.temp.toInt()
                        showNotification(context)
                    }
                }
            }
        }
    }


    private fun showNotification(context: Context) {
        val channelId = CHANNEL_ID
        makeMyNotificationChannel(context, channelId)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val dismissIntent = Intent(context, AlarmBroadCastReciver::class.java).apply {
            action = ACTION_DISMISS
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openFragmentIntent = Intent(context,AlarmFragment::class.java)
        val openFragmentPendingIntent = PendingIntent.getActivity(context,0,openFragmentIntent,
            PendingIntent.FLAG_IMMUTABLE)


        val address = getReadableLocation(context,latitude!!,longitude!!)
        Log.d("TAG", "showNotification: adreess lon&lat $address ")
       val language =context.getSharedPreferences(MyConstants.MY_SHARED_PREFERANCE, Context.MODE_PRIVATE) .
            getString(MyConstants.MY_LANGYAGE_API_KEY,"en")?:"en"

        when (language)
        {
            "en"->{
                val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.clear)
                    .setContentTitle("The Weather App")
                    .setContentText("The Teperature you wanted to know in $address is $temp now   ")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .addAction(
                        android.R.drawable.ic_menu_close_clear_cancel,
                        "Dismiss",
                        dismissPendingIntent
                    )
                    .setAutoCancel(true)
                    .setSound(null)  // Disable notification sound
                    .setContentIntent(openFragmentPendingIntent)

                val notificationManager =
                    ContextCompat.getSystemService(context, NotificationManager::class.java)
                notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
            }
                else ->{
                    val notificationBuilder = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.clear)
                        .setContentTitle("تطبيق الطقس")
                        .setContentText(" درجه الحراره في$address هي $temp  ")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                        .addAction(
                            android.R.drawable.ic_menu_close_clear_cancel,
                            "استبعاد",
                            dismissPendingIntent
                        )
                        .setAutoCancel(true)
                        .setSound(null)  // Disable notification sound
                        .setContentIntent(openFragmentPendingIntent)

                    val notificationManager =
                        ContextCompat.getSystemService(context, NotificationManager::class.java)
                    notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
                }
            }



        val NotifiCondition =
            context.getSharedPreferences(MyConstants.MY_SHARED_PREFERANCE, Context.MODE_PRIVATE)
                .getString(MyConstants.MY_NOTIFICATION_CONDITION, "EnableSound")

        Log.d("TAG", "showNotification: da el NotifiCondition $NotifiCondition ")
        // turn on notification sound based on my shared pref
        if (NotifiCondition == "EnableSound") {
            Log.d("TAG", "showNotification: da el NotifiCondition gaw el if $NotifiCondition ")
            NotificationSoundActiveted(context, soundUri)
        }
        else
        {
            // silent notification
            Log.d("TAG", "showNotification: da el NotifiCondition  gwa el else $NotifiCondition ")
        }


    }


    private fun NotificationSoundActiveted(context: Context, soundUri: android.net.Uri) {
        Myringtone = RingtoneManager.getRingtone(context, soundUri)
        (context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)?.let { audioManager ->
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                Myringtone?.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                Myringtone?.play()
            }
        }
    }

    private fun closeMyAlert(context: Context) {
        // wa2af el ringTone
        Myringtone?.stop()
        // Cancel el notification
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.cancel(NOTIFICATION_ID)
    }

    private fun makeMyNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "The Weather Alerts"
            val descriptionText = "Channel for weather alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                setSound(null, null)  // Disable  ringTone first // 3shan lw msh 3ayezha
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun getReadableLocation(context: Context,latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context)
        val readableLocation = geocoder.getFromLocation(latitude, longitude, 1)

        if (readableLocation.isNullOrEmpty())
        {
            return "unKnownCity"
        }
        else
        {
            val address = readableLocation?.get(0)

            val city = address?.subLocality ?: address?.locality ?: "Unknown City"

            return "$city "
        }

    }



}