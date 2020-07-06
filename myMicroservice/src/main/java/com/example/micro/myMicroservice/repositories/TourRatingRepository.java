package com.example.micro.myMicroservice.repositories;



import com.example.micro.myMicroservice.domain.TourRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

/**
 * Tour Rating Repository Interface
 *
 * Created by Mary Ellen Bowman
 */
@RepositoryRestResource(exported = false)
public interface TourRatingRepository extends JpaRepository<TourRating, Integer> {

    List<TourRating> findByTourId(Integer tourId);

    Page<TourRating> findByTourId(Integer tourId, Pageable pageable);

    Optional<TourRating> findByTourIdAndCustomerId(Integer tourId, Integer customerId);
}