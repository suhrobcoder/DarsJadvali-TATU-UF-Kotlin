package uz.suhrob.darsjadvalitatuuf.di

import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module
import uz.suhrob.darsjadvalitatuuf.data.database.AppDatabase
import uz.suhrob.darsjadvalitatuuf.data.pref.AppPref
import uz.suhrob.darsjadvalitatuuf.data.repository.Repository

val appModule = module {
    single { FirebaseFirestore.getInstance() }
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "app.db")
            .build()
    }
    factory { get<AppDatabase>().getLessonDao() }
    factory { AppPref(get()) }
    factory { Repository(get(), get(), get()) }
}