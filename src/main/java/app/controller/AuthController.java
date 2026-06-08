package app.controller;

import app.model.User;
import app.repository.UserRepository;
import app.util.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * ============================================================
 * Controller: AuthController
 * ============================================================
 *
 * Controller adalah lapisan yang menerima request (permintaan)
 * dari luar (misalnya dari aplikasi mobile/web/Postman) dan
 * memberikan response (jawaban) balik.
 *
 * Analogi sederhana:
 *   Client (Postman/App) -> [Request] -> Controller -> Repository -> Database
 *   Client (Postman/App) <- [Response] <- Controller <- Repository <- Database
 *
 * Apa itu REST API?
 *   REST API adalah cara berkomunikasi antar aplikasi melalui
 *   HTTP (protokol yang sama dengan saat kamu buka website).
 *   Data dikirim dalam format JSON.
 *
 * Anotasi yang digunakan:
 *   @RestController    -> Kelas ini adalah controller REST API
 *   @RequestMapping    -> Semua endpoint diawali dengan "/api/auth"
 *   @CrossOrigin       -> Mengizinkan request dari domain mana saja
 *                         (penting agar frontend bisa akses backend)
 *   @PostMapping       -> Endpoint ini hanya menerima method POST
 *   @RequestBody       -> Data JSON dari request body otomatis diubah ke Map Java
 *
 * Apa itu HTTP Method?
 *   GET    -> Mengambil data (seperti membaca)
 *   POST   -> Mengirim data baru (seperti mengisi form)
 *   PUT    -> Memperbarui data yang ada
 *   DELETE -> Menghapus data
 *
 * Apa itu HTTP Status Code?
 *   200 OK          -> Berhasil
 *   201 Created     -> Data berhasil dibuat/disimpan
 *   400 Bad Request -> Data yang dikirim tidak valid
 *   401 Unauthorized -> Email/password salah
 *   409 Conflict    -> Data sudah ada (misal email duplikat)
 *   500 Server Error -> Ada error di dalam server
 *
 * Endpoints yang tersedia:
 *   POST /api/auth/register -> Daftarkan akun baru
 *   POST /api/auth/login    -> Masuk dengan email & password
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    /**
     * @Autowired -> Spring otomatis menyuntikkan (inject) instance
     * UserRepository ke sini. Kita tidak perlu membuat object-nya
     * secara manual dengan "new UserRepository()".
     */
    @Autowired
    private UserRepository userRepository;

    // ============================================================
    // POST /api/auth/register
    // ============================================================

    /**
     * Endpoint untuk mendaftarkan akun pengguna baru.
     *
     * Cara pakai (dari Postman/aplikasi):
     *   Method  : POST
     *   URL     : http://localhost:8080/api/auth/register
     *   Body    : JSON (raw)
     *   {
     *     "email"         : "user@example.com",
     *     "password"      : "password123",
     *     "beratBadan"    : 70.5,
     *     "tinggiBadan"   : 175.0,
     *     "tanggalLahir"  : "2000-05-15",
     *     "target"        : "menurunkan_berat_badan",
     *     "jenisKegiatan" : "sedang"
     *   }
     *
     * Nilai target yang valid:
     *   - "menurunkan_berat_badan"
     *   - "menaikkan_berat_badan"
     *   - "menstabilkan_berat_badan"
     *
     * Nilai jenisKegiatan yang valid:
     *   - "ringan"
     *   - "sedang"
     *   - "berat"
     *
     * Response sukses (HTTP 201):
     *   { "status": "success", "message": "Registrasi berhasil!", "token": "...", "data": {...} }
     *
     * Response gagal (HTTP 400/409):
     *   { "status": "error", "message": "Pesan error..." }
     *
     * @param body Data JSON yang dikirim dalam request body
     * @return ResponseEntity berisi JSON response dan HTTP status code
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        try {
            // --- Validasi Email ---
            String email = (String) body.get("email");
            if (email == null || email.trim().isEmpty())
                return badRequest("Email wajib diisi!");
            if (!email.contains("@"))
                return badRequest("Format email tidak valid!");
            if (userRepository.existsByEmail(email.trim()))
                return conflict("Email sudah terdaftar!");


            String nama = (String) body.get("nama");
            if (nama == null || nama.trim().isEmpty())
                return badRequest("Nama wajib diisi");
            if (nama.length() < 4)
                return badRequest("Nama minimal 3 karakter");
            


            // --- Validasi Password ---
            String password = (String) body.get("password");
            if (password == null || password.trim().isEmpty())
                return badRequest("Password wajib diisi!");
            if (password.length() < 6)
                return badRequest("Password minimal 6 karakter!");

            // --- Validasi Berat Badan ---
            Double beratBadan = parseDouble(body.get("beratBadan"));
            if (beratBadan == null || beratBadan <= 0)
                return badRequest("Berat badan wajib diisi dan harus lebih dari 0!");

            // --- Validasi Tinggi Badan ---
            Double tinggiBadan = parseDouble(body.get("tinggiBadan"));
            if (tinggiBadan == null || tinggiBadan <= 0)
                return badRequest("Tinggi badan wajib diisi dan harus lebih dari 0!");

            // --- Validasi Tanggal Lahir ---
            String tglStr = (String) body.get("tanggalLahir");
            if (tglStr == null || tglStr.trim().isEmpty())
                return badRequest("Tanggal lahir wajib diisi! Format: YYYY-MM-DD");
            LocalDate tanggalLahir;
            try {
                tanggalLahir = LocalDate.parse(tglStr.trim());
            } catch (Exception e) {
                return badRequest("Format tanggal tidak valid! Gunakan: YYYY-MM-DD (contoh: 2000-05-15)");
            }

            // --- Validasi Target ---
            String target = (String) body.get("target");
            List<String> validTarget = Arrays.asList(
                "menurunkan_berat_badan", "menaikkan_berat_badan", "menstabilkan_berat_badan"
            );
            if (target == null || !validTarget.contains(target.trim()))
                return badRequest("Target tidak valid! Pilihan: menurunkan_berat_badan | menaikkan_berat_badan | menstabilkan_berat_badan");

            // --- Validasi Jenis Kegiatan ---
            String jenisKegiatan = (String) body.get("jenisKegiatan");
            List<String> validKegiatan = Arrays.asList("ringan", "sedang", "berat");
            if (jenisKegiatan == null || !validKegiatan.contains(jenisKegiatan.trim()))
                return badRequest("Jenis kegiatan tidak valid! Pilihan: ringan | sedang | berat");

            // --- Hash Password & Simpan ke Database ---
            String hashedPassword = HashUtils.hashPassword(password);
            User newUser = new User(
                email.trim(),       // 1. String email
                nama.trim(),        // 2. String nama 
                hashedPassword,     // 3. String password
                beratBadan,         // 4. Double beratBadan
                tinggiBadan,        // 5. Double tinggiBadan
                tanggalLahir,       // 6. LocalDate tanggalLahir
                target.trim(),      // 7. String target
                jenisKegiatan.trim()// 8. String jenisKegiatan
            );
            userRepository.save(newUser);

            // --- Kirim Response Sukses ---
            String token = UUID.randomUUID().toString(); // Generate token unik
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("message", "Registrasi berhasil!");
            res.put("token", token);
            res.put("data", buildUserData(newUser));
            return ResponseEntity.status(HttpStatus.CREATED).body(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    // ============================================================
    // POST /api/auth/login
    // ============================================================

    /**
     * Endpoint untuk login pengguna yang sudah terdaftar.
     *
     * Cara pakai (dari Postman/aplikasi):
     *   Method  : POST
     *   URL     : http://localhost:8080/api/auth/login
     *   Body    : JSON (raw)
     *   {
     *     "email"    : "user@example.com",
     *     "password" : "password123"
     *   }
     *
     * Proses verifikasi:
     *   1. Cari user berdasarkan email di database
     *   2. Hash password yang diinput
     *   3. Bandingkan dengan hash yang tersimpan di database
     *   4. Jika cocok -> login berhasil, kembalikan token
     *
     * Response sukses (HTTP 200):
     *   { "status": "success", "message": "Login berhasil!", "token": "...", "data": {...} }
     *
     * Response gagal (HTTP 401):
     *   { "status": "error", "message": "Email atau password salah!" }
     *
     * @param body Data JSON yang dikirim dalam request body
     * @return ResponseEntity berisi JSON response dan HTTP status code
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        try {
            // --- Validasi Input ---
            String email = (String) body.get("email");
            if (email == null || email.trim().isEmpty())
                return badRequest("Email wajib diisi!");

            String password = (String) body.get("password");
            if (password == null || password.trim().isEmpty())
                return badRequest("Password wajib diisi!");

            // --- Cari User di Database ---
            Optional<User> userOpt = userRepository.findByEmail(email.trim());
            if (!userOpt.isPresent())
                return unauthorized("Email atau password salah!");

            // --- Verifikasi Password ---
            User user = userOpt.get();
            if (!HashUtils.hashPassword(password).equals(user.getPassword()))
                return unauthorized("Email atau password salah!");

            // --- Kirim Response Sukses ---
            String token = UUID.randomUUID().toString(); // Generate token unik
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("message", "Login berhasil!");
            res.put("token", token);
            res.put("data", buildUserData(user));
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    // ============================================================
    // HELPER METHODS (Method Pembantu - Private)
    // Method ini hanya bisa dipakai di dalam kelas ini
    // ============================================================

    /**
     * Membuat object Map berisi data user yang aman untuk dikembalikan
     * sebagai response. Password TIDAK dimasukkan ke sini untuk keamanan.
     *
     * @param user Object User dari database
     * @return Map berisi data user (tanpa password)
     */
    private Map<String, Object> buildUserData(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("email", user.getEmail());
        data.put("nama", user.getNama());
        data.put("beratBadan", user.getBeratBadan());
        data.put("tinggiBadan", user.getTinggiBadan());
        data.put("tanggalLahir", user.getTanggalLahir());
        data.put("target", user.getTarget());
        data.put("jenisKegiatan", user.getJenisKegiatan());
        return data;
    }

    /**
     * Membuat response error 400 Bad Request.
     * Digunakan ketika data yang dikirim tidak valid/tidak lengkap.
     *
     * @param msg Pesan error yang akan ditampilkan
     * @return ResponseEntity dengan status 400 dan body error
     */
    private ResponseEntity<?> badRequest(String msg) {
        return ResponseEntity.badRequest().body(errorBody(msg));
    }

    /**
     * Membuat response error 409 Conflict.
     * Digunakan ketika data yang dikirim sudah ada di database (misal email duplikat).
     *
     * @param msg Pesan error yang akan ditampilkan
     * @return ResponseEntity dengan status 409 dan body error
     */
    private ResponseEntity<?> conflict(String msg) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(msg));
    }

    /**
     * Membuat response error 401 Unauthorized.
     * Digunakan ketika email/password tidak cocok.
     *
     * @param msg Pesan error yang akan ditampilkan
     * @return ResponseEntity dengan status 401 dan body error
     */
    private ResponseEntity<?> unauthorized(String msg) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody(msg));
    }

    /**
     * Membuat response error 500 Internal Server Error.
     * Digunakan ketika terjadi error yang tidak terduga di server.
     *
     * @param msg Pesan error yang akan ditampilkan
     * @return ResponseEntity dengan status 500 dan body error
     */
    private ResponseEntity<?> serverError(String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody(msg));
    }

    /**
     * Membuat struktur JSON standar untuk response error.
     * Format: { "status": "error", "message": "..." }
     *
     * @param message Pesan error
     * @return Map yang akan diubah menjadi JSON
     */
    private Map<String, Object> errorBody(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("status", "error");
        err.put("message", message);
        return err;
    }

    /**
     * Mengubah Object (dari JSON) menjadi Double.
     * Diperlukan karena JSON number bisa datang sebagai Integer atau Double.
     *
     * @param val Nilai dari JSON body
     * @return Double jika berhasil diubah, null jika gagal
     */
    private Double parseDouble(Object val) {
        if (val == null) return null;
        try { return Double.parseDouble(val.toString()); }
        catch (NumberFormatException e) { return null; }
    }
}
