package com.example.firebasetesting

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.R
import com.example.firebasetesting.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.btnLogOut.setOnClickListener {
            logoutUser()
            checkLoggedInState()
        }
        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile(){
        val user = auth.currentUser
        user?.let {user ->
            val userName = binding.username.text.toString()
            val photoUri = Uri.parse("android.resource://$packageName/${com.example.firebasetesting.R.drawable.ic_profile}")
            val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(userName).setPhotoUri(photoUri).build()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdate).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity,"Successfully Updated user Profile",Toast.LENGTH_LONG).show()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun registerUser() {
        val email = binding.etEmailRegister.text.toString()
        val password = binding.etPasswordRegister.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }

    private fun logoutUser(){
        auth.signOut()
    }

    private fun loginUser() {
        val email = binding.etEmailLogin.text.toString()
        val password = binding.etPasswordLogin.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }

    private fun checkLoggedInState() {
        val user = auth.currentUser
        if (user == null){
            binding.tvLoggedIn.text = "You are not logged in"
        }
        else{
            binding.tvLoggedIn.text = "You are logged in"
            binding.username.setText(user.displayName)
            binding.profile.setImageURI(user.photoUrl)
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }
}