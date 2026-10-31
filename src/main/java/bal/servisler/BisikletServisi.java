package bal.servisler;

import bal.varliklar.Bisiklet;
import bal.veri_baglama.JsonBaglayici;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.JsonDosyaYoneticisi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BisikletServisi {
    private final JsonDosyaYoneticisi dosyaYoneticisi;
    private final Gson gson = new Gson();

    public BisikletServisi() {
        this.dosyaYoneticisi = new JsonDosyaYoneticisi("bisikletler.json");
    }

    public String nesnedenJsona(Object nesne) {
        return gson.toJson(nesne);
    }

    public Bisiklet jsondanNesneye(String json) {
        if (json == null || json.trim().equals("") || json.trim().equals("{}")) {
            return new Bisiklet();
        }
        Bisiklet b = gson.fromJson(json, Bisiklet.class);
        if (b == null) b = new Bisiklet();
        if (b.bisikletKodu == null) b.bisikletKodu = "";
        if (b.modelBilgisi == null) b.modelBilgisi = "";
        if (b.durum == null || b.durum.equals("")) b.durum = "Müsait";
        return b;
    }

    public List<Bisiklet> jsondanListeye(String json) {
        List<Bisiklet> liste = new ArrayList<Bisiklet>();
        if (json == null || json.trim().equals("")) return liste;
        try {
            Type tip = new TypeToken<List<Bisiklet>>(){}.getType();
            List<Bisiklet> parsed = gson.fromJson(json, tip);
            if (parsed != null) {
                for (Bisiklet b : parsed) {
                    if (b == null) continue;
                    if (b.bisikletKodu == null) b.bisikletKodu = "";
                    if (b.modelBilgisi == null) b.modelBilgisi = "";
                    if (b.durum == null || b.durum.equals("")) b.durum = "Müsait";
                    liste.add(b);
                }
            }
        } catch (Exception e) {
        }
        return liste;
    }

    public boolean kaydet(Bisiklet bisiklet) {
        if (bisiklet == null) return false;
        if (bisiklet.silindiMi == true) {
            // kirada ise silinemez
            if ("Kirada".equals(bisiklet.durum)) {
                return false;
            }
            return dosyaYoneticisi.sil(bisiklet.id);
        } else if (bisiklet.id == -1) {
            String json = nesnedenJsona(bisiklet);
            int yeniId = dosyaYoneticisi.ekle(json);
            bisiklet.id = yeniId;
            return yeniId >= 0;
        } else {
            String json = nesnedenJsona(bisiklet);
            return dosyaYoneticisi.guncelle(json);
        }
    }

    public int ekle(Bisiklet bisiklet) {
        String json = JsonBaglayici.toJson(bisiklet);
        int yeniId = dosyaYoneticisi.ekle(json);
        bisiklet.id = yeniId;
        return yeniId;
    }

    public boolean guncelle(Bisiklet bisiklet) {
        String json = JsonBaglayici.toJson(bisiklet);
        return dosyaYoneticisi.guncelle(json);
    }

    public boolean sil(int id) {
        return dosyaYoneticisi.sil(id);
    }

    public String tumunuGetir() {
        return dosyaYoneticisi.tumunuGetirJson();
    }

    public List<Bisiklet> tumBisikletleriGetir() {
        return jsondanListeye(dosyaYoneticisi.tumunuGetirJson());
    }

    public String getById(int id) {
        List<Bisiklet> liste = tumBisikletleriGetir();
        for (Bisiklet b : liste) {
            if (b.id == id) return nesnedenJsona(b);
        }
        return "{}";
    }

    public Bisiklet idIleGetir(int id) {
        List<Bisiklet> liste = tumBisikletleriGetir();
        for (Bisiklet b : liste) {
            if (b.id == id) return b;
        }
        return new Bisiklet();
    }

    public Bisiklet kodIleGetir(String kod) {
        List<Bisiklet> liste = tumBisikletleriGetir();
        for (Bisiklet b : liste) {
            if (b.bisikletKodu != null && b.bisikletKodu.equals(kod)) return b;
        }
        return new Bisiklet();
    }
}
