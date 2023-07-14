package me.lwb.androidtool.library.platform.jvm

import me.lwb.logger.loggerForClass


object ShutdownHooks {
    val logger = loggerForClass()
    private val tasks: MutableList<Runnable> = ArrayList()
    private var hook: Thread? = null

    @Synchronized
    fun add(task: Runnable) {
        if (hook == null) {
            hook = object : Thread("Shutdown Hook") {
                override fun run() {
                    runTasks()
                }
            }.also { Runtime.getRuntime().addShutdownHook(it) }
        }
        tasks.add(task)
    }

    @Synchronized
    private fun runTasks() {
        for (task in tasks.toTypedArray()) {
            try {
                task.run()
            } catch (e: Throwable) {
                logger.w("Task failed", e)
            }
        }
        tasks.clear()
    }

    @Synchronized
    fun remove(task: Runnable) {
        val thread = hook ?: return
        tasks.remove(task)
        if (tasks.isEmpty()) {
            try {
                Runtime.getRuntime().removeShutdownHook(thread)
            } catch (e: IllegalStateException) {
            }
            hook = null
        }
    }

}