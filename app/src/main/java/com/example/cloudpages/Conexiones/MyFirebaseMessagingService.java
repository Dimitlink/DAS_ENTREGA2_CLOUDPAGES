package com.example.cloudpages.Conexiones;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cloudpages.EscribirDiario;
import com.example.cloudpages.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    //Clase utilizada para realizar las acciones necesarias al recibir el mensaje
    @Override //Metodo que se activa cuando llega un mensaje remoto desde Firebase
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Si el mensaje es una notificacino
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            //Acciones a realizar si la version de sdk es mayor que tiramisu
            NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getApplicationContext(), "IdCanal");

            //Si la version de sdk es mayor que la version oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //Creamos el canal de comunicacion
                NotificationChannel channel = new NotificationChannel("IdCanal", "canal", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Canal");

                //Creamos la notificacion
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

                //Definimos los detalles de la notificacion
                elBuilder.setSmallIcon(R.drawable.icono)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true);

                //Lanzamos la notificacion
                elManager.notify(1, elBuilder.build());
            }

        }
    }








}

