package ir.moke.microfox.system;

import ir.moke.kafir.annotation.POST;

public interface MicroFoxAdminAPI {
    @POST("/api/v1/statistics")
    void statistics(SystemDTO dto);
}
