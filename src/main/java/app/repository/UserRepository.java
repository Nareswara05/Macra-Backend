package app.repository;

import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================
 * Repository: UserRepository
 * ============================================================
 *
 * Repository adalah lapisan yang bertanggung jawab untuk
 * berkomunikasi langsung dengan database. Semua operasi
 * baca/tulis/hapus data user ke MySQL dilakukan di sini.
 *
 * Analogi sederhana:
 *   Controller  = Kasir (menerima request dari pelanggan)
 *   Repository  = Gudang (menyimpan & mengambil barang/data)
 *   Database    = Rak penyimpanan fisik
 *
 * Dengan mengextend JpaRepository<User, Long>, kita otomatis
 * mendapatkan banyak method siap pakai TANPA perlu menulis SQL:
 *
 *   save(user)           -> INSERT atau UPDATE data user
 *   findById(id)         -> SELECT * WHERE id = ?
 *   findAll()            -> SELECT * FROM users
 *   deleteById(id)       -> DELETE WHERE id = ?
 *   count()              -> SELECT COUNT(*) FROM users
 *   existsById(id)       -> apakah data dengan id ini ada?
 *
 * Method tambahan di bawah dibuat dengan konvensi nama Spring,
 * Spring otomatis tahu query SQL-nya dari nama method tersebut.
 *
 * ============================================================
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Mencari user berdasarkan email.
     *
     * Spring otomatis mengubah method ini menjadi SQL:
     *   SELECT * FROM users WHERE email = ?
     *
     * Mengembalikan Optional<User> artinya hasilnya bisa ada
     * (jika email ditemukan) atau kosong (jika tidak ditemukan).
     * Ini menghindari error NullPointerException.
     *
     * Cara pakai:
     *   Optional<User> user = userRepository.findByEmail("test@mail.com");
     *   if (user.isPresent()) { ... }
     *
     * @param email Email yang dicari
     * @return Optional berisi User jika ditemukan, kosong jika tidak
     */
    Optional<User> findByEmail(String email);

    /**
     * Mengecek apakah email sudah terdaftar di database.
     *
     * Spring otomatis mengubah method ini menjadi SQL:
     *   SELECT COUNT(*) > 0 FROM users WHERE email = ?
     *
     * Digunakan saat registrasi untuk memastikan tidak ada
     * email yang sama (duplikat).
     *
     * Cara pakai:
     *   boolean sudahAda = userRepository.existsByEmail("test@mail.com");
     *   if (sudahAda) { return "Email sudah terdaftar!"; }
     *
     * @param email Email yang dicek
     * @return true jika email sudah ada, false jika belum
     */
    boolean existsByEmail(String email);
}
