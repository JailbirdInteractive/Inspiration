package com.rebirth.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import com.yuyakaido.android.cardstackview.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
val photoList:ArrayList<Photo?> = ArrayList()
lateinit var cardStackView:CardStackView
lateinit var listAdapter: ListAdapter
var pageCount:Int = 1
var query:String=""
class CardsActivity : AppCompatActivity(), View.OnClickListener,CardStackListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)
        val refreshButton = findViewById<Button>(R.id.refresh_button)
        refreshButton.setOnClickListener(this)
        cardStackView = findViewById(R.id.card_stack_view) as CardStackView
        listAdapter = ListAdapter(photoList)
        cardStackView.adapter= listAdapter
        val manager:CardStackLayoutManager = CardStackLayoutManager(this,this)
        manager.setVisibleCount(3)
        manager.setStackFrom(StackFrom.Top)
        cardStackView.layoutManager=manager
        val intent:Intent = getIntent()
        query=intent.getStringExtra("query").toString()
        getUrl(query, pageCount)
    }

    override fun onResume() {


        super.onResume()
        Handler().postDelayed(
                {
                    // This method will be executed once the timer is over
                    listAdapter.notifyDataSetChanged()
                },
                1000 // value in milliseconds
        )
    }
    private fun getUrl(query:String,page:Int){
        val client = OkHttpClient()
        //photoList.clear()

        val request = Request.Builder()
            .url("https://api.pexels.com/v1/search/?page=$page&per_page=25&query=$query portrait")
                //.url("https://api.pexels.com/v1/curated?per_page=20")
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
                            var photo=Photo(pic.getString("id"),pic.getString("url"),pic.getString("photographer"),pic.getString("photographer_url"),src.getString("original"),src.getString("medium"),src.getString("small"),"", src.getString("large2x"))
                            if(!photoList.contains(photo))
                                photoList.add(photo)
                        }
                        println("${photoList.size} photos added")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

pageCount++
                }
            }

        })

    }

    override fun onClick(view: View?) {
when(view?.id){
    R.id.refresh_button ->{
        listAdapter.notifyDataSetChanged()
    }

}
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {

    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
        if (position == photoList.size - 2) {
            getUrl(query, pageCount)
        }
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        if(position== photoList.size-1){
        cardStackView.adapter?.notifyDataSetChanged()
}
    }
}