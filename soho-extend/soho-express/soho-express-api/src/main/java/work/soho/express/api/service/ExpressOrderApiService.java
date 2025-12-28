package work.soho.express.api.service;

import work.soho.express.api.dto.SimpleExpressOrderDTO;

public interface ExpressOrderApiService {
    Boolean createExpressOrder(SimpleExpressOrderDTO simpleExpressOrderDTO);
}
