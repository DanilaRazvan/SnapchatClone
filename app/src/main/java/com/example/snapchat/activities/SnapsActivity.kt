package com.example.snapchat.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.snapchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_snaps.*

class SnapsActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var snapsListView: ListView
    private var emails = ArrayList<String>()

    private var snaps = ArrayList<DataSnapshot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)
        setSupportActionBar(toolbar)

        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapsListView.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("users").child(mAuth.currentUser?.uid!!)
            .child("snaps").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    emails.add(p0.child("from").value as String)
                    snaps.add(p0)
                    adapter.notifyDataSetChanged()
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    for ((index, snap) in snaps.withIndex()) {
                        if (snap.key == p0.key) {
                            snaps.removeAt(index)
                            emails.removeAt(index)
                            adapter.notifyDataSetChanged()
                            break
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            })

        snapsListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapshot = snaps[i]

            val intent = Intent(this, ViewSnapActivity::class.java)
            intent.putExtra("imageName", snapshot.child("imageName").value as String)
            intent.putExtra("imageUrl", snapshot.child("imageUrl").value as String)
            intent.putExtra("message", snapshot.child("message").value as String)
            intent.putExtra("snapKey", snapshot.key)
            startActivity(intent)
        }

        fab.setOnClickListener {
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val infalter = menuInflater
        infalter.inflate(R.menu.snaps, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.createSnap -> {
                val intent = Intent(this, CreateSnapActivity::class.java)
                startActivity(intent)
            }

            R.id.logout -> {
                mAuth.signOut()
                finish()
            }

            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut()
    }

}
