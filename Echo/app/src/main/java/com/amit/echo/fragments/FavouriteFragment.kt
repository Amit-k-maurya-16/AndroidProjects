package com.amit.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.amit.echo.EchoData.EchoData
import com.amit.echo.R
import com.amit.echo.R.id.PlayPauseButton
import com.amit.echo.Songs
import com.amit.echo.adapters.favAdapter
import kotlinx.android.synthetic.main.fragment_favourite.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FavouriteFragment : Fragment() {

    var myActivity: Activity? = null
    var getSongsList: ArrayList<Songs>? = null

    var noFavourite: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseBottom: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null

    var trackPosition: Int = 0
    var favouriteContent : EchoData? = null

    var refreshList: ArrayList<Songs>? = null
    var getListFromDatabase: ArrayList<Songs>? = null

    object Statified{
        var mediaPlayer :MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
           activity?.title = "Favorites"
          noFavourite = view?.findViewById(R.id.noFavourite)
          nowPlayingBottomBar = view.findViewById(R.id.hiddenBarFavScreen)
          playPauseBottom = view.findViewById(R.id.PlayPauseButton)
          songTitle = view.findViewById(R.id.songTitleMain)
          recyclerView = view.findViewById(R.id.favouriteRecycler)

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favouriteContent = EchoData(myActivity)
        display_favourite_by_searching()
           getSongsList =refreshList
        if (getSongsList == null){
            recyclerView?.visibility = View.INVISIBLE
            noFavourite?.visibility = View.VISIBLE
        }else{
            recyclerView?.visibility = View.VISIBLE
            noFavourite?.visibility = View.INVISIBLE
               var favAdapter = favAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
               val mLayoutManager = LinearLayoutManager(activity)
               recyclerView?.layoutManager = mLayoutManager
               recyclerView?.itemAnimator = DefaultItemAnimator()
               recyclerView?.adapter = favAdapter
               recyclerView?.setHasFixedSize(true)

        }

        bottomBarSetup()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
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
                nowPlayingBottomBar?.visibility = View.VISIBLE
            }else{
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler(){
        nowPlayingBottomBar?.setOnClickListener({
            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer

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

        playPauseBottom?.setOnClickListener({
           if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
               SongPlayingFragment.Statified.mediaPlayer?.pause()
               trackPosition = SongPlayingFragment.Statified.mediaPlayer?.getCurrentPosition() as Int
               playPauseBottom?.setBackgroundResource(R.drawable.play_icon)
           }else{
               SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
               SongPlayingFragment.Statified.mediaPlayer?.start()
               playPauseBottom?.setBackgroundResource(R.drawable.pause_icon)
           }
        })
    }

    fun display_favourite_by_searching(){
        if (favouriteContent?.checkSize() as Int > 0){
                refreshList = ArrayList<Songs>( )
            getListFromDatabase = favouriteContent?.queryDBList()
            var fetchListFromDevice = getSongsFromDevice()
            if (fetchListFromDevice != null){
                for( i in 0..fetchListFromDevice?.size - 1){
                    for (j in 0..getListFromDatabase?.size as Int-1)
                        if ((getListFromDatabase?.get(j)?.songID)===(fetchListFromDevice?.get(i)?.songID))
                            refreshList?.add((getListFromDatabase as ArrayList<Songs>)[j])

                }
            }else{

            }
        }

    }
}
