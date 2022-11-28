package com.example.bookingservice

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registrasi.*
import java.util.*
import kotlin.collections.HashMap

class RegistrasiActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebasefirestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        auth = FirebaseAuth.getInstance()
        firebasefirestore = FirebaseFirestore.getInstance()

        Registrasibtn.setOnClickListener {
            val namausr  = namaEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (namausr.isEmpty() || namausr.length > 9){
                namaEt.error = "Nama terlalu panjang"
                namaEt.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()){
                emailEt.error = "Email tidak boleh kosong!!!!"
                emailEt.requestFocus()
                return@setOnClickListener
            }
//            validasi email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailEt.error = "Email tidak tidak valid"
                emailEt.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6){
                passwordEt.error = "Password harus lebih dari 6 huruf"
                passwordEt.requestFocus()
                return@setOnClickListener
            }

            registeruser(email, password, namausr)
        }

        Login_lagibtn.setOnClickListener {
            Intent(this@RegistrasiActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun registeruser(email: String, password: String, namausr: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
            if (it.isSuccessful()){
                Toast.makeText(this,"User Created.", Toast.LENGTH_SHORT).show()
                val userid:String= auth.currentUser!!.uid
                firebasefirestore.collection("user").document(userid).get()
                val db = Firebase.firestore
                val user = HashMap<String, String>()
                user.put("nama",namausr)
                user.put("email",email)
                user.put("password",password)
                val userRef = db.collection("user")
                userRef.document(userid).set(user)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                Intent(this@RegistrasiActivity, HomeActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }

            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            Intent(this@RegistrasiActivity, HomeActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
}