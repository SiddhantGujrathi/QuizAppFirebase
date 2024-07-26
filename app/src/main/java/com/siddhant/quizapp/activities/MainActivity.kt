package com.siddhant.quizapp.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.GridLayoutManager
import com.siddhant.quizappnew.R
import com.siddhant.quizapp.adapters.QuizAdapter
import com.siddhant.quizapp.models.Quiz
import com.siddhant.quizappnew.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.os.BuildCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var adapter: QuizAdapter
    private var quizList = mutableListOf<Quiz>()
    lateinit var firestore: FirebaseFirestore
    private lateinit var checkNetworkConnection: CheckNetworkConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* To set all the things required on Main Activity*/
        setUpViews()

        /* To start animation on navigation drawer */
        startAnimation()

        /* To check network is connected or not */
        callNetworkConnection()

        //Finish this Activity once you clicked back
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }
    }


    /* It will check it is connected to internet or not */
    private fun callNetworkConnection() {
        checkNetworkConnection = CheckNetworkConnection(application)
        checkNetworkConnection.observe(this) { isConnected ->
            if (!isConnected) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            }
        }
    }

    /* To start animation of drawer layout */
    private fun startAnimation() {
        val animationDrawable = binding.quizRecyclerView.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2500)
        animationDrawable.setExitFadeDuration(5000)
        animationDrawable.start()
    }

    private fun setUpViews() {
        /* Fetching data from Firebase and storing in array of Quiz models */
        setUpFireStore()

        /* Setting Contents of Drawer Layout */
        setUpDrawerLayout()

        /* To set the data fetched from firestore to Main Layout*/
        setUpRecyclerView()

        /* On clicking date picker user able to directly open the test of that date */
        setUpDatePicker()
    }

    private fun setUpDatePicker() {
        binding.btnDatePicker.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")

            /* If user click on okay button */
            datePicker.addOnPositiveButtonClickListener {
                Log.d("DATEPICKER", datePicker.headerText)
                val intent = Intent(this, QuestionActivity::class.java)

                /* Here we are passing the date to next activity */
                intent.putExtra("DATE", datePicker.headerText)
                startActivity(intent)
            }
            datePicker.addOnNegativeButtonClickListener {
                Log.d("DATEPICKER", datePicker.headerText)
            }
            /* If user click on cancel button */
            datePicker.addOnCancelListener {
                Log.d("DATEPICKER", "Date Picker Cancelled")
            }
        }
    }


    /* Fetch the data from firebase  */
    private fun setUpFireStore() {
        /* To get FirebaseFirestore instance to fetch data */
        firestore = FirebaseFirestore.getInstance()

        /* Fetching collection named as "quizes" */
        val collectionReference = firestore.collection("quizes")
        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            Log.d("DATA", value.toObjects(Quiz::class.java).toString())
            quizList.clear()

            /* Here we are fetching subcollections of "quizes" and giving one by one values to initialize Quiz instances
            * And then appending all the Quizes in quizList */
            quizList.addAll(value.toObjects(Quiz::class.java))
            /* Must Remember the value keys must be match to keys in Quiz data model */

            adapter.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclerView() {
        /* Initialize adapter with this */
        adapter = QuizAdapter(this, quizList)

        /* We are using Grid Layout with 2 columns */
        binding.quizRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.quizRecyclerView.adapter = adapter
    }

    private fun setUpDrawerLayout() {
        /*Set up for actionBar*/
        setSupportActionBar(binding.appBar)

        /* Here we set main Layout where we need to show drawer */
        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.mainDrawer, R.string.app_name, R.string.app_name)

        /* Synchronise the state of the drawer */
        actionBarDrawerToggle.syncState()

        /* Functionality when we click on buttons in navigationDrawer*/
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                /* Redirected to Profile Activity */
                R.id.btnProfile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                /* Redirected to RankList Activity */
                R.id.btnRankList -> {
                    val intent = Intent(this, RankList::class.java)
                    startActivity(intent)
                }
                /* Redirected to Whatsapp App for Any Help */
                R.id.btnHelp -> {
                    val phoneNumber = "9527458833"
                    val message = "Hello,\n\nI need some help with your app. Here are the details:\n\n[Describe the issue here]\n\nThank you!"
                    val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                        setPackage("com.whatsapp")
                    }
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            binding.mainDrawer.closeDrawers()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}