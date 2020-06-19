package au.com.letsjoinin.android

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import au.com.letsjoinin.android.UI.adapter.ChatAdapter
import com.google.android.gms.tasks.OnCanceledListener
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    private lateinit var adapter: ChatAdapter
    val list: ArrayList<GroupCommentsFireBase> = ArrayList()
    private lateinit var myRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("FbaseChat")
        listenForMessages()

        send_button_chat_log.setOnClickListener {
            send_button_chat_log.setText("Stop")
            send_button_chat_log.isEnabled = false;
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }
    }

    private fun listenForMessages() {
//        adapter = ChatAdapter(null, applicationContext,null)
        recyclerview_chat_log.adapter = adapter
        myRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val value = dataSnapshot.getValue(GroupCommentsFireBase::class.java)
                if (value != null) {
                    list.add(value)
                }
//                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }
    var i =0;
    var mHandler = Handler()
    fun stopRepeatingTask() {
        mHandler.removeCallbacks(runnableCode)
    }
    val runnableCode = object: Runnable {
        override fun run() {
            mHandler.postDelayed(this, 2000)
            val text = "A's Comment " + i
            val groupCommentsFireBase = GroupCommentsFireBase()
            groupCommentsFireBase.CommentText = text;
            groupCommentsFireBase.LJIID = "A";
            val mGroupId = myRef.child("FbaseChat").push()
            myRef.child(mGroupId.key.toString()).setValue(groupCommentsFireBase).addOnSuccessListener {
                i = i+1
                edittext_chat_log.text.clear()
                list.add(groupCommentsFireBase)
//                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                adapter.notifyDataSetChanged()
            }.addOnCanceledListener(OnCanceledListener {
            })

        }
    }

    private fun performSendMessage() {

        runnableCode.run()



    }
}
