package com.example.micro.myMicroservice;

import com.example.micro.myMicroservice.domain.Difficulty;
import com.example.micro.myMicroservice.domain.Region;
import com.example.micro.myMicroservice.services.TourPackageService;
import com.example.micro.myMicroservice.services.TourService;
import static com.example.micro.myMicroservice.MyMicroserviceApplication.TourFromFile.importTours;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class MyMicroserviceApplication implements CommandLineRunner {
	@Autowired
	private TourPackageService tourPackageService;
	@Autowired
	private TourService tourService;

	public static void main(String[] args) {
		SpringApplication.run(MyMicroserviceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//Create the default tour packages
		tourPackageService.createTourPackage("BC", "Backpack Cal");
		tourPackageService.createTourPackage("CC", "California Calm");
		tourPackageService.createTourPackage("CH", "California Hot springs");
		tourPackageService.createTourPackage("CY", "Cycle California");
		tourPackageService.createTourPackage("DS", "From Desert to Sea");
		tourPackageService.createTourPackage("KC", "Kids California");
		tourPackageService.createTourPackage("NW", "Nature Watch");
		tourPackageService.createTourPackage("SC", "Snowboard Cali");
		tourPackageService.createTourPackage("TC", "Taste of California");

		tourPackageService.lookup().forEach(System.out::println);

		importTours().forEach(tour-> tourService.createTour(
				tour.title,
				tour.description,
				tour.blurb,
				Integer.parseInt(tour.price),
				tour.length,
				tour.bullets,
				tour.keywords,
				tour.packageType,
				Difficulty.valueOf(tour.difficulty),
				Region.findByLabel(tour.region)));
		System.out.println("Number of tours =" + tourService.total());

	}

	/**
	 * Helper class to import the records in the ExploreCalifornia.json
	 */
	static class TourFromFile {
		//attributes as listed in the .json file
		private String packageType, title, description, blurb, price, length, bullets, keywords,  difficulty, region;

		/**
		 * Open the ExploreCalifornia.json, unmarshal every entry into a TourFromFile Object.
		 *
		 * @return a List of TourFromFile objects.
		 * @throws IOException if ObjectMapper unable to open file.
		 */
		static List<TourFromFile> importTours() throws IOException {
			return new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).
					readValue(TourFromFile.class.getResourceAsStream("/static/ExploreCalifornia.json"),
							new TypeReference<List<TourFromFile>>(){});
		}
	}
}