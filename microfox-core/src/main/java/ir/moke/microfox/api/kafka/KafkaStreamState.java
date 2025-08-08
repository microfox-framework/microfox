package ir.moke.microfox.api.kafka;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum KafkaStreamState {
    CREATED(1, 3),
    REBALANCING(2, 3, 5),
    RUNNING(1, 2, 3, 5),
    PENDING_SHUTDOWN(4),
    NOT_RUNNING,
    PENDING_ERROR(6),
    ERROR;
    private final Set<Integer> validTransitions = new HashSet<>();

    KafkaStreamState(final Integer... validTransitions) {
        this.validTransitions.addAll(Arrays.asList(validTransitions));
    }

    public boolean hasNotStarted() {
        return equals(CREATED);
    }

    public boolean isRunningOrRebalancing() {
        return equals(RUNNING) || equals(REBALANCING);
    }

    public boolean isShuttingDown() {
        return equals(PENDING_SHUTDOWN) || equals(PENDING_ERROR);
    }

    public boolean hasCompletedShutdown() {
        return equals(NOT_RUNNING) || equals(ERROR);
    }

    public boolean hasStartedOrFinishedShuttingDown() {
        return isShuttingDown() || hasCompletedShutdown();
    }

    public boolean isValidTransition(final KafkaStreamState newState) {
        return validTransitions.contains(newState.ordinal());
    }
}
