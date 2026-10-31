package ui.dogrulama;

public class GirisDogrulama {

    public static boolean bosMu(String deger) {
        return deger == null || deger.trim().equals("");
    }

    public static boolean zorunluAlanDoluMu(String... alanlar) {
        if (alanlar == null) return false;
        for (String a : alanlar) {
            if (bosMu(a)) return false;
        }
        return true;
    }

    public static boolean sayiGecerliMi(int deger) {
        return deger != -1;
    }

    public static boolean pozitifMi(int deger) {
        return deger > 0;
    }

    public static boolean kapasiteGecerliMi(int kapasite) {
        return kapasite >= 0;
    }

    public static boolean telefonGecerliMi(String telefon) {
        if (bosMu(telefon)) return false;
        // basit: en az 10 rakam
        String rakam = telefon.replaceAll("[^0-9]", "");
        return rakam.length() >= 10;
    }
}