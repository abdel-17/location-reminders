package com.udacity.project4.locationreminders.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.LocationServices
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService() {
    
    // Get the local repository instance
    private val remindersLocalRepository: ReminderDataSource by inject()
    
    private var coroutineJob = Job()
    
    private val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob
    
    private val geofencingClient by lazy {
        LocationServices.getGeofencingClient(this)
    }
    
    override fun onHandleWork(intent: Intent) {
        // Return from this method if the event is null. This happens if
        // it doesn't contain a geofence event.
        val geofenceEvent = GeofencingEvent.fromIntent(intent) ?: return
        Log.i(TAG, "Received geofencing intent")
        if (geofenceEvent.hasError()) {
            // Check if the given intent has been triggered by en error.
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofenceEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
        // At this point, the geofence event is valid.
        when (val geofenceTransition = geofenceEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER ->
                // A single event can trigger multiple geofences. This list is `null` only if
                // the received intent does not contain a geofence event, but we already
                // handled this case above, so we don't need to worry about it.
                geofenceEvent.triggeringGeofences?.forEach { geofence ->
                    sendNotificationAndDeleteReminder(geofence)
                }
            // Since we only listen to `ENTER` transitions, receiving any other event is unexpected.
            else -> Log.e(TAG, "Unexpected geofence transition $geofenceTransition")
        }
    }
    
    private fun sendNotificationAndDeleteReminder(geofence: Geofence) {
        val requestId = geofence.requestId
        Log.i(TAG, "Sending notification for geofence with id $requestId")
        // Interaction to the repository has to be through a coroutine scope.
        CoroutineScope(coroutineContext).launch(SupervisorJob()) {
            // The geofence request id is the same as the reminder's id.
            when (val result = remindersLocalRepository.getReminder(requestId)) {
                // Send a notification to the user with the reminder details on success.
                is Result.Success<ReminderDTO> -> {
                    val reminder = result.data.toReminderDataItem()
                    sendNotification(this@GeofenceTransitionsJobIntentService, reminder)
                    // Delete the stored reminder since it has been handled.
                    remindersLocalRepository.deleteReminder(requestId)
                    geofencingClient.removeGeofences(listOf(requestId))
                }
                is Result.Error ->
                    Log.e(TAG, "Reminder could not be found ${result.errorMessage}")
            }
        }
    }
    
    companion object {
        private const val TAG = "GeofenceTransitions"
        
        private const val JOB_ID = 573
        
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeofenceTransitionsJobIntentService::class.java, JOB_ID, intent)
        }
    }
}