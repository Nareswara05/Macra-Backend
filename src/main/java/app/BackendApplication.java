package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * BackendApplication - Entry Point Aplikasi
 * ============================================================
 *
 * Ini adalah kelas utama yang menjalankan seluruh aplikasi
 * Spring Boot. Ketika kamu menjalankan perintah:
 *
 *   .\mvnw.cmd spring-boot:run
 *
 * Program akan masuk ke method main() di sini, lalu Spring Boot
 * secara otomatis akan:
 *   1. Membaca konfigurasi dari application.properties
 *   2. Terhubung ke database MySQL
 *   3. Mendaftarkan semua endpoint (controller)
 *   4. Menjalankan web server di port 8080
 *
 * Anotasi @SpringBootApplication adalah kombinasi dari:
 *   - @Configuration       -> file ini adalah konfigurasi
 *   - @EnableAutoConfiguration -> aktifkan konfigurasi otomatis
 *   - @ComponentScan       -> scan semua class di package 'app'
 *
 * ============================================================
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Method utama yang pertama kali dijalankan saat aplikasi start.
     *
     * @param args Argumen dari command line (biasanya kosong)
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
