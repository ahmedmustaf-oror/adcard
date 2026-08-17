package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.ActivationRequest
import com.example.data.OrorApiService
import com.example.util.NotificationHelper
import kotlinx.coroutines.*

class RequestsMonitoringService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val api = OrorApiService.create()

    private val knownRequestIds = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)

        // Load saved known IDs
        val prefs = getSharedPreferences("oror_admin_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("known_request_ids", null)
        if (saved == null) {
            serviceScope.launch {
                try {
                    val res = api.getRequests()
                    if (res.success && res.requests != null) {
                        for (req in res.requests) {
                            if (req.id.isNotBlank()) {
                                knownRequestIds.add(req.id)
                            }
                        }
                        prefs.edit().putStringSet("known_request_ids", HashSet(knownRequestIds)).apply()
                    }
                } catch (e: Exception) {}
            }
        } else {
            knownRequestIds.addAll(saved)
        }

        // Start foreground notification
        val notification = createForegroundNotification("جاري مراقبة طلبات التفعيل في الخلفية...")
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)

        // Start polling loop
        startBackgroundMonitoring()
    }

    private fun createForegroundNotification(content: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NotificationHelper.SERVICE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("لوحة إدارة OROR")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun startBackgroundMonitoring() {
        serviceScope.launch {
            while (isActive) {
                try {
                    val res = api.getRequests()
                    if (res.success && res.requests != null) {
                        val newRequests: List<ActivationRequest> = res.requests
                        val newlyAdded = newRequests.filter { req -> req.id !in knownRequestIds }

                        for (req in newlyAdded) {
                            knownRequestIds.add(req.id)
                            NotificationHelper.showRequestNotification(
                                context = applicationContext,
                                username = req.username,
                                phoneOrDetails = req.phone,
                                requestId = req.id
                            )
                        }

                        if (newlyAdded.isNotEmpty()) {
                            val prefs = getSharedPreferences("oror_admin_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putStringSet("known_request_ids", HashSet(knownRequestIds)).apply()
                        }
                    }
                } catch (e: Exception) {
                    // Ignore transient network errors during background sleep
                }

                delay(5000) // Poll every 5 seconds
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 9991

        fun start(context: Context) {
            val intent = Intent(context, RequestsMonitoringService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, RequestsMonitoringService::class.java)
            context.stopService(intent)
        }
    }
}
