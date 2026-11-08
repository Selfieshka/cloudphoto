package ru.itis.cloudphoto.service

import ru.itis.cloudphoto.exception.CloudPhotoException
import ru.itis.cloudphoto.util.ALBUM_NOT_FOUND_MESSAGE
import ru.itis.cloudphoto.util.Utf8ByteComparator
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.BucketCannedACL
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

private const val DELETE_BATCH_SIZE = 1000
private const val JPEG_CONTENT_TYPE = "image/jpeg"
private const val HTML_CONTENT_TYPE = "text/html; charset=utf-8"

class PhotoArchive(
    private val s3: S3Client,
    private val bucket: String
) {

    fun listAlbums(): List<String> =
        s3.listObjectsV2Paginator { it.bucket(bucket).prefix(StorageLayout.ALBUMS_PREFIX).delimiter("/") }
            .commonPrefixes()
            .map { StorageLayout.albumFromPrefix(it.prefix()) }
            .sortedWith(Utf8ByteComparator)

    fun albumExists(album: String): Boolean =
        s3.listObjectsV2 { it.bucket(bucket).prefix(StorageLayout.albumPrefix(album)).maxKeys(1) }
            .keyCount() > 0

    fun requireAlbum(album: String) {
        if (!albumExists(album)) throw CloudPhotoException(ALBUM_NOT_FOUND_MESSAGE + album)
    }

    fun listPhotos(album: String): List<String> =
        listKeys(album)
            .map { StorageLayout.photoFromKey(album, it) }
            .filter { StorageLayout.isPhotoName(it) }
            .sortedWith(Utf8ByteComparator)

    fun createAlbum(album: String) {
        s3.putObject({ it.bucket(bucket).key(StorageLayout.markerKey(album)) }, RequestBody.empty())
    }

    fun uploadPhoto(album: String, file: Path) {
        s3.putObject(
            { it.bucket(bucket).key(StorageLayout.photoKey(album, file.fileName.toString())).contentType(JPEG_CONTENT_TYPE) },
            RequestBody.fromFile(file)
        )
    }

    fun downloadPhoto(album: String, photo: String, target: Path) {
        s3.getObject { it.bucket(bucket).key(StorageLayout.photoKey(album, photo)) }.use {
            Files.copy(it, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun photoExists(album: String, photo: String): Boolean =
        StorageLayout.isPhotoName(photo) && try {
            s3.headObject { it.bucket(bucket).key(StorageLayout.photoKey(album, photo)) }
            true
        } catch (e: NoSuchKeyException) {
            false
        }

    fun deletePhoto(album: String, photo: String) {
        s3.deleteObject { it.bucket(bucket).key(StorageLayout.photoKey(album, photo)) }
    }

    fun deleteAlbum(album: String) {
        listKeys(album).chunked(DELETE_BATCH_SIZE).forEach { keys ->
            val response = s3.deleteObjects { request ->
                request.bucket(bucket).delete { delete ->
                    delete.objects(keys.map { ObjectIdentifier.builder().key(it).build() })
                }
            }
            response.errors().firstOrNull()?.let { throw CloudPhotoException(it.message()) }
        }
    }

    fun enablePublicRead() {
        s3.putBucketAcl { it.bucket(bucket).acl(BucketCannedACL.PUBLIC_READ) }
    }

    fun configureWebsite(indexDocument: String, errorDocument: String) {
        s3.putBucketWebsite { request ->
            request.bucket(bucket).websiteConfiguration { website ->
                website.indexDocument { it.suffix(indexDocument) }
                    .errorDocument { it.key(errorDocument) }
            }
        }
    }

    fun putHtml(key: String, html: String) {
        s3.putObject({ it.bucket(bucket).key(key).contentType(HTML_CONTENT_TYPE) }, RequestBody.fromString(html))
    }

    private fun listKeys(album: String): List<String> =
        s3.listObjectsV2Paginator { it.bucket(bucket).prefix(StorageLayout.albumPrefix(album)) }
            .contents()
            .map { it.key() }
}
