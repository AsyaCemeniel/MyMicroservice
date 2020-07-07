package com.example.micro.myMicroservice.web;


import com.example.micro.myMicroservice.domain.TourRating;
import com.example.micro.myMicroservice.services.TourRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping(path = "/ratings")
public class RatingController {
    private TourRatingService tourRatingService;
    private RatingAssembler assembler;

    @Autowired
    public RatingController(TourRatingService tourRatingService, RatingAssembler assembler) {
        this.tourRatingService = tourRatingService;
        this.assembler = assembler;
    }

    @GetMapping
    public Collection<RatingDto> getAll() {
        return assembler.toCollectionModel(tourRatingService.lookupAll()).getContent();
    }

    @GetMapping("/{id}")
    public RatingDto getRating(@PathVariable("id") Integer id) {
        return assembler.toModel(tourRatingService.lookupRatingById(id)
                .orElseThrow(() -> new NoSuchElementException("Rating " + id + " not found")
                ));
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex) {
        return ex.getMessage();
    }
}
