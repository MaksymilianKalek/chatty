package com.makscorp.chatty

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendBtn: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var coordinateTxt: TextView
    private lateinit var db: DatabaseReference
    private lateinit var locationManager: LocationManager
    private var locationRefreshTime: Long = 15000 // 15 seconds to update
    private var locationRefreshDistance: Float = 10f // 500 meters to update

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val username = intent.getStringExtra("username")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = username

        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageTxt)
        sendBtn = findViewById(R.id.sendBtn)
        coordinateTxt = findViewById(R.id.coordinates)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        db =
            FirebaseDatabase.getInstance("https://chatty-400fc-default-rtdb.europe-west1.firebasedatabase.app").reference

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        var longitude: Double = .0
        var latitude: Double = .0

        val mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                longitude = location.longitude
                latitude = location.latitude
            }
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

        db.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    val latestMessage = messageList.last()
                    coordinateTxt.text = "Lat: ${latestMessage.latitude} Long: ${latestMessage.longitude}"
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        sendBtn.setOnClickListener {
            val message = messageInput.text.toString()
            val messageObj = Message(message, senderUid, latitude, longitude)

            db.child("chats").child(senderRoom!!).child("messages").push().setValue(messageObj)
                .addOnSuccessListener {
                    db.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObj)
                }
            messageInput.setText("")
        }
    }
}