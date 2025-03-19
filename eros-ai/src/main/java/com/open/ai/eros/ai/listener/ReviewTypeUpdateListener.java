package com.open.ai.eros.ai.listener;

import com.open.ai.eros.db.event.ReviewTypeUpdatedEvent;
import com.open.ai.eros.ai.strategy.ReviewStatusStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewTypeUpdateListener {

    private final List<ReviewStatusStrategy> strategies;

    @EventListener
    public void handleReviewTypeUpdate(ReviewTypeUpdatedEvent event) {
        strategies.stream()
                .filter(strategy -> strategy.supports(event.getNewType()))
                .forEach(strategy -> strategy.handle(event.getAmResume()));
    }
}
