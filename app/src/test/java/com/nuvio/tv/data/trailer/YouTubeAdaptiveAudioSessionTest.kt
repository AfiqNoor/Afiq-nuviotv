package com.nuvio.tv.data.trailer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class YouTubeAdaptiveAudioSessionTest {
    @Test
    fun `separate audio is available only for its matched video URL`() {
        val videoUrl = "https://r1.googlevideo.com/videoplayback?id=unit-test-a"
        val audioUrl = "https://r1.googlevideo.com/videoplayback?id=unit-test-a-audio"
        YouTubeAdaptiveAudioSession.register(videoUrl, audioUrl)

        assertEquals(audioUrl, YouTubeAdaptiveAudioSession.audioUrlFor(videoUrl))
        assertNull(YouTubeAdaptiveAudioSession.audioUrlFor("https://r2.googlevideo.com/videoplayback?id=other"))
    }

    @Test
    fun `missing audio clears a previously registered video`() {
        val videoUrl = "https://r1.googlevideo.com/videoplayback?id=unit-test-b"
        YouTubeAdaptiveAudioSession.register(videoUrl, "https://r1.googlevideo.com/audio?id=unit-test-b")
        YouTubeAdaptiveAudioSession.register(videoUrl, null)

        assertNull(YouTubeAdaptiveAudioSession.audioUrlFor(videoUrl))
    }

    @Test
    fun `non YouTube URLs cannot attach a second audio stream`() {
        val videoUrl = "https://example.com/movie.mp4"
        YouTubeAdaptiveAudioSession.register(videoUrl, "https://example.com/other.mp3")

        assertNull(YouTubeAdaptiveAudioSession.audioUrlFor(videoUrl))
    }
}
