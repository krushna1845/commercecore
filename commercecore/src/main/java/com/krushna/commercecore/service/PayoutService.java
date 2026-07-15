package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.PayoutDTO;
import com.krushna.commercecore.model.Payout;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.PayoutRepository;
import com.krushna.commercecore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayoutService {

    private final PayoutRepository payoutRepository;
    private final UserRepository userRepository;
    private final SellerAnalyticsService sellerAnalyticsService;

    @Transactional
    public PayoutDTO requestPayout(Long sellerId, double requestedAmount) {
        if (requestedAmount <= 0) {
            throw new RuntimeException("Payout amount must be greater than zero");
        }

        double availablePayout = sellerAnalyticsService.getSellerAnalytics(sellerId).getRevenue().getAvailablePayout();

        if (requestedAmount > availablePayout) {
            throw new RuntimeException("Insufficient available balance. You requested " + requestedAmount + " but only " + availablePayout + " is available.");
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Payout payout = new Payout();
        payout.setSeller(seller);
        payout.setAmount(requestedAmount);
        payout.setStatus(Payout.Status.PENDING);
        
        payout = payoutRepository.save(payout);
        return mapToDTO(payout);
    }

    public List<PayoutDTO> getSellerPayouts(Long sellerId) {
        return payoutRepository.findBySellerIdOrderByCreatedAtDesc(sellerId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // For Platform Admins
    @Transactional
    public PayoutDTO processPayout(Long payoutId, String referenceNumber, boolean isSuccessful) {
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout request not found"));

        if (payout.getStatus() != Payout.Status.PENDING) {
            throw new RuntimeException("Only PENDING payouts can be processed");
        }

        if (isSuccessful) {
            payout.setStatus(Payout.Status.PAID);
            payout.setReferenceNumber(referenceNumber);
            payout.setPaidAt(LocalDateTime.now());
        } else {
            payout.setStatus(Payout.Status.FAILED);
            payout.setReferenceNumber(referenceNumber); // e.g. Error code from bank
        }

        return mapToDTO(payoutRepository.save(payout));
    }

    private PayoutDTO mapToDTO(Payout payout) {
        PayoutDTO dto = new PayoutDTO();
        dto.setId(payout.getId());
        dto.setSellerId(payout.getSeller().getId());
        dto.setSellerName(payout.getSeller().getUsername());
        dto.setAmount(payout.getAmount());
        dto.setStatus(payout.getStatus().name());
        dto.setReferenceNumber(payout.getReferenceNumber());
        dto.setCreatedAt(payout.getCreatedAt());
        dto.setPaidAt(payout.getPaidAt());
        return dto;
    }
}
