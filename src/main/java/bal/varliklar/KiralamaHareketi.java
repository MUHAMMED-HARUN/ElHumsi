package bal.varliklar;

/**
 * KiralamaHareketi sınıfı - Bisiklet kiralama ve iade hareketlerini tutar.
 * Açıklama: Null kullanılmaz, boş değerler -1 veya "" olarak atanır.
 */
public class KiralamaHareketi {
    public int id = -1;
    public String bisikletKodu = "";
    public int kullaniciId = -1;
    public int baslangicIstasyonId = -1;
    public int iadeIstasyonId = -1;
    public boolean aktifMi = true;
    public boolean silindiMi = false;

    // Basit constructor
    public KiralamaHareketi() {}

    public KiralamaHareketi(int id, String bisikletKodu, int kullaniciId, int baslangicIstasyonId, int iadeIstasyonId, boolean aktifMi) {
        this.id = id;
        this.bisikletKodu = bisikletKodu != null ? bisikletKodu : "";
        this.kullaniciId = kullaniciId;
        this.baslangicIstasyonId = baslangicIstasyonId;
        this.iadeIstasyonId = iadeIstasyonId;
        this.aktifMi = aktifMi;
    }
}
