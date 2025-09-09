package jasionowicz.warhammer.builder.SelectedUpgrade;

import jasionowicz.warhammer.builder.Army.ArmyService;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SelectedUpgradeController {

    private final SelectedUpgradeService selectedUpgradeService;

    public SelectedUpgradeController(SelectedUpgradeService selectedUpgradeService) {
        this.selectedUpgradeService = selectedUpgradeService;
    }


    @PostMapping("/addUpgrade")
    public ResponseEntity<String> addUpgrade(@RequestParam Integer upgradeId) {
        selectedUpgradeService.addUpgrade(upgradeId);


        return ResponseEntity.ok("Upgrade added successfully");
    }


    @PostMapping("/removeSelectedUpgrade")
    public ResponseEntity<String> removeSelectedUpgrade(int upgradeId) {

        selectedUpgradeService.removeSelectedUpgrade(upgradeId);
        return ResponseEntity.ok().body("Error while deleting selectedUpgrade");
    }
}
