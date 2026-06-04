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
    │   │   └── 📄 User.java            → Data pengguna (email, password, profil)
    │   │
    │   ├── 📁 repository\               → Akses & query ke database
    │   │   └── 📄 UserRepository.java  → Operasi CRUD untuk tabel users
    │   │
    │   ├── 📁 controller\               → Penerima request & pengirim response
    │   │   └── 📄 AuthController.java  → Endpoint register & login
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
Controller (AuthController) → menerima & validasi request
    ↓
Repository (UserRepository) → ambil/simpan data
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

> **Catatan:** Database `db_stdata` akan dibuat otomatis jika belum ada. Tabel `users` juga terbuat otomatis saat pertama kali dijalankan.

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

**Jika MySQL kamu menggunakan password**, ubah baris `spring.datasource.password=` menjadi:
```properties
spring.datasource.password=password_kamu
```

---

## 📡 Daftar Endpoint API <a name="daftar-endpoint-api"></a>

Base URL: `http://localhost:8080`

### 1. Register (Daftar Akun Baru)

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/register` |
| **Deskripsi** | Mendaftarkan pengguna baru ke sistem |

**Request Body (JSON):**
```json
{
  "email": "budi@example.com",
  "password": "password123",
  "beratBadan": 70.5,
  "tinggiBadan": 175.0,
  "tanggalLahir": "2000-05-15",
  "target": "menurunkan_berat_badan",
  "jenisKegiatan": "sedang"
}
```

**Pilihan nilai `target`:**
| Nilai | Keterangan |
|-------|------------|
| `menurunkan_berat_badan` | Ingin menurunkan berat badan |
| `menaikkan_berat_badan` | Ingin menaikkan berat badan |
| `menstabilkan_berat_badan` | Ingin menjaga berat badan |

**Pilihan nilai `jenisKegiatan`:**
| Nilai | Keterangan |
|-------|------------|
| `ringan` | Aktivitas ringan (duduk, kerja kantoran) |
| `sedang` | Olahraga 3-5x seminggu |
| `berat` | Olahraga intens setiap hari |

**Response Sukses (HTTP 201):**
```json
{
  "status": "success",
  "message": "Registrasi berhasil!",
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "email": "budi@example.com",
    "beratBadan": 70.5,
    "tinggiBadan": 175.0,
    "tanggalLahir": "2000-05-15",
    "target": "menurunkan_berat_badan",
    "jenisKegiatan": "sedang"
  }
}
```

**Response Error:**
| HTTP Status | Kondisi |
|------------|---------|
| 400 Bad Request | Data tidak lengkap atau format salah |
| 409 Conflict | Email sudah terdaftar |

---

### 2. Login (Masuk ke Akun)

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/login` |
| **Deskripsi** | Autentikasi pengguna yang sudah terdaftar |

**Request Body (JSON):**
```json
{
  "email": "budi@example.com",
  "password": "password123"
}
```

**Response Sukses (HTTP 200):**
```json
{
  "status": "success",
  "message": "Login berhasil!",
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "data": {
    "id": 1,
    "email": "budi@example.com",
    "beratBadan": 70.5,
    "tinggiBadan": 175.0,
    "tanggalLahir": "2000-05-15",
    "target": "menurunkan_berat_badan",
    "jenisKegiatan": "sedang"
  }
}
```

**Response Error:**
| HTTP Status | Kondisi |
|------------|---------|
| 400 Bad Request | Email atau password tidak diisi |
| 401 Unauthorized | Email atau password salah |

---

## 🧪 Cara Testing Endpoint <a name="cara-testing-endpoint"></a>

### Cara 1: Halaman Web (Termudah)
Setelah server berjalan, buka browser: **http://localhost:8080**
Halaman testing sudah tersedia, tinggal isi form dan klik tombol.

### Cara 2: Menggunakan Postman
1. Download & install [Postman](https://www.postman.com/downloads/)
2. Buat request baru → pilih method `POST`
3. Masukkan URL endpoint
4. Di tab `Body` → pilih `raw` → pilih `JSON`
5. Tempel JSON body yang sesuai
6. Klik `Send`

### Cara 3: Menggunakan curl (Command Line)
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "beratBadan": 70,
    "tinggiBadan": 170,
    "tanggalLahir": "2000-01-01",
    "target": "menurunkan_berat_badan",
    "jenisKegiatan": "sedang"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'
```

---

## 📚 Penjelasan Konsep <a name="penjelasan-konsep"></a>

### Apa itu REST API?
REST API adalah cara dua aplikasi berkomunikasi melalui internet.
- **Client** (aplikasi mobile/web) mengirim **Request**
- **Server** (backend ini) memproses dan mengirim **Response**
- Data diformat dalam **JSON** (teks yang mudah dibaca mesin)

### Apa itu JSON?
Format data yang digunakan untuk pertukaran informasi:
```json
{
  "nama": "Budi",
  "umur": 20,
  "aktif": true
}
```

### Apa itu Token?
Token adalah kode unik yang diberikan server setelah login berhasil.
Token ini nantinya digunakan sebagai "tiket masuk" untuk mengakses
fitur-fitur yang membutuhkan autentikasi.

### Kenapa password di-hash?
Untuk keamanan. Jika database bocor, peretas tidak bisa tahu password
asli pengguna karena yang tersimpan hanyalah hasil hash (tidak bisa dikembalikan).

---

## 👥 Kolaborasi

### Untuk menjalankan proyek ini:
1. Clone/download folder proyek ini
2. Pastikan MySQL berjalan
3. Jalankan: `.\mvnw.cmd spring-boot:run`
4. Buka browser: `http://localhost:8080`

### Jika ingin menambahkan endpoint baru:
1. Tambahkan method di `AuthController.java` (atau buat controller baru)
2. Jika butuh tabel baru, buat model baru di folder `model/`
3. Buat repository baru di folder `repository/`
4. Jalankan ulang server → tabel akan terbuat otomatis

---

*Dibuat dengan ❤️ menggunakan Java Spring Boot*
