# Automation Test Framework - Web UI & API (Final Project)

Repository ini merupakan portofolio Automation Testing yang menggabungkan pengujian **Web UI** dan **API** dalam satu framework terintegrasi. Project ini dibangun untuk memenuhi kriteria Final Project dengan standar industri tahun 2025.

##  Website & API Target
- **Web UI:** [Demoblaze](www.demoblaze.com) (E-commerce platform)
- **API:** [DummyAPI.io](dummyapi.io) (User Management System)

##  Tools & Library yang Digunakan
Framework ini dibangun dengan kombinasi tools modern:
- **Java 21 (Amazon Corretto)** sebagai bahasa pemrograman utama.
- **Gradle 9.2.1** sebagai build tool dan task manager.
- **Selenium Java 4.21.0** untuk otomasi pengujian Web UI.
- **Rest Assured 5.5.0** untuk otomasi pengujian API (REST).
- **Cucumber 7.20.1** untuk implementasi Behavior Driven Development (BDD) dengan format Gherkin.
- **JUnit 5 (JUnit Platform Suite)** sebagai test engine dan runner.
- **WebDriverManager** untuk manajemen driver browser otomatis.
- **GitHub Actions** untuk implementasi Continuous Integration (CI).

## Cara Menjalankan Test

### 1. Melalui IntelliJ IDEA (Recommended)
1. Buka proyek di [IntelliJ IDEA](www.jetbrains.com).
2. Pastikan SDK sudah diatur ke **Java 21**.
3. Navigasi ke direktori \`src/test/java/runners\`.
4. Klik kanan pada salah satu file runner:
    - \`ApiRunnerTest.java\` (untuk pengujian API).
    - \`WebRunnerTest.java\` (untuk pengujian Web UI).
5. Pilih **Run 'Runner...'**.

### 2. Melalui Terminal (Gradle CLI)
Gunakan perintah berikut untuk menjalankan seluruh rangkaian pengujian:
\`\`\`bash
./gradlew clean test
\`\`\`

##  Pelaporan (Reporting)
Setelah pengujian selesai, framework akan menghasilkan laporan berbasis HTML yang dapat ditemukan di direktori berikut:
- **Cucumber Report:** \`target/cucumber-reports.html\`
- **Gradle Test Report:** \`build/reports/tests/test/index.html\`

##  Struktur Project
Project menggunakan struktur paket yang terpisah antara Web dan API sesuai instruksi:
```text
src/test
├── java
│   └── api
│       ├── runners         <-- Runner Class (ApiRunnerTest, WebRunnerTest)
│       └── stepdefinitions <-- Implementasi kode Gherkin (Web & API)
└── resources
    └── features
        ├── api             <-- Skenario API (.feature)
        └── web             <-- Skenario Web UI (.feature)

