package com.example.micro.myMicroservice.services;

import com.example.micro.myMicroservice.domain.TourPackage;
import com.example.micro.myMicroservice.repositories.TourPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TourPackageService {
    private TourPackageRepository repository;

    @Autowired
    public TourPackageService(TourPackageRepository repository) {
        this.repository = repository;
    }

    public TourPackage createTourPackage(String code, String name) {
        if (!repository.existsById(code)) {
            repository.save(new TourPackage(code, name));
        }
        return null;
    }

    public Iterable<TourPackage> lookup() {
        return repository.findAll();
    }

    public long total() {
        return repository.count();
    }
}
