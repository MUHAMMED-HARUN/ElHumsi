package bal.varliklar;

/**
 * Istasyon sınıfı - Bisiklet istasyonunu temsil eder.
 * Açıklama: Bu sınıf basit alanlar içerir, null kullanılmaz.
 */
public class Istasyon {
    public int id = -1;
    public String istasyonAdi = "";
    public int kapasite = 0;
    public int mevcutBisikletSayisi = 0;
    public boolean silindiMi = false;

    /**
     * Boş yer sayısını hesaplar.
     * @return istasyonun boş kalan kapasitesi
     */
    public int bosYerSayisiHesapla() {
        return kapasite - mevcutBisikletSayisi;
    }

    // Basit constructor
    public Istasyon() {}

    public String toString() {
        return id + " - " + istasyonAdi;
    }

    public Istasyon(int id, String istasyonAdi, int kapasite, int mevcutBisikletSayisi) {
        this.id = id;
        this.istasyonAdi = istasyonAdi != null ? istasyonAdi : "";
        this.kapasite = kapasite;
        this.mevcutBisikletSayisi = mevcutBisikletSayisi;
    }
}
