package app.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * ============================================================
 * Model: MakananRiwayat
 * ============================================================
 *
 * Model ini merepresentasikan riwayat makanan yang dikonsumsi
 * oleh pengguna secara rinci (nama makanan beserta kandungan nutrisinya).
 *
 * ============================================================
 */
@Entity
@Table(name = "makanan_riwayat")
public class MakananRiwayat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "nama_makanan", nullable = false)
    private String namaMakanan;

    @Column(name = "kalori")
    private Double kalori = 0.0;

    @Column(name = "protein")
    private Double protein = 0.0;

    @Column(name = "karbo")
    private Double karbo = 0.0;

    @Column(name = "lemak")
    private Double lemak = 0.0;

    @Column(name = "serat")
    private Double serat = 0.0;

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public MakananRiwayat() {}

    public MakananRiwayat(Long userId, String namaMakanan, Double kalori, Double protein, Double karbo, Double lemak, Double serat, LocalDate tanggal) {
        this.userId = userId;
        this.namaMakanan = namaMakanan;
        this.kalori = kalori != null ? kalori : 0.0;
        this.protein = protein != null ? protein : 0.0;
        this.karbo = karbo != null ? karbo : 0.0;
        this.lemak = lemak != null ? lemak : 0.0;
        this.serat = serat != null ? serat : 0.0;
        this.tanggal = tanggal;
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNamaMakanan() { return namaMakanan; }
    public void setNamaMakanan(String namaMakanan) { this.namaMakanan = namaMakanan; }

    public Double getKalori() { return kalori; }
    public void setKalori(Double kalori) { this.kalori = kalori; }

    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }

    public Double getKarbo() { return karbo; }
    public void setKarbo(Double karbo) { this.karbo = karbo; }

    public Double getLemak() { return lemak; }
    public void setLemak(Double lemak) { this.lemak = lemak; }

    public Double getSerat() { return serat; }
    public void setSerat(Double serat) { this.serat = serat; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
}
