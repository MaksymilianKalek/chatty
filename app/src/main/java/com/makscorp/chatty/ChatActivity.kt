package com.makscorp.chatty

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import java.util.concurrent.Executor

class ChatActivity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendBtn: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var db: DatabaseReference
    private lateinit var locationManager: LocationManager
    private lateinit var senderUid: String
    private var locationRefreshTime: Long = 5000 // 5 seconds to update
    private var locationRefreshDistance: Float = 10f // 500 meters to update
    private var longitude = .0
    private var latitude = .0
    private lateinit var executor: Executor

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initializeVariables()
        updateLocation()

        listenForKeyboardOpen()
        listenForMessageSent()
        listenForMessageReceived()
    }

    private fun listenForKeyboardOpen() {
        messageRecyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                messageRecyclerView.post {
                    if (messageList.isNotEmpty()) {
                        messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    private fun initializeVariables() {
        val username = intent.getStringExtra("username")
        val receiverUid = intent.getStringExtra("uid")

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = username

        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageTxt)
        sendBtn = findViewById(R.id.sendBtn)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        db =
            FirebaseDatabase.getInstance("https://chatty-400fc-default-rtdb.europe-west1.firebasedatabase.app").reference

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        executor = Executor { sendMessage() }
    }

    private fun listenForMessageSent() {
        sendBtn.setOnClickListener {
            executor.execute {
                sendMessage()
            }
        }
    }

    private fun sendMessage() {
        val message = messageInput.text.toString().trim()
        var location = ""
        if (message.isNotEmpty()) {
            if (latitude != .0 || longitude != .0) {
                try {
                    val geo = Geocoder(this, Locale.getDefault())
                    val addresses =
                        geo.getFromLocation(latitude, longitude, 1)
                    if (addresses.isNotEmpty()) {
                        val address = addresses.first()
                        location =
                            "${address.thoroughfare ?: ""} ${address.subThoroughfare ?: ""}, ${address.postalCode} ${address.locality},  ${address.countryCode}"
                    }
                } catch (e: Exception) {
                    println(e)
                }
            }
            val messageObj = Message(message, senderUid, location)

            db.child("chats").child(senderRoom!!).child("messages").push().setValue(messageObj)
                .addOnSuccessListener {
                    db.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObj)
                }
            messageInput.setText("")
        }
    }

    private fun listenForMessageReceived() {
        db.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyItemInserted(messageList.lastIndex)
                    if (messageList.isNotEmpty()) {
                        messageRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun updateLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val mLocationListener = LocationListener { location ->
            longitude = location.longitude
            latitude = location.latitude
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                locationRefreshTime,
                locationRefreshDistance,
                mLocationListener
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1337
            )
        }
    }
}