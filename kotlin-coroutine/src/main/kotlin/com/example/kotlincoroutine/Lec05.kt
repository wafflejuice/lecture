package com.example.kotlincoroutine

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() {
//    example01()
//    example02()
//    example03()
//    example04()
//    example05()
    example06()
}

private fun example01() {
    runBlocking {
        val job = CoroutineScope(Dispatchers.Default).launch {
            throw IllegalArgumentException()
        }

        delay(1_000L)
    }
}

private fun example02() {
    runBlocking {
        val job = CoroutineScope(Dispatchers.Default).async {
            throw IllegalArgumentException()
        }

        delay(1_000L)
        job.await()
    }
}

private fun example03() {
    runBlocking {
        val job = async {
            throw IllegalArgumentException()
        }

        delay(1_000L)
    }
}

private fun example04() {
    runBlocking {
        val job = async(SupervisorJob()) {
            throw IllegalArgumentException()
        }

        delay(1_000L)
        // job.await()
    }
}

private fun example05() {
    runBlocking {
        val job = launch {
            try {
                throw IllegalArgumentException()
            } catch (e: IllegalArgumentException) {
                printWithThread("정상 종료")
            }
        }
    }
}

private fun example06() {
    runBlocking {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            printWithThread("exception")

            throw throwable
        }

        val job = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
            throw IllegalArgumentException()
        }

        delay(1_000L)
    }
}
