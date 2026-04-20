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
 * Scans all Spring beans at startup and registers the ones annotated with @PaymentProvider.
 * Uses reflection to read the annotation metadata (name, displayName, description) at runtime
 * so we never need to hardcode or manually register new payment methods anywhere.
 */
@Slf4j
@Component
public class PaymentStrategyRegistry {

    private final ApplicationContext applicationContext;

    private final Map<String, PaymentStrategy> strategyMap = new ConcurrentHashMap<>();
    private final List<PaymentMethodInfoDTO> availableProviders = new ArrayList<>();

    public PaymentStrategyRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void discoverAndRegisterProviders() {
        log.info("=== Payment Strategy Registry: Scanning for @PaymentProvider beans ===");

        Map<String, Object> annotatedBeans =
                applicationContext.getBeansWithAnnotation(PaymentProvider.class);

        for (Map.Entry<String, Object> entry : annotatedBeans.entrySet()) {
            Object bean = entry.getValue();
            Class<?> beanClass = bean.getClass();

            // read the annotation via reflection
            PaymentProvider annotation = beanClass.getAnnotation(PaymentProvider.class);

            // Spring may wrap the bean in a CGLIB proxy, so check the superclass too
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

            String providerName = annotation.name();
            strategyMap.put(providerName, strategy);

            availableProviders.add(PaymentMethodInfoDTO.builder()
                    .name(annotation.name())
                    .displayName(annotation.displayName())
                    .description(annotation.description())
                    .build());

            log.info("  Registered payment provider: '{}' → {} ({})",
                    providerName, beanClass.getSimpleName(), annotation.displayName());
        }

        log.info("=== Registry initialized with {} provider(s): {} ===",
                strategyMap.size(), strategyMap.keySet());
    }

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

    public List<PaymentMethodInfoDTO> getAvailableProviders() {
        return List.copyOf(availableProviders);
    }

    public boolean isSupported(String providerName) {
        return strategyMap.containsKey(providerName);
    }
}
