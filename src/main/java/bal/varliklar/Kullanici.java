package bal.varliklar;

/**
 * Kullanıcı sınıfı - Sistemdeki kullanıcıyı temsil eder.
 * Açıklama: Basit alanlar, null kullanılmaz.
 */
public class Kullanici {
    public int id = -1;
    public String adSoyad = "";
    public String telefon = "";
    public boolean silindiMi = false;

    // Basit constructor
    public Kullanici() {}

    public String toString() {
        return id + " - " + adSoyad;
    }

    public Kullanici(int id, String adSoyad, String telefon) {
        this.id = id;
        this.adSoyad = adSoyad != null ? adSoyad : "";
        this.telefon = telefon != null ? telefon : "";
    }
}
