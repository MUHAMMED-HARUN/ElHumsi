package bal.veri_baglama;

import com.google.gson.Gson;

/**
 * JsonBaglayici - Basit JSON dönüşüm yardımcı sınıfı.
 * Kullanım: nesneleri JSON stringine ve tersine dönüştürür.
 */
public class JsonBaglayici {
    private static final Gson gson = new Gson();

    /**
     * Nesneyi JSON stringine çevirir.
     * @param obj JSON'a dönüştürülecek nesne
     * @return JSON string
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * JSON stringini belirtilen sınıfa dönüştürür.
     * @param json JSON string
     * @param clazz Hedef sınıf
     * @param <T> Tip
     * @return Dönüştürülmüş nesne
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
