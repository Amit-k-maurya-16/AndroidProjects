package com.amit.echo.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.TelephonyManager
import com.amit.echo.R
import com.amit.echo.activities.HomeScreen
import com.amit.echo.fragments.SongPlayingFragment
import kotlin.reflect.KMutableProperty1

class CaptureBroadCast: BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            try {
                HomeScreen.Statified.notificationManager?.cancel(1978)
            }catch(e:Exception){
                e.printStackTrace()
            }
           try {
               if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                   SongPlayingFragment.Statified.mediaPlayer?.pause()
                   SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
               }
           }catch (e: Exception){
               e.printStackTrace()
           }

        }else{
            val tm:TelephonyManager = p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    try {
                        HomeScreen.Statified.notificationManager?.cancel(1978)
                    }catch(e:Exception){
                        e.printStackTrace()
                    }
                    try {
                           if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                               SongPlayingFragment.Statified.mediaPlayer?.pause()
                               SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                    }
                    }catch(e: Exception){
                e.printStackTrace()
                    }
                }
                else->{

                  }
                }
    }

}
}