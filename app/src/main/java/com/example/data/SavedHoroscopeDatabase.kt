package com.example.data

import android.content.Context
import androidx.room.*
import com.example.model.BirthData
import com.example.model.Gender
import kotlinx.coroutines.flow.Flow

/**
 * Privacy-First Optional Horoscope Storage.
 * IMPORTANT: Nothing is ever saved automatically or by default.
 * Records are inserted ONLY when the user explicitly confirms "സംരക്ഷിക്കുക" in the opt-in dialog.
 */
@Entity(tableName = "saved_horoscopes")
data class SavedHoroscopeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val genderName: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val placeNameMalayalam: String,
    val placeNameEnglish: String,
    val districtMalayalam: String,
    val districtEnglish: String,
    val state: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String,
    val timezoneOffsetHours: Double,
    val lagnaMalayalam: String,
    val rashiMalayalam: String,
    val nakshatraMalayalam: String,
    val savedAtEpochMillis: Long = System.currentTimeMillis()
) {
    fun toBirthData(): BirthData = BirthData(
        name = name,
        gender = Gender.entries.find { it.name == genderName } ?: Gender.MALE,
        year = year,
        month = month,
        day = day,
        hour = hour,
        minute = minute,
        second = second,
        placeNameMalayalam = placeNameMalayalam,
        placeNameEnglish = placeNameEnglish,
        districtMalayalam = districtMalayalam,
        districtEnglish = districtEnglish,
        state = state,
        country = country,
        latitude = latitude,
        longitude = longitude,
        timezoneId = timezoneId,
        timezoneOffsetHours = timezoneOffsetHours
    )
}

@Dao
interface SavedHoroscopeDao {
    @Query("SELECT * FROM saved_horoscopes ORDER BY savedAtEpochMillis DESC")
    fun getAllSavedHoroscopes(): Flow<List<SavedHoroscopeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedHoroscope(entity: SavedHoroscopeEntity): Long

    @Query("DELETE FROM saved_horoscopes WHERE id = :id")
    suspend fun deleteHoroscopeById(id: Long)

    @Query("DELETE FROM saved_horoscopes")
    suspend fun deleteAllHoroscopeData()
}

@Database(entities = [SavedHoroscopeEntity::class], version = 1, exportSchema = false)
abstract class AstraPrivacyDatabase : RoomDatabase() {
    abstract fun savedHoroscopeDao(): SavedHoroscopeDao

    companion object {
        @Volatile
        private var INSTANCE: AstraPrivacyDatabase? = null

        fun getInstance(context: Context): AstraPrivacyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AstraPrivacyDatabase::class.java,
                    "astra_optional_saved_jathakam.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class HoroscopePrivacyRepository(private val dao: SavedHoroscopeDao) {
    val savedHoroscopes: Flow<List<SavedHoroscopeEntity>> = dao.getAllSavedHoroscopes()

    suspend fun saveHoroscopeExplicitly(
        birthData: BirthData,
        lagnaMal: String,
        rashiMal: String,
        nakshatraMal: String
    ) {
        dao.insertSavedHoroscope(
            SavedHoroscopeEntity(
                name = birthData.name,
                genderName = birthData.gender.name,
                year = birthData.year,
                month = birthData.month,
                day = birthData.day,
                hour = birthData.hour,
                minute = birthData.minute,
                second = birthData.second,
                placeNameMalayalam = birthData.placeNameMalayalam,
                placeNameEnglish = birthData.placeNameEnglish,
                districtMalayalam = birthData.districtMalayalam,
                districtEnglish = birthData.districtEnglish,
                state = birthData.state,
                country = birthData.country,
                latitude = birthData.latitude,
                longitude = birthData.longitude,
                timezoneId = birthData.timezoneId,
                timezoneOffsetHours = birthData.timezoneOffsetHours,
                lagnaMalayalam = lagnaMal,
                rashiMalayalam = rashiMal,
                nakshatraMalayalam = nakshatraMal
            )
        )
    }

    suspend fun deleteSingleHoroscope(id: Long) {
        dao.deleteHoroscopeById(id)
    }

    suspend fun deleteAllSavedData() {
        dao.deleteAllHoroscopeData()
    }
}
