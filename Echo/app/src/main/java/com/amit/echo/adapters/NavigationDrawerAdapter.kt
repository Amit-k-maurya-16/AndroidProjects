package com.amit.echo.adapters

import android.content.Context
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.amit.echo.R
import com.amit.echo.activities.HomeScreen
import com.amit.echo.fragments.AboutUsFragment
import com.amit.echo.fragments.FavouriteFragment
import com.amit.echo.fragments.MainScreenFragment
import com.amit.echo.fragments.SettingsFragments

class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages: IntArray, _context: Context):
                                                       RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>(){

    var contentList : ArrayList<String>? = null
    var getImages: IntArray? = null
    var mContext: Context? = null

    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mContext = _context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {

        var itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_navigation_drawer, parent, false)
        val returnThis = NavViewHolder(itemView)
        return returnThis
    }

    override fun getItemCount(): Int {
        return contentList?.size as Int
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder?.icon_get?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_get?.setText(contentList?.get(position))
        holder?.contentHolder?.setOnClickListener({
            if(position == 0){
                val mainScreenFragment = MainScreenFragment()
                (mContext as HomeScreen).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragDetail, mainScreenFragment)
                        .commit()
            } else if(position == 1){
            val favouriteFragment = FavouriteFragment()
            (mContext as HomeScreen).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragDetail, favouriteFragment)
                    .commit()
        } else if(position == 2){
            val settingsFragments = SettingsFragments()
            (mContext as HomeScreen).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragDetail, settingsFragments)
                    .commit()
        } else{
            val aboutUsFragment = AboutUsFragment()
            (mContext as HomeScreen).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragDetail, aboutUsFragment)
                    .commit()
        }
        HomeScreen.Statified.drawerLayout?.closeDrawers()
        })


    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
           var icon_get : ImageView?= null
           var text_get : TextView?= null
           var contentHolder : RelativeLayout?= null

        init {
            icon_get= itemView?.findViewById(R.id.icon_navdrawer)
            text_get= itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }

    }
}