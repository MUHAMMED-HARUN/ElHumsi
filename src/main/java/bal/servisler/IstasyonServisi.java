package bal.servisler;

import bal.varliklar.Istasyon;
import bal.veri_baglama.JsonBaglayici;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.JsonDosyaYoneticisi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * IstasyonServisi - İstasyon varlıkları için CRUD işlemleri ve kaydet geçidi.
 */
public class IstasyonServisi {
    private final JsonDosyaYoneticisi dosyaYoneticisi;
    private final Gson gson = new Gson();

    public IstasyonServisi() {
        this.dosyaYoneticisi = new JsonDosyaYoneticisi("istasyonlar.json");
    }

    // --- Dönüştürme fonksiyonları ---
    public String nesnedenJsona(Object nesne) {
        return gson.toJson(nesne);
    }

    public Istasyon jsondanNesneye(String json) {
        if (json == null || json.trim().equals("") || json.trim().equals("{}")) {
            return new Istasyon();
        }
        Istasyon i = gson.fromJson(json, Istasyon.class);
        if (i == null) {
            i = new Istasyon();
        }
        if (i.istasyonAdi == null) i.istasyonAdi = "";
        return i;
    }

    public List<Istasyon> jsondanListeye(String json) {
        List<Istasyon> liste = new ArrayList<Istasyon>();
        if (json == null || json.trim().equals("")) {
            return liste;
        }
        try {
            Type tip = new TypeToken<List<Istasyon>>(){}.getType();
            List<Istasyon> parsed = gson.fromJson(json, tip);
            if (parsed != null) {
                for (Istasyon i : parsed) {
                    if (i == null) continue;
                    if (i.istasyonAdi == null) i.istasyonAdi = "";
                    liste.add(i);
                }
            }
        } catch (Exception e) {
            // bozuk json -> boş liste
        }
        return liste;
    }

    // --- Kaydet geçidi ---
    public boolean kaydet(Istasyon istasyon) {
        if (istasyon == null) return false;
        if (istasyon.silindiMi == true) {
            if (istasyon.mevcutBisikletSayisi > 0) {
                return false;
            }
            return dosyaYoneticisi.sil(istasyon.id);
        } else if (istasyon.id == -1) {
            String json = nesnedenJsona(istasyon);
            int yeniId = dosyaYoneticisi.ekle(json);
            istasyon.id = yeniId;
            return yeniId >= 0;
        } else {
            String json = nesnedenJsona(istasyon);
            return dosyaYoneticisi.guncelle(json);
        }
    }

    // --- Eski/uyumlu CRUD ---
    public int ekle(Istasyon istasyon) {
        String json = JsonBaglayici.toJson(istasyon);
        int yeniId = dosyaYoneticisi.ekle(json);
        istasyon.id = yeniId;
        return yeniId;
    }

    public boolean guncelle(Istasyon istasyon) {
        String json = JsonBaglayici.toJson(istasyon);
        return dosyaYoneticisi.guncelle(json);
    }

    public boolean sil(int id) {
        return dosyaYoneticisi.sil(id);
    }

    public String tumunuGetir() {
        return dosyaYoneticisi.tumunuGetirJson();
    }

    public List<Istasyon> tumIstasyonlariGetir() {
        return jsondanListeye(dosyaYoneticisi.tumunuGetirJson());
    }

    public String getById(int id) {
        // Uyumluluk: eski imza (String döner)
        List<Istasyon> liste = tumIstasyonlariGetir();
        for (Istasyon i : liste) {
            if (i.id == id) {
                return nesnedenJsona(i);
            }
        }
        return "{}";
    }

    public Istasyon idIleGetir(int id) {
        List<Istasyon> liste = tumIstasyonlariGetir();
        for (Istasyon i : liste) {
            if (i.id == id) return i;
        }
        return new Istasyon();
    }
}
