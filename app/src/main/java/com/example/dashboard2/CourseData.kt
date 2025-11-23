package com.example.dashboard2

// ✅ Complete Course Data Model
data class CourseData(
    val id: Int,
    val title: String,
    val imageRes: Int,
    val imageLargeRes: Int,
    val price: String,
    val rating: String,
    val description: String,
    val category: String,
    val lessons: List<CourseLesson>
)

data class CourseLesson(
    val id: Int,
    val title: String,
    val duration: String,
    val videoUrl: String
)

// ✅ Database semua course (Single Source of Truth)
object CourseDatabase {
    fun getAllCourses(): List<CourseData> = listOf(
        // Course 1: Beginner Investor (Default enrolled)
        CourseData(
            id = 1,
            title = "Beginner Investor",
            imageRes = R.drawable.investor,
            imageLargeRes = R.drawable.belajarsaham_large,
            price = "Free",
            rating = "4.9",
            description = """Course ini dirancang khusus untuk kamu yang belum pernah berinvestasi sama sekali dan ingin mulai membangun pondasi keuangan yang kuat. Dengan pendekatan yang sederhana, sistematis, dan praktis, kamu akan dipandu langkah demi langkah untuk memahami dunia investasi, mulai dari konsep dasar hingga cara memilih instrumen investasi yang paling cocok dengan tujuan dan profil risikomu.
Materi disusun secara bertahap dan terstruktur, menggunakan bahasa yang mudah dipahami oleh siapa pun, bahkan jika kamu tidak memiliki latar belakang keuangan.""",
            category = "Reksadana",  // ✅ ASSIGNED
            lessons = listOf(
                CourseLesson(1, "Dasar-dasar Investasi", "04.30 min", "https://youtu.be/xDiRHt9vlqI?si=BCvvuappDw_q0gJ0"),
                CourseLesson(2, "Mengenal Instrumen Investasi", "04.30 min", "https://youtu.be/xDiRHt9vlqI?si=BCvvuappDw_q0gJ0"),
                CourseLesson(3, "Menentukan Tujuan Investasi", "15.00 min", "https://youtu.be/oU40sOr3alg?si=GKidO8QZtnb-SlQi"),
                CourseLesson(4, "Mulai Berinvestasi", "08.00 min", "https://youtu.be/XQwUFRKLevg?si=3LZFJEzKh19oZtLN")
            )
        ),

        // Course 2: Belajar Saham dari Nol
        CourseData(
            id = 2,
            title = "Belajar saham dari nol",
            imageRes = R.drawable.belajarsaham,
            imageLargeRes = R.drawable.belajarsaham_large,
            price = "Free",
            rating = "4.9",
            description = """Course ini akan membimbingmu untuk memahami dunia saham dari dasar. Kamu akan belajar tentang apa itu saham, bagaimana cara kerjanya, dan strategi untuk memulai investasi saham dengan aman dan menguntungkan. Cocok untuk pemula yang ingin terjun ke pasar modal.""",
            category = "Saham",  // ✅ ASSIGNED
            lessons = listOf(
                CourseLesson(1, "Memahami Apa Itu Saham", "07.33 min", "https://youtu.be/uGzToPCX8nU?si=m5_vREfE-ChJHvbo"),
                CourseLesson(2, "Cara Membeli dan Menjual Saham", "21.04 min", "https://youtu.be/uMjzuBwoIRM?si=8e4vbqHISchWdFaF"),
                CourseLesson(3, "Analisis Fundamental Saham", "15.25 min", "https://youtu.be/N4L2e7vr6OM?si=1zHcP04hSYwurUsT"),
                CourseLesson(4, "Strategi Trading Saham", "13.45 min", "https://youtu.be/jW-5J9dzyvc?si=QvWq-3EECtbFhUBH")
            )
        ),

        // Course 3: Reksadana Pemula
        CourseData(
            id = 3,
            title = "Reksadana pemula",
            imageRes = R.drawable.reksadanapemula,
            imageLargeRes = R.drawable.reksadanapemula_large,
            price = "Free",
            rating = "4.8",
            description = """Pelajari tentang reksadana, instrumen investasi yang cocok untuk pemula. Course ini akan mengajarkanmu jenis-jenis reksadana, cara memilih yang tepat, dan strategi investasi jangka panjang yang menguntungkan.""",
            category = "Reksadana",  // ✅ ASSIGNED
            lessons = listOf(
                CourseLesson(1, "Apa Itu Reksadana", "14.40 min", "https://youtu.be/ySolMNPZm7s?si=V9hNEkKbaNhjjBkv"),
                CourseLesson(2, "Jenis-jenis Reksadana", "09.00 min", "https://youtu.be/5zTaOoew-vg?si=Vu8SAzU3dUmdPGgr"),
                CourseLesson(3, "Cara Memilih Reksadana", "09.36 min", "https://youtu.be/5HuiFzoVOkU?si=0zf9jya-HOdJBg57"),
                CourseLesson(4, "Cara Hidup dari Return Reksadana", "09.25 min", "https://youtu.be/zb3ar_kpxcU?si=WbWJGVSS60RUMJMR")
            )
        ),

        // Course 4: Investasi Reksadana
        CourseData(
            id = 4,
            title = "Investasi Reksadana",
            imageRes = R.drawable.investasireksadana,
            imageLargeRes = R.drawable.investasireksadana_large,
            price = "Free",
            rating = "4.7",
            description = """Course lanjutan untuk yang ingin mendalami strategi investasi reksadana. Belajar tentang diversifikasi portfolio, monitoring investasi, dan cara memaksimalkan return dari reksadana.""",
            category = "Reksadana",  // ✅ ASSIGNED
            lessons = listOf(
                CourseLesson(1, "Strategi Investasi Reksadana", "09.06 min", "https://youtu.be/1pI_DSpYJYA?si=FrcOivQNFlRzDHA9"),
                CourseLesson(2, "Diversifikasi Portfolio", "48.49 min", "https://youtu.be/s2b6UygLY28?si=LSOCZQTmsf_GOQi8"),
                CourseLesson(3, "Monitoring Investasi", "05.00", "https://youtu.be/PfW30m2PdkY?si=OTtXqeznZ2cgtE3u"),
                CourseLesson(4, "Rebalancing Portfolio", "12.22 min", "https://youtu.be/OgEC4MoQ5fk?si=Bo5Wn7hO7uW-CTGj")
            )
        ),

        // Course 5: Mengatur Gaji Gen Z
        CourseData(
            id = 5,
            title = "Mengatur Gaji Gen Z",
            imageRes = R.drawable.mengaturgaji,
            imageLargeRes = R.drawable.mengaturgaji_large,
            price = "Free",
            rating = "4.9",
            description = """Khusus untuk Gen Z yang baru mulai bekerja! Pelajari cara mengatur gaji dengan smart, budgeting yang efektif, dan tips menabung sambil tetap menikmati hidup. Plus, ide-ide side hustle untuk income tambahan.""",
            category = "Reksadana",  // ✅ ASSIGNED (Financial Planning masuk Reksadana)
            lessons = listOf(
                CourseLesson(1, "Budgeting untuk Pemula", "07.40 min", "https://youtu.be/9O73aOoper0?si=RwdEUnCb-xdLz8Bd"),
                CourseLesson(2, "Menabung dengan Smart", "52.48 min", "https://www.youtube.com/live/pu46-PLjZSQ?si=azzUk2VP--5okQXF"),
                CourseLesson(3, "Side Hustle Ideas", "20.55 min", "https://youtu.be/EJFxY_tMY70?si=T7GH9tAJprU-QyWi"),
                CourseLesson(4, "Tips Bebas Finansial", "20.24 min", "https://youtu.be/-QFzlLfl7IE?si=nXnBtFzJLs-ziBAZ")
            )
        ),

        // Course 6: Belajar Crypto dari 0
        CourseData(
            id = 6,
            title = "Belajar Crypto dari 0",
            imageRes = R.drawable.belajarcrypto,
            imageLargeRes = R.drawable.belajarcrypto_large,
            price = "Free",
            rating = "4.6",
            description = """Masuki dunia cryptocurrency dengan pemahaman yang benar. Course ini akan mengajarkan dasar-dasar crypto, blockchain technology, dan cara memulai investasi crypto dengan aman.""",
            category = "Crypto",  // ✅ ASSIGNED
            lessons = listOf(
                CourseLesson(1, "Apa Itu Cryptocurrency", "03.51 min", "https://youtu.be/leI9VOpNY1w?si=qt63ZTjO65u0mbzL"),
                CourseLesson(2, "Blockchain Technology", "03.32 min", "https://youtu.be/pVGGOn_ntdg?si=mvQMnxU47uIq3eam"),
                CourseLesson(3, "Trading Crypto Basics", "35.35 min", "https://youtu.be/tdUuyhZH5wI?si=nLMj9ILPA4ae-sAv"),
                CourseLesson(4, "Wallet dan Security", "03.49 min", "https://youtu.be/mMJdOBuNP70?si=V60Q2VDfQ_tNvmaL")
            )
        ),

        // Course 7: Crypto Trading untuk Pemula
        CourseData(
            id = 7,
            title = "Crypto Trading untuk Pemula",
            imageRes = R.drawable.cryptotrading,
            imageLargeRes = R.drawable.cryptotrading_large,
            price = "Free",
            rating = "4.7",
            description = """Course advanced untuk crypto trading. Pelajari technical analysis, risk management, dan strategi trading yang profitable di pasar crypto yang volatile.""",
            category = "Crypto",  // ✅ ASSIGNED
            lessons = listOf(
                CourseLesson(1, "Setup Trading Account", "32.25 min", "https://youtu.be/SPYmdfHPFs4?si=8I5W_2Uxn5dK99pz"),
                CourseLesson(2, "Technical Analysis", "28.21 min", "https://youtu.be/fQ0b3uaT4zs?si=TVsqKiUEISIzncbk"),
                CourseLesson(3, "Risk Management", "06.38 min", "https://youtu.be/a-Om1L8ZA0M?si=pZ0EhvjkY-OGPqDI"),
                CourseLesson(4, "Trading Strategy", "12.00 min", "https://youtu.be/0-3-g46NPAA?si=I-30GGjNQRQjoqSk")
            )
        )
    )

    // ✅ Get course by ID
    fun getCourseById(courseId: Int): CourseData? {
        return getAllCourses().find { it.id == courseId }
    }

    // ✅ Get multiple courses by IDs
    fun getCoursesByIds(courseIds: List<Int>): List<CourseData> {
        return getAllCourses().filter { it.id in courseIds }
    }

    // ✅ NEW: Get courses by category (Dynamic filtering)
    fun getCoursesByCategory(category: String): List<CourseData> {
        return getAllCourses().filter {
            it.category.equals(category, ignoreCase = true)
        }
    }
}