package com.example.dashboard2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class untuk News Item
data class NewsItem(
    val id: Int,
    val category: String,
    val title: String,
    val source: String,
    val date: String,
    val imageRes: Int,
    val logoRes: Int,
    val content: String = "" // Konten berita lengkap
)

// Fungsi helper untuk mendapatkan semua berita
fun getAllNews(): List<NewsItem> {
    return listOf(
        NewsItem(
            id = 0,
            category = "Crypto",
            title = "Bitcoin Tembus Rp1 Miliar: Apakah Saatnya Jual atau Beli?",
            source = "CryptoToday.co.id",
            date = "Juli 18 2025",
            imageRes = R.drawable.bitcoin1m,
            logoRes = R.drawable.logo1,
            content = """Setelah berbulan-bulan mengalami fluktuasi tajam, harga Bitcoin akhirnya menembus angka fantastis: Rp1 miliar per koin. Kenaikan ini memicu euforia di kalangan investor kripto, sekaligus menimbulkan pertanyaan besar — apakah ini saat yang tepat untuk menjual, atau justru membeli?

Kenaikan harga Bitcoin kali ini didorong oleh kombinasi faktor global: meningkatnya adopsi institusional, pengumuman ETF Bitcoin spot di berbagai negara, serta kondisi ekonomi dunia yang mendorong investor mencari aset lindung nilai di luar mata uang fiat. Tak heran jika pasar kripto kembali menggeliat dengan optimisme baru.

Namun, di balik euforia, ada pula kekhawatiran akan potensi koreksi harga. Sejarah mencatat, setiap kali Bitcoin menembus rekor tertinggi, volatilitas ekstrem biasanya mengikuti. Para analis menyarankan agar investor berhati-hati dan menyesuaikan strategi sesuai profil risiko masing-masing.

Bagi sebagian orang, Rp1 miliar dianggap sebagai sinyal jual — momen untuk merealisasikan keuntungan sebelum kemungkinan koreksi besar. Tapi bagi lainnya, ini justru dianggap sebagai awal dari siklus bullish baru, di mana harga Bitcoin bisa melesat lebih tinggi seiring meningkatnya minat pasar dan terbatasnya pasokan.

Pada akhirnya, tidak ada jawaban tunggal. Apakah ini saatnya jual atau beli, bergantung pada seberapa besar keyakinan seseorang terhadap masa depan Bitcoin. Yang pasti, kenaikan ini kembali membuktikan satu hal: kripto tetap menjadi aset yang penuh kejutan dan berpotensi besar bagi mereka yang berani — dan bijak — dalam mengambil keputusan."""
        ),
        NewsItem(
            id = 1,
            category = "Reksadana",
            title = "Reksadana Pasar Uang Makin Diminati: Aman untuk Pemula?",
            source = "FinSmart.id",
            date = "Juli 18 2025",
            imageRes = R.drawable.newsreksadana,
            logoRes = R.drawable.logo2,
            content = """Dalam beberapa tahun terakhir, reksadana pasar uang semakin populer di kalangan investor muda dan pemula. Di tengah ketidakpastian ekonomi dan fluktuasi pasar saham, instrumen ini dianggap sebagai tempat “parkir dana” yang aman dan stabil, sambil tetap memberikan imbal hasil lebih tinggi daripada tabungan biasa.

Reksadana pasar uang merupakan produk investasi yang menempatkan dananya pada instrumen keuangan jangka pendek seperti deposito, Sertifikat Bank Indonesia (SBI), dan obligasi dengan jatuh tempo di bawah satu tahun. Karena risikonya relatif rendah, produk ini sering direkomendasikan bagi mereka yang baru mulai belajar investasi atau memiliki tujuan keuangan jangka pendek.

Tren peningkatan minat ini juga didorong oleh kemudahan akses melalui platform digital. Kini, siapa pun bisa mulai berinvestasi hanya dengan modal puluhan ribu rupiah, tanpa perlu repot membuka rekening efek atau memahami pasar modal secara mendalam.

Meski begitu, penting diingat bahwa reksadana pasar uang bukan berarti tanpa risiko. Nilai investasinya tetap bisa berfluktuasi, terutama jika ada perubahan suku bunga atau kondisi ekonomi tertentu. Namun secara historis, pergerakannya jauh lebih stabil dibandingkan reksadana saham atau campuran.

Bagi investor pemula, reksadana pasar uang bisa menjadi langkah awal yang aman dan realistis untuk belajar berinvestasi. Dengan memahami profil risiko dan tujuan keuangan, produk ini dapat menjadi pondasi yang solid sebelum melangkah ke instrumen dengan potensi (dan risiko) yang lebih tinggi."""
        ),
        NewsItem(
            id = 2,
            category = "Crypto",
            title = "Ethereum Bakal Update Lagi: Apa Dampaknya bagi Investor Retail?",
            source = "BlockVerse.id",
            date = "Juli 18 2025",
            imageRes = R.drawable.newsethereum,
            logoRes = R.drawable.logo1,
            content = """Setelah sukses dengan beberapa pembaruan besar seperti Merge dan Shanghai Upgrade, Ethereum kembali bersiap untuk melakukan update terbaru. Pembaruan ini disebut-sebut akan meningkatkan efisiensi jaringan, menurunkan biaya transaksi (gas fee), dan memperkuat keamanan ekosistem — hal yang tentu menarik perhatian para investor, termasuk investor retail.

Ethereum, sebagai platform blockchain terbesar kedua setelah Bitcoin, memainkan peran penting dalam dunia DeFi (Decentralized Finance), NFT, dan berbagai aplikasi Web3. Maka, setiap pembaruan yang dilakukan bisa berdampak langsung pada ekosistem dan harga ETH itu sendiri.

Bagi investor retail, kabar update ini membawa dua sisi mata uang. Di satu sisi, update besar sering kali memicu sentimen positif, karena diharapkan memperkuat fundamental Ethereum dan menarik lebih banyak proyek baru untuk bergabung ke dalam jaringan. Ini bisa meningkatkan permintaan terhadap ETH dan mendorong kenaikan harga dalam jangka menengah hingga panjang.

Namun di sisi lain, volatilitas jangka pendek juga perlu diwaspadai. Menjelang pembaruan besar, pasar kripto biasanya dipenuhi spekulasi — harga bisa melonjak cepat sebelum update, lalu terkoreksi setelahnya. Investor pemula disarankan untuk tidak terburu-buru membeli hanya karena euforia, melainkan memahami tujuan investasi dan manajemen risikonya.

Secara keseluruhan, update Ethereum ini menjadi langkah lanjutan menuju ekosistem blockchain yang lebih cepat, efisien, dan ramah pengguna. Bagi investor retail, ini bukan sekadar momentum untuk berspekulasi, tetapi juga kesempatan untuk memahami arah masa depan aset digital terbesar kedua di dunia ini."""
        ),
        NewsItem(
            id = 3,
            category = "Saham",
            title = "Saham Teknologi RI Melonjak: Bukti Ekonomi Digital Membaik?",
            source = "Saham360.com",
            date = "Juli 18 2025",
            imageRes = R.drawable.newssahamteknologi,
            logoRes = R.drawable.logo2,
            content = """Pasar saham Indonesia tengah diramaikan oleh kebangkitan sektor teknologi, setelah beberapa emiten digital besar mencatatkan kenaikan harga signifikan dalam beberapa pekan terakhir. Saham-saham seperti GOTO, BUKA, dan EMTK menjadi sorotan utama, seiring meningkatnya optimisme terhadap pemulihan ekonomi digital Tanah Air.

Kenaikan ini tak lepas dari perbaikan kinerja keuangan sejumlah perusahaan teknologi, yang mulai menunjukkan efisiensi dan kemampuan mencapai profitabilitas setelah masa-masa ekspansi besar-besaran. Investor melihat sinyal positif bahwa sektor digital Indonesia kini mulai memasuki fase yang lebih matang dan berkelanjutan.

Selain faktor fundamental, dukungan pemerintah terhadap transformasi digital dan meningkatnya adopsi teknologi di masyarakat juga menjadi katalis penting. Dari layanan e-commerce, transportasi daring, hingga keuangan digital, ekosistem ekonomi berbasis teknologi di Indonesia terus tumbuh pesat.

Namun, analis mengingatkan bahwa volatilitas di sektor teknologi masih tinggi. Sentimen global, terutama dari pergerakan saham teknologi di Amerika Serikat seperti Nasdaq, dapat memengaruhi arah pasar domestik. Karena itu, investor disarankan tetap selektif dan fokus pada emiten dengan model bisnis kuat serta arus kas yang sehat.

Secara keseluruhan, lonjakan saham teknologi menjadi indikasi bahwa kepercayaan terhadap ekonomi digital Indonesia mulai pulih. Jika tren ini berlanjut, bukan tidak mungkin sektor teknologi akan kembali menjadi motor pertumbuhan baru bagi pasar modal nasional — sekaligus bukti bahwa transformasi digital benar-benar mulai membuahkan hasil nyata."""
        ),
        NewsItem(
            id = 4,
            category = "Reksadana",
            title = "Tren ESG di Reksadana: Investasi Ramah Lingkungan Kian Dilirik",
            source = "GreenInvest.co",
            date = "Juli 18 2025",
            imageRes = R.drawable.newstrenesg,
            logoRes = R.drawable.logo1,
            content = """Dalam beberapa tahun terakhir, dunia investasi tidak lagi hanya berfokus pada keuntungan finansial semata. Kini, semakin banyak investor yang menaruh perhatian pada faktor lingkungan, sosial, dan tata kelola (ESG — Environmental, Social, Governance). Di Indonesia, tren ini mulai terlihat jelas di sektor reksadana, di mana produk berbasis ESG semakin banyak bermunculan dan diminati.

Konsep ESG menekankan bahwa investasi seharusnya tidak hanya mengejar profit, tetapi juga memberikan dampak positif bagi bumi dan masyarakat. Reksadana dengan prinsip ESG umumnya menempatkan dananya pada perusahaan yang menerapkan praktik ramah lingkungan, menjaga kesejahteraan karyawan, serta memiliki tata kelola yang transparan dan etis.

Minat investor terhadap reksadana ESG meningkat seiring dengan kesadaran global terhadap isu perubahan iklim dan keberlanjutan ekonomi. Banyak investor muda, terutama generasi milenial dan Gen Z, yang kini lebih selektif dalam memilih produk investasi yang sejalan dengan nilai-nilai sosial dan lingkungan yang mereka dukung.

Dari sisi kinerja, reksadana ESG juga mulai menunjukkan daya tahan yang baik terhadap volatilitas pasar, terutama karena perusahaan-perusahaan yang berkomitmen terhadap keberlanjutan cenderung memiliki manajemen risiko yang lebih kuat dan reputasi yang positif di mata publik.

Namun, penting bagi calon investor untuk tetap memahami portofolio dan strategi pengelolaan dari masing-masing produk ESG, karena tidak semua reksadana dengan label “hijau” memiliki tingkat keberlanjutan yang sama. Melakukan riset dan membaca prospektus tetap menjadi langkah bijak sebelum berinvestasi.

Pada akhirnya, tren ESG di dunia reksadana bukan sekadar tren sesaat — tetapi mencerminkan arah baru dunia investasi yang lebih bertanggung jawab, beretika, dan berkelanjutan. Bagi investor masa kini, ini bukan hanya tentang “uang bekerja untuk kita”, tapi juga tentang bagaimana uang kita bisa bekerja untuk masa depan bumi."""
        ),
        NewsItem(
            id = 5,
            category = "Saham",
            title = "Bursa Saham Dibuka Menguat: Sentimen Positif dari Global Market",
            source = "MarketInsight.id",
            date = "Juli 18 2025",
            imageRes = R.drawable.newsbursasaham,
            logoRes = R.drawable.logo2,
            content = """Bursa saham Indonesia dibuka menguat pada awal perdagangan hari ini, mengikuti jejak positif dari pergerakan bursa global. Indeks Harga Saham Gabungan (IHSG) langsung bergerak di zona hijau, didorong oleh optimisme investor terhadap pemulihan ekonomi global serta meredanya kekhawatiran terhadap inflasi dan kebijakan suku bunga.

Kenaikan bursa global, terutama di Wall Street, memberikan sentimen positif yang kuat ke pasar regional Asia, termasuk Indonesia. Indeks Dow Jones dan Nasdaq yang kompak ditutup menguat menjadi sinyal bahwa investor global mulai kembali masuk ke aset berisiko setelah beberapa pekan ketidakpastian.

Sektor-sektor seperti perbankan, energi, dan konsumer menjadi penopang utama penguatan IHSG pagi ini. Harga komoditas yang stabil, terutama minyak dan batu bara, turut mendorong saham-saham berbasis sumber daya alam untuk bergerak positif. Investor asing juga tercatat melakukan aksi beli bersih (net buy), memperkuat momentum optimisme pasar.

Meski demikian, analis mengingatkan bahwa pergerakan pasar masih bersifat jangka pendek, dan investor disarankan tetap memperhatikan rilis data ekonomi global seperti inflasi AS, keputusan suku bunga The Fed, serta laporan kinerja emiten domestik yang akan datang.

Secara keseluruhan, penguatan bursa kali ini menunjukkan bahwa kepercayaan pasar mulai pulih setelah periode ketidakpastian yang panjang. Jika tren positif global berlanjut, bukan tidak mungkin IHSG berpotensi menembus level psikologis baru dalam beberapa pekan ke depan."""
        )
    )
}

@Composable
fun NewsInformationScreen(
    onBackClick: () -> Unit = {},
    onNewsClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val newsList = getAllNews()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.color_Foundation_Blue_Normal)
    ) {
        // Header dengan tombol back
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(y = 56.dp)
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(31.dp)
                    .rotate(degrees = 90f)
                    .clickable { onBackClick() }
            )

            Text(
                text = "News & Information",
                color = Color.White,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
        }

        // Content area
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(y = 152.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color(0xfff6f6f6))
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(
                    start = 32.dp,
                    end = 32.dp,
                    top = 32.dp,
                    bottom = 32.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(newsList) { index, news ->
                    NewsItemCard(
                        news = news,
                        onClick = { onNewsClick(news.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NewsItemCard(
    news: NewsItem,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // News Image
        Image(
            painter = painterResource(id = news.imageRes),
            contentDescription = news.title,
            modifier = Modifier
                .width(100.dp)
                .height(90.dp)
                .clip(shape = RoundedCornerShape(15.dp))
        )

        // News Content
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Category
            Text(
                text = news.category,
                color = AppColors.color_Foundation_Blue_Normal,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Title
            Text(
                text = news.title,
                color = Color.Black,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp
                ),
                maxLines = 3
            )

            // Source and Date
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Source Logo
                Image(
                    painter = painterResource(id = news.logoRes),
                    contentDescription = news.source,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(shape = RoundedCornerShape(150.dp))
                )

                // Source Name
                Text(
                    text = news.source,
                    color = Color.Black.copy(alpha = 0.6f),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Date
                Text(
                    text = news.date,
                    color = Color.Black.copy(alpha = 0.6f),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun NewsInformationScreenPreview() {
    NewsInformationScreen()
}