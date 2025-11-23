package com.example.dashboard2

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Helper function untuk membuka URL di browser eksternal
 */
fun openUrlInBrowser(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        // Handle error jika tidak ada browser atau URL tidak valid
    }
}

/**
 * Helper function khusus untuk membuka YouTube video
 * Akan coba buka di YouTube app dulu, kalau gagal baru ke browser
 */
fun openYoutubeVideo(context: Context, videoUrl: String) {
    try {
        // Extract video ID dari URL
        val videoId = extractYoutubeVideoId(videoUrl)

        if (videoId != null) {
            // Coba buka di YouTube app
            val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
            youtubeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            try {
                context.startActivity(youtubeIntent)
            } catch (e: Exception) {
                // Kalau YouTube app tidak ada, buka di browser
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                context.startActivity(browserIntent)
            }
        } else {
            // Kalau gagal extract ID, langsung buka di browser
            openUrlInBrowser(context, videoUrl)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Extract video ID dari YouTube URL
 * Support format:
 * - https://www.youtube.com/watch?v=VIDEO_ID
 * - https://youtu.be/VIDEO_ID
 * - https://m.youtube.com/watch?v=VIDEO_ID
 */
private fun extractYoutubeVideoId(url: String): String? {
    return try {
        when {
            url.contains("youtube.com/watch?v=") -> {
                url.substringAfter("watch?v=").substringBefore("&")
            }
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?")
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}