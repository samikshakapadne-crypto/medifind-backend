package com.medifind.backend.service;

import com.medifind.backend.dto.request.RareMedicineRequestDto;
import com.medifind.backend.dto.response.RareMedicineResponse;
import com.medifind.backend.entity.RareMedicineRequest;
import com.medifind.backend.entity.User;
import com.medifind.backend.enums.RequestStatus;
import com.medifind.backend.repository.RareMedicineRepository;
import com.medifind.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RareMedicineService {

    private final RareMedicineRepository rareMedicineRepository;
    private final UserRepository userRepository;

    public RareMedicineService(
            RareMedicineRepository rareMedicineRepository,
            UserRepository userRepository
    ) {
        this.rareMedicineRepository = rareMedicineRepository;
        this.userRepository = userRepository;
    }

    public RareMedicineResponse createRequest(
            String customerEmail,
            RareMedicineRequestDto request
    ) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found")
                );

        RareMedicineRequest rareRequest = new RareMedicineRequest();

        rareRequest.setCustomer(customer);
        rareRequest.setMedicineName(request.getMedicineName().trim());
        rareRequest.setGenericName(request.getGenericName().trim());
        rareRequest.setStrength(request.getStrength().trim());
        rareRequest.setDescription(request.getDescription());

        rareRequest.setStatus(RequestStatus.PENDING);
        rareRequest.setRequestedAt(LocalDateTime.now());

        RareMedicineRequest savedRequest =
                rareMedicineRepository.save(rareRequest);

        return mapToResponse(savedRequest);
    }

    public List<RareMedicineResponse> getMyRequests(
            String customerEmail
    ) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found")
                );

        return rareMedicineRepository
                .findByCustomerIdOrderByRequestedAtDesc(
                        customer.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<RareMedicineResponse> getAllPendingRequests() {

        return rareMedicineRepository
                .findByStatusOrderByRequestedAtAsc(
                        RequestStatus.PENDING
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RareMedicineResponse getRequestById(Long id) {

        RareMedicineRequest request =
                rareMedicineRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Rare medicine request not found"
                                )
                        );

        return mapToResponse(request);
    }

    public RareMedicineResponse approveRequest(Long id) {

        RareMedicineRequest request =
                rareMedicineRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Rare medicine request not found"
                                )
                        );

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending requests can be approved"
            );
        }

        request.setStatus(RequestStatus.APPROVED);

        RareMedicineRequest savedRequest =
                rareMedicineRepository.save(request);

        return mapToResponse(savedRequest);
    }

    public RareMedicineResponse rejectRequest(Long id) {

        RareMedicineRequest request =
                rareMedicineRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Rare medicine request not found"
                                )
                        );

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending requests can be rejected"
            );
        }

        request.setStatus(RequestStatus.REJECTED);

        RareMedicineRequest savedRequest =
                rareMedicineRepository.save(request);

        return mapToResponse(savedRequest);
    }

    private RareMedicineResponse mapToResponse(
            RareMedicineRequest request
    ) {

        return new RareMedicineResponse(
                request.getId(),
                request.getMedicineName(),
                request.getGenericName(),
                request.getStrength(),
                request.getStatus(),
                request.getRequestedAt()
        );
    }
}