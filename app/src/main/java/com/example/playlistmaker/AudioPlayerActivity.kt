package com.example.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private lateinit var buttonBack: ImageView
    private lateinit var playButton : ImageButton
    private var previewUrl: String? = null
    private var mediaPlayer: MediaPlayer? = null

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.oblozhka)
        playButton = findViewById(R.id.buttonPlay)
        buttonBack = findViewById(R.id.back)
        buttonBack.setOnClickListener {
            finish()
        }
        playButton.setOnClickListener {
            playbackControl()
        }
        val trackId = intent.getIntExtra(Constants.TRACK_ID, 0)
        val trackName = intent.getStringExtra(Constants.TRACK_NAME)
        val artistName = intent.getStringExtra(Constants.ARTIST_NAME)
        val collectionName = intent.getStringExtra(Constants.COLLECTION_NAME)
        val releaseDate = intent.getStringExtra(Constants.RELEASE_DATE)
        val primaryGenreName = intent.getStringExtra(Constants.PRIMARY_GENRE_NAME)
        val country = intent.getStringExtra(Constants.COUNTRY)
        val trackTimeMillis = intent.getLongExtra(Constants.TRACK_TIME_MILLIS, 0)
        val artworkUrl100 = intent.getStringExtra(Constants.ART_WORK_URL)
        previewUrl = intent.getStringExtra(Constants.PREVIEW_URL)
        val track = Track(
            trackId = trackId,
            trackName = trackName ?: "",
            artistName = artistName ?: "",
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100 ?: "",
            collectionName = collectionName ?: "",
            releaseDate = releaseDate ?: "",
            primaryGenreName = primaryGenreName ?: "",
            country = country ?: "",
            previewUrl = previewUrl ?: ""
        )


        val artworkUrlView = findViewById<ImageView>(R.id.imageMusic)
        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(track.trackTimeMillis)
        findViewById<TextView>(R.id.trackTime).text = formattedTime

        val collectionNameView = findViewById<TextView>(R.id.collectionName)
        if (collectionName == null) {
            collectionNameView.visibility = View.GONE
        }
        collectionNameView.text = track.collectionName

        val year = releaseDate?.substring(0, 4)
        findViewById<TextView>(R.id.releaseDate).text = year

        findViewById<TextView>(R.id.primaryGenreName).text = track.primaryGenreName
        findViewById<TextView>(R.id.country).text = track.country

        val cornerRadius = dpToPx(8f, this)
        Glide.with(artworkUrlView.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(artworkUrlView)

        preparePlayer()

    }

    private fun preparePlayer() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(previewUrl ?: "")
                prepareAsync()
                setOnPreparedListener {
                    playButton.isEnabled = true
                    playerState = STATE_PREPARED
                }
                setOnCompletionListener {
                    playerState = STATE_PREPARED
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun startPlayer() {
        mediaPlayer?.start()
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        playerState = STATE_PAUSED
    }
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
