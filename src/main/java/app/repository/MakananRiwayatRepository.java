package app.repository;

import app.model.MakananRiwayat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * Repository: MakananRiwayatRepository
 * ============================================================
 *
 * Repository untuk operasi database pada tabel 'makanan_riwayat'.
 *
 * ============================================================
 */
@Repository
public interface MakananRiwayatRepository extends JpaRepository<MakananRiwayat, Long> {
    List<MakananRiwayat> findByUserIdOrderByTanggalDescIdDesc(Long userId);
}
