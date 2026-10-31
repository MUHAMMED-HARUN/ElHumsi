package dal;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * JsonDosyaYoneticisi - JSON dosyaları üzerinde temel CRUD işlemlerini sağlar.
 *
 * Kullanım: Yeni bir JsonDosyaYoneticisi("istasyonlar.json") oluşturulduğunda,
 * dosya proje kök dizinindeki "data" klasöründe aranır. Eğer dosya yoksa
 * otomatik olarak "[]" (boş dizi) içeriğiyle oluşturulur.
 */
public class JsonDosyaYoneticisi {
    private final Path dosyaYolu;
    private final Gson gson = new Gson();

    /**
     * Constructor
     * @param dosyaAdi JSON dosyasının adı (örnek: "istasyonlar.json")
     */
    public JsonDosyaYoneticisi(String dosyaAdi) {
        // Proje kök dizinini bul ve data klasörünü oluştur
        Path dataKlasor = Path.of("data");       
       try {
             Files.createDirectories(dataKlasor);
        } catch (IOException e) {
            throw new RuntimeException("Data klasoru olusturulamadi", e);
        }
        this.dosyaYolu = dataKlasor.resolve(dosyaAdi);
        // Dosya yoksa boş dizi ile oluştur
        if (!Files.exists(this.dosyaYolu)) {
            try {
                Files.writeString(this.dosyaYolu, "[]", StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException("Json dosyasi olusturulamadi: " + dosyaAdi, e);
            }
        }
    }

    /**
     * Tüm JSON içeriğini string olarak döndürür.
     */
    public String tumunuGetirJson() {
        try {
            return Files.readString(this.dosyaYolu, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Json dosyasi okunamadi", e);
        }
    }

    /**
     * Yeni bir kayıt ekler. id otomatik olarak en büyük mevcut id + 1 olarak atanır.
     * @param jsonVeri Yeni kaydın JSON stringi (id alanı olmadan)
     * @return Atanan yeni id değeri
     */
    public int ekle(String jsonVeri) {
        JsonArray dizi = readJsonArray();
        // En büyük id'yi bul
        int enBuyukId = -1;
        for (JsonElement el : dizi) {
            JsonObject obj = el.getAsJsonObject();
            if (obj.has("id")) {
                int cur = obj.get("id").getAsInt();
                if (cur > enBuyukId) {
                    enBuyukId = cur;
                }
            }
        }
        int yeniId = enBuyukId + 1;
        JsonObject yeniObj = JsonParser.parseString(jsonVeri).getAsJsonObject();
        yeniObj.addProperty("id", yeniId);
        dizi.add(yeniObj);
        writeJsonArray(dizi);
        return yeniId;
    }

    /**
     * Var olan bir kaydı günceller. jsonVeri içinde id alanı bulunmalıdır.
     * @param jsonVeri Güncellenmiş kaydın JSON stringi
     * @return Güncelleme başarılıysa true
     */
    public boolean guncelle(String jsonVeri) {
        JsonObject guncelObj = JsonParser.parseString(jsonVeri).getAsJsonObject();
        if (!guncelObj.has("id")) {
            return false;
        }
        int hedefId = guncelObj.get("id").getAsInt();
        JsonArray dizi = readJsonArray();
        boolean bulundu = false;
        for (int i = 0; i < dizi.size(); i++) {
            JsonObject obj = dizi.get(i).getAsJsonObject();
            if (obj.has("id") && obj.get("id").getAsInt() == hedefId) {
                dizi.set(i, guncelObj);
                bulundu = true;
                break;
            }
        }
        if (bulundu) {
            writeJsonArray(dizi);
        }
        return bulundu;
    }

    /**
     * Belirtilen id'ye sahip kaydı siler.
     * @param id Silinecek kaydın id'si
     * @return Silme başarılıysa true
     */
    public boolean sil(int id) {
        JsonArray dizi = readJsonArray();
        boolean silindi = false;
        for (int i = 0; i < dizi.size(); i++) {
            JsonObject obj = dizi.get(i).getAsJsonObject();
            if (obj.has("id") && obj.get("id").getAsInt() == id) {
                dizi.remove(i);
                silindi = true;
                break;
            }
        }
        if (silindi) {
            writeJsonArray(dizi);
        }
        return silindi;
    }

    // Yardımcı metodlar ---------------------------------------------------
    private JsonArray readJsonArray() {
        try {
            String icerik = Files.readString(this.dosyaYolu, StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(icerik);
            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            } else {
                // Dosya bozuksa yeni bir dizi oluştur
                return new JsonArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Json dosyasi okunamadi", e);
        }
    }

    private void writeJsonArray(JsonArray dizi) {
        try {
            String json = gson.toJson(dizi);
            Files.writeString(this.dosyaYolu, json, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Json dosyasi yazilamadi", e);
        }
    }
}
