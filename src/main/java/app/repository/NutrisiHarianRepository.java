package app.repository;

import app.model.NutrisiHarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * ============================================================
 * Repository: NutrisiHarianRepository
 * ============================================================
 *
 * Repository untuk operasi database pada tabel 'nutrisi_harian'.
 * Bertanggung jawab untuk membaca dan menyimpan data konsumsi
 * nutrisi harian pengguna.
 *
 * ============================================================
 */
@Repository
public interface NutrisiHarianRepository extends JpaRepository<NutrisiHarian, Long> {

    /**
     * Mencari record konsumsi nutrisi berdasarkan user dan tanggal.
     *
     * Spring otomatis mengubah method ini menjadi SQL:
     *   SELECT * FROM nutrisi_harian WHERE user_id = ? AND tanggal = ?
     *
     * Digunakan di dashboard untuk mengambil data konsumsi hari ini:
     *   findByUserIdAndTanggal(userId, LocalDate.now())
     *
     * Jika hasilnya kosong (Optional.empty()), berarti user belum
     * mencatat konsumsi apapun hari ini -> tampilkan semua 0.
     *
     * @param userId  ID user yang dicari
     * @param tanggal Tanggal yang dicari (biasanya LocalDate.now())
     * @return Optional berisi NutrisiHarian jika ada, kosong jika belum ada
     */
    Optional<NutrisiHarian> findByUserIdAndTanggal(Long userId, LocalDate tanggal);
}
