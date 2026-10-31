package bal.varliklar;

/**
 * Bisiklet sınıfı - Bisiklet bilgisini tutar.
 * Açıklama: Basit alanlar, null kullanılmaz.
 */
public class Bisiklet {
    public int id = -1;
    public String bisikletKodu = "";
    public String modelBilgisi = "";
    public String durum = "Müsait"; // örnek: "Müsait", "Kirada", "Bakımda"
    public int bulunduguIstasyonId = -1;
    public int kiralanmaSayisi = 0;
    public boolean silindiMi = false;
    // Basit constructor
    public Bisiklet() {}

    public String toString() {
        return bisikletKodu + " (" + modelBilgisi + ")";
    }

    public Bisiklet(int id, String bisikletKodu, String modelBilgisi, String durum, int bulunduguIstasyonId, int kiralanmaSayisi) {
        this.id = id;
        this.bisikletKodu = bisikletKodu != null ? bisikletKodu : "";
        this.modelBilgisi = modelBilgisi != null ? modelBilgisi : "";
        this.durum = durum != null ? durum : "";
        this.bulunduguIstasyonId = bulunduguIstasyonId;
        this.kiralanmaSayisi = kiralanmaSayisi;
    }
}
