package com.example.micro.myMicroservice.web;

import com.example.micro.myMicroservice.domain.Tour;
import com.example.micro.myMicroservice.domain.TourRating;
import com.example.micro.myMicroservice.domain.TourRatingPk;
import com.example.micro.myMicroservice.repositories.TourRatingRepository;
import com.example.micro.myMicroservice.repositories.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/tours/{tourId}/ratings")
public class TourRatingController {
    TourRatingRepository tourRatingRepository;
    TourRepository tourRepository;

    @Autowired
    public TourRatingController(TourRatingRepository tourRatingRepository, TourRepository tourRepository) {
        this.tourRatingRepository = tourRatingRepository;
        this.tourRepository = tourRepository;
    }

    protected TourRatingController() {

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTourRating(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        Tour tour = verifyTour(tourId);
        tourRatingRepository.save(new TourRating(new TourRatingPk(tour, ratingDto.getCustomerId()), ratingDto.getScore(), ratingDto.getComment()));

    }

    @GetMapping
    public Page<RatingDto> getAllRatingsForTour(@PathVariable(value = "tourId") int tourId, Pageable pageable) {
        verifyTour(tourId);
        Page<TourRating> tourRatingPage = tourRatingRepository.findByPkTourId(tourId, pageable);
        List<RatingDto> ratingDtoList = tourRatingPage.getContent().stream().map(this::toDto).collect(Collectors.toList());

        return new PageImpl<RatingDto>(ratingDtoList, pageable, tourRatingPage.getTotalPages());
    }

    @GetMapping(path = "/average")
    public AbstractMap.SimpleEntry<String, Double> getAverage(@PathVariable(value = "tourId") int tourId) {
        verifyTour(tourId);
        List<TourRating> tourRatings = tourRatingRepository.findByPkTourId(tourId);
        OptionalDouble average = tourRatings.stream().mapToInt(TourRating::getScore).average();

        return new AbstractMap.SimpleEntry<String, Double>("average", average.isPresent() ? average.getAsDouble() : null);
    }

    @PutMapping
    public RatingDto updateWithPut(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        TourRating tourRating = verifyTourRating(tourId, ratingDto.getCustomerId());
        tourRating.setScore(ratingDto.getScore());
        tourRating.setComment(ratingDto.getComment());

        return toDto(tourRatingRepository.save(tourRating));
    }

    @PatchMapping
    public RatingDto updateWithPatch(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        TourRating tourRating = verifyTourRating(tourId, ratingDto.getCustomerId());
        Integer score = ratingDto.getScore();
        String comment = ratingDto.getComment();

        if(score!= null) {
            tourRating.setScore(score);
        }
        if (comment!=null) {
            tourRating.setComment(comment);
        }

        return toDto(tourRatingRepository.save(tourRating));
    }

    @DeleteMapping(path = "/{customerId}")
    public void delete(@PathVariable(value = "tourId") int tourId, @PathVariable(value = "customerId") int customerId) {
        TourRating tourRating = verifyTourRating(tourId, customerId);
        tourRatingRepository.delete(tourRating);
    }


    private RatingDto toDto(TourRating tourRating) {
        return new RatingDto(tourRating.getScore(), tourRating.getComment(), tourRating.getPk().getCustomerId());
    }


    private TourRating verifyTourRating(int tourId, int customerId) throws NoSuchElementException {
        TourRating rating = tourRatingRepository.findByPkTourIdAndPkCustomerId(tourId, customerId);
        if (rating == null) {
            throw new NoSuchElementException("Tour-Rating pair for request("
                    + tourId + " for customer" + customerId);
        }
        return rating;
    }


    private Tour verifyTour(int tourId) throws NoSuchElementException {
        return tourRepository.findById(tourId).orElseThrow(
                () -> new NoSuchElementException("Tour does not exist " + tourId));
    }

    /**
     * Exception handler if NoSuchElementException is thrown in this Controller
     *
     * @param ex
     * @return Error message String.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex) {
        return ex.getMessage();

    }

}
