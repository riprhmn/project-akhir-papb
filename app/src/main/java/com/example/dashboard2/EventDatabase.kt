package com.example.dashboard2

import com.example.dashboard2.data.model.EventItem

object EventDatabase {
    private val events = listOf(
        EventItem(
            id = "event_1",
            title = "Talk Show Investasi Saham",
            date = "Saturday, 18 January 2025, 09:00 AM",
            location = "Grand Ballroom, Hotel Tunjungan Surabaya",
            imageRes = R.drawable.event1,
            category = "Workshop",
            description = "Pelajari strategi investasi saham dari para ahli. Cocok untuk pemula hingga investor berpengalaman."
        ),
        EventItem(
            id = "event_2",
            title = "Seminar Pasar Modal",
            date = "Sunday, 19 January 2025, 10:00 AM",
            location = "Aula Utama, Universitas Brawijaya",
            imageRes = R.drawable.event2,
            category = "Seminar",
            description = "Memahami dinamika pasar modal Indonesia dan peluang investasi di tahun 2025."
        ),
        EventItem(
            id = "event_3",
            title = "Smart Finance Live",
            date = "Saturday, 25 January 2025, 14:00 PM",
            location = "Auditorium, Gedung BEI Surabaya",
            imageRes = R.drawable.event3,
            category = "Talk Show",
            description = "Live talk show interaktif tentang perencanaan keuangan pribadi dan keluarga."
        ),
        EventItem(
            id = "event_4",
            title = "Workshop Analisis Fundamental",
            date = "Sunday, 26 January 2025, 09:00 AM",
            location = "Meeting Room 2, Menara Mandiri",
            imageRes = R.drawable.event1,
            category = "Workshop",
            description = "Belajar menganalisis laporan keuangan perusahaan untuk keputusan investasi yang lebih baik."
        )
    )

    /**
     * Get all events
     */
    fun getAllEvents(): List<EventItem> = events

    /**
     * Get event by ID
     */
    fun getEventById(eventId: String): EventItem? {
        return events.find { it.id == eventId }
    }

    /**
     * Get events by category
     */
    fun getEventsByCategory(category: String): List<EventItem> {
        return events.filter { it.category == category }
    }
}