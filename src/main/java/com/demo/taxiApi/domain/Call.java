package com.demo.taxiApi.domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author yunsung Kim
 */
@Entity
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User passenger;

    @ManyToOne
    private User driver;

    private String address;

    @Enumerated(EnumType.STRING)
    private CallTypeCode callType;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date requestDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date assignDate;
}
