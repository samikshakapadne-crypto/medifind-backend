package com.medifind.backend.entity;

import com.medifind.backend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rare_medicine_requests")
@Getter
@Setter
@NoArgsConstructor
public class RareMedicineRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(nullable = false)
    private String medicineName;

    @Column(nullable = false)
    private String genericName;

    @Column(nullable = false)
    private String strength;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime requestedAt;
}