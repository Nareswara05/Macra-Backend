package app.controller;

import app.model.NutrisiHarian;
import app.model.User;
import app.repository.NutrisiHarianRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================
 * Controller: DashboardController
 * ============================================================
 *
 * Controller ini menyediakan endpoint untuk halaman dashboard
 * pengguna, yang menampilkan:
 *
 *   1. Kebutuhan nutrisi harian (dihitung dari data diri user):
 *      - Kalori  : berdasarkan BMR + TDEE + target badan
 *      - Protein : 25% kalori / 4
 *      - Karbo   : 50% kalori / 4
 *      - Lemak   : 25% kalori / 9
 *      - Serat   : 28 gram (nilai tetap)
 *
 *   2. Konsumsi nutrisi hari ini:
 *      - Diambil dari tabel nutrisi_harian berdasarkan tanggal hari ini
 *      - Jika belum ada data hari ini, semua nilai = 0
 *
 *   3. Sisa kebutuhan (kebutuhan - konsumsi):
 *      - Menunjukkan berapa nutrisi yang masih perlu dikonsumsi
 *
 * Rumus Kalkulasi:
 *   BMR (Mifflin-St Jeor):
 *     Pria  : (10 × BB) + (6.25 × TB) − (5 × usia) + 5
 *     Wanita: (10 × BB) + (6.25 × TB) − (5 × usia) − 161
 *
 *   TDEE = BMR × faktor_aktivitas
 *     ringan -> × 1.375
 *     sedang -> × 1.550
 *     berat  -> × 1.725
 *
 *   Kalori Target:
 *     menurunkan_berat_badan    -> TDEE - 500
 *     menstabilkan_berat_badan  -> TDEE
 *     menaikkan_berat_badan     -> TDEE + 300
 *
 * Autentikasi:
 *   Semua endpoint memerlukan header:
 *     Authorization: Bearer <token>
 *   Token didapat dari response login/register.
 *
 * Endpoints:
 *   GET  /api/dashboard               -> Data dashboard lengkap
 *   POST /api/dashboard/tambah-konsumsi -> Catat konsumsi makanan
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NutrisiHarianRepository nutrisiHarianRepository;

    // ============================================================
    // GET /api/dashboard
    // ============================================================

    /**
     * Endpoint utama dashboard — mengembalikan kebutuhan nutrisi harian
     * dan konsumsi nutrisi hari ini untuk pengguna yang sedang login.
     *
     * Cara pakai (dari Postman/aplikasi):
     *   Method  : GET
     *   URL     : http://localhost:8080/api/dashboard
     *   Headers : Authorization: Bearer <token_dari_login>
     *
     * Response sukses (HTTP 200):
     * {
     *   "status": "success",
     *   "data": {
     *     "user": { "id": 1, "email": "...", "gender": "pria", ... },
     *     "kebutuhanNutrisi": { "kalori": 1927.5, "protein": 120.5, ... },
     *     "konsumsiHariIni": { "tanggal": "2026-06-07", "kalori": 450.0, ... },
     *     "sisaKebutuhan": { "kalori": 1477.5, "protein": 90.5, ... }
     *   }
     * }
     *
     * Response gagal (HTTP 401): Token tidak valid / tidak disertakan
     *
     * @param authHeader Nilai header "Authorization" (format: "Bearer <token>")
     * @return ResponseEntity berisi data dashboard lengkap
     */
    @GetMapping
    public ResponseEntity<?> getDashboard(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // --- Autentikasi: Validasi Token ---
            User user = getUserFromToken(authHeader);
            if (user == null)
                return unauthorized("Token tidak valid atau tidak disertakan! " +
                        "Sertakan header: Authorization: Bearer <token>");

            // --- Hitung Kebutuhan Nutrisi ---
            Map<String, Object> kebutuhan = hitungKebutuhanNutrisi(user);

            // --- Ambil Konsumsi Hari Ini ---
            LocalDate hari = LocalDate.now();
            Optional<NutrisiHarian> konsumsiOpt =
                    nutrisiHarianRepository.findByUserIdAndTanggal(user.getId(), hari);

            Map<String, Object> konsumsiHariIni = buildKonsumsiData(konsumsiOpt, hari);

            // --- Hitung Sisa Kebutuhan ---
            Map<String, Object> sisaKebutuhan = hitungSisaKebutuhan(kebutuhan, konsumsiHariIni);

            // --- Bangun Response ---
            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("gender", user.getGender());
            userData.put("nama", user.getNama());
            userData.put("beratBadan", user.getBeratBadan());
            userData.put("tinggiBadan", user.getTinggiBadan());
            userData.put("usia", hitungUsia(user));
            userData.put("tanggalLahir", user.getTanggalLahir());
            userData.put("target", user.getTarget());
            userData.put("jenisKegiatan", user.getJenisKegiatan());

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("user", userData);
            data.put("kebutuhanNutrisi", kebutuhan);
            data.put("konsumsiHariIni", konsumsiHariIni);
            data.put("sisaKebutuhan", sisaKebutuhan);

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("data", data);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    // ============================================================
    // POST /api/dashboard/tambah-konsumsi
    // ============================================================

    /**
     * Endpoint untuk mencatat konsumsi nutrisi hari ini.
     * Nilai yang dikirim akan DITAMBAHKAN ke total konsumsi hari ini.
     * Data konsumsi otomatis ter-reset esok hari (karena disimpan by tanggal).
     *
     * Cara pakai (dari Postman/aplikasi):
     *   Method  : POST
     *   URL     : http://localhost:8080/api/dashboard/tambah-konsumsi
     *   Headers : Authorization: Bearer <token_dari_login>
     *   Body    : JSON (raw)
     *   {
     *     "kalori"  : 350.0,
     *     "protein" : 25.0,
     *     "karbo"   : 45.0,
     *     "lemak"   : 10.0,
     *     "serat"   : 3.5
     *   }
     *
     * Semua field bersifat opsional (jika tidak dikirim, dianggap 0).
     *
     * Response sukses (HTTP 200):
     *   { "status": "success", "message": "Konsumsi berhasil dicatat!", "konsumsiHariIni": {...} }
     *
     * @param authHeader Nilai header "Authorization"
     * @param body       Data nutrisi yang dikonsumsi
     * @return ResponseEntity berisi data konsumsi terbaru
     */
    @PostMapping("/tambah-konsumsi")
    public ResponseEntity<?> tambahKonsumsi(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> body) {
        try {
            // --- Autentikasi: Validasi Token ---
            User user = getUserFromToken(authHeader);
            if (user == null)
                return unauthorized("Token tidak valid atau tidak disertakan! " +
                        "Sertakan header: Authorization: Bearer <token>");

            // --- Ambil nilai nutrisi dari body (default 0 jika tidak ada) ---
            double kalori  = parseDouble(body.get("kalori"),  0.0);
            double protein = parseDouble(body.get("protein"), 0.0);
            double karbo   = parseDouble(body.get("karbo"),   0.0);
            double lemak   = parseDouble(body.get("lemak"),   0.0);
            double serat   = parseDouble(body.get("serat"),   0.0);

            // --- Cari atau buat record konsumsi hari ini ---
            LocalDate hari = LocalDate.now();
            NutrisiHarian konsumsi = nutrisiHarianRepository
                    .findByUserIdAndTanggal(user.getId(), hari)
                    .orElse(new NutrisiHarian(user.getId(), hari));

            // --- Tambahkan nilai konsumsi baru ke akumulasi ---
            konsumsi.setKaloriDikonsumsi(konsumsi.getKaloriDikonsumsi() + kalori);
            konsumsi.setProteinDikonsumsi(konsumsi.getProteinDikonsumsi() + protein);
            konsumsi.setKarboDikonsumsi(konsumsi.getKarboDikonsumsi() + karbo);
            konsumsi.setLemakDikonsumsi(konsumsi.getLemakDikonsumsi() + lemak);
            konsumsi.setSeratDikonsumsi(konsumsi.getSeratDikonsumsi() + serat);

            // --- Simpan ke database ---
            nutrisiHarianRepository.save(konsumsi);

            // --- Kirim Response ---
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("message", "Konsumsi berhasil dicatat!");
            res.put("konsumsiHariIni", buildKonsumsiData(
                    Optional.of(konsumsi), hari));
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    // ============================================================
    // HELPER METHODS — Kalkulasi Nutrisi
    // ============================================================

    /**
     * Menghitung kebutuhan nutrisi harian user berdasarkan data diri.
     *
     * Langkah kalkulasi:
     *   1. Hitung usia dari tanggal lahir
     *   2. Hitung BMR dengan rumus Mifflin-St Jeor (berbeda per gender)
     *   3. Kalikan BMR dengan faktor aktivitas -> TDEE
     *   4. Sesuaikan kalori berdasarkan target badan
     *   5. Distribusikan kalori ke makronutrien
     *
     * @param user Object User dari database
     * @return Map berisi kebutuhan kalori, protein, karbo, lemak, serat
     */
    private Map<String, Object> hitungKebutuhanNutrisi(User user) {
        int usia = hitungUsia(user);

        // --- Langkah 1: Hitung BMR (Basal Metabolic Rate) ---
        // Mifflin-St Jeor Equation
        double bmr;
        if ("pria".equalsIgnoreCase(user.getGender())) {
            // Pria: (10 × BB) + (6.25 × TB) − (5 × usia) + 5
            bmr = (10 * user.getBeratBadan())
                + (6.25 * user.getTinggiBadan())
                - (5 * usia) + 5;
        } else {
            // Wanita: (10 × BB) + (6.25 × TB) − (5 × usia) − 161
            bmr = (10 * user.getBeratBadan())
                + (6.25 * user.getTinggiBadan())
                - (5 * usia) - 161;
        }

        // --- Langkah 2: Hitung TDEE (Total Daily Energy Expenditure) ---
        // Kalikan BMR dengan faktor aktivitas fisik
        double faktorAktivitas;
        switch (user.getJenisKegiatan()) {
            case "ringan": faktorAktivitas = 1.375; break;
            case "berat":  faktorAktivitas = 1.725; break;
            default:       faktorAktivitas = 1.55;  break; // sedang
        }
        double tdee = bmr * faktorAktivitas;

        // --- Langkah 3: Sesuaikan Kalori Berdasarkan Target ---
        double kaloriTarget;
        switch (user.getTarget()) {
            case "menurunkan_berat_badan": kaloriTarget = tdee - 500; break;
            case "menaikkan_berat_badan":  kaloriTarget = tdee + 300; break;
            default:                        kaloriTarget = tdee;       break; // menstabilkan
        }
        // Pastikan kalori tidak negatif
        if (kaloriTarget < 1200) kaloriTarget = 1200;

        // --- Langkah 4: Distribusi Makronutrien ---
        // Protein : 25% kalori, 1 gram protein = 4 kkal
        double proteinGram = round2((kaloriTarget * 0.25) / 4);
        // Karbo   : 50% kalori, 1 gram karbo   = 4 kkal
        double karboGram   = round2((kaloriTarget * 0.50) / 4);
        // Lemak   : 25% kalori, 1 gram lemak   = 9 kkal
        double lemakGram   = round2((kaloriTarget * 0.25) / 9);
        // Serat   : nilai tetap 28 gram/hari (rekomendasi WHO)
        double seratGram   = 28.0;

        Map<String, Object> kebutuhan = new LinkedHashMap<>();
        kebutuhan.put("kalori",  round2(kaloriTarget));
        kebutuhan.put("protein", proteinGram);
        kebutuhan.put("karbo",   karboGram);
        kebutuhan.put("lemak",   lemakGram);
        kebutuhan.put("serat",   seratGram);
        return kebutuhan;
    }

    /**
     * Membangun Map data konsumsi hari ini dari Optional<NutrisiHarian>.
     * Jika belum ada record (Optional kosong), semua nilai = 0.
     *
     * @param konsumsiOpt Optional berisi NutrisiHarian hari ini
     * @param hari        Tanggal hari ini
     * @return Map berisi data konsumsi hari ini
     */
    private Map<String, Object> buildKonsumsiData(
            Optional<NutrisiHarian> konsumsiOpt, LocalDate hari) {

        Map<String, Object> konsumsi = new LinkedHashMap<>();
        konsumsi.put("tanggal", hari.toString());

        if (konsumsiOpt.isPresent()) {
            NutrisiHarian k = konsumsiOpt.get();
            konsumsi.put("kalori",  round2(k.getKaloriDikonsumsi()));
            konsumsi.put("protein", round2(k.getProteinDikonsumsi()));
            konsumsi.put("karbo",   round2(k.getKarboDikonsumsi()));
            konsumsi.put("lemak",   round2(k.getLemakDikonsumsi()));
            konsumsi.put("serat",   round2(k.getSeratDikonsumsi()));
        } else {
            // Belum ada konsumsi hari ini — semua 0
            konsumsi.put("kalori",  0.0);
            konsumsi.put("protein", 0.0);
            konsumsi.put("karbo",   0.0);
            konsumsi.put("lemak",   0.0);
            konsumsi.put("serat",   0.0);
        }
        return konsumsi;
    }

    /**
     * Menghitung sisa kebutuhan nutrisi hari ini.
     * Sisa = Kebutuhan - Konsumsi. Nilai minimum = 0 (tidak bisa negatif).
     *
     * @param kebutuhan      Map kebutuhan nutrisi harian
     * @param konsumsiHariIni Map konsumsi nutrisi hari ini
     * @return Map berisi sisa kebutuhan nutrisi
     */
    private Map<String, Object> hitungSisaKebutuhan(
            Map<String, Object> kebutuhan, Map<String, Object> konsumsiHariIni) {

        Map<String, Object> sisa = new LinkedHashMap<>();
        String[] keys = {"kalori", "protein", "karbo", "lemak", "serat"};

        for (String key : keys) {
            double butuh = toDouble(kebutuhan.get(key));
            double konsumsi = toDouble(konsumsiHariIni.get(key));
            double sisaNilai = round2(Math.max(0, butuh - konsumsi));
            sisa.put(key, sisaNilai);
        }
        return sisa;
    }

    /**
     * Menghitung usia pengguna dalam tahun berdasarkan tanggal lahir.
     *
     * @param user Object User
     * @return Usia dalam tahun
     */
    private int hitungUsia(User user) {
        return Period.between(user.getTanggalLahir(), LocalDate.now()).getYears();
    }

    // ============================================================
    // HELPER METHODS — Autentikasi Token
    // ============================================================

    /**
     * Mengekstrak token dari header Authorization dan mencari user-nya.
     *
     * Format header yang valid: "Bearer <token>"
     *
     * @param authHeader Nilai header Authorization
     * @return User jika token valid, null jika tidak valid
     */
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;

        String token = authHeader.substring(7).trim(); // Hapus "Bearer " di depan
        if (token.isEmpty()) return null;

        Optional<User> userOpt = userRepository.findByToken(token);
        return userOpt.orElse(null);
    }

    // ============================================================
    // HELPER METHODS — Response Builder & Utility
    // ============================================================

    private ResponseEntity<?> unauthorized(String msg) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("status", "error");
        err.put("message", msg);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    private ResponseEntity<?> serverError(String msg) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("status", "error");
        err.put("message", msg);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    /**
     * Membulatkan nilai double ke 2 desimal.
     *
     * @param value Nilai yang akan dibulatkan
     * @return Nilai yang sudah dibulatkan
     */
    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Mengubah Object dari JSON menjadi double, dengan nilai default jika null.
     *
     * @param val          Nilai dari JSON body
     * @param defaultValue Nilai default jika val null atau tidak valid
     * @return Double hasil parsing
     */
    private double parseDouble(Object val, double defaultValue) {
        if (val == null) return defaultValue;
        try { return Double.parseDouble(val.toString()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    /** Mengubah Object menjadi double (untuk kalkulasi sisa kebutuhan) */
    private double toDouble(Object val) {
        if (val == null) return 0.0;
        try { return Double.parseDouble(val.toString()); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
