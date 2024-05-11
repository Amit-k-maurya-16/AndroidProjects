package com.amit.echo.fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.*
import com.amit.echo.CurrentSongHelper
import com.amit.echo.EchoData.EchoData

import com.amit.echo.R
import com.amit.echo.Songs
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import kotlinx.android.synthetic.main.fragment_main_screen.*
import kotlinx.android.synthetic.main.fragment_song_playing.*
import org.intellij.lang.annotations.JdkConstants
import org.w3c.dom.Text
import java.util.*
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {

    object Statified {
        var myActicity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var currentlyPlayingBottomBar: RelativeLayout? = null
        var currentSongHelper: CurrentSongHelper? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null

        var fav: ImageButton? = null
        var currentlyPlayingTitle: TextView? = null

        var favouriteContent: EchoData? = null

        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener?= null

        var MY_PREFS_NAME= "ShakeFeature"
        var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = Statified.mediaPlayer?.currentPosition
                Statified.startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))))

                Statified.seekBar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }
        }

    }




        object Staticated {
            var MY_PREFS_SHUFFLE = "Shuffle feature"
            var MY_PREFS_LOOP = "Loop feature"


            fun onSongComplete() {
                if (Statified.currentSongHelper?.isShuffle as Boolean) {
                    playNext("PlayNextLikeNormalShuffle")
                    Statified.currentSongHelper?.isPlaying = true
                } else {
                    if (Statified.currentSongHelper?.isLoop as Boolean) {

                        Statified.currentSongHelper?.isPlaying = true
                        var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)

                        Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                        Statified.currentSongHelper?.songPath = nextSong?.songData
                        Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                        Statified.currentSongHelper?.songId = nextSong?.songID as Long
                        Statified.currentSongHelper?.songArtist = nextSong?.artist
                        Statified.mediaPlayer?.reset()
                        updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

                        try {
                            Statified.mediaPlayer?.setDataSource(Statified.myActicity, Uri.parse(Statified.currentSongHelper?.songPath))
                            Statified.mediaPlayer?.prepare()
                            Statified.mediaPlayer?.start()
                            processInformation(Statified.mediaPlayer as MediaPlayer)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        playNext("PlayNextNormal")
                        Statified.currentSongHelper?.isPlaying = true
                    }

                }
                if(Statified.favouriteContent?.checkIfIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                    Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_on))
                }else{
                    Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_off))
                }

            }

            fun updateTextViews(songTitle: String, songArtist: String) {
               var songTitleUpdated = songTitle
                var songArtistUpdated = songArtist
                if (songTitle.equals("<unknown>", true)){
                   songTitleUpdated = "unknown"
               }
                if (songArtist.equals("<unknown>", true)){
                    songArtistUpdated = "unknown"
                }
                Statified.songTitleView?.setText(songTitleUpdated)
                Statified.songArtistView?.setText(songArtistUpdated)
                Statified.currentlyPlayingTitle?.setText(songTitleUpdated)

            }

            fun processInformation(mediaPlayer: MediaPlayer) {
                val finalTime = mediaPlayer.duration
                val startTime = mediaPlayer.currentPosition
                Statified.seekBar?.max = finalTime
                Statified.startTimeText?.setText(String.format("%d m: %d s",
                        TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong() as Long) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long))))
                Statified.endTimeText?.setText(String.format("%d m: %d s",
                        TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong() as Long) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long))))
                Statified.seekBar?.setProgress(startTime)
                Handler().postDelayed(Statified.updateSongTime, 100)
            }

            fun playNext(check: String) {
                if (check.equals("PlayNextNormal", true)) {
                    Statified.currentPosition = Statified.currentPosition + 1
                } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                    var randomObject = Random()
                    var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                    Statified.currentPosition = randomPosition
                }

                if (Statified.currentPosition == Statified.fetchSongs?.size) {
                    Statified.currentPosition = 0
                }
                Statified.currentSongHelper?.isLoop = false
                var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                Statified.currentSongHelper?.songPath = nextSong?.songData
                Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                Statified.currentSongHelper?.songId = nextSong?.songID as Long
                Statified.currentSongHelper?.songArtist = nextSong?.artist

                updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

                Statified.mediaPlayer?.reset()
                try {
                    Statified.mediaPlayer?.setDataSource(Statified.myActicity, Uri.parse(Statified.currentSongHelper?.songPath))
                    Statified.mediaPlayer?.prepare()
                    Statified.mediaPlayer?.start()
                    processInformation(Statified.mediaPlayer as MediaPlayer)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if(Statified.favouriteContent?.checkIfIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                    Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_on))
                }else{
                    Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_off))
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = Statified.myActicity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }


    var mAcceleration : Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast:Float = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)

        setHasOptionsMenu(true)

        activity?.title = "Now Playing"
        Statified.seekBar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fav = view?.findViewById(R.id.favouriteIcon)
        Statified.fav?.alpha = 0.8f
        Statified.currentlyPlayingBottomBar = view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        Statified.currentlyPlayingTitle = view?.findViewById(R.id.songTitleMain)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActicity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActicity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener, Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        Statified.audioVisualization?.onPause()
        super.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroyView() {
        Statified.audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect ->{
                Statified.myActicity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        val item:MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true

        val item2:MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.mediaPlayer?.pause()

        Statified.favouriteContent = EchoData(Statified.myActicity)
        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0



        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("SongId")!!.toLong()

            Statified.currentPosition = arguments!!.getInt("songPosition")
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")

            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var fromFavBottom = arguments?.get("FavBottomBar") as? String

        if (fromFavBottom != null){
            Statified.mediaPlayer = FavouriteFragment.Statified.mediaPlayer
        }else {
            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActicity, Uri.parse(path))
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Statified.mediaPlayer?.start()
        }
        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        clickHandler()

        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActicity as Context, 0)
        Statified.audioVisualization?.linkTo(visualizationHandler)

       var prefsForShuffle = Statified.myActicity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if(isShuffleAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }else{
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }


        var prefsForLoop = Statified.myActicity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)

        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if(isLoopAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle = false
            Statified.currentSongHelper?.isLoop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        }else{
            Statified.currentSongHelper?.isLoop = false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }

        if(Statified.favouriteContent?.checkIfIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
            Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_on))
        }else{
            Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_off))
        }
    }

    fun clickHandler() {


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    Statified.mediaPlayer?.seekTo( i )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })



        Statified.fav?.setOnClickListener({
            if(Statified.favouriteContent?.checkIfIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_off))
                Statified.favouriteContent?.deleteFavourite(Statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(Statified.myActicity, "Removed from favourites", Toast.LENGTH_SHORT).show()
            }else{
                Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_on))
                Statified.favouriteContent?.storeAsFavourite(Statified.currentSongHelper?.songId?.toInt(), Statified.currentSongHelper?.songArtist as String, Statified.currentSongHelper?.songTitle, Statified.currentSongHelper?.songPath )
                Toast.makeText(Statified.myActicity, "Added to favourites", Toast.LENGTH_SHORT).show()

            }
        })
        Statified.shuffleImageButton?.setOnClickListener({
            var editorShuffle = Statified.myActicity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActicity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            }
            else {
                Statified.currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })
        Statified.nextImageButton?.setOnClickListener({

            Statified.currentSongHelper?.isPlaying = true

            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }

        })
        Statified.loopImageButton?.setOnClickListener({
            var editorShuffle = Statified.myActicity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActicity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            } else {
                Statified.currentSongHelper?.isLoop = true
                Statified.currentSongHelper?.isShuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })
        Statified.previousImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        Statified.playPauseImageButton?.setOnClickListener({
            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                Statified.mediaPlayer?.start()
                Statified.currentSongHelper?.isPlaying = true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    fun playPrevious() {
        Statified.currentPosition = Statified.currentPosition - 1
        if (Statified.currentPosition == -1) {
            Statified.currentPosition = 0
        }
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.currentSongHelper?.isLoop = false
        val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
        Statified.currentSongHelper?.songTitle = nextSong?.songTitle
        Statified.currentSongHelper?.songPath = nextSong?.songData
        Statified.currentSongHelper?.currentPosition = Statified.currentPosition
        Statified.currentSongHelper?.songId = nextSong?.songID as Long
        Statified.currentSongHelper?.songArtist= nextSong.artist
        Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

        Statified.mediaPlayer?.reset()
        try {
            Statified.mediaPlayer?.setDataSource(activity, Uri.parse(Statified.currentSongHelper?.songPath))
            Statified.mediaPlayer?.prepare()
            Statified.mediaPlayer?.start()
            Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(Statified.favouriteContent?.checkIfIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
            Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_on))
        }else{
            Statified.fav?.setImageDrawable(ContextCompat.getDrawable(Statified.myActicity!!, R.drawable.favorite_off))
        }

    }


    fun bindShakeListener(){
        Statified.mSensorListener = object: SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent) {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]

                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x*x+y*y+z*z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration* 0.9f + delta

                if(mAcceleration > 12){
                    val prefs = Statified.myActicity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean){
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }

        }
    }



}

