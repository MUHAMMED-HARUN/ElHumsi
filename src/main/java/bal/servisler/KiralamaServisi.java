package bal.servisler;

import bal.varliklar.Bisiklet;
import bal.varliklar.Istasyon;
import bal.varliklar.KiralamaHareketi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.JsonDosyaYoneticisi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class KiralamaServisi {
    private final JsonDosyaYoneticisi dosyaYoneticisi;
    private final Gson gson = new Gson();

    public KiralamaServisi() {
        this.dosyaYoneticisi = new JsonDosyaYoneticisi("kiralamalar.json");
    }

    public String nesnedenJsona(Object nesne) {
        return gson.toJson(nesne);
    }

    public KiralamaHareketi jsondanNesneye(String json) {
        if (json == null || json.trim().equals("") || json.trim().equals("{}")) {
            return new KiralamaHareketi();
        }
        KiralamaHareketi k = gson.fromJson(json, KiralamaHareketi.class);
        if (k == null) k = new KiralamaHareketi();
        if (k.bisikletKodu == null) k.bisikletKodu = "";
        return k;
    }

    public List<KiralamaHareketi> jsondanListeye(String json) {
        List<KiralamaHareketi> liste = new ArrayList<KiralamaHareketi>();
        if (json == null || json.trim().equals("")) return liste;
        try {
            Type tip = new TypeToken<List<KiralamaHareketi>>(){}.getType();
            List<KiralamaHareketi> parsed = gson.fromJson(json, tip);
            if (parsed != null) {
                for (KiralamaHareketi k : parsed) {
                    if (k == null) continue;
                    if (k.bisikletKodu == null) k.bisikletKodu = "";
                    liste.add(k);
                }
            }
        } catch (Exception e) {
        }
        return liste;
    }

    public boolean kaydet(KiralamaHareketi hareket) {
        if (hareket == null) return false;
        if (hareket.silindiMi == true) {
            return dosyaYoneticisi.sil(hareket.id);
        } else if (hareket.id == -1) {
            String json = nesnedenJsona(hareket);
            int yeniId = dosyaYoneticisi.ekle(json);
            hareket.id = yeniId;
            return yeniId >= 0;
        } else {
            String json = nesnedenJsona(hareket);
            return dosyaYoneticisi.guncelle(json);
        }
    }

    public List<KiralamaHareketi> tumHareketleriGetir() {
        return jsondanListeye(dosyaYoneticisi.tumunuGetirJson());
    }

    public List<KiralamaHareketi> aktifHareketleriGetir() {
        List<KiralamaHareketi> tum = tumHareketleriGetir();
        List<KiralamaHareketi> aktif = new ArrayList<KiralamaHareketi>();
        for (KiralamaHareketi k : tum) {
            if (k.aktifMi == true) aktif.add(k);
        }
        return aktif;
    }

    public List<KiralamaHareketi> kullaniciHareketleri(int kullaniciId) {
        List<KiralamaHareketi> tum = tumHareketleriGetir();
        List<KiralamaHareketi> sonuc = new ArrayList<KiralamaHareketi>();
        for (KiralamaHareketi k : tum) {
            if (k.kullaniciId == kullaniciId) sonuc.add(k);
        }
        return sonuc;
    }

    public boolean kirala(int kullaniciId, String bisikletKodu, int baslangicIstasyonId) {
        if (bisikletKodu == null || bisikletKodu.equals("")) return false;
        BisikletServisi bs = new BisikletServisi();
        IstasyonServisi is = new IstasyonServisi();
        Bisiklet bisiklet = bs.kodIleGetir(bisikletKodu);
        if (bisiklet.id == -1) return false;
        if (!"Müsait".equals(bisiklet.durum)) return false;
        Istasyon istasyon = is.idIleGetir(baslangicIstasyonId);
        if (istasyon.id == -1) return false;

        // istasyon sayısını düşür
        if (istasyon.mevcutBisikletSayisi > 0) {
            istasyon.mevcutBisikletSayisi = istasyon.mevcutBisikletSayisi - 1;
        }
        is.kaydet(istasyon);

        bisiklet.durum = "Kirada";
        bisiklet.bulunduguIstasyonId = -1;
        bisiklet.kiralanmaSayisi = bisiklet.kiralanmaSayisi + 1;
        bs.kaydet(bisiklet);

        KiralamaHareketi h = new KiralamaHareketi();
        h.bisikletKodu = bisikletKodu;
        h.kullaniciId = kullaniciId;
        h.baslangicIstasyonId = baslangicIstasyonId;
        h.iadeIstasyonId = -1;
        h.aktifMi = true;
        String json = nesnedenJsona(h);
        int yeniId = dosyaYoneticisi.ekle(json);
        h.id = yeniId;
        return yeniId >= 0;
    }

    public boolean iadeEt(String bisikletKodu, int iadeIstasyonId) {
        if (bisikletKodu == null || bisikletKodu.equals("")) return false;
        // aktif hareketi bul
        KiralamaHareketi hareket = null;
        List<KiralamaHareketi> tum = tumHareketleriGetir();
        for (int i = tum.size() - 1; i >= 0; i--) {
            KiralamaHareketi k = tum.get(i);
            if (bisikletKodu.equals(k.bisikletKodu) && k.aktifMi == true) {
                hareket = k;
                break;
            }
        }
        if (hareket == null) return false;

        BisikletServisi bs = new BisikletServisi();
        IstasyonServisi is = new IstasyonServisi();
        Bisiklet bisiklet = bs.kodIleGetir(bisikletKodu);
        if (bisiklet.id == -1) return false;
        if (!"Kirada".equals(bisiklet.durum)) return false;
        Istasyon istasyon = is.idIleGetir(iadeIstasyonId);
        if (istasyon.id == -1) return false;
        if (istasyon.mevcutBisikletSayisi >= istasyon.kapasite) {
            return false; // istasyon dolu
        }

        istasyon.mevcutBisikletSayisi = istasyon.mevcutBisikletSayisi + 1;
        is.kaydet(istasyon);

        bisiklet.durum = "Müsait";
        bisiklet.bulunduguIstasyonId = iadeIstasyonId;
        bs.kaydet(bisiklet);

        hareket.iadeIstasyonId = iadeIstasyonId;
        hareket.aktifMi = false;
        kaydet(hareket);
        return true;
    }
}
