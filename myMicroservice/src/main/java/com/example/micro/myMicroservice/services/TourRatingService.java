package com.example.micro.myMicroservice.services;


import com.example.micro.myMicroservice.domain.Tour;
import com.example.micro.myMicroservice.domain.TourRating;
import com.example.micro.myMicroservice.repositories.TourRatingRepository;
import com.example.micro.myMicroservice.repositories.TourRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class TourRatingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TourRatingService.class);

    private TourRatingRepository tourRatingRepository;
    private TourRepository tourRepository;


    @Autowired
    public TourRatingService(TourRatingRepository tourRatingRepository, TourRepository tourRepository) {
        this.tourRatingRepository = tourRatingRepository;
        this.tourRepository = tourRepository;
    }

    public void createNew(int tourId, Integer customerId, Integer score, String comment) throws NoSuchElementException {
        LOGGER.info("Create Rating for tour {} of customers {}", tourId, customerId);
        tourRatingRepository.save(new TourRating(verifyTour(tourId), customerId,
                score, comment));
    }

    public Optional<TourRating> lookupRatingById(int id)  {
        LOGGER.info("Lookup Rating {}", id);
        return tourRatingRepository.findById(id);
    }


    public List<TourRating> lookupAll()  {
        LOGGER.info("Lookup all Ratings");
        return tourRatingRepository.findAll();
    }


    public Page<TourRating> lookupRatings(int tourId, Pageable pageable) throws NoSuchElementException  {
        LOGGER.info("Lookup Rating for tour {}", tourId);
        return tourRatingRepository.findByTourId(verifyTour(tourId).getId(), pageable);
    }


    public TourRating update(int tourId, Integer customerId, Integer score, String comment) throws NoSuchElementException {
        LOGGER.info("Update all of Rating for tour {} of customers {}", tourId, customerId);
        TourRating rating = verifyTourRating(tourId, customerId);
        rating.setScore(score);
        rating.setComment(comment);
        return tourRatingRepository.save(rating);
    }

    public TourRating updateSome(int tourId, Integer customerId, Integer score, String comment)
            throws NoSuchElementException {
        LOGGER.info("Update some of Rating for tour {} of customers {}", tourId, customerId);
        TourRating rating = verifyTourRating(tourId, customerId);
        if (score != null) {
            rating.setScore(score);
        }
        if (comment!= null) {
            rating.setComment(comment);
        }
        return tourRatingRepository.save(rating);
    }

    public void delete(int tourId, Integer customerId) throws NoSuchElementException {
        LOGGER.info("Delete Rating for tour {} and customer {}", tourId, customerId);
        TourRating rating = verifyTourRating(tourId, customerId);
        tourRatingRepository.delete(rating);
    }

    public Double getAverageScore(int tourId)  throws NoSuchElementException  {
        LOGGER.info("Get average score of tour {}", tourId);
        List<TourRating> ratings = tourRatingRepository.findByTourId(verifyTour(tourId).getId());
        OptionalDouble average = ratings.stream().mapToInt((rating) -> rating.getScore()).average();
        return average.isPresent() ? average.getAsDouble():null;
    }

    public void rateMany(int tourId,  int score, Integer [] customers) {
        LOGGER.info("Rate tour {} by customers {}", tourId, Arrays.asList(customers).toString());
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new NoSuchElementException());
        for (Integer c : customers) {
            LOGGER.debug("Attempt to create Tour Rating for customer {}", c);
            tourRatingRepository.save(new TourRating(tour, c, score));
        }
    }


    private Tour verifyTour(int tourId) throws NoSuchElementException {
        return tourRepository.findById(tourId).orElseThrow(() ->
                new NoSuchElementException("Tour does not exist " + tourId)
        );
    }


    private TourRating verifyTourRating(int tourId, int customerId) throws NoSuchElementException {
        return tourRatingRepository.findByTourIdAndCustomerId(tourId, customerId).orElseThrow(() ->
                new NoSuchElementException("Tour-Rating pair for request("
                        + tourId + " for customer" + customerId));
    }


}
