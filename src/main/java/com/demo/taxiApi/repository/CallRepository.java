package com.demo.taxiApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.taxiApi.domain.Call;


/**
 * @author yunsung Kim
 */
public interface CallRepository extends JpaRepository<Call, Long> {

}
