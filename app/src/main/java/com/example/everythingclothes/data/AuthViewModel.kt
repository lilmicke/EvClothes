package com.example.everythingclothes.data

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.everythingclothes.models.Users
import com.example.everythingclothes.navigation.ADD_PRODUCTS_URL
import com.example.everythingclothes.navigation.HOME_URL
import com.example.everythingclothes.navigation.LOGIN_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel (var navController:NavController,
                     var context:Context){
    val mAuth:FirebaseAuth
    val progress:ProgressDialog
    init {
        mAuth=FirebaseAuth.getInstance()
        progress= ProgressDialog(context)
        progress.setTitle("Loading")
        progress.setMessage("Please wait..")
    }



    // AuthViewModel.kt
    fun signup(name: String, email: String, password: String) {
        progress.show()
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser!!.uid
                val userProfile = Users(name, email, password, userId)

                // Create a reference to the "Users" table inside the Firebase database
                val userRef = FirebaseDatabase.getInstance().getReference().child("Users/$userId")

                userRef.setValue(userProfile).addOnCompleteListener { addTask ->
                    progress.dismiss()
                    if (addTask.isSuccessful) {
                        Toast.makeText(context, "User successfully added", Toast.LENGTH_SHORT).show()
                        navController.navigate(ADD_PRODUCTS_URL)
                    } else {
                        Toast.makeText(context, "Error adding user", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                progress.dismiss()
                Toast.makeText(context, " Welcome Seller", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(email: String, password: String) {
        progress.show()
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progress.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                navController.navigate(HOME_URL)
            } else {
                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun logout(){
        mAuth.signOut()
        navController.navigate(HOME_URL)
    }
    fun isloggedIn():Boolean=mAuth.currentUser !=null

}

