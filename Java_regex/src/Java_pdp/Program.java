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
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Scanner;
import java.io.File;

public class Program {
    public static void main(String[] args) throws IOException {
       
    	Scanner scanner = new Scanner(System.in);
        																				
        System.out.print("Lütfen analiz yapmak istediğiniz reponun linkini girin:  ");     // Kullanıcıdan GitHub deposu URL'sini al
       
        String repoUrl = scanner.nextLine();
        
        String currentWorkingDirectory = System.getProperty("user.dir");
        String destinationPath = currentWorkingDirectory + File.separator + "doc";

        prepareDestinationDirectory(destinationPath);                                // Hedef dizini hazırla, varsa içeriğini temizle
              
        GitService gitService = new GitService(repoUrl, destinationPath);            // GitService sınıfı ile GitHub deposunu klonlama
        try {
        	
            gitService.cloneRepo();   
            																	    // Klonlanan dizindeki .java dosyalarını bulma ve analiz etme
            try (Stream<Path> stream = Files.walk(Paths.get(destinationPath))) {
                stream.filter(file -> file.toString().endsWith(".java"))
                      .filter(Program::dosyaIcerigindeSinifVarMi) 					// Sadece class içeren dosyaları filtrele
                      .forEach(file -> {
                          try {
                              JavaDosyaAnaliz.dosyaAnaliziYap(file);
                          } catch (IOException e) {
                              e.printStackTrace();
                          }
                      });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close(); 														// Scanner nesnesini kapat
        }
    }

    private static void prepareDestinationDirectory(String destinationPath) throws IOException {
        File directory = new File(destinationPath);

        																		     // Eğer dizin varsa, içindekileri temizle.
        if (directory.exists() && directory.isDirectory()) {
            Files.walk(Paths.get(destinationPath))
                    .map(Path::toFile)
                    .sorted((o1, o2) -> -o1.compareTo(o2))
                    .forEach(File::delete);
        } else if (!directory.exists()) {
        																			  // Dizin yoksa oluştur.
            boolean wasDirectoryMade = directory.mkdirs();
            if (!wasDirectoryMade) {
                System.out.println("Hedef dizin oluşturulamadı: " + destinationPath);
                throw new IOException("Hedef dizin oluşturulamadı.");
            }
        }
    }
    
   
    private static boolean dosyaIcerigindeSinifVarMi(Path yol) {
        try (Stream<String> satirlar = Files.lines(yol)) {
            return satirlar.anyMatch(satir -> satir.contains(" class "));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
