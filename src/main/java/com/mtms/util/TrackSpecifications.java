package com.mtms.util;



import com.mtms.model.FilterParameters;

import com.mtms.model.Track;

import org.springframework.data.jpa.domain.Specification;



import java.time.LocalDateTime;

import java.util.ArrayList;

import java.util.List;

import jakarta.persistence.criteria.Predicate;



public class TrackSpecifications {



    public static Specification<Track> filterTracks(FilterParameters filterParams) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            LocalDateTime start_time = null;

            LocalDateTime end_time = null;

            String input;



            input = filterParams.getSourceId();

            if (input != null && !input.isEmpty()) {

                predicates.add(criteriaBuilder.equal(root.get("source_id"), input));

            }

            input = filterParams.getTrackNumber();

            if (input != null && !input.isEmpty()) {

                predicates.add(criteriaBuilder.equal(root.get("track_number"), input));

            }

            input = filterParams.getIdentity();

            if (input != null && !input.isEmpty()) {

                predicates.add(criteriaBuilder.equal(root.get("identity_type"), input));

            }

            input = filterParams.getStatus();

            if (input != null && !input.isEmpty()) {

                predicates.add(criteriaBuilder.equal(root.get("track_status"), input));

            }

            input = filterParams.getSequenceNumber();

            if (input != null && !input.isEmpty()) {

                predicates.add(criteriaBuilder.equal(root.get("sequence_number"), input));

            }

            input = filterParams.getMinSpeed();

            if (input != null && !input.isEmpty()) {

                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), input));

            }

            input = filterParams.getMaxSpeed();

            if (input != null && !input.isEmpty()) {

                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("speed"), input));

            }



            input = filterParams.getObservationType();

            if (input != null && !input.isEmpty()) {

                if (input.equals("current")) {

                    start_time = LocalDateTime.now().minusMinutes(5);

                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("created_time"), start_time));

                }

                else {

                    start_time = LocalDateTime.of(2025, 6, 1, 0, 0, 0);

                    end_time = LocalDateTime.now().minusMinutes(5);

                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("created_time"), start_time));

                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("created_time"), end_time));

                }

            }

            else {

                input = filterParams.getStartTime();

                if (input != null && !input.isEmpty()) {

                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("created_time"), input));

                }

                input = filterParams.getEndTime();

                if (input != null && !input.isEmpty()) {

                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("created_time"), input));

                }

            }



            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };

    }

}