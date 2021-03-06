package com.ravimishra.tradzhub.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ravimishra.tradzhub.Activity.ItemDetailActivity
import com.ravimishra.tradzhub.Activity.ProductActivity
import com.ravimishra.tradzhub.Activity.StoreActivity
import com.ravimishra.tradzhub.Adapter.*
import com.ravimishra.tradzhub.Model.*
import com.ravimishra.tradzhub.R
import com.ravimishra.tradzhub.Utils.Constants
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*


class HomeFragment : Fragment() {

    private var topMenuRecyclerView: RecyclerView? = null
    private var popularRecyclerView: RecyclerView? = null
    private var recylerView2: RecyclerView? = null
    private var recylerView3: RecyclerView? = null
    private var storeRecyclerView: RecyclerView? = null

    //ViewPager
    private var viewP: ViewPager? = null
    private var viewPager2: ViewPager? = null
    var progressBar: ProgressBar? = null
    var featuredProgressBar: ProgressBar? = null
    var popularProgressbar: ProgressBar? = null
    var pbNewArrivals: ProgressBar? = null
    var pbPopularStories: ProgressBar? = null
    var pbBanner: ProgressBar? = null
    var swipeTimer: Timer? = null
    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 500
    val PERIOD_MS: Long = 3000

    private val bannerImageArray: MutableList<String> = ArrayList()
    private var root: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        root = inflater.inflate(R.layout.fragment_home, container, false)

        progressBar = root?.findViewById(R.id.pbTopMenur)
        pbNewArrivals = root?.findViewById(R.id.pbNewArrivals)
        featuredProgressBar = root?.findViewById(R.id.pbFeaturedProducts)
        popularProgressbar = root?.findViewById(R.id.pbPopularProducts)
        pbPopularStories = root?.findViewById(R.id.pbPopularStores)
        pbBanner = root?.findViewById(R.id.pbBanner)

        // ApiCall();
        setUpData()
        popularRecyclerView = root?.findViewById(R.id.popularRecyclerView)
        topMenuRecyclerView = root?.findViewById(R.id.topMenuRecylerView)
        recylerView2 = root?.findViewById(R.id.recyclerView2)
        recylerView3 = root?.findViewById(R.id.recyclerView3)

//        root?.newArrivalViewAllBtn = root?.findViewById(R.id.newArrivalViewAllBtn)!!
//        popularViewAllBtn = root?.findViewById(R.id.newArrivalViewAllBtn)!!
//        newArrivalViewAllBtn = root?.findViewById(R.id.newArrivalViewAllBtn)!!

        root?.newArrivalViewAllBtn?.isEnabled = false
        root?.popularViewAllBtn?.isEnabled = false
        root?.featuredViewAllBtn?.isEnabled = false
        setUpButtons()
        storeRecyclerView =  root?.findViewById(R.id.storeRecyclerView)
        //root?.tabLayout =  root?.findViewById(R.id.storeRecyclerView)

        viewP = root?.findViewById(R.id.viewPager)
        viewPager2 = root?.findViewById(R.id.viewpager2)
        setupViewPager(viewPager2)
        root?.tabLayout?.setupWithViewPager(viewPager2)
        return root
    }

    fun setupViewPager(viewPager: ViewPager?) {
        val adapter = TabAdapter(fragmentManager)
        adapter.addFragment(JustInFragment(), "Just In")
        adapter.addFragment(BestSellingFragment(), "Best Selling")
        adapter.addFragment(FlashDealFragment(), "Flash Sale")
        viewPager!!.adapter = adapter
    }

    fun setUpButtons() {
        root?.newArrivalViewAllBtn?.setOnClickListener { v: View? ->
            val i = Intent(activity, ItemDetailActivity::class.java)
            i.putExtra("title", "New Arrivals")
            i.putExtra("category", "new")
            swipeTimer?.cancel()
            startActivity(i)
        }
        root?.popularViewAllBtn?.setOnClickListener {
            val i = Intent(activity, ItemDetailActivity::class.java)
            i.putExtra("title", "Popular")
            i.putExtra("category", "popular")
            swipeTimer?.cancel()
            startActivity(i)
        }
        root?.featuredViewAllBtn?.setOnClickListener { v: View? ->
            val i = Intent(activity, ItemDetailActivity::class.java)
            i.putExtra("title", "Featured")
            i.putExtra("category", "featured")
            swipeTimer?.cancel()
            startActivity(i)
        }

        root?.popularRecyclerView?.setOnClickListener { v: View? ->
            val i = Intent(activity, ProductActivity::class.java)
            swipeTimer?.cancel()
            startActivity(i)
        }
        recylerView2!!.setOnClickListener { v: View? ->
            val i = Intent(activity, ProductActivity::class.java)
            startActivity(i)
        }
        recylerView3!!.setOnClickListener { v: View? ->
            val i = Intent(activity, ProductActivity::class.java)
            swipeTimer?.cancel()
            startActivity(i)
        }
    }

    private fun setUpData() {
        setUpBanner()
        setCategoryData()
        setUpPopularProducts()
        setUpNewArrival()
        setFeaturedProduct()
        setStoreProduct()
    }

    private fun setCategoryData(){
        val database = FirebaseDatabase.getInstance("https://tradzhub-58133-default-rtdb.firebaseio.com")
        val myRef = database.getReference("categories")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val catModel: MutableList<Category> = ArrayList()
                for (ds in dataSnapshot.children) {
                    val id = ds.child("id").getValue(Long::class.java)!!
                    val name = ds.child("name").getValue(String::class.java)!!
                    val imgUrl = ds.child("imgUrl").getValue(String::class.java)!!
                    val cat = Category(id, name, imgUrl)
                    catModel.add(cat)
                }
                topMenuRecyclerView!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                val topMenuAdapter = context?.let { TopMenuAdapter(it, catModel) }
                topMenuRecyclerView!!.adapter = topMenuAdapter
                progressBar!!.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }
    private fun  setUpBanner() {

        val database = FirebaseDatabase.getInstance(Constants.BASE_FIREBASE_URL)
        val myRef = database.getReference("banner")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var bannerUrls: MutableList<String> = ArrayList()
                for (ds in dataSnapshot.children) {
                    val imgUrl = ds.getValue(String::class.java)!!
                    bannerUrls.add(imgUrl)
                }

                viewP!!.adapter = BannerAddapter(activity, bannerUrls)
                /*After setting the adapter use the timer */
                val handler = Handler()
                val Update = Runnable {
                    if (currentPage === 3) {
                        currentPage = 0
                    }
                    viewPager.setCurrentItem(currentPage++, true)
                }

                timer = Timer() // This will create a new Thread

                timer!!.schedule(object : TimerTask() {
                    // task to be scheduled
                    override fun run() {
                        handler.post(Update)
                    }
                }, DELAY_MS, PERIOD_MS)

                pbBanner!!.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }
    private fun setUpPopularProducts(){

        val database = FirebaseDatabase.getInstance(Constants.BASE_FIREBASE_URL)
        val myRef = database.getReference("product").child("popular")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val productArray: MutableList<Product> = ArrayList()
                for (ds in dataSnapshot.children) {
                    val id = ds.child("id").getValue(Long::class.java)!!
                    val name = ds.child("name").getValue(String::class.java)!!
                    val imgUrl = ds.child("img_url").getValue(String()::class.java)!!
                    val price = ds.child("price").getValue(Int::class.java)!!
                    val discount = ds.child("discount").getValue(Int::class.java)!!

                    val desc = ds.child("desc").getValue(String()::class.java)!!

                    val cart = ds.child("cart").getValue(String()::class.java)!!
                    val wishlist = ds.child("wishlist").getValue(String()::class.java)!!

                    val category = ds.child("category").getValue(String()::class.java)!!
                    val product = Product(id, name, price, discount, imgUrl, desc, wishlist, cart, category)
                    productArray.add(product)
                }
                popularRecyclerView!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                val onEAdpater = OnEAdpater(activity, productArray)
                popularRecyclerView!!.adapter = onEAdpater
                root?.popularViewAllBtn?.isEnabled = true
                popularProgressbar!!.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }
    private fun setUpNewArrival(){

        val database = FirebaseDatabase.getInstance(Constants.BASE_FIREBASE_URL)
        val myRef = database.getReference("product").child("new")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val productArray: MutableList<Product> = ArrayList()
                for (ds in dataSnapshot.children) {
                    val id = ds.child("id").getValue(Long::class.java)!!
                    val name = ds.child("name").getValue(String::class.java)!!
                    val imgUrl = ds.child("img_url").getValue(String()::class.java)!!
                    val price = ds.child("price").getValue(Int::class.java)!!
                    val discount = ds.child("discount").getValue(Int::class.java)!!

                    val desc = ds.child("desc").getValue(String()::class.java)!!

                    val cart = ds.child("cart").getValue(String()::class.java)!!
                    val wishlist = ds.child("wishlist").getValue(String()::class.java)!!

                    val category = ds.child("category").getValue(String()::class.java)!!
                    val product = Product(id, name, price, discount, imgUrl, desc, wishlist, cart, category)

                    productArray.add(product)
                }
                recylerView2!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                val onEAdpater = OnEAdpater(activity, productArray)
                recylerView2!!.adapter = onEAdpater
                root?.newArrivalViewAllBtn?.isEnabled = true
                pbNewArrivals!!.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }
    private  fun setFeaturedProduct(){

        val database = FirebaseDatabase.getInstance(Constants.BASE_FIREBASE_URL)
        val myRef = database.getReference("product").child("featured")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val productArray: MutableList<Product> = ArrayList()
                for (ds in dataSnapshot.children) {
                    val id = ds.child("id").getValue(Long::class.java)!!
                    val name = ds.child("name").getValue(String::class.java)!!
                    val imgUrl = ds.child("img_url").getValue(String()::class.java)!!
                    val price = ds.child("price").getValue(Int::class.java)!!
                    val discount = ds.child("discount").getValue(Int::class.java)!!

                    val desc = ds.child("desc").getValue(String()::class.java)!!

                    val cart = ds.child("cart").getValue(String()::class.java)!!
                    val wishlist = ds.child("wishlist").getValue(String()::class.java)!!

                    val category = ds.child("category").getValue(String()::class.java)!!
                    val product = Product(id, name, price, discount, imgUrl, desc, wishlist, cart, category)

                    productArray.add(product)
                }
                recylerView3!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                val onEAdpater = OnEAdpater(activity, productArray)
                recylerView3!!.adapter = onEAdpater
                root?.featuredViewAllBtn?.isEnabled = true
                featuredProgressBar!!.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }

private  fun setStoreProduct(){

    val database = FirebaseDatabase.getInstance(Constants.BASE_FIREBASE_URL)
    val myRef = database.getReference("store")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val storeArray: MutableList<Store> = ArrayList()
            for (ds in dataSnapshot.children) {
                val id = ds.child("id").getValue(Long::class.java)!!
                val name = ds.child("name").getValue(String::class.java)!!
                val imgUrl = ds.child("imgUrl").getValue(String()::class.java)!!
                val storeDesc = ds.child("storeDesc").getValue(String()::class.java)!!

                val store = Store(id, name, imgUrl, storeDesc)
                storeArray.add(store)
            }
            storeRecyclerView!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            val adapter = StoreAdapter(activity, storeArray)
            storeRecyclerView!!.adapter = adapter
            root?.newArrivalViewAllBtn?.isEnabled = true
            pbPopularStores?.visibility = View.GONE
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    })

}

    private fun setUpanner() {
        viewP!!.adapter = BannerAddapter(activity, bannerImageArray)
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == bannerImageArray.size) {
                currentPage = 0
            }
            viewP!!.setCurrentItem(currentPage++, true)
        }
        swipeTimer = Timer()
        swipeTimer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 3000, 3000)
    }

    companion object {
        private var currentPage = 0
        private const val TAG = "HomeFragment"
    }
}
