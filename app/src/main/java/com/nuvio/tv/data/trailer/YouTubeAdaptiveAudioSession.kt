package com.nuvio.tv.data.trailer

import java.util.concurrent.ConcurrentHashMap

/**
 * Short-lived handoff from the YouTube stream resolver to the ExoPlayer MediaSource.
 *
 * Unlike storing the signed audio URL in the navigation route (which can exceed
 * Android navigation limits), we attach it to the exact signed video URL for
 * the current app process. No URLs are persisted on disk.
 */
internal object YouTubeAdaptiveAudioSession {
    private const val SESSION_TTL_MS = 20 * 60 * 1000L
    private const val MAX_SESSIONS = 24

    private data class Session(val audioUrl: String, val createdAt: Long)
    private val sessions = ConcurrentHashMap<String, Session>()

    fun register(videoUrl: String, audioUrl: String?) {
        if (!videoUrl.contains("googlevideo.com", ignoreCase = true)) return
        if (audioUrl.isNullOrBlank()) {
            sessions.remove(videoUrl)
            return
        }

        val now = System.currentTimeMillis()
        sessions.entries.removeAll { (_, value) -> now - value.createdAt > SESSION_TTL_MS }
        if (sessions.size >= MAX_SESSIONS) {
            sessions.keys.firstOrNull()?.let(sessions::remove)
        }
        sessions[videoUrl] = Session(audioUrl, now)
    }

    fun audioUrlFor(videoUrl: String): String? {
        val session = sessions[videoUrl] ?: return null
        if (System.currentTimeMillis() - session.createdAt > SESSION_TTL_MS) {
            sessions.remove(videoUrl, session)
            return null
        }
        return session.audioUrl
    }
}
