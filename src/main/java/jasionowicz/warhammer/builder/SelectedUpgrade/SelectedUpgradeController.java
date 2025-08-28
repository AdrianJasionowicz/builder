package jasionowicz.warhammer.builder.SelectedUpgrade;

import jasionowicz.warhammer.builder.Army.ArmyService;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SelectedUpgradeController {

    private final SelectedUnitService selectedUnitService;
    private SelectedUpgradeService selectedUpgradeService;
    private ArmyService armyService;

    public SelectedUpgradeController(SelectedUpgradeService selectedUpgradeService, ArmyService armyService, SelectedUnitService selectedUnitService) {
        this.selectedUpgradeService = selectedUpgradeService;
        this.armyService = armyService;
        this.selectedUnitService = selectedUnitService;
    }


    @PostMapping("/addUpgrade")
    public ResponseEntity<String> addUpgrade(@RequestParam Integer upgradeId) {
        selectedUpgradeService.addUpgrade(upgradeId);

      //  armyService.calculateDedicatedPoints();
        selectedUnitService.calculateTotalCostOfUnits();

        return ResponseEntity.ok("Upgrade added successfully");
    }


    @PostMapping("/removeSelectedUpgrade")
    public ResponseEntity<String> removeSelectedUpgrade(int upgradeId) {
        selectedUnitService.calculateTotalCostOfUnits();

        selectedUpgradeService.removeSelectedUpgrade(upgradeId);
        return ResponseEntity.ok().body("Error while deleting selectedUpgrade");
    }

    @GetMapping("/units/{id}/upgrades")
    public ResponseEntity<List<SelectedUpgradeDTO>> getSelectedUpgrades(@PathVariable Integer id) {
        List<SelectedUpgradeDTO> selectedUpgradeDTO = selectedUpgradeService.getSelectedUpgradesBySelectedUnitId(id);

        return ResponseEntity.ok().body(selectedUpgradeDTO);
    }
}
