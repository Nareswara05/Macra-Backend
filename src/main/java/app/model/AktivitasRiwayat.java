package app.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * ============================================================
 * Model: AktivitasRiwayat
 * ============================================================
 *
 * Model ini merepresentasikan riwayat aktivitas fisik pengguna
 * beserta kalori yang berkurang/terbakar dari aktivitas tersebut.
 *
 * ============================================================
 */
@Entity
@Table(name = "aktivitas_riwayat")
public class AktivitasRiwayat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "nama_aktivitas", nullable = false)
    private String namaAktivitas;

    @Column(name = "kalori", nullable = false)
    private Double kalori = 0.0;

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public AktivitasRiwayat() {}

    public AktivitasRiwayat(Long userId, String namaAktivitas, Double kalori, LocalDate tanggal) {
        this.userId = userId;
        this.namaAktivitas = namaAktivitas;
        this.kalori = kalori != null ? kalori : 0.0;
        this.tanggal = tanggal;
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNamaAktivitas() { return namaAktivitas; }
    public void setNamaAktivitas(String namaAktivitas) { this.namaAktivitas = namaAktivitas; }

    public Double getKalori() { return kalori; }
    public void setKalori(Double kalori) { this.kalori = kalori; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
}
