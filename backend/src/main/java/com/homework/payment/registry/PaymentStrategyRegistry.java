package com.homework.payment.registry;

import com.homework.payment.annotation.PaymentProvider;
import com.homework.payment.dto.PaymentMethodInfoDTO;
import com.homework.payment.exception.PaymentException;
import com.homework.payment.strategy.PaymentStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that uses Java Reflection to dynamically discover all payment
 * providers at application startup — no hardcoded provider lists needed.
 *
 * HOW REFLECTION IS USED:
 *   1. ApplicationContext.getBeansWithAnnotation(@PaymentProvider.class)
 *      returns every Spring bean whose class carries the @PaymentProvider annotation.
 *   2. For each discovered bean, we call:
 *         clazz.getAnnotation(PaymentProvider.class)
 *      to read the annotation's metadata fields (name, displayName, description)
 *      at runtime via reflection — without knowing the concrete class at compile time.
 *   3. The strategy is stored in a map keyed by the provider's name string.
 *
 * WHY THIS MATTERS FOR OCP:
 *   Adding a new payment method requires ONLY creating a new class annotated
 *   with @PaymentProvider — this registry picks it up automatically.
 *   No registration code, no factory switch-case, no configuration change.
 */
@Slf4j
@Component
public class PaymentStrategyRegistry {

    private final ApplicationContext applicationContext;

    // Thread-safe map: provider name → strategy instance
    private final Map<String, PaymentStrategy> strategyMap = new ConcurrentHashMap<>();

    // Ordered list of provider metadata for the API endpoint
    private final List<PaymentMethodInfoDTO> availableProviders = new ArrayList<>();

    public PaymentStrategyRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Runs once after Spring context is fully initialized.
     * Scans for @PaymentProvider beans and registers them via Reflection.
     */
    @PostConstruct
    public void discoverAndRegisterProviders() {
        log.info("=== Payment Strategy Registry: Scanning for @PaymentProvider beans ===");

        // Step 1: Ask Spring for all beans annotated with @PaymentProvider
        Map<String, Object> annotatedBeans =
                applicationContext.getBeansWithAnnotation(PaymentProvider.class);

        for (Map.Entry<String, Object> entry : annotatedBeans.entrySet()) {
            Object bean = entry.getValue();
            Class<?> beanClass = bean.getClass();

            // Step 2: Use Java Reflection to read annotation metadata at runtime
            PaymentProvider annotation = beanClass.getAnnotation(PaymentProvider.class);

            // Spring CGLIB proxies wrap the real class, so we may need to walk up
            if (annotation == null) {
                annotation = beanClass.getSuperclass().getAnnotation(PaymentProvider.class);
            }

            if (annotation == null) {
                log.warn("Bean '{}' found via annotation scan but annotation not readable — skipping.", entry.getKey());
                continue;
            }

            if (!(bean instanceof PaymentStrategy strategy)) {
                log.warn("Bean '{}' has @PaymentProvider but does not implement PaymentStrategy — skipping.", entry.getKey());
                continue;
            }

            // Step 3: Register the strategy
            String providerName = annotation.name();
            strategyMap.put(providerName, strategy);

            // Step 4: Build metadata DTO entirely from reflected annotation values
            PaymentMethodInfoDTO info = PaymentMethodInfoDTO.builder()
                    .name(annotation.name())
                    .displayName(annotation.displayName())
                    .description(annotation.description())
                    .build();
            availableProviders.add(info);

            log.info("  Registered payment provider: '{}' → {} ({})",
                    providerName, beanClass.getSimpleName(), annotation.displayName());
        }

        log.info("=== Registry initialized with {} provider(s): {} ===",
                strategyMap.size(), strategyMap.keySet());
    }

    /**
     * Looks up a payment strategy by provider name.
     *
     * @param providerName the name from @PaymentProvider.name() (e.g. "CREDIT_CARD")
     * @return the matching PaymentStrategy
     * @throws PaymentException if no strategy is registered for the given name
     */
    public PaymentStrategy getStrategy(String providerName) {
        PaymentStrategy strategy = strategyMap.get(providerName);
        if (strategy == null) {
            throw new PaymentException(
                    "Unsupported payment method: '" + providerName +
                    "'. Available methods: " + strategyMap.keySet()
            );
        }
        return strategy;
    }

    /**
     * Returns metadata for all registered payment providers.
     * Used by the /api/payments/methods endpoint.
     */
    public List<PaymentMethodInfoDTO> getAvailableProviders() {
        return List.copyOf(availableProviders);
    }

    /**
     * Checks whether a provider name is registered.
     */
    public boolean isSupported(String providerName) {
        return strategyMap.containsKey(providerName);
    }
}
