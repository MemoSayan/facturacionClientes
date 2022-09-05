package mx.com.spring.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import mx.com.spring.app.models.service.IUploadFileService;

@SpringBootApplication
public class SpringBootDajaJpaApplication implements CommandLineRunner{
	
	@Autowired
	IUploadFileService uploadsFileService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDajaJpaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		uploadsFileService.deleteAll();
		uploadsFileService.init();
		
	}

}
