package jasionowicz.warhammer.builder.Upgrade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class UpgradeController {
    private final UpgradeService upgradesService;

    public UpgradeController(UpgradeService upgradeService) {
        this.upgradesService = upgradeService;
    }

    @PostMapping("/addUpgrade")
    public ResponseEntity<String> addUpgrade(@RequestBody UpgradeDTO upgrade) {
        upgradesService.addUpgrade(upgrade);

        return ResponseEntity.ok("Upgrade added");
    }

    @DeleteMapping("/deleteUpgrade")
    public ResponseEntity<String> deleteUpgrade(@RequestParam int id) {
        upgradesService.deleteUpgradeById(id);
        return ResponseEntity.ok("Upgrade deleted");
    }

    @PostMapping("/updateUpgrade")
    public ResponseEntity<String> updateUpgrade(@RequestBody UpgradeDTO upgrade) {
        upgradesService.updateUpgrade(upgrade.getId() , upgrade);
        return ResponseEntity.ok("Upgrade updated");
    }

}