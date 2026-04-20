package com.homework.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Carries metadata about a registered payment provider.
 * Populated via Reflection from @PaymentProvider annotation fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodInfoDTO {

    private String name;
    private String displayName;
    private String description;
}
