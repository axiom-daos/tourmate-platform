package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configurable operational thresholds for {@code TourMonitoring} business rules.
 */
@Configuration
@ConfigurationProperties(prefix = "tourmate.monitoring")
@Getter
@Setter
public class TourMonitoringProperties {

    /**
     * Minimum number of checked-in participants required before a tour execution can start.
     */
    private int minCheckIns = 1;

    /**
     * Number of open MEDIUM/HIGH incidents required to automatically escalate the tour's RiskLevel.
     */
    private int mediumIncidentsThreshold = 2;

    /**
     * Allowed delay tolerance in minutes past a checkpoint's TimeWindow before generating a delay alert.
     */
    private long checkpointDelayToleranceMinutes = 15L;
}
