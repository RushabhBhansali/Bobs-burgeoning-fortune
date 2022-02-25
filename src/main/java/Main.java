import com.bob.PrintFortune;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.http.HttpClient;
import java.time.Duration;


public class Main {

    public static void main(String[] args) {
        String filepath = args[0];
        //String filepath = "./src/main/resources/bobs_crypto.txt";
        try {
            //new PrintFortune(new FileReader("C:\\Software Projects\\Bobs-burgeoning-fortune\\src\\bobs_crypto.txt")).readFile();
            System.out.println("If using relative path, the application is looking for the file in working directory: +"
                    + System.getProperty("user.dir"));

            new PrintFortune(new FileReader(filepath), HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
            ).processFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
