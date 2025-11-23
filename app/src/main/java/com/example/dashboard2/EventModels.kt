package com.example.dashboard2.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.example.dashboard2.R
import kotlinx.parcelize.Parcelize

/**
 * Data class untuk Event Registration
 */
@Parcelize  // ✅ Add Parcelable
data class EventRegistration(
    val eventId: String = "",
    val eventTitle: String = "",
    val eventDate: String = "",
    val eventLocation: String = "",
    val eventLatitude: Double = 0.0,
    val eventLongitude: Double = 0.0,
    val userId: String = "",
    val userEmail: String = "",
    val userName: String = "",
    val registeredAt: Long = System.currentTimeMillis(),
    val status: String = "registered"
) : Parcelable {  // ✅ Implement Parcelable
    // Empty constructor untuk Firestore
    constructor() : this(
        eventId = "",
        eventTitle = "",
        eventDate = "",
        eventLocation = "",
        eventLatitude = 0.0,
        eventLongitude = 0.0,
        userId = "",
        userEmail = "",
        userName = "",
        registeredAt = 0L,
        status = "registered"
    )

    fun getDocumentId(): String = "${userId}_$eventId"
}

/**
 * Data class untuk Event Item
 */
@Parcelize  // ✅ Add Parcelable
data class EventItem(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @DrawableRes val imageRes: Int = 0,
    val imageUrl: String = "",
    val category: String = "Workshop",
    val description: String = "",
    val price: Double = 0.0,
    val capacity: Int = 0,
    val registeredCount: Int = 0,
    val createdAt: Long = 0L
) : Parcelable {  // ✅ Implement Parcelable
    constructor() : this(
        id = "",
        title = "",
        date = "",
        time = "",
        location = "",
        latitude = 0.0,
        longitude = 0.0,
        imageRes = 0,
        imageUrl = "",
        category = "Workshop",
        description = "",
        price = 0.0,
        capacity = 0,
        registeredCount = 0,
        createdAt = 0L
    )
}

/**
 * Data class untuk Event dengan Distance
 */
data class EventWithDistance(
    val event: EventItem,
    val distanceKm: Double,
    val distanceText: String
) {
    companion object {
        fun fromEvent(event: EventItem): EventWithDistance {
            return EventWithDistance(
                event = event,
                distanceKm = 0.0,
                distanceText = "Lokasi tidak tersedia"
            )
        }
    }
}

/**
 * Complete Event Data Model
 */
data class EventData(
    val id: String,
    val title: String,
    @DrawableRes val imageRes: Int,
    val imageUrl: String = "",
    val date: String,
    val time: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val category: String,
    val price: Double,
    val capacity: Int,
    val registeredCount: Int = 0,
    val organizer: String = "FinSmart",
    val tags: List<String> = emptyList()
) {
    // ✅ ADD THIS EXTENSION FUNCTION
    fun toEventItem(): EventItem {
        return EventItem(
            id = this.id,
            title = this.title,
            date = this.date,
            time = this.time,
            location = this.location,
            latitude = this.latitude,
            longitude = this.longitude,
            imageRes = this.imageRes,
            imageUrl = this.imageUrl,
            category = this.category,
            description = this.description,
            price = this.price,
            capacity = this.capacity,
            registeredCount = this.registeredCount,
            createdAt = System.currentTimeMillis()
        )
    }
}

/**
 * Event Database
 */
object EventDatabase {
    private val events = listOf(
        EventData(
            id = "event_1",
            title = "Talk Show Investasi Saham : Update terbaru saham 2025",
            imageRes = R.drawable.event1,
            date = "Saturday, 18 July 2025",
            time = "09:00 - 12:00 WIB",
            location = "Samantha Krida UB",
            latitude = -7.95294,
            longitude = 112.61608,
            description = "Workshop intensif tentang investasi saham untuk pemula hingga menengah. Pelajari strategi investasi terkini dan analisis pasar saham 2025.",
            category = "Invest",
            price = 0.0,
            capacity = 100,
            organizer = "FinSmart Community",
            tags = listOf("Investment", "Stock Market", "Financial Literacy")
        ),
        EventData(
            id = "event_2",
            title = "Financial Planning Workshop",
            imageRes = R.drawable.event2,
            date = "Monday, 20 July 2025",
            time = "14:00 - 17:00 WIB",
            location = "Hotel Tugu Malang",
            latitude = -7.97718,
            longitude = 112.63325,
            description = "Perencanaan keuangan untuk masa depan yang lebih baik. Belajar membuat budget, menabung, dan berinvestasi dengan bijak.",
            category = "Planning",
            price = 50000.0,
            capacity = 50,
            organizer = "Financial Experts Indonesia",
            tags = listOf("Financial Planning", "Budgeting", "Investment")
        ),
        EventData(
            id = "event_3",
            title = "Cryptocurrency Investment Seminar",
            imageRes = R.drawable.event3,
            date = "Wednesday, 22 July 2025",
            time = "18:00 - 21:00 WIB",
            location = "Gedung Rektorat UB",
            latitude = -7.95220,
            longitude = 112.61296,
            description = "Memahami dunia cryptocurrency dan blockchain. Peluang investasi di era digital dengan teknologi terkini.",
            category = "Crypto",
            price = 0.0,
            capacity = 150,
            organizer = "Crypto Indonesia",
            tags = listOf("Cryptocurrency", "Blockchain", "Digital Investment")
        ),
        EventData(
            id = "event_4",
            title = "Seminar Investasi Properti: Strategi Cerdas di 2025",
            imageRes = R.drawable.event1,
            date = "Friday, 25 July 2025",
            time = "10:00 - 13:00 WIB",
            location = "Hotel Mulia Senayan, Jakarta",
            latitude = -6.21877,
            longitude = 106.80206,
            description = "Pelajari cara membangun portofolio properti yang menguntungkan. Dapatkan insight dari para ahli properti nasional mengenai tren pasar 2025.",
            category = "Property",
            price = 75000.0,
            capacity = 200,
            organizer = "Indonesia Property Forum",
            tags = listOf("Property", "Investment", "Real Estate")
        ),
        EventData(
            id = "event_5",
            title = "Smart Investing for Millennials",
            imageRes = R.drawable.event2,
            date = "Sunday, 27 July 2025",
            time = "13:00 - 16:00 WIB",
            location = "Santika Premiere Hotel, Yogyakarta",
            latitude = -7.78289,
            longitude = 110.36708,
            description = "Khusus untuk generasi muda! Temukan strategi investasi efektif untuk mencapai kebebasan finansial lebih cepat di era digital.",
            category = "Finance",
            price = 30000.0,
            capacity = 120,
            organizer = "Young Investors Club",
            tags = listOf("Millennial", "Investment", "Personal Finance")
        ),
        EventData(
            id = "event_6",
            title = "Forum Investasi Syariah Nasional 2025",
            imageRes = R.drawable.event3,
            date = "Tuesday, 29 July 2025",
            time = "08:00 - 12:00 WIB",
            location = "Trans Studio Convention Centre, Bandung",
            latitude = -6.92656,
            longitude = 107.63442,
            description = "Pelajari prinsip investasi syariah dan bagaimana menerapkannya dalam dunia modern. Cocok bagi investor yang ingin berinvestasi sesuai nilai-nilai Islam.",
            category = "Syariah",
            price = 0.0,
            capacity = 300,
            organizer = "OJK Syariah & Sharia Investment Forum",
            tags = listOf("Syariah", "Halal Investment", "Finance")
        )
    )

    fun getAllEvents(): List<EventItem> {
        return events.map { eventData ->
            EventItem(
                id = eventData.id,
                title = eventData.title,
                date = eventData.date,
                time = eventData.time,
                location = eventData.location,
                latitude = eventData.latitude,
                longitude = eventData.longitude,
                imageRes = eventData.imageRes,
                imageUrl = eventData.imageUrl,
                category = eventData.category,
                description = eventData.description,
                price = eventData.price,
                capacity = eventData.capacity,
                registeredCount = 0,
                createdAt = System.currentTimeMillis()
            )
        }
    }

    fun getEventById(eventId: String): EventItem? {
        return getAllEvents().find { it.id == eventId }
    }

    fun getEventsByCategory(category: String): List<EventItem> {
        return getAllEvents().filter { it.category == category }
    }

    fun searchEvents(query: String): List<EventItem> {
        return getAllEvents().filter { event ->
            event.title.contains(query, ignoreCase = true) ||
                    event.description.contains(query, ignoreCase = true) ||
                    event.category.contains(query, ignoreCase = true)
        }
    }
}