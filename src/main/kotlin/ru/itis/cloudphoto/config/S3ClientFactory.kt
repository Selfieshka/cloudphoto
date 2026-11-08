package ru.itis.cloudphoto.config

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

private const val ENDPOINT = "https://storage.yandexcloud.net"
private const val REGION = "ru-central1"

fun createS3Client(config: Config): S3Client =
    S3Client.builder()
        .endpointOverride(URI.create(ENDPOINT))
        .region(Region.of(REGION))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(config.accessKeyId, config.secretAccessKey)
            )
        )
        .build()
