package ir.moke.microfox.api.job;

public class JobOption {
    private final boolean allowConcurrent;
    private final boolean distributed;
    private final String identity;

    private JobOption(Builder builder) {
        this.allowConcurrent = builder.allowConcurrent;
        this.distributed = builder.distributed;
        this.identity = builder.identity;
    }

    public boolean isAllowConcurrent() {
        return allowConcurrent;
    }

    public boolean isDistributed() {
        return distributed;
    }

    public String getIdentity() {
        return identity;
    }

    public static class Builder {
        private boolean allowConcurrent = false;
        private boolean distributed = false;
        private String identity;

        public Builder setAllowConcurrent(boolean allowConcurrent) {
            this.allowConcurrent = allowConcurrent;
            return this;
        }

        public Builder setDistributed(boolean distributed) {
            this.distributed = distributed;
            return this;
        }

        public Builder setIdentity(String identity) {
            this.identity = identity;
            return this;
        }

        public JobOption build() {
            return new JobOption(this);
        }
    }
}
