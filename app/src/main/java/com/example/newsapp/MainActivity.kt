package com.example.newsapp

import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.net.InetAddress
import java.net.UnknownHostException

class MainActivity : AppCompatActivity(), NewsListAdapter.OnItemClickListener{
    lateinit var mAdapter: NewsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createRecyclerView()
    }

    private fun createRecyclerView(){
        recyclerView.layoutManager=LinearLayoutManager(this)
        mAdapter=NewsListAdapter(this)
        fetchData()
        recyclerView.hasFixedSize()
        recyclerView.adapter=mAdapter
    }

    private fun fetchData() {
        val url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=a33bdb54a4aa4e7997278e0dc1412680"
        val newsJsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,

            { response ->
                val newsJsonArray = response.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }

                mAdapter.updateNews(newsArray)

            },
            {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["User-Agent"]="Mozilla/5.0"
                return headers
            }
        }

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(newsJsonObjectRequest)
    }

    override fun onItemClick(news: News) {
//        Toast.makeText(this,"$news",Toast.LENGTH_SHORT).show()
        val url = news.url
        val builder:CustomTabsIntent.Builder=CustomTabsIntent.Builder()
        val customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.search_menu,menu)

        val searchItem=menu?.findItem(R.id.action_search);
        val searchView:SearchView= searchItem?.actionView as SearchView
        searchView.imeOptions=EditorInfo.IME_ACTION_DONE
        var textView = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as TextView;
        textView.setTextColor(Color.WHITE)
        textView.setHintTextColor(Color.WHITE)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                mAdapter.filter.filter(newText)
                return false
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })
        return true
    }

//    private fun isNetworkAvailable():Boolean{
//        val connectivityManager=getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//        val capabilities=connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//        return (capabilities!=null && capabilities.hasCapability(NET_CAPABILITY_INTERNET))
//    }
//    private fun isNetworkAvailable(): Boolean {
//        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
////        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
//        val activeNetworkInfo=connectivityManager.activeNetworkInfo
//    Toast.makeText(this,"Hell",Toast.LENGTH_LONG).show()
//        return activeNetworkInfo!=null && (activeNetworkInfo.type==ConnectivityManager.TYPE_WIFI || activeNetworkInfo.type==ConnectivityManager.TYPE_MOBILE)
//
//    }
//    private fun isInternetAvailable(): Boolean {
//        try {
//            val address: InetAddress = InetAddress.getByName("www.google.com")
//            return !address.equals("")
//        } catch (e: UnknownHostException) {
//            // Log error
//            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
//        }
//        return false
//    }
//    private fun drawLayout(){
//        if(isNetworkAvailable()){
//            internetLayout.visibility= View.VISIBLE
//            noInternetLayout.visibility=View.GONE
//            //it will load
//            createRecyclerView()
//        }else{
//            noInternetLayout.visibility=View.VISIBLE
//            Toast.makeText(this,"No Connection",Toast.LENGTH_LONG).show()
//            internetLayout.visibility=View.GONE
//        }
//    }

    fun tryAgainBtn(view: android.view.View) {}
}