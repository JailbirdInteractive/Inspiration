package com.rebirth.myapplication

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import kotlin.random.Random

val quotes: ArrayList<String> = ArrayList()
val photos:ArrayList<Photo?> = ArrayList()
lateinit var quoteText: TextView
lateinit var quoteList: RecyclerView
lateinit var pic:ImageView
lateinit var customAdapter:CustomAdapter
class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val quoteButton: Button = findViewById(R.id.quote_button) as Button
        quoteText = findViewById(R.id.quote_text) as TextView
        quoteList = findViewById(R.id.recycler_view) as RecyclerView
        //getCats()
        val layoutManager= StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        quoteList.layoutManager=layoutManager
        customAdapter=CustomAdapter(photos)

        quoteList.adapter=customAdapter


        pic=findViewById(R.id.img_view) as ImageView
        MyAssyn().execute("",getCats(), customAdapter.notifyDataSetChanged())

        quoteButton.setOnClickListener(this)
       /* try {
            val obj = JSONObject(loadJSONFromAsset())
            val quoteArray = obj.getJSONArray("quotes")
            for (i in 0 until quoteArray.length()) {
                val quote = quoteArray.getJSONObject(i)
                quotes.add(quote.getString("quote"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }*/


    }

    override fun onResume() {
        Handler().postDelayed(
                {
                    // This method will be executed once the timer is over
                quoteList.adapter?.notifyDataSetChanged()
                },
                1000 // value in milliseconds
        )

        //quoteList.adapter=customAdapter
        super.onResume()
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.quote_button -> {
                val rando = java.util.Random()
                //val quoteNum = rando.nextInt(quotes.size) + 1
               // quoteText.setText("${quotes.get(quoteNum)}")
                //getUrl("lingerie")
                //getCats()
                //quoteList.layoutManager=layoutManager
                //quoteList.adapter=customAdapter
                quoteList.adapter?.notifyDataSetChanged()
            /*
                GlideApp.with(applicationContext)
                    .load("https://images.pexels.com/photos/4993100/pexels-photo-4993100.jpeg")
                    .into(pic)*/
            }
        }
    }
private fun getUrl(query:String){
     val client = OkHttpClient()

    val request = Request.Builder()
            .url("https://api.pexels.com/v1/search?query=portraits")
            .addHeader("Authorization","563492ad6f917000010000015e2674b19032466e8183a60bfe71ea71")
            .build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for ((name, value) in response.headers) {
                    println("$name: $value")
                }

                //println(response.body!!.string())
                try {

                    val obj = JSONObject(response.body!!.string())
                    val picArray = obj.getJSONArray("photos")
                    //var src=picArray.("src")
                    println(picArray.getJSONObject(0).get("src"))
                    for (i in 0 until picArray.length()) {
                        val pic = picArray.getJSONObject(i)
                        val src=pic.getJSONObject("src")
                        var photo=Photo(pic.getString("id"),pic.getString("url"),pic.getString("photographer"),pic.getString("photographer_url"),src.getString("original"),src.getString("medium"),src.getString("small"),"", src.getString("large"))
                        if(!photos.contains(photo))
                        photos.add(photo)
                    }
                    println("${photos.size} photos added")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            }
        }
    })

}
    private fun loadJSONFromAsset(): String {
        val json: String?
        try {
            val inputStream = assets.open("quotes.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            val charset: Charset = Charsets.UTF_8
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

    private fun getPhoto(query:String){
         var photo:Photo?=null

        val client = OkHttpClient()

        val request = Request.Builder()
                .url("https://api.pexels.com/v1/search?query=$query portrait")
                .addHeader("Authorization","563492ad6f917000010000015e2674b19032466e8183a60bfe71ea71")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    //println(response.body!!.string())
                    try {

                        val obj = JSONObject(response.body!!.string())
                        val picArray = obj.getJSONArray("photos")
                        //var src=picArray.("src")
                        println(picArray.getJSONObject(0).get("src"))

                            val pic = picArray.getJSONObject(0)
                            val src=pic.getJSONObject("src")
                             photo=Photo(pic.getString("id"),pic.getString("url"),query,pic.getString("photographer_url"),src.getString("original"),src.getString("medium"),src.getString("small"),"",src.getString("large2x"))
                            if (!photos.contains(photo))
                            photos.add(photo)
                        //customAdapter.notifyDataSetChanged()

                        println("${photos.size} photos added")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }


                }
            }

        })


        //return photo


    }
    public fun getCats():Unit{
        val photos:ArrayList<Photo?> = ArrayList()
        val cats:Array<String> = arrayOf("","Wedding","Black and White","School","Professional","Lingerie","Women","Men","Pets","Beach")
for (cat in cats){
    (getPhoto(cat))
    quoteList.adapter?.notifyDataSetChanged()

}
        quoteList.adapter?.notifyDataSetChanged()
    /*val layoutManager= StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val customAdapter=CustomAdapter(photos)*/
        /*quoteList.layoutManager=layoutManager
        quoteList.adapter=customAdapter*/

    }

    class  MyAssyn : AsyncTask<Any, Any, Any>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Any?) {

        }

        override fun onPostExecute(result: Any?) {
            quoteList.adapter?.notifyDataSetChanged()
            super.onPostExecute(result)
        }
    }
}