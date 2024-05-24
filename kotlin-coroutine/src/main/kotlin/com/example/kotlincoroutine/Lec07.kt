package com.example.kotlincoroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    example01()
}

private fun example01() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 1")
    }
    Thread.sleep(1_500L) // job1 . }
    suspend fun main() {
        val job = CoroutineScope(Dispatchers.Default).launch {
            delay(1_000L)
            printWithThread("Job 1")
        }
        job.join()
    }
}

class AsyncLogic {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomething() {
        scope.launch {
            // Do something
        }
    }

    fun destroy() {
        scope.cancel()
    }
}

private fun example02() {
    val asyncLogic = AsyncLogic()
    asyncLogic.doSomething()
    asyncLogic.destroy()
}

private fun example03() {
    val coroutineContext1 = CoroutineName("My coroutine") + SupervisorJob()

    val coroutineContext2 = Dispatchers.Default
    coroutineContext2 + CoroutineName("My coroutine")

    val coroutineContext3 = Dispatchers.Default
    coroutineContext3.minusKey(CoroutineName.Key)
}
