package app.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * ============================================================
 * Model: User
 * ============================================================
 *
 * Model adalah representasi data yang disimpan di database.
 * Kelas ini merepresentasikan satu baris data di tabel 'users'.
 *
 * Analogi sederhana:
 *   - Tabel 'users' di MySQL = Kelas User di Java
 *   - Satu baris di tabel    = Satu object User
 *   - Kolom di tabel         = Field di dalam kelas
 *
 * Anotasi yang digunakan:
 *   @Entity      -> Memberi tahu Spring bahwa kelas ini terhubung ke tabel database
 *   @Table       -> Menentukan nama tabel yang digunakan ('users')
 *   @Id          -> Menandai field sebagai Primary Key
 *   @GeneratedValue -> ID akan di-generate otomatis oleh database (auto increment)
 *   @Column      -> Menentukan detail kolom (nama, boleh null atau tidak, unik atau tidak)
 *
 * Struktur Tabel 'users' di MySQL:
 * ┌─────────────────┬───────────────┬───────────────────────────────────────────┐
 * │ Nama Kolom      │ Tipe Data     │ Keterangan                                │
 * ├─────────────────┼───────────────┼───────────────────────────────────────────┤
 * │ id              │ BIGINT        │ Primary Key, auto increment               │
 * │ email           │ VARCHAR(255)  │ Email unik, tidak boleh kosong            │
 * │ password        │ VARCHAR(255)  │ Password dalam bentuk SHA-256 hash        │
 * │ gender          │ VARCHAR(10)   │ Jenis kelamin: pria / wanita              │
 * │ berat_badan     │ DOUBLE        │ Berat badan pengguna dalam kg             │
 * │ tinggi_badan    │ DOUBLE        │ Tinggi badan pengguna dalam cm            │
 * │ tanggal_lahir   │ DATE          │ Tanggal lahir (format: YYYY-MM-DD)        │
 * │ target          │ VARCHAR(255)  │ Tujuan: menurunkan/menaikkan/menstabilkan │
 * │ jenis_kegiatan  │ VARCHAR(255)  │ Aktivitas: ringan / sedang / berat        │
 * │ token           │ VARCHAR(255)  │ Session token (UUID) dari login terakhir  │
 * └─────────────────┴───────────────┴───────────────────────────────────────────┘
 *
 * ============================================================
 */
@Entity
@Table(name = "users")
public class User {

    /** ID unik pengguna, di-generate otomatis oleh database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email pengguna - harus unik, tidak boleh kosong */
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /** Password pengguna - disimpan dalam bentuk SHA-256 hash (bukan plain text!) */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Jenis kelamin pengguna.
     * Nilai yang valid:
     *   - "pria"
     *   - "wanita"
     * Digunakan untuk kalkulasi BMR (Basal Metabolic Rate) yang akurat.
     */
    @Column(name = "gender", nullable = false)
    private String gender;

    /** Berat badan dalam satuan kilogram (kg) */
    @Column(name = "berat_badan")
    private Double beratBadan;

    /** Tinggi badan dalam satuan sentimeter (cm) */
    @Column(name = "tinggi_badan")
    private Double tinggiBadan;

    /** Tanggal lahir pengguna */
    @Column(name = "tanggal_lahir")
    private LocalDate tanggalLahir;

    /**
     * Tujuan/target pengguna.
     * Nilai yang valid:
     *   - "menurunkan_berat_badan"
     *   - "menaikkan_berat_badan"
     *   - "menstabilkan_berat_badan"
     */
    @Column(name = "target")
    private String target;

    /**
     * Tingkat aktivitas fisik pengguna.
     * Nilai yang valid:
     *   - "ringan"  (duduk, kerja kantoran)
     *   - "sedang"  (olahraga 3-5x seminggu)
     *   - "berat"   (olahraga intens setiap hari)
     */
    @Column(name = "jenis_kegiatan")
    private String jenisKegiatan;

    /**
     * Session token yang di-generate saat login/register.
     * Digunakan untuk autentikasi endpoint yang memerlukan login.
     * Dikirim melalui header: Authorization: Bearer <token>
     * Nilai berubah setiap kali user login.
     */
    @Column(name = "token", unique = true)
    private String token;

    // ============================================================
    // CONSTRUCTORS
    // Constructor = cara membuat object User baru
    // ============================================================

    /**
     * Constructor kosong - wajib ada untuk JPA/Hibernate.
     * Digunakan oleh Spring secara internal.
     */
    public User() {}

    /**
     * Constructor lengkap - digunakan saat mendaftarkan user baru.
     *
     * @param email         Email pengguna
     * @param password      Password yang sudah di-hash SHA-256
     * @param gender        Jenis kelamin pengguna
     * @param beratBadan    Berat badan dalam kg
     * @param tinggiBadan   Tinggi badan dalam cm
     * @param tanggalLahir  Tanggal lahir
     * @param target        Tujuan pengguna
     * @param jenisKegiatan Tingkat aktivitas fisik
     */
    public User(String email, String password, String gender, Double beratBadan, Double tinggiBadan,
                LocalDate tanggalLahir, String target, String jenisKegiatan) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.beratBadan = beratBadan;
        this.tinggiBadan = tinggiBadan;
        this.tanggalLahir = tanggalLahir;
        this.target = target;
        this.jenisKegiatan = jenisKegiatan;
    }

    // ============================================================
    // GETTERS & SETTERS
    // Getter = mengambil nilai field
    // Setter = mengubah nilai field
    // Wajib ada agar Spring/JPA bisa baca & tulis data
    // ============================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getBeratBadan() { return beratBadan; }
    public void setBeratBadan(Double beratBadan) { this.beratBadan = beratBadan; }

    public Double getTinggiBadan() { return tinggiBadan; }
    public void setTinggiBadan(Double tinggiBadan) { this.tinggiBadan = tinggiBadan; }

    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getJenisKegiatan() { return jenisKegiatan; }
    public void setJenisKegiatan(String jenisKegiatan) { this.jenisKegiatan = jenisKegiatan; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
