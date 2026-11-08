package ru.itis.cloudphoto

import ru.itis.cloudphoto.cli.parseArgs
import ru.itis.cloudphoto.command.DeleteCommand
import ru.itis.cloudphoto.command.DownloadCommand
import ru.itis.cloudphoto.command.ListCommand
import ru.itis.cloudphoto.command.MkSiteCommand
import ru.itis.cloudphoto.command.UploadCommand
import ru.itis.cloudphoto.config.createS3Client
import ru.itis.cloudphoto.config.loadConfig
import ru.itis.cloudphoto.exception.CloudPhotoException
import ru.itis.cloudphoto.model.Command
import ru.itis.cloudphoto.service.PhotoArchive
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        execute(args)
    } catch (e: CloudPhotoException) {
        System.err.println(e.message)
        exitProcess(1)
    } catch (e: Exception) {
        System.err.println(e.message ?: e.toString())
        exitProcess(1)
    }
}

fun execute(args: Array<String>) {
    val config = loadConfig()
    val command = parseArgs(args)
    createS3Client(config).use { s3 ->
        val archive = PhotoArchive(s3, config.bucket)
        when (command) {
            is Command.ListCmd -> ListCommand(archive).execute(command)
            is Command.Upload -> UploadCommand(archive).execute(command)
            is Command.Download -> DownloadCommand(archive).execute(command)
            is Command.Delete -> DeleteCommand(archive).execute(command)
            Command.MkSite -> MkSiteCommand(archive).execute()
        }
    }
}
