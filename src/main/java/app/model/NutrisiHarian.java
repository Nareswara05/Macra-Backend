package app.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * ============================================================
 * Model: NutrisiHarian
 * ============================================================
 *
 * Model ini merepresentasikan data konsumsi nutrisi pengguna
 * untuk satu hari tertentu. Setiap baris di tabel ini mewakili
 * akumulasi nutrisi yang dikonsumsi seorang user pada satu tanggal.
 *
 * Mekanisme Reset Harian:
 *   Data TIDAK dihapus, tapi karena setiap pencatatan konsumsi
 *   dilakukan berdasarkan kombinasi (user_id + tanggal), maka:
 *   - Hari ini  -> record dengan tanggal = 2026-06-07
 *   - Besok     -> record BARU dengan tanggal = 2026-06-08
 *   Dashboard selalu menampilkan record hari ini (LocalDate.now()).
 *   Jika belum ada record untuk hari ini, semua konsumsi dianggap 0.
 *
 * Struktur Tabel 'nutrisi_harian' di MySQL:
 * ┌─────────────────────┬───────────────┬─────────────────────────────────────────┐
 * │ Nama Kolom          │ Tipe Data     │ Keterangan                              │
 * ├─────────────────────┼───────────────┼─────────────────────────────────────────┤
 * │ id                  │ BIGINT        │ Primary Key, auto increment             │
 * │ user_id             │ BIGINT        │ Foreign Key ke tabel users              │
 * │ tanggal             │ DATE          │ Tanggal konsumsi (YYYY-MM-DD)           │
 * │ kalori_dikonsumsi   │ DOUBLE        │ Total kalori yang dikonsumsi hari ini   │
 * │ protein_dikonsumsi  │ DOUBLE        │ Total protein (gram) hari ini           │
 * │ karbo_dikonsumsi    │ DOUBLE        │ Total karbohidrat (gram) hari ini       │
 * │ lemak_dikonsumsi    │ DOUBLE        │ Total lemak (gram) hari ini             │
 * │ serat_dikonsumsi    │ DOUBLE        │ Total serat (gram) hari ini             │
 * └─────────────────────┴───────────────┴─────────────────────────────────────────┘
 *
 * Constraint UNIQUE (user_id, tanggal) memastikan hanya ada
 * satu record per user per hari.
 *
 * ============================================================
 */
@Entity
@Table(
    name = "nutrisi_harian",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tanggal"})
)
public class NutrisiHarian {

    /** ID unik, di-generate otomatis oleh database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID user pemilik data ini.
     * Merupakan Foreign Key yang merujuk ke kolom id di tabel users.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Tanggal konsumsi ini dicatat. Satu record per user per hari. */
    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    /** Total kalori yang sudah dikonsumsi hari ini (dalam kkal) */
    @Column(name = "kalori_dikonsumsi")
    private Double kaloriDikonsumsi = 0.0;

    /** Total protein yang sudah dikonsumsi hari ini (dalam gram) */
    @Column(name = "protein_dikonsumsi")
    private Double proteinDikonsumsi = 0.0;

    /** Total karbohidrat yang sudah dikonsumsi hari ini (dalam gram) */
    @Column(name = "karbo_dikonsumsi")
    private Double karboDikonsumsi = 0.0;

    /** Total lemak yang sudah dikonsumsi hari ini (dalam gram) */
    @Column(name = "lemak_dikonsumsi")
    private Double lemakDikonsumsi = 0.0;

    /** Total serat yang sudah dikonsumsi hari ini (dalam gram) */
    @Column(name = "serat_dikonsumsi")
    private Double seratDikonsumsi = 0.0;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    /** Constructor kosong - wajib ada untuk JPA/Hibernate */
    public NutrisiHarian() {}

    /**
     * Constructor untuk membuat record konsumsi baru (hari pertama konsumsi).
     *
     * @param userId  ID user pemilik record
     * @param tanggal Tanggal konsumsi
     */
    public NutrisiHarian(Long userId, LocalDate tanggal) {
        this.userId = userId;
        this.tanggal = tanggal;
        this.kaloriDikonsumsi = 0.0;
        this.proteinDikonsumsi = 0.0;
        this.karboDikonsumsi = 0.0;
        this.lemakDikonsumsi = 0.0;
        this.seratDikonsumsi = 0.0;
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public Double getKaloriDikonsumsi() { return kaloriDikonsumsi; }
    public void setKaloriDikonsumsi(Double kaloriDikonsumsi) { this.kaloriDikonsumsi = kaloriDikonsumsi; }

    public Double getProteinDikonsumsi() { return proteinDikonsumsi; }
    public void setProteinDikonsumsi(Double proteinDikonsumsi) { this.proteinDikonsumsi = proteinDikonsumsi; }

    public Double getKarboDikonsumsi() { return karboDikonsumsi; }
    public void setKarboDikonsumsi(Double karboDikonsumsi) { this.karboDikonsumsi = karboDikonsumsi; }

    public Double getLemakDikonsumsi() { return lemakDikonsumsi; }
    public void setLemakDikonsumsi(Double lemakDikonsumsi) { this.lemakDikonsumsi = lemakDikonsumsi; }

    public Double getSeratDikonsumsi() { return seratDikonsumsi; }
    public void setSeratDikonsumsi(Double seratDikonsumsi) { this.seratDikonsumsi = seratDikonsumsi; }
}
