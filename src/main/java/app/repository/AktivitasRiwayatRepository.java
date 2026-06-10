package app.repository;

import app.model.AktivitasRiwayat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * Repository: AktivitasRiwayatRepository
 * ============================================================
 *
 * Repository untuk operasi database pada tabel 'aktivitas_riwayat'.
 *
 * ============================================================
 */
@Repository
public interface AktivitasRiwayatRepository extends JpaRepository<AktivitasRiwayat, Long> {
    List<AktivitasRiwayat> findByUserIdOrderByTanggalDescIdDesc(Long userId);
}
