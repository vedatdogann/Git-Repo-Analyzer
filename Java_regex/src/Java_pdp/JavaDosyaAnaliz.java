/**
*
* @author VEDAT DOGAN - vedat202dogan@gmail.com
* @since  07.04.2024
* <p>
*         1-B Subesi
* </p>
*/

package Java_pdp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class JavaDosyaAnaliz {
		
	 public static void dosyaAnaliziYap(Path dosyaYolu) throws IOException {
	        List<String> satirlar = Files.readAllLines(dosyaYolu);

	        long javadocYorumSayisi = javadocYorumSayisiHesapla(satirlar);
	        long tekliYorumSayisi = tekSatirlikYorumSayisiHesapla(satirlar);
	        long cokluYorumSayisi = cokSatirlikYorumSayisiHesapla(satirlar);
	        long kodSatirSayisi = kodSatirSayisiHesapla(satirlar);
	        long loc = locHesapla(satirlar);
	        long fonksiyonSayisi = fonksiyonSayisiHesapla(satirlar);
	        
	        double yorumSapmaYuzdesi = yorumSapmaYuzdesiHesapla(javadocYorumSayisi, tekliYorumSayisi,cokluYorumSayisi, kodSatirSayisi, fonksiyonSayisi);


	        System.out.println("Sınıf: " + dosyaYolu.getFileName());
	        System.out.println("Javadoc Satır Sayısı: " + javadocYorumSayisi);
	        System.out.println("Diger Yorum Satır Sayısı: " + (tekliYorumSayisi+cokluYorumSayisi));
	        System.out.println("Kod Satır Sayısı: " + kodSatirSayisi);
	        System.out.println("LOC: " + loc);
	        System.out.println("Fonksiyon Sayısı: " + fonksiyonSayisi);
	        System.out.println("Yorum Sapma Yüzdesi: %" + yorumSapmaYuzdesi );
	        System.out.println("-----------------------------------------");

	       
	    }
    
    
	 //   ---------------------------JAVADOC YORUM SAYISI HESAPLAMA ---------------------------------------------------
	 
    private static long javadocYorumSayisiHesapla(List<String> satirlar) {     // Javadoc Yorum Satır Sayısı Hesaplama - ornek git reposu örnek alındığı icin başlangıç ve bitiş satırları dahil edilmedi 
   
        String sourceCode = String.join("\n", satirlar);

        																		// Javadoc yorumlarını bulmak için regex 
        Pattern pattern = Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL);   //pattern nesnesi oluşturduk 
        Matcher matcher = pattern.matcher(sourceCode);						   //matcher nesnesi oluşturduk

        long sayac = 0;
        while (matcher.find()) {
            																	//Javadoc yorum bloğu için satır sayısını hesaplama
            String javadocComment = matcher.group();
            // Yorumun satır sayısını al
            int linesInComment = javadocComment.split("\n").length;

            																	// Ornek repodan dolayı yorum çok satırlıysa, başlangıç ve bitiş satırlarını saymıyorum
            if (linesInComment > 1) {
                sayac += linesInComment - 2;     								// Başlangıç ve bitiş satırlarını hariç tut
            }
        }

        return sayac;
        }
    
    
    
	 //   ---------------------------DİGER YORUM SAYISI HESAPLAMA ---------------------------------------------------

    
    
    private static long tekSatirlikYorumSayisiHesapla(List<String> satirlar) {  
    																	// tek satırlı ve çok satırlı yorumları ayrı ayrı hesaba kattık işlem kolaylığı olması için
        String sourceCode = String.join("\n", satirlar);
        Pattern pattern = Pattern.compile("//.*", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(sourceCode);

        long count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }
    private static long cokSatirlikYorumSayisiHesapla(List<String> satirlar) {
        String sourceCode = String.join("\n", satirlar);
        
        Pattern pattern = Pattern.compile("/\\*(?!\\*).*?\\*/", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sourceCode);

        long count = 0;
        while (matcher.find()) {
            String match = matcher.group();
            long lineEnds = match.split("\r?\n", -1).length - 1;
            count += Math.max(0, lineEnds - 1); 							// Başlangıç ve bitiş satırlarını çıkardık
        }

        return count;
    }
    
 //---------------------------------------------- KOD SATIR SAYISI HESAPLAMA ----------------------------

    private static long kodSatirSayisiHesapla(List<String> satirlar) {
        long kodVeYorumIcerenSatirlar = 0;
        boolean cokluYorumIcinde = false;

        for (String satir : satirlar) {
            String temizSatir = satir.trim();
            if (temizSatir.startsWith("//") || (temizSatir.startsWith("/*") && temizSatir.endsWith("*/"))) {
                //sadece tek satır yorum içeriyor, kod içermiyor.
                continue;
            }
            if (temizSatir.startsWith("/*")) {
                cokluYorumIcinde = true;
                continue;
            }
            if (temizSatir.endsWith("*/")) {
                cokluYorumIcinde = false;
                continue;
            }
            if (cokluYorumIcinde || temizSatir.isEmpty()) { 	// Çok satırlı yorumların içindeysek veya satır boşsa dikkate alma
                
                continue;
            }
            													
            kodVeYorumIcerenSatirlar++;							// Yorumlar dışındaki her satırı say (yorum ile birlikte kod içeren satırlar dahil).
        }

        return kodVeYorumIcerenSatirlar;
        }

    //-----------------------------------------------LOC (Line of Code) HESAPLAMA ----------------------------
    
    
    private static long locHesapla(List<String> satirlar) {		// Dosyadaki toplam satır sayısı
        return satirlar.size(); 
    }

  //-----------------------------------------------FONKSİYON SAYISI HESAPLAMA ----------------------------

    private static long fonksiyonSayisiHesapla(List<String> satirlar) {
        long sayac = 0;
        for (String satir : satirlar) {
            satir = satir.trim(); 									// Başındaki ve sonundaki boşlukları temizle
            
            														// Metod tanımı olup olmadığını kontrol ederken sınıf tanımlarını dışarıda bırak
            														//"public class" gibi durumlarda fonksiyona dahil etmemesi icin ayrı ayrı hesaba kattık
            if ((satir.matches("public\\s+.*\\(.*\\).*") || 
                 satir.matches("protected\\s+.*\\(.*\\).*") || 
                 satir.matches("private\\s+.*\\(.*\\).*") ||
                 (satir.contains(" void ") && satir.contains("(") && satir.contains(")"))) && 
                 !satir.contains("=") && !satir.contains(";") && 
                 !satir.startsWith("//") && !satir.startsWith("/*") && 
                 !satir.startsWith("*") && !satir.matches(".*\\bclass\\b.*")) {
                sayac++;
            }
        }
        return sayac;
    }


  //-----------------------------------------------YORUM SAPMA YÜZDESİ HESAPLAMA ----------------------------
    
    private static double yorumSapmaYuzdesiHesapla(double javadocYorumSayisi, double tekliYorumSayisi,double cokluYorumSayisi, double kodSatirSayisi, double fonksiyonSayisi) {
        double YG = ((javadocYorumSayisi + (tekliYorumSayisi+cokluYorumSayisi)) * 0.8) / fonksiyonSayisi;
        double YH = ( kodSatirSayisi / fonksiyonSayisi) * 0.3;
        double sonuc=((100 * YG) / YH) -100;
        double yuvarlanmisSonuc = Math.round(sonuc * 100.0) / 100.0;                // belirtilen sonucu yuvarlama işlemi 
        return yuvarlanmisSonuc;
    }
    
   

    
}

