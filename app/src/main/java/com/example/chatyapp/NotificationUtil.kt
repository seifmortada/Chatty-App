package com.example.chatyapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationUtil{
     val CHANNEL_ID="channelId"

        fun notificationBuilder(context:Context,userId:String,message:String){
            val builder=NotificationCompat.Builder(context,CHANNEL_ID)
            builder.setSmallIcon(R.drawable.icon)
                .setContentTitle(userId)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            with(NotificationManagerCompat.from(context)){
                if(ActivityCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)
                !=PackageManager.PERMISSION_GRANTED){
                    return
                }
                notify(1,builder.build())
            }
        }

}