package com.example.micro.myMicroservice.services;

import com.example.micro.myMicroservice.domain.Difficulty;
import com.example.micro.myMicroservice.domain.Region;
import com.example.micro.myMicroservice.domain.Tour;
import com.example.micro.myMicroservice.domain.TourPackage;
import com.example.micro.myMicroservice.repositories.TourPackageRepository;
import com.example.micro.myMicroservice.repositories.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TourService {
    private TourPackageRepository packageRepository;
    private TourRepository tourRepository;

    @Autowired
    public TourService(TourPackageRepository packageRepository, TourRepository tourRepository) {
        this.packageRepository = packageRepository;
        this.tourRepository = tourRepository;
    }

    public Tour createTour(String title, String description, String blurb, Integer price, String duration, String bullets,
                           String keywords, String tourPackageName, Difficulty difficulty, Region region) {
        TourPackage tourPackage = packageRepository.findByName(tourPackageName).orElseThrow(
                () -> new RuntimeException("Tour package does not exist: " + tourPackageName));

        return tourRepository.save(new Tour(title,description, blurb, price, duration, bullets, keywords,
                                    tourPackage, difficulty,region));
    }

    public Iterable<Tour> lookup() {
        return tourRepository.findAll();
    }

    public long total() {
        return tourRepository.count();
    }
}
