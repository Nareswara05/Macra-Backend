package app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ============================================================
 * Util: HashUtils
 * ============================================================
 *
 * Util (Utility) adalah kelas pembantu yang berisi fungsi-fungsi
 * yang bisa dipakai di mana saja dalam aplikasi.
 *
 * Kelas ini bertugas untuk mengamankan password pengguna
 * menggunakan teknik "hashing".
 *
 * Apa itu Hashing?
 *   Hashing adalah proses mengubah teks (password asli) menjadi
 *   rangkaian karakter acak yang tidak bisa dikembalikan ke
 *   bentuk aslinya.
 *
 *   Contoh:
 *     Input  : "password123"
 *     Output : "ef92b778bafe771207..." (64 karakter hex)
 *
 * Kenapa password di-hash dan tidak disimpan langsung?
 *   Keamanan! Jika database bocor/diretas, peretas TIDAK BISA
 *   mengetahui password asli pengguna karena yang tersimpan
 *   hanyalah hasil hash-nya.
 *
 * Algoritma yang digunakan: SHA-256 (Secure Hash Algorithm 256-bit)
 *   - Standar industri yang banyak digunakan
 *   - Tersedia bawaan di Java (tidak perlu library tambahan)
 *   - Menghasilkan output 64 karakter hexadecimal
 *
 * ============================================================
 */
public class HashUtils {

    /**
     * Mengubah password plain text menjadi SHA-256 hash.
     *
     * Proses yang terjadi di dalam method ini:
     *   1. Buat instance MessageDigest dengan algoritma SHA-256
     *   2. Ubah string password menjadi array byte (UTF-8)
     *   3. Proses dengan SHA-256 -> hasilkan array byte hash
     *   4. Ubah setiap byte menjadi format hexadecimal (2 digit)
     *   5. Gabungkan semua jadi satu string panjang 64 karakter
     *
     * Contoh penggunaan:
     *   String hash = HashUtils.hashPassword("password123");
     *   // hash = "ef92b778bafe771207..." (64 karakter)
     *
     * Saat login, kita hash password yang dimasukkan user,
     * lalu dibandingkan dengan hash yang tersimpan di database.
     * Jika sama -> password benar. Jika beda -> password salah.
     *
     * @param password Password asli dalam bentuk plain text
     * @return String hash 64 karakter dalam format hexadecimal
     * @throws RuntimeException jika algoritma SHA-256 tidak tersedia (sangat jarang terjadi)
     */
    public static String hashPassword(String password) {
        try {
            // 1. Inisialisasi SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 2. Hash password -> hasilkan array byte
            byte[] encodedHash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8)
            );

            // 3. Ubah byte array menjadi string hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // padding 0 di depan jika perlu
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: Algoritma SHA-256 tidak ditemukan", e);
        }
    }
}
