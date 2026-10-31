package bal.servisler;

import bal.varliklar.Kullanici;
import bal.veri_baglama.JsonBaglayici;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.JsonDosyaYoneticisi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class KullaniciServisi {
    private final JsonDosyaYoneticisi dosyaYoneticisi;
    private final Gson gson = new Gson();

    public KullaniciServisi() {
        this.dosyaYoneticisi = new JsonDosyaYoneticisi("kullanicilar.json");
    }

    public String nesnedenJsona(Object nesne) {
        return gson.toJson(nesne);
    }

    public Kullanici jsondanNesneye(String json) {
        if (json == null || json.trim().equals("") || json.trim().equals("{}")) {
            return new Kullanici();
        }
        Kullanici k = gson.fromJson(json, Kullanici.class);
        if (k == null) k = new Kullanici();
        if (k.adSoyad == null) k.adSoyad = "";
        if (k.telefon == null) k.telefon = "";
        return k;
    }

    public List<Kullanici> jsondanListeye(String json) {
        List<Kullanici> liste = new ArrayList<Kullanici>();
        if (json == null || json.trim().equals("")) return liste;
        try {
            Type tip = new TypeToken<List<Kullanici>>(){}.getType();
            List<Kullanici> parsed = gson.fromJson(json, tip);
            if (parsed != null) {
                for (Kullanici k : parsed) {
                    if (k == null) continue;
                    if (k.adSoyad == null) k.adSoyad = "";
                    if (k.telefon == null) k.telefon = "";
                    liste.add(k);
                }
            }
        } catch (Exception e) {
        }
        return liste;
    }

    public boolean kaydet(Kullanici kullanici) {
        if (kullanici == null) return false;
        if (kullanici.silindiMi == true) {
            return dosyaYoneticisi.sil(kullanici.id);
        } else if (kullanici.id == -1) {
            String json = nesnedenJsona(kullanici);
            int yeniId = dosyaYoneticisi.ekle(json);
            kullanici.id = yeniId;
            return yeniId >= 0;
        } else {
            String json = nesnedenJsona(kullanici);
            return dosyaYoneticisi.guncelle(json);
        }
    }

    public int ekle(Kullanici kullanici) {
        String json = JsonBaglayici.toJson(kullanici);
        int yeniId = dosyaYoneticisi.ekle(json);
        kullanici.id = yeniId;
        return yeniId;
    }

    public boolean guncelle(Kullanici kullanici) {
        String json = JsonBaglayici.toJson(kullanici);
        return dosyaYoneticisi.guncelle(json);
    }

    public boolean sil(int id) {
        return dosyaYoneticisi.sil(id);
    }

    public String tumunuGetir() {
        return dosyaYoneticisi.tumunuGetirJson();
    }

    public List<Kullanici> tumKullanicilariGetir() {
        return jsondanListeye(dosyaYoneticisi.tumunuGetirJson());
    }

    public String getById(int id) {
        List<Kullanici> liste = tumKullanicilariGetir();
        for (Kullanici k : liste) {
            if (k.id == id) return nesnedenJsona(k);
        }
        return "{}";
    }

    public Kullanici idIleGetir(int id) {
        List<Kullanici> liste = tumKullanicilariGetir();
        for (Kullanici k : liste) {
            if (k.id == id) return k;
        }
        return new Kullanici();
    }
}
