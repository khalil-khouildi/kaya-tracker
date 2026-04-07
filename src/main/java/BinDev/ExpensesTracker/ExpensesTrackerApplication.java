package BinDev.ExpensesTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpensesTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpensesTrackerApplication.class, args);
	}

}
