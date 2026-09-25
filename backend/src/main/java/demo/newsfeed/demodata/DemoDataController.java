package demo.newsfeed.demodata;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
class DemoDataController {

    private final DemoDataService demoData;

    DemoDataController(DemoDataService demoData) {
        this.demoData = demoData;
    }

    /** Removes all current posts and restores the original fictional dataset. */
    @PostMapping("/reset")
    DemoDataService.ResetResult reset() {
        return demoData.reset();
    }
}
