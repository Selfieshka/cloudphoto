package ru.itis.cloudphoto

import ru.itis.cloudphoto.cli.parseArgs
import ru.itis.cloudphoto.config.loadConfig
import ru.itis.cloudphoto.exception.CloudPhotoException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        loadConfig()
        parseArgs(args)
    } catch (e: CloudPhotoException) {
        System.err.println(e.message)
        exitProcess(1)
    } catch (e: Exception) {
        System.err.println(e.message ?: e.toString())
        exitProcess(1)
    }
}
