package com.example.micro.myMicroservice.web;


import com.example.micro.myMicroservice.domain.TourRating;
import com.example.micro.myMicroservice.repositories.TourRepository;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class RatingAssembler implements RepresentationModelAssembler<TourRating, RatingDto> {

    //Helper to fetch Spring Data Rest Repository links.
    private RepositoryEntityLinks entityLinks;

    public RatingAssembler( RepositoryEntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public RatingDto toModel(TourRating tourRating) {
        RatingDto rating = new RatingDto(tourRating.getScore(), tourRating.getComment(), tourRating.getCustomerId());

        // "self" : ".../ratings/{ratingId}"
        WebMvcLinkBuilder ratingLink = linkTo(methodOn(RatingController.class).getRating(tourRating.getId()));
        rating.add(ratingLink.withSelfRel());

        //"tour" : ".../tours/{tourId}"
       Link tourLink = entityLinks.linkToItemResource(TourRepository.class, tourRating.getTour().getId());
       rating.add(tourLink.withRel("tour"));
       return rating;
    }


}
