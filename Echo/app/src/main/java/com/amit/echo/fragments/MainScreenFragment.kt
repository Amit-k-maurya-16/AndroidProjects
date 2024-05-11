package com.amit.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.amit.echo.R
import com.amit.echo.R.id.hiddenBarMainScreen
import com.amit.echo.Songs
import com.amit.echo.adapters.MainScreenAdapter
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainScreenFragment : Fragment() {

    var getSongsList: ArrayList<Songs>?= null
    var currentlyPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton?=null
    var songTitle: TextView?= null
    var visibleLayout: RelativeLayout?=null
    var noSongs: RelativeLayout?=null
    var recyclerView: RecyclerView?= null
    var myActivity: Activity?=null
    var _mainScreenAdapter: MainScreenAdapter?= null

    var trackPosition = 0

    object Statified {
        var mMediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        activity?.title= "All Songs"
        visibleLayout = view?.findViewById<RelativeLayout>(R.id.visibleLayout)
        noSongs = view?.findViewById<RelativeLayout>(R.id.NoSongs)
        currentlyPlayingBottomBar = view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        songTitle = view?.findViewById<TextView>(R.id.songTitleMain)
        playPauseButton = view?.findViewById<ImageButton>(R.id.PlayPauseButton)
        recyclerView = view?.findViewById<RecyclerView>(R.id.contentMain)

        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getSongsFromDevice()

        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_Sort_Ascending = prefs?.getString("action_Sort_Ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")
        _mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)


        if (getSongsList == null){
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        }else{
            _mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            var mLayoutManager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
        }


        if (getSongsList != null){
            if (action_Sort_Ascending!!.equals("true", true)){
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }else if (action_sort_recent!!.equals("true", true)){
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }
        bottomBarSetup()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
      menu?.clear()
        inflater?.inflate(R.menu.home_screen, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
       val switcher = item?.itemId
        if (switcher == R.id.action_Sort_Ascending){
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_Sort_Ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
             if (getSongsList != null){
                 Collections.sort(getSongsList, Songs.Statified.nameComparator)
             }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        }else if (switcher == R.id.action_sort_recent){
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
                    editor?.putString("action_Sort_Ascending", "false")
                    editor?.putString("action_sort_recent", "true")
                    editor?.apply()
                    if (getSongsList != null){
                    Collections.sort(getSongsList, Songs.Statified.dateComparator)
                }
                _mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onAttach(context: Context?){
        super.onAttach(context)
        myActivity = context as Activity
    }
    override fun onAttach(activity: Activity?){
        super.onAttach(activity)
        myActivity = activity
    }

    fun getSongsFromDevice(): ArrayList<Songs>{
        var arrayList= ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null,null,null, null)
       if(songCursor != null && songCursor.moveToFirst()){
           val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
           val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
           val songArtist= songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
           val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
           val dateIndex= songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

       while(songCursor.moveToNext()){
          var currentId= songCursor.getLong(songId)
           var currentTitle= songCursor.getString(songTitle)
           var currentArtist= songCursor.getString(songArtist)
           var currentData= songCursor.getString(songData)
           var currentDate= songCursor.getLong(dateIndex)

           arrayList.add(Songs(currentId, currentTitle, currentArtist,currentData,currentDate))
       }

       }

    return arrayList
    }

    fun  bottomBarSetup(){
        try{
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            })
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                currentlyPlayingBottomBar?.visibility = View.VISIBLE
            }else{
                currentlyPlayingBottomBar?.visibility = View.INVISIBLE
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun bottomBarClickHandler(){
        currentlyPlayingBottomBar?.setOnClickListener({
            FavouriteFragment.Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer

            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("SongId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)

            args.putString("FavBottomBar", "success")
            songPlayingFragment.arguments = args

            fragmentManager!!.beginTransaction()
                    .replace(R.id.fragDetail, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        })

        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.getCurrentPosition() as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }else{
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }



}
