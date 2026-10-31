# AI Coding Skills & Architectural Rules: VY-016 (Bisiklet Kiralama ve İstasyon Yönetim Sistemi)

## 1. المبادئ الأساسية ومستوى الكود (Temel İlkeler ve Kod Seviyesi)
- **المشروع:** نظام تأجير الدراجات وإدارة المحطات (`VY-016 | Bisiklet Kiralama ve İstasyon Yönetim Sistemi`).
- **اللغة والواجهة:** Java (Standard JDK) + Java Swing (`javax.swing.*`, `java.awt.*`).
- **قاعدة التسمية (İsimlendirme Kuralı):** جميع أسماء الحزم (Packages)، الكلاسات (Classes)، الدوال (Methods)، والمتغيرات (Variables) يجب أن تكون **باللغة التركية** باستخدام الأحرف الإنجليزية القياسية (ASCII Turkish - مثال: `istasyonAdi`, `silindiMi`, `kaydet`).
- **البساطة المطلقة (1. Sınıf Öğrenci Seviyesi):**
  - يُمنع منعاً باتاً استخدام أي مكتبات خارجية كمالية مثل (AutoMapper, FluentValidation, Hibernate/ORM, Lombok, Streams API المعقدة، أو Lambda Expressions).
  - يُكتب كل المنطق يدوياً باستخدام أساسيات لغة جافا (`for`, `while`, `if-else`, `switch`) ليكون الكود مقروءاً وسهل الشرح لطالب سنة أولى.
  - المكتبة الوحيدة المسموحة للتعامل مع ملفات الـ JSON هي **`com.google.gson.Gson`**.

---

## 2. تمثيل القيم الفارغة والافتراضية (Boş ve Varsayılan Değer Kuralları)
يُمنع الاعتماد على `null` في المنطق البرمجي، وتُعتمد القيم الافتراضية التالية لتمثيل الحقول الفارغة أو غير المحددة:
- **الأرقام (`int`):** القيمة الفارغة أو الجديدة هي **`-1`** (مثال: `id = -1` يعني سجل جديد، `bulunduguIstasyonId = -1` يعني الدراجة خارج المحطات حالياً).
- **النصوص (`String`):** القيمة الفارغة هي **`""`** (نص فارغ وليس `null`).
- **التواريخ أو القيم الصغرى:** استخدام القيمة الدنيا الصريحة (`minDeger` أو `""`).
- **حالة الحذف (`boolean`):** كل كائن (Model) يجب أن يحتوي إلزامياً على حقل **`public boolean silindiMi = false;`** (المقابل لـ `isDeleted`).

---

## 3. هياكل البيانات المعتمدة (Kullanılacak Veri Yapıları)
تُستخدم هياكل البيانات القياسية والواضحة في جافا (`java.util.*`) بأسماء متغيرات تركية واضحة:
- **`ArrayList` / `LinkedList` (`Liste`):** للاحتفاظ بالقوائم الأساسية وترتيبها (مثل `istasyonListesi` و `bisikletListesi`).
- **`HashMap` (`Sozluk / Dict`):** للوصول السريع ($O(1)$) للكائنات عبر المعرف (مثل `bisikletSozlugu` لربط `bisikletKodu` بكائن الدراجة، أو `istasyonSozlugu` لربط `id` بكائن المحطة).
- **`HashSet` (`Kume / HashList`):** لمنع تكرار المعرفات أو الأكواد الفريدة (مثل `kodKumesi` للتأكد من عدم تكرار `bisikletKodu`).
- **`Stack` (`Yigin`):** لحفظ سجل العمليات الأخيرة (`islemYigini`) أو التراجع البسيط عند الحاجة.

---

## 4. المعمارية ثلاثية الطبقات بالمسميات التركية (3 Katmanlı Mimari)

### الطبقة الأولى: `dal` (Veri Erişim Katmanı - JSON Dosya İşlemleri)
- **اسم الكلاس الأساسي:** `JsonDosyaYoneticisi.java`
- **ملفات التخزين:** `istasyonlar.json`, `bisikletler.json`, `kullanicilar.json`, `kiralamalar.json`.
- **المسؤولية:** القراءة والكتابة من وإلى ملفات `.json` فقط. يستقبل نص `json` ويعيد نص `json` إلى طبقة الـ `bal`.
- **آلية الكتابة في الملفات (Tam Dosya Yeniden Yazma):**
  - عند استدعاء أي دالة كتابة (`ekle`, `guncelle`, `sil`)، يتم قراءة القائمة الحالية، إجراء التعديل المطلوب، ثم **إعادة كتابة ملف الـ JSON بالكامل من جديد**.
- **توقيع الدوال الإلزامي في `dal` (`JsonDosyaYoneticisi`):**
  1. **`int ekle(String jsonVeri)` (Add):** يولّد رقم معرّف جديد (`yeniId = enBuyukId + 1`)، يضيف السجل، يعيد كتابة الملف، **ويرجع `yeniId` (`int`)**.
  2. **`boolean guncelle(String jsonVeri)` (Update):** يبحث عن السجل بنفس الـ `id`، يستبدل بياناته، يعيد كتابة الملف، **ويرجع `true` أو `false` (`boolean`)**.
  3. **`boolean sil(int id)` (Delete):** إذا كان السجل مطلوباً حذفه (`silindiMi == true`)، **تتم إعادة كتابة ملف الـ JSON بدون هذا السجل نهائياً**، **وترجع الدالة `true` أو `false` (`boolean`)**.
  4. **`String tumunuGetirJson()` (GetAll):** تقرأ محتوى ملف الـ JSON بالكامل وتعيده كنص `String` إلى طبقة الـ `bal`.

---

### الطبقة الثانية: `bal` (İş Mantığı Katmanı - Business Logic Layer)

#### أ. كلاسات الجداول (`bal.varliklar` / TableClass)
جميع الكلاسات تحتوي على `int id = -1` و `boolean silindiMi = false`:
1. **`Istasyon.java` (المحطة):**
   - `public int id = -1;`
   - `public String istasyonAdi = "";`
   - `public int kapasite = 0;`
   - `public int mevcutBisikletSayisi = 0;`
   - `public boolean silindiMi = false;`
   - دالة مساعدة: `public int bosYerSayisiHesapla() { return kapasite - mevcutBisikletSayisi; }`.
2. **`Bisiklet.java` (الدراجة):**
   - `public int id = -1;`
   - `public String bisikletKodu = "";`
   - `public String modelBilgisi = "";`
   - `public String durum = "Müsait";` *(القيم المسموحة: `"Müsait"`, `"Kirada"`, `"Bakımda"`)*.
   - `public int bulunduguIstasyonId = -1;`
   - `public int kiralanmaSayisi = 0;`
   - `public boolean silindiMi = false;`
3. **`Kullanici.java` (المستخدم):**
   - `public int id = -1;`
   - `public String adSoyad = "";`
   - `public String telefon = "";`
   - `public boolean silindiMi = false;`
4. **`KiralamaHareketi.java` (حركة التأجير والإرجاع):**
   - `public int id = -1;`
   - `public String bisikletKodu = "";`
   - `public int kullaniciId = -1;`
   - `public int baslangicIstasyonId = -1;`
   - `public int iadeIstasyonId = -1;`
   - `public boolean aktifMi = true;`
   - `public boolean silindiMi = false;`

#### ب. دوال التحويل الإلزامية في كل خدمة داخل `bal.servisler`
كل كلاس خدمة (`IstasyonServisi`, `BisikletServisi`, `KullaniciServisi`, `KiralamaServisi`) يجب أن يحتوي صراحةً على دوال التحويل عبر `Gson`:
1. **`public String nesnedenJsona(Object nesne)` (`toJson`):** لتحويل الكائن أو القائمة إلى نص `JSON` قبل إرساله إلى الـ `dal`.
2. **`public T jsondanNesneye(String json)` و `public List<T> jsondanListeye(String json)` (`fromJson`):** لتحويل نص الـ `JSON` القادم من الـ `dal` إلى كائن أو قائمة.

#### ج. بوابة الحفظ الموحدة (`kaydet` - Save Gateway Pattern)
تكون دالة **`kaydet(T nesne)`** في كل كلاس خدمة داخل `bal.servisler` هي البوابة الوحيدة للتواصل بين `ui` و `bal`:

```java
public boolean kaydet(Istasyon istasyon) {
    // 1. حالة الحذف (Silme Durumu): إذا كان الكائن معلماً للحذف
    if (istasyon.silindiMi == true) {
        // التحقق من شروط الحذف: لا يمكن حذف محطة غير فارغة
        if (istasyon.mevcutBisikletSayisi > 0) {
            return false; // ممنوع حذف محطة فيها دراجات
        }
        return dosyaYoneticisi.sil(istasyon.id); // يعيد كتابة الملف بدون هذا السجل
    }
    // 2. حالة الإضافة (Ekleme Durumu): إذا كان المعرف يساوي -1
    else if (istasyon.id == -1) {
        String json = nesnedenJsona(istasyon);
        int yeniId = dosyaYoneticisi.ekle(json);
        istasyon.id = yeniId;
        return yeniId > 0;
    }
    // 3. حالة التعديل (Güncelleme Durumu): إذا لم يكن محذوفاً والـ id أكبر من -1
    else {
        String json = nesnedenJsona(istasyon);
        return dosyaYoneticisi.guncelle(json);
    }
}