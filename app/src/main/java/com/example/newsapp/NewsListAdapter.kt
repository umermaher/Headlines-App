package com.example.newsapp

import android.content.Context
import android.view.LayoutInflater
import android.view.OnReceiveContentListener
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList

class NewsListAdapter(private val listener: OnItemClickListener?): Adapter<NewsListAdapter.NewsViewHolder>(), Filterable {
    private var items=ArrayList<News>()
    private var fullItems=ArrayList<News>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.title.text=items[position].title
        holder.author.text=items[position].author
        Glide.with(holder.itemView.context).load(items[position].imageUrl).into(holder.image)
    }

    override fun getItemCount(): Int = items.size

    fun updateNews(updatedNews:ArrayList<News>){
        items.clear()
        items.addAll(updatedNews)
        fullItems.clear()
        fullItems.addAll(updatedNews)
        //reloading the recycler view
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClick(news:News)
    }

    inner class NewsViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val title:TextView=itemView.findViewById(R.id.newsTitle)
        val author:TextView=itemView.findViewById(R.id.newsAuthor)
        val image:ImageView=itemView.findViewById(R.id.newsImage)

        init {
            itemView.setOnClickListener{
                listener?.onItemClick(items[adapterPosition])
            }
        }
    }

    override fun getFilter(): Filter = filter

    private val filter=object:Filter(){
        override fun performFiltering(p0: CharSequence?): FilterResults {
            var filteredList=ArrayList<News>()
            if(p0==null || p0.isEmpty()){
                filteredList.addAll(fullItems)
            }else{
                val filterPattern= p0.toString().lowercase(Locale.getDefault()).trim()
                for(item in fullItems){
                    if(item.title.lowercase(Locale.getDefault()).contains(filterPattern)){
                        filteredList.add(item)
                    }
                }
            }
            val result=FilterResults()
            result.values =filteredList
            return result
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            items.clear()
            items.addAll(p1?.values as ArrayList<News>)
            notifyDataSetChanged()
        }
    }
}
