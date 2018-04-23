package com.kis.health.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.kis.health.domain.Address;
import com.kis.health.domain.*; // for static metamodels
import com.kis.health.repository.AddressRepository;
import com.kis.health.service.dto.AddressCriteria;


/**
 * Service for executing complex queries for Address entities in the database.
 * The main input is a {@link AddressCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Address} or a {@link Page} of {@link Address} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AddressQueryService extends QueryService<Address> {

    private final Logger log = LoggerFactory.getLogger(AddressQueryService.class);


    private final AddressRepository addressRepository;

    public AddressQueryService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Return a {@link List} of {@link Address} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Address> findByCriteria(AddressCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Address> specification = createSpecification(criteria);
        return addressRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Address} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Address> findByCriteria(AddressCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Address> specification = createSpecification(criteria);
        return addressRepository.findAll(specification, page);
    }

    /**
     * Function to convert AddressCriteria to a {@link Specifications}
     */
    private Specifications<Address> createSpecification(AddressCriteria criteria) {
        Specifications<Address> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Address_.id));
            }
            if (criteria.getAddressLine() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine(), Address_.addressLine));
            }
            if (criteria.getAddressLine2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine2(), Address_.addressLine2));
            }
            if (criteria.getNeighborhood() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNeighborhood(), Address_.neighborhood));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), Address_.city));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildStringSpecification(criteria.getState(), Address_.state));
            }
            if (criteria.getCountry() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountry(), Address_.country));
            }
            if (criteria.getZipCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getZipCode(), Address_.zipCode));
            }
        }
        return specification;
    }

}
