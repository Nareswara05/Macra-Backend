package app.controller;

import app.model.NutrisiHarian;
import app.model.User;
import app.model.MakananRiwayat;
import app.model.AktivitasRiwayat;
import app.repository.NutrisiHarianRepository;
import app.repository.UserRepository;
import app.repository.MakananRiwayatRepository;
import app.repository.AktivitasRiwayatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================
 * Controller: DashboardController
 * ============================================================
 *
 * Controller ini menyediakan endpoint untuk halaman dashboard
 * pengguna, riwayat konsumsi, dan riwayat aktivitas.
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

    @Autowired
    private MakananRiwayatRepository makananRiwayatRepository;

    @Autowired
    private AktivitasRiwayatRepository aktivitasRiwayatRepository;

    // ============================================================
    // GET /api/dashboard
    // ============================================================

    /**
     * Endpoint utama dashboard — mengembalikan kebutuhan nutrisi harian
     * dan konsumsi nutrisi hari ini untuk pengguna yang sedang login.
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
     * Endpoint untuk mencatat konsumsi makanan/minuman beserta nutrisinya.
     */
    @PostMapping("/tambah-konsumsi")
    public ResponseEntity<?> tambahKonsumsi(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> body) {
        try {
            User user = getUserFromToken(authHeader);
            if (user == null)
                return unauthorized("Token tidak valid atau tidak disertakan! " +
                        "Sertakan header: Authorization: Bearer <token>");

            String namaMakanan = (String) body.get("namaMakanan");
            if (namaMakanan == null || namaMakanan.trim().isEmpty())
                return badRequest("Nama makanan wajib diisi!");

            double kalori  = parseDouble(body.get("kalori"),  0.0);
            double protein = parseDouble(body.get("protein"), 0.0);
            double karbo   = parseDouble(body.get("karbo"),   0.0);
            double lemak   = parseDouble(body.get("lemak"),   0.0);
            double serat   = parseDouble(body.get("serat"),   0.0);

            LocalDate hari = LocalDate.now();

            // Simpan riwayat makanan
            MakananRiwayat riwayat = new MakananRiwayat(
                user.getId(),
                namaMakanan.trim(),
                kalori,
                protein,
                karbo,
                lemak,
                serat,
                hari
            );
            makananRiwayatRepository.save(riwayat);

            // Update NutrisiHarian harian
            NutrisiHarian konsumsi = nutrisiHarianRepository
                    .findByUserIdAndTanggal(user.getId(), hari)
                    .orElse(new NutrisiHarian(user.getId(), hari));

            konsumsi.setKaloriDikonsumsi(konsumsi.getKaloriDikonsumsi() + kalori);
            konsumsi.setProteinDikonsumsi(konsumsi.getProteinDikonsumsi() + protein);
            konsumsi.setKarboDikonsumsi(konsumsi.getKarboDikonsumsi() + karbo);
            konsumsi.setLemakDikonsumsi(konsumsi.getLemakDikonsumsi() + lemak);
            konsumsi.setSeratDikonsumsi(konsumsi.getSeratDikonsumsi() + serat);
            nutrisiHarianRepository.save(konsumsi);

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("message", "Konsumsi berhasil dicatat!");
            res.put("data", riwayat);
            res.put("konsumsiHariIni", buildKonsumsiData(Optional.of(konsumsi), hari));
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    // ============================================================
    // POST /api/dashboard/tambah-aktivitas
    // ============================================================

    /**
     * Endpoint untuk mencatat aktivitas fisik/olahraga yang membakar kalori.
     */
    @PostMapping("/tambah-aktivitas")
    public ResponseEntity<?> tambahAktivitas(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> body) {
        try {
            User user = getUserFromToken(authHeader);
            if (user == null)
                return unauthorized("Token tidak valid atau tidak disertakan! " +
                        "Sertakan header: Authorization: Bearer <token>");

            String namaAktivitas = (String) body.get("namaAktivitas");
            if (namaAktivitas == null || namaAktivitas.trim().isEmpty())
                return badRequest("Nama aktivitas wajib diisi!");

            double kalori = parseDouble(body.get("kalori"), 0.0);
            if (kalori <= 0)
                return badRequest("Nilai kalori wajib diisi dan harus lebih dari 0!");

            LocalDate hari = LocalDate.now();

            // Simpan riwayat aktivitas
            AktivitasRiwayat riwayat = new AktivitasRiwayat(
                user.getId(),
                namaAktivitas.trim(),
                kalori,
                hari
            );
            aktivitasRiwayatRepository.save(riwayat);

            // Update NutrisiHarian (tambahkan ke kaloriTerbakar)
            NutrisiHarian konsumsi = nutrisiHarianRepository
                    .findByUserIdAndTanggal(user.getId(), hari)
                    .orElse(new NutrisiHarian(user.getId(), hari));
            konsumsi.setKaloriTerbakar(konsumsi.getKaloriTerbakar() + kalori);
            nutrisiHarianRepository.save(konsumsi);

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("message", "Aktivitas berhasil dicatat!");
            res.put("data", riwayat);
            res.put("konsumsiHariIni", buildKonsumsiData(Optional.of(konsumsi), hari));
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    // ============================================================
    // HISTORY ENDPOINTS
    // ============================================================

    /**
     * Endpoint untuk mengambil semua riwayat (makanan & aktivitas).
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistoryAll(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = getUserFromToken(authHeader);
            if (user == null)
                return unauthorized("Token tidak valid atau tidak disertakan!");

            List<MakananRiwayat> makananList = makananRiwayatRepository
                    .findByUserIdOrderByTanggalDescIdDesc(user.getId());
            List<AktivitasRiwayat> aktivitasList = aktivitasRiwayatRepository
                    .findByUserIdOrderByTanggalDescIdDesc(user.getId());

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("makanan", makananList);
            data.put("aktivitas", aktivitasList);

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("data", data);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    /**
     * Endpoint untuk mengambil riwayat makanan saja.
     */
    @GetMapping("/history/makanan")
    public ResponseEntity<?> getHistoryMakanan(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = getUserFromToken(authHeader);
            if (user == null)
                return unauthorized("Token tidak valid atau tidak disertakan!");

            List<MakananRiwayat> makananList = makananRiwayatRepository
                    .findByUserIdOrderByTanggalDescIdDesc(user.getId());

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("data", makananList);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    /**
     * Endpoint untuk mengambil riwayat aktivitas saja.
     */
    @GetMapping("/history/aktivitas")
    public ResponseEntity<?> getHistoryAktivitas(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = getUserFromToken(authHeader);
            if (user == null)
                return unauthorized("Token tidak valid atau tidak disertakan!");

            List<AktivitasRiwayat> aktivitasList = aktivitasRiwayatRepository
                    .findByUserIdOrderByTanggalDescIdDesc(user.getId());

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "success");
            res.put("data", aktivitasList);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return serverError("Terjadi kesalahan server: " + e.getMessage());
        }
    }

    // ============================================================
    // HELPER METHODS — Kalkulasi Nutrisi
    // ============================================================

    private Map<String, Object> hitungKebutuhanNutrisi(User user) {
        int usia = hitungUsia(user);

        double bmr;
        if ("pria".equalsIgnoreCase(user.getGender())) {
            bmr = (10 * user.getBeratBadan())
                + (6.25 * user.getTinggiBadan())
                - (5 * usia) + 5;
        } else {
            bmr = (10 * user.getBeratBadan())
                + (6.25 * user.getTinggiBadan())
                - (5 * usia) - 161;
        }

        double faktorAktivitas;
        switch (user.getJenisKegiatan()) {
            case "ringan": faktorAktivitas = 1.375; break;
            case "berat":  faktorAktivitas = 1.725; break;
            default:       faktorAktivitas = 1.55;  break; // sedang
        }
        double tdee = bmr * faktorAktivitas;

        double kaloriTarget;
        switch (user.getTarget()) {
            case "menurunkan_berat_badan": kaloriTarget = tdee - 500; break;
            case "menaikkan_berat_badan":  kaloriTarget = tdee + 300; break;
            default:                        kaloriTarget = tdee;       break; // menstabilkan
        }
        if (kaloriTarget < 1200) kaloriTarget = 1200;

        double proteinGram = round2((kaloriTarget * 0.25) / 4);
        double karboGram   = round2((kaloriTarget * 0.50) / 4);
        double lemakGram   = round2((kaloriTarget * 0.25) / 9);
        double seratGram   = 28.0;

        Map<String, Object> kebutuhan = new LinkedHashMap<>();
        kebutuhan.put("kalori",  round2(kaloriTarget));
        kebutuhan.put("protein", proteinGram);
        kebutuhan.put("karbo",   karboGram);
        kebutuhan.put("lemak",   lemakGram);
        kebutuhan.put("serat",   seratGram);
        return kebutuhan;
    }

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
            konsumsi.put("kaloriTerbakar", round2(k.getKaloriTerbakar()));
        } else {
            konsumsi.put("kalori",  0.0);
            konsumsi.put("protein", 0.0);
            konsumsi.put("karbo",   0.0);
            konsumsi.put("lemak",   0.0);
            konsumsi.put("serat",   0.0);
            konsumsi.put("kaloriTerbakar", 0.0);
        }
        return konsumsi;
    }

    private Map<String, Object> hitungSisaKebutuhan(
            Map<String, Object> kebutuhan, Map<String, Object> konsumsiHariIni) {

        Map<String, Object> sisa = new LinkedHashMap<>();
        String[] keys = {"kalori", "protein", "karbo", "lemak", "serat"};

        for (String key : keys) {
            double butuh = toDouble(kebutuhan.get(key));
            double konsumsi = toDouble(konsumsiHariIni.get(key));
            double sisaNilai;
            if ("kalori".equals(key)) {
                double terbakar = toDouble(konsumsiHariIni.get("kaloriTerbakar"));
                sisaNilai = round2(Math.max(0, butuh - konsumsi + terbakar));
            } else {
                sisaNilai = round2(Math.max(0, butuh - konsumsi));
            }
            sisa.put(key, sisaNilai);
        }
        return sisa;
    }

    private int hitungUsia(User user) {
        return Period.between(user.getTanggalLahir(), LocalDate.now()).getYears();
    }

    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) return null;

        Optional<User> userOpt = userRepository.findByToken(token);
        return userOpt.orElse(null);
    }

    private ResponseEntity<?> unauthorized(String msg) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("status", "error");
        err.put("message", msg);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    private ResponseEntity<?> badRequest(String msg) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("status", "error");
        err.put("message", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    private ResponseEntity<?> serverError(String msg) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("status", "error");
        err.put("message", msg);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double parseDouble(Object val, double defaultValue) {
        if (val == null) return defaultValue;
        try { return Double.parseDouble(val.toString()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    private double toDouble(Object val) {
        if (val == null) return 0.0;
        try { return Double.parseDouble(val.toString()); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
