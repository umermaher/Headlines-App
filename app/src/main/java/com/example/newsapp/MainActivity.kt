package com.example.newsapp

import android.graphics.Color
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
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.Boolean as Boolean1

class MainActivity : AppCompatActivity(), NewsListAdapter.OnItemClickListener{
    lateinit var connectionLiveData: CustomConnectionLiveData
    lateinit var mAdapter: NewsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectionLiveData= CustomConnectionLiveData(this)
        connectionLiveData.observe(this, {isNetworkAvailable->
            if (isNetworkAvailable) {
                noInternetLayout.visibility= View.GONE
            } else {
                noInternetLayout.visibility= View.VISIBLE
            }
        })

        createRecyclerView()

        swipeRefreshLayout.setOnRefreshListener {
            createRecyclerView()
            swipeRefreshLayout.isRefreshing=false
        }
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
//                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean1 {
        val inflater=menuInflater
        inflater.inflate(R.menu.search_menu,menu)

        val searchItem=menu?.findItem(R.id.action_search);
        val searchView:SearchView= searchItem?.actionView as SearchView
        searchView.imeOptions=EditorInfo.IME_ACTION_DONE
        var textView = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as TextView;
        textView.setTextColor(Color.WHITE)
        textView.setHintTextColor(Color.WHITE)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean1 {
                mAdapter.filter.filter(newText)
                return false
            }
            override fun onQueryTextSubmit(query: String?): Boolean1 {
                return false
            }
        })
        return true
    }
}