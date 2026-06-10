# 🚀 UAS Backend - Java Spring Boot + MySQL

Backend REST API untuk proyek UAS, dibangun dengan Java Spring Boot dan terhubung ke database MySQL.

---

## 📋 Daftar Isi
- [Teknologi yang Digunakan](#teknologi)
- [Struktur Folder](#struktur-folder)
- [Cara Menjalankan](#cara-menjalankan)
- [Konfigurasi Database](#konfigurasi-database)
- [Daftar Endpoint API](#daftar-endpoint-api)
- [Cara Testing Endpoint](#cara-testing-endpoint)
- [Penjelasan Konsep](#penjelasan-konsep)

---

## 🛠️ Teknologi yang Digunakan <a name="teknologi"></a>

| Teknologi | Versi | Kegunaan |
|-----------|-------|----------|
| Java | 1.8 (Java 8) | Bahasa pemrograman utama |
| Spring Boot | 2.7.18 | Framework untuk membangun REST API |
| MySQL | 8.x | Database untuk menyimpan data |
| Maven | 3.8.8 | Build tool & dependency manager |
| Hibernate/JPA | 5.6.x | ORM - menghubungkan Java dengan database |

---

## 📁 Struktur Folder <a name="struktur-folder"></a>

```
d:\JAVA PROJECT\UAS\
│
├── 📄 mvnw.cmd                          → Perintah untuk build & run (Windows)
├── 📄 pom.xml                           → Daftar library/dependency yang digunakan
├── 📄 README.md                         → Dokumentasi proyek ini
│
└── 📁 src\main\
    │
    ├── 📁 java\app\                     → Semua kode Java ada di sini
    │   │
    │   ├── 📄 BackendApplication.java   → [MAIN] Titik masuk aplikasi, dijalankan pertama kali
    │   │
    │   ├── 📁 model\                    → Representasi tabel database
    │   │   ├── 📄 User.java            → Data pengguna (nama, email, gender, profil)
    │   │   ├── 📄 NutrisiHarian.java   → Ringkasan konsumsi nutrisi harian pengguna
    │   │   ├── 📄 MakananRiwayat.java  → Detail riwayat makanan yang dikonsumsi
    │   │   └── 📄 AktivitasRiwayat.java → Detail riwayat aktivitas/olahraga
    │   │
    │   ├── 📁 repository\               → Akses & query ke database
    │   │   ├── 📄 UserRepository.java  → Operasi CRUD untuk tabel users
    │   │   ├── 📄 NutrisiHarianRepository.java → Operasi CRUD untuk tabel nutrisi_harian
    │   │   ├── 📄 MakananRiwayatRepository.java → Operasi CRUD untuk tabel makanan_riwayat
    │   │   └── 📄 AktivitasRiwayatRepository.java → Operasi CRUD untuk tabel aktivitas_riwayat
    │   │
    │   ├── 📁 controller\               → Penerima request & pengirim response
    │   │   ├── 📄 AuthController.java  → Endpoint register, login & update profil
    │   │   └── 📄 DashboardController.java → Endpoint dashboard, konsumsi, aktivitas & riwayat
    │   │
    │   └── 📁 util\                     → Fungsi-fungsi pembantu
    │       └── 📄 HashUtils.java       → Hashing password dengan SHA-256
    │
    └── 📁 resources\
        ├── 📄 application.properties   → Konfigurasi database & server
        └── 📁 static\
            └── 📄 index.html           → Halaman web untuk testing endpoint
```

**Alur data singkat:**
```
Request dari Client (Postman/App)
    ↓
Controller (AuthController / DashboardController) → menerima & validasi request
    ↓
Repository (User/Nutrisi/Makanan/Aktivitas Repository) → ambil/simpan data
    ↓
Database (MySQL - db_stdata) → penyimpanan data
    ↑
Response JSON dikirim balik ke Client
```

---

## ▶️ Cara Menjalankan <a name="cara-menjalankan"></a>

### Prasyarat
Pastikan sudah terinstall:
- ✅ Java JDK 8
- ✅ MySQL Server (berjalan di port 3306)

### Langkah 1: Pastikan MySQL berjalan
Buka MySQL dan pastikan service-nya aktif.

### Langkah 2: Jalankan aplikasi
Buka terminal di folder `d:\JAVA PROJECT\UAS\` lalu jalankan:

```cmd
.\mvnw.cmd spring-boot:run
```

### Langkah 3: Tunggu hingga muncul pesan
```
Started BackendApplication in X seconds
Tomcat started on port(s): 8080
```

### Langkah 4: Buka halaman testing
Buka browser dan akses:
```
http://localhost:8080
```

### Langkah 5: Hentikan server
Tekan `Ctrl + C` di terminal untuk menghentikan server.

> **Catatan:** Database `db_stdata` akan dibuat otomatis jika belum ada. Tabel `users`, `nutrisi_harian`, `makanan_riwayat`, dan `aktivitas_riwayat` juga terbuat otomatis saat pertama kali dijalankan.

---

## ⚙️ Konfigurasi Database <a name="konfigurasi-database"></a>

File konfigurasi ada di: `src\main\resources\application.properties`

```properties
# URL koneksi ke MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/db_stdata?createDatabaseIfNotExist=true

# Username MySQL (default: root)
spring.datasource.username=root

# Password MySQL (kosong jika tidak ada password)
spring.datasource.password=
```

### Struktur Tabel Database

#### 1. Tabel `users`
| Nama Kolom | Tipe Data | Keterangan |
|------------|-----------|------------|
| `id` | BIGINT | Primary Key, auto increment |
| `nama` | VARCHAR(255) | Nama lengkap pengguna |
| `email` | VARCHAR(255) | Email unik, tidak boleh kosong |
| `password` | VARCHAR(255) | Password dalam bentuk SHA-256 hash |
| `gender` | VARCHAR(10) | Jenis kelamin: `pria` / `wanita` |
| `berat_badan` | DOUBLE | Berat badan dalam kg |
| `tinggi_badan` | DOUBLE | Tinggi badan dalam cm |
| `tanggal_lahir` | DATE | Tanggal lahir (format: YYYY-MM-DD) |
| `target` | VARCHAR(255) | Tujuan: menurunkan / menaikkan / menstabilkan |
| `jenis_kegiatan` | VARCHAR(255) | Aktivitas: ringan / sedang / berat |
| `token` | VARCHAR(255) | Session token (UUID) dari login terakhir |

#### 2. Tabel `nutrisi_harian`
| Nama Kolom | Tipe Data | Keterangan |
|------------|-----------|------------|
| `id` | BIGINT | Primary Key, auto increment |
| `user_id` | BIGINT | FK ke tabel users |
| `tanggal` | DATE | Tanggal ringkasan (auto-reset tiap hari baru) |
| `kalori_dikonsumsi` | DOUBLE | Total kalori yang dikonsumsi hari ini |
| `protein_dikonsumsi` | DOUBLE | Total protein yang dikonsumsi (gram) |
| `karbo_dikonsumsi` | DOUBLE | Total karbohidrat yang dikonsumsi (gram) |
| `lemak_dikonsumsi` | DOUBLE | Total lemak yang dikonsumsi (gram) |
| `serat_dikonsumsi` | DOUBLE | Total serat yang dikonsumsi (gram) |
| `kalori_terbakar` | DOUBLE | Total kalori yang berkurang dari aktivitas (kkal) |

#### 3. Tabel `makanan_riwayat`
| Nama Kolom | Tipe Data | Keterangan |
|------------|-----------|------------|
| `id` | BIGINT | Primary Key, auto increment |
| `user_id` | BIGINT | FK ke tabel users |
| `nama_makanan` | VARCHAR(255) | Nama makanan/minuman yang dikonsumsi |
| `kalori` | DOUBLE | Kandungan kalori (kkal) |
| `protein` | DOUBLE | Kandungan protein (gram) |
| `karbo` | DOUBLE | Kandungan karbohidrat (gram) |
| `lemak` | DOUBLE | Kandungan lemak (gram) |
| `serat` | DOUBLE | Kandungan serat (gram) |
| `tanggal` | DATE | Tanggal pencatatan |

#### 4. Tabel `aktivitas_riwayat`
| Nama Kolom | Tipe Data | Keterangan |
|------------|-----------|------------|
| `id` | BIGINT | Primary Key, auto increment |
| `user_id` | BIGINT | FK ke tabel users |
| `nama_aktivitas` | VARCHAR(255) | Nama aktivitas olahraga |
| `kalori` | DOUBLE | Jumlah kalori yang dibakar/dikurangi (kkal) |
| `tanggal` | DATE | Tanggal pencatatan |

---

## 📡 Daftar Endpoint API <a name="daftar-endpoint-api"></a>

Base URL: `http://localhost:8080`

| # | Method | URL | Auth | Deskripsi |
|---|--------|-----|------|-----------|
| 1 | `POST` | `/api/auth/register` | ❌ | Daftarkan akun baru |
| 2 | `POST` | `/api/auth/login` | ❌ | Masuk ke akun |
| 3 | `PUT` | `/api/auth/profile` | ✅ Bearer token | Update data profil |
| 4 | `GET` | `/api/dashboard` | ✅ Bearer token | Dashboard nutrisi harian |
| 5 | `POST` | `/api/dashboard/tambah-konsumsi` | ✅ Bearer token | Catat makanan + nutrisi |
| 6 | `POST` | `/api/dashboard/tambah-aktivitas` | ✅ Bearer token | Catat aktivitas + kurangi kalori |
| 7 | `GET` | `/api/dashboard/history` | ✅ Bearer token | Ambil semua riwayat |
| 8 | `GET` | `/api/dashboard/history/makanan` | ✅ Bearer token | Ambil riwayat makanan saja |
| 9 | `GET` | `/api/dashboard/history/aktivitas` | ✅ Bearer token | Ambil riwayat aktivitas saja |

---

### 1. Register (Daftar Akun Baru)

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/register` |

**Request Body (JSON):**
```json
{
  "nama": "Budi Santoso",
  "email": "budi@example.com",
  "password": "password123",
  "gender": "pria",
  "beratBadan": 70.5,
  "tinggiBadan": 175.0,
  "tanggalLahir": "2000-05-15",
  "target": "menurunkan_berat_badan",
  "jenisKegiatan": "sedang"
}
```

---

### 2. Login (Masuk ke Akun)

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/login` |

**Request Body (JSON):**
```json
{
  "email": "budi@example.com",
  "password": "password123"
}
```

**Response Sukses (HTTP 200):**
Mengembalikan `token` session untuk disertakan di header request berikutnya.

---

### 3. Update Profil

| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/auth/profile` |
| **Auth** | ✅ Wajib — `Authorization: Bearer <token>` |

Semua field bersifat **opsional** — hanya field yang dikirim yang akan diperbarui.

---

### 4. Dashboard (Kebutuhan & Konsumsi Nutrisi)

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/dashboard` |
| **Auth** | ✅ Wajib — `Authorization: Bearer <token>` |

Kalkulasi sisa kalori: `kebutuhan.kalori - konsumsi.kalori + terbakar.kalori`

**Response Sukses (HTTP 200):**
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": 1,
      "email": "budi@example.com",
      "gender": "pria",
      "nama": "Budi Santoso",
      "beratBadan": 70.5,
      "tinggiBadan": 175.0,
      "usia": 25,
      "tanggalLahir": "2000-05-15",
      "target": "menurunkan_berat_badan",
      "jenisKegiatan": "sedang"
    },
    "kebutuhanNutrisi": {
      "kalori": 1927.5,
      "protein": 120.47,
      "karbo": 240.94,
      "lemak": 53.54,
      "serat": 28.0
    },
    "konsumsiHariIni": {
      "tanggal": "2026-06-08",
      "kalori": 450.0,
      "protein": 30.0,
      "karbo": 60.0,
      "lemak": 12.0,
      "serat": 5.0,
      "kaloriTerbakar": 300.0
    },
    "sisaKebutuhan": {
      "kalori": 1777.5,
      "protein": 90.47,
      "karbo": 180.94,
      "lemak": 41.54,
      "serat": 23.0
    }
  }
}
```

---

### 5. Tambah Konsumsi Makanan

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/dashboard/tambah-konsumsi` |
| **Auth** | ✅ Wajib — `Authorization: Bearer <token>` |

Mencatat makanan yang dikonsumsi dan menambahkan kandungannya ke akumulasi harian.

**Request Body (JSON):**
```json
{
  "namaMakanan": "Nasi Goreng Telur",
  "kalori": 350.0,
  "protein": 12.5,
  "karbo": 45.0,
  "lemak": 10.0,
  "serat": 2.5
}
```

**Response Sukses (HTTP 200):**
```json
{
  "status": "success",
  "message": "Konsumsi berhasil dicatat!",
  "data": {
    "id": 1,
    "userId": 1,
    "namaMakanan": "Nasi Goreng Telur",
    "kalori": 350.0,
    "protein": 12.5,
    "karbo": 45.0,
    "lemak": 10.0,
    "serat": 2.5,
    "tanggal": "2026-06-08"
  },
  "konsumsiHariIni": {
    "tanggal": "2026-06-08",
    "kalori": 350.0,
    "protein": 12.5,
    "karbo": 45.0,
    "lemak": 10.0,
    "serat": 2.5,
    "kaloriTerbakar": 0.0
  }
}
```

---

### 6. Tambah Aktivitas (Mengurangi Kalori)

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/dashboard/tambah-aktivitas` |
| **Auth** | ✅ Wajib — `Authorization: Bearer <token>` |

Mencatat aktivitas olahraga dan mengurangi total kalori harian.

**Request Body (JSON):**
```json
{
  "namaAktivitas": "Jogging Sore",
  "kalori": 300.0
}
```

**Response Sukses (HTTP 200):**
```json
{
  "status": "success",
  "message": "Aktivitas berhasil dicatat!",
  "data": {
    "id": 1,
    "userId": 1,
    "namaAktivitas": "Jogging Sore",
    "kalori": 300.0,
    "tanggal": "2026-06-08"
  },
  "konsumsiHariIni": {
    "tanggal": "2026-06-08",
    "kalori": 350.0,
    "protein": 12.5,
    "karbo": 45.0,
    "lemak": 10.0,
    "serat": 2.5,
    "kaloriTerbakar": 300.0
  }
}
```

---

### 7. Ambil Semua Riwayat (All History)

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/dashboard/history` |
| **Auth** | ✅ Wajib — `Authorization: Bearer <token>` |

**Response Sukses (HTTP 200):**
```json
{
  "status": "success",
  "data": {
    "makanan": [
      {
        "id": 1,
        "userId": 1,
        "namaMakanan": "Nasi Goreng Telur",
        "kalori": 350.0,
        "protein": 12.5,
        "karbo": 45.0,
        "lemak": 10.0,
        "serat": 2.5,
        "tanggal": "2026-06-08"
      }
    ],
    "aktivitas": [
      {
        "id": 1,
        "userId": 1,
        "namaAktivitas": "Jogging Sore",
        "kalori": 300.0,
        "tanggal": "2026-06-08"
      }
    ]
  }
}
```

---

### 8. Ambil Riwayat Makanan Saja

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/dashboard/history/makanan` |
| **Auth** | ✅ Wajib — `Authorization: Bearer <token>` |

**Response Sukses (HTTP 200):**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "namaMakanan": "Nasi Goreng Telur",
      "kalori": 350.0,
      "protein": 12.5,
      "karbo": 45.0,
      "lemak": 10.0,
      "serat": 2.5,
      "tanggal": "2026-06-08"
    }
  ]
}
```

---

### 9. Ambil Riwayat Aktivitas Saja

| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/dashboard/history/aktivitas` |
| **Auth** | ✅ Wajib — `Authorization: Bearer <token>` |

**Response Sukses (HTTP 200):**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "namaAktivitas": "Jogging Sore",
      "kalori": 300.0,
      "tanggal": "2026-06-08"
    }
  ]
}
```

---

## 🧪 Cara Testing Endpoint <a name="cara-testing-endpoint"></a>

### Cara 1: Menggunakan Postman
1. Buat request baru di Postman.
2. Tambahkan header: `Authorization: Bearer <token_kamu>`.
3. Gunakan method dan body JSON yang sesuai dengan dokumentasi di atas.

### Cara 2: Menggunakan curl
```bash
# Tambah Konsumsi Makanan
curl -X POST http://localhost:8080/api/dashboard/tambah-konsumsi \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"namaMakanan": "Pisang", "kalori": 105, "karbo": 27}'

# Tambah Aktivitas Olahraga
curl -X POST http://localhost:8080/api/dashboard/tambah-aktivitas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"namaAktivitas": "Lari", "kalori": 250}'

# Ambil Semua Riwayat
curl -X GET http://localhost:8080/api/dashboard/history \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 🧠 Penjelasan Konsep <a name="penjelasan-konsep"></a>

Proyek ini dirancang menggunakan konsep pemrograman berorientasi objek (OOP) di Java, serta menggunakan struktur data berupa Array, List, Map (LinkedHashMap), dan mekanisme Keamanan (Hashing). Berikut adalah dokumentasi penggunaannya:

### 1. Object-Oriented Programming (OOP)
Konsep OOP diterapkan hampir di semua komponen karena Java menggunakan paradigma berbasis kelas.

* **Class & Object (Kelas & Objek)**
  * **Fungsi**: Kelas berfungsi sebagai cetakan (blueprint) untuk objek.
  * **Cuplikan Kode (`app/model/User.java`):**
    ```java
    @Entity
    @Table(name = "users")
    public class User {
        private String nama;
        private String email;
        // ...
        public User(String email, String nama, ...) {
            this.email = email;
            this.nama = nama;
        }
    }
    ```
  * **Penjelasan**: Kelas `User` mendefinisikan struktur data pengguna di database. Objek nyata dibuat saat registrasi dengan memanggil constructor kelas ini: `new User(...)`.

* **Encapsulation (Enkapsulasi)**
  * **Fungsi**: Membatasi akses langsung ke variabel instansi (field) dengan membungkusnya menggunakan modifier `private` dan menyediakan akses melalui method `public` (Getter & Setter).
  * **Cuplikan Kode (`app/model/User.java`):**
    ```java
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    ```
  * **Penjelasan**: Atribut `email` bersifat `private` agar tidak bisa diubah langsung dari luar kelas tanpa validasi. Akses baca/tulis dijembatani oleh `getEmail()` dan `setEmail()`.

* **Inheritance (Pewarisan) & Abstraction (Abstraksi)**
  * **Fungsi**: Mewarisi sifat dan fungsi dari kelas atau interface lain untuk mengurangi penulisan kode berulang (redudansi).
  * **Cuplikan Kode (`app/repository/MakananRiwayatRepository.java`):**
    ```java
    public interface MakananRiwayatRepository extends JpaRepository<MakananRiwayat, Long> {
        List<MakananRiwayat> findByUserIdOrderByTanggalDescIdDesc(Long userId);
    }
    ```
  * **Penjelasan**: Kelas repository mewarisi (`extends`) interface `JpaRepository` dari Spring Data JPA. Dengan ini, kita secara otomatis mendapatkan fitur CRUD database bawaan seperti `.save()`, `.findAll()`, dll., tanpa perlu menulis kodenya secara manual.

---

### 2. Penggunaan Array & List

* **Array Biasa (`[]`)**
  * **Fungsi**: Menyimpan sekumpulan data dengan tipe data yang sama dan ukuran yang tetap.
  * **Cuplikan Kode (`app/controller/DashboardController.java`):**
    ```java
    String[] keys = {"kalori", "protein", "karbo", "lemak", "serat"};
    for (String key : keys) {
        double butuh = toDouble(kebutuhan.get(key));
        // ...
    }
    ```
  * **Penjelasan**: Array `keys` digunakan untuk menyimpan daftar kunci nutrisi yang akan dihitung sisa kebutuhannya secara berulang (looping) dalam satu proses.

* **List (Dynamic Array)**
  * **Fungsi**: Menyimpan kumpulan data objek yang ukurannya dapat bertambah dan berkurang secara dinamis.
  * **Cuplikan Kode (`app/controller/AuthController.java`):**
    ```java
    List<String> validGender = Arrays.asList("pria", "wanita");
    if (gender == null || !validGender.contains(gender.trim())) {
        return badRequest("Gender tidak valid!");
    }
    ```
  * **Penjelasan**: `validGender` bertipe `List<String>` digunakan untuk memvalidasi apakah input gender dari request body sesuai dengan pilihan yang diizinkan.

---

### 3. Penggunaan Hashing Map & Cryptographic Hashing

* **Map & LinkedHashMap (Hashing Map)**
  * **Fungsi**: Struktur data pasangan Kunci-Nilai (Key-Value) berbasis tabel hash. `LinkedHashMap` digunakan agar urutan data (insertion order) tetap terjaga saat dikonversi menjadi format JSON untuk response REST API.
  * **Cuplikan Kode (`app/controller/DashboardController.java`):**
    ```java
    Map<String, Object> res = new LinkedHashMap<>();
    res.put("status", "success");
    res.put("data", data);
    return ResponseEntity.ok(res);
    ```
  * **Penjelasan**: `LinkedHashMap` digunakan untuk merangkai response JSON yang dikirimkan kembali ke klien (frontend/Postman).

* **Cryptographic Hashing (SHA-256 Hashing)**
  * **Fungsi**: Mengamankan password pengguna dengan cara mengubah password teks biasa menjadi bentuk kode hash satu arah yang tidak bisa didekripsi kembali.
  * **Cuplikan Kode (`app/util/HashUtils.java`):**
    ```java
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
    ```
  * **Penjelasan**: Kelas `HashUtils` menggunakan algoritma SHA-256 untuk memproses password pengguna sebelum disimpan ke database MySQL demi alasan keamanan.

---

## 👥 Kolaborasi
Setiap kali ada perubahan entity atau model database, Hibernate secara otomatis akan memperbarui tabel di database MySQL local (`db_stdata`).

---

*Dibuat dengan ❤️ menggunakan Java Spring Boot*
