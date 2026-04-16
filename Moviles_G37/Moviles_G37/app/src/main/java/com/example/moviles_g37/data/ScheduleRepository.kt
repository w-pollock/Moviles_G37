package com.example.moviles_g37.data

import android.util.Log
import com.example.moviles_g37.ui.screens.ScheduleItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScheduleRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: "anonymous"
    }

    private fun defaultSchedule() = listOf(
        ScheduleItem(
            subject = "Mobile Development",
            time = "9:30 AM - 10:50 AM",
            room = "ML-603",
            professor = "Prof. Linares"
        ),
        ScheduleItem(
            subject = "Algorithms",
            time = "11:00 AM - 12:20 PM",
            room = "SD-805",
            professor = "Prof. Pérez"
        )
    )

    suspend fun getSchedule(): List<ScheduleItem>{
        return try {
            val userId = getUserId()
            val snapshot = db
                .collection("users")
                .document(userId)
                .collection("schedule")
                .get()
                .await()

            val schedule = snapshot.documents.mapNotNull { doc ->
                ScheduleItem(
                    subject = doc.getString("subject") ?: return@mapNotNull null,
                    time = doc.getString("time") ?: return@mapNotNull null,
                    room = doc.getString("room") ?: return@mapNotNull null,
                    professor = doc.getString("professor") ?: return@mapNotNull null
                )
            }

            if (schedule.isEmpty()){
                Log.d("ScheduleRepository", "No schedule in Firestore, using defaults")
                defaultSchedule()
            } else {
                Log.d("ScheduleRepository", "Loaded ${schedule.size} classes from Firestore")
                schedule
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error getting schedule: ${e.message}")
            defaultSchedule()
        }
    }
}