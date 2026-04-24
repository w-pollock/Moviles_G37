package com.example.moviles_g37.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar


class ScheduleRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userId(): String = auth.currentUser?.uid ?: "anonymous"

    suspend fun getSchedule(): List<ScheduleEntry> {
        return try {
            val snapshot = db.collection("users")
                .document(userId())
                .collection("schedule")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                ScheduleEntry(
                    id = doc.id,
                    subject = doc.getString("subject") ?: return@mapNotNull null,
                    dayOfWeek = (doc.getLong("dayOfWeek") ?: Calendar.MONDAY.toLong()).toInt(),
                    startHour = (doc.getLong("startHour") ?: 0L).toInt(),
                    startMinute = (doc.getLong("startMinute") ?: 0L).toInt(),
                    endHour = (doc.getLong("endHour") ?: 0L).toInt(),
                    endMinute = (doc.getLong("endMinute") ?: 0L).toInt(),
                    room = doc.getString("room") ?: "",
                    professor = doc.getString("professor") ?: ""
                )
            }.sortedWith(compareBy({ it.dayOfWeekSortKey() }, { it.startTotalMinutes }))
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error getting schedule: ${e.message}")
            emptyList()
        }
    }

    suspend fun addOrUpdate(entry: ScheduleEntry): ScheduleEntry {
        return try {
            val collection = db.collection("users")
                .document(userId())
                .collection("schedule")

            val data = hashMapOf<String, Any?>(
                "subject" to entry.subject,
                "dayOfWeek" to entry.dayOfWeek,
                "startHour" to entry.startHour,
                "startMinute" to entry.startMinute,
                "endHour" to entry.endHour,
                "endMinute" to entry.endMinute,
                "room" to entry.room,
                "professor" to entry.professor
            )

            if (entry.id.isBlank()) {
                val ref = collection.add(data).await()
                entry.copy(id = ref.id)
            } else {
                collection.document(entry.id).set(data).await()
                entry
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error saving entry: ${e.message}")
            entry
        }
    }

    suspend fun delete(id: String) {
        try {
            db.collection("users").document(userId())
                .collection("schedule").document(id).delete().await()
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error deleting entry: ${e.message}")
        }
    }


    private fun ScheduleEntry.dayOfWeekSortKey(): Int =
        if (dayOfWeek == Calendar.SUNDAY) 8 else dayOfWeek
}
