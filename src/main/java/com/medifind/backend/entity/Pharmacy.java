package com.medifind.backend.entity;

import com.medifind.backend.enums.PharmacyApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pharmacies")
@Getter
@Setter
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pharmacyName;

    private String ownerName;

    @Column(unique = true)
    private String email;

    private String phone;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PharmacyApprovalStatus approvalStatus;

    @Column(length = 500)
    private String rejectionReason;
}