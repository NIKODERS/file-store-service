package nikheel.rh.fss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nikheel.rh.fss.service.FileStoreServiceAPI;

@SpringBootApplication
public class FileStoreServiceApplication implements CommandLineRunner {

    @Autowired
    private FileStoreServiceAPI fileStoreService;

    public static void main(String[] args) {
        SpringApplication.run(FileStoreServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    	//to support eager loading for in memory DB,loading data into cache at the same time
        fileStoreService.loadExistingFiles();
    }

}
