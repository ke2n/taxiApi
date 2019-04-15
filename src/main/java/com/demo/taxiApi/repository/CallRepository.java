package com.demo.taxiApi.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.demo.taxiApi.domain.Call;


/**
 * @author yunsung Kim
 */
public interface CallRepository extends JpaRepository<Call, Long> {

    @EntityGraph(attributePaths = "driver")
    @Query("select c from Call c join fetch c.passenger")
    List<Call> findAllBy(Pageable pageable);
}
