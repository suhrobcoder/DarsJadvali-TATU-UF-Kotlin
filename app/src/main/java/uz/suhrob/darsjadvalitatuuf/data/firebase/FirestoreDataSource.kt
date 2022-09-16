package uz.suhrob.darsjadvalitatuuf.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uz.suhrob.darsjadvalitatuuf.models.Group

class FirestoreDataSource(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getGroups(): List<Group>? {
        val res = firestore.collection("groups").get().await()
        return if (res.isEmpty) {
            null
        } else {
            res.toObjects(Group::class.java)
        }
    }
}