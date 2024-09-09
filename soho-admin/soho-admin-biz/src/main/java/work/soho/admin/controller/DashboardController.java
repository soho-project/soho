package work.soho.admin.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.api.admin.event.DashboardEvent;
import work.soho.common.core.result.R;

@RestController
@Api(tags = "Dashboard")
@RequestMapping("/admin/admin/adminDashboard")
public class DashboardController {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/index")
    public R<DashboardEvent> index() {
        DashboardEvent numberCardEvent = new DashboardEvent("");
        applicationEventPublisher.publishEvent(numberCardEvent);
        return R.success(numberCardEvent);
    }
}
