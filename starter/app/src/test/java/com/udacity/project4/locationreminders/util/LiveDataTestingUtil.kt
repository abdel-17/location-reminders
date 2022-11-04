package com.udacity.project4.locationreminders.util

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

// This function should not be used outside of testing
@VisibleForTesting
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    onObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = Observer { value: T ->
        data = value
        // Return from the `latch.await` call below. If this isn't called within
        // the passed duration, the timeout exception is thrown.
        latch.countDown()
    }
    this.observeForever(observer)
    
    try {
        onObserve()
        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        // Remove the observer to prevent memory leaks.
        this.removeObserver(observer)
    }
    
    @Suppress("UNCHECKED_CAST")
    return data as T
}