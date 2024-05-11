package com.amit.echo.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.amit.echo.R

class  splash : AppCompatActivity() {

    var permissionString = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                   android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                   android.Manifest.permission.READ_PHONE_STATE,
                                   android.Manifest.permission.PROCESS_OUTGOING_CALLS,
                                   android.Manifest.permission.RECORD_AUDIO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


    if(!hasPermissions(this@splash, *permissionString)){
        //will code later
        ActivityCompat.requestPermissions(this@splash, permissionString, 121)
    }
    else{
        Handler().postDelayed({
            val startAct= Intent(this@splash, HomeScreen::class.java)
            startActivity(startAct)
            this.finish()
        },1000)
    }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){

            121-> {  if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED
                                                    && grantResults[1]== PackageManager.PERMISSION_GRANTED
                                                    && grantResults[2]== PackageManager.PERMISSION_GRANTED
                                                    && grantResults[3]== PackageManager.PERMISSION_GRANTED
                                                    && grantResults[4]== PackageManager.PERMISSION_GRANTED)
                    Handler().postDelayed({
                        
                        val startA = Intent( this@splash, HomeScreen::class.java)
                        startActivity(startA)
                        this.finish()
                    }, 1000)
                    else{
                Toast.makeText(this@splash, "Pagal hai kya sabhi permission de", Toast.LENGTH_SHORT).show()
                this.finish()
            }

                return
            }
            else-> {
               Toast.makeText(this@splash, "ho gyi n galti", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }


        }


    }


    fun hasPermissions(context: Context, vararg permissions: String ):Boolean{
        var hasAllPermissions = true
        for(permission in permissions){
            val res = context.checkCallingOrSelfPermission(permission)
            if(res != PackageManager.PERMISSION_GRANTED){
                hasAllPermissions= false
            }
        }
        return hasAllPermissions
    }
}
