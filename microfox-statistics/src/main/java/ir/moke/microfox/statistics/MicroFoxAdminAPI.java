package ir.moke.microfox.statistics;

import ir.moke.kafir.annotation.POST;

public interface MicroFoxAdminAPI {
    @POST("/api/v1/statistics")
    void statistics(StatisticsDTO dto);
}
