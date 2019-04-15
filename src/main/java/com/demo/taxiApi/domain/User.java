package com.demo.taxiApi.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author yunsung Kim
 */
@Entity(name = "USERS")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id @GeneratedValue
    @JsonProperty(access = Access.WRITE_ONLY)
    private Long id;

    private String email;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserTypeCode userType;
}
