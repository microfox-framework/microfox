package ir.moke.microfox.api.http;

import java.util.List;

public interface SecuredRoute extends Route {
    SecurityStrategy securityStrategy();
    List<String> authorities();
}
