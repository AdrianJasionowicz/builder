package jasionowicz.warhammer.builder.Army;

import jakarta.persistence.criteria.CriteriaBuilder;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
public class ArmyController {

    private ArmyService armyService;

    public ArmyController(ArmyService armyService) {
        this.armyService = armyService;
    }

    @PostMapping("/setPointsRestriction")
    public Map<String, Double> setPoints(@RequestParam("points") double newPoints) {
        Map<String, Double> pointsRestriction;
        pointsRestriction = armyService.calculatePointsLimitsByType(newPoints);
        return pointsRestriction;
    }

    @GetMapping("/usedPoints")
    public Map<String, Double> usedPoints(@RequestParam Long armyId) {
        return armyService.calculateDedicatedPoints(armyId);
    }


    @PostMapping("/army/create")
    public ResponseEntity<Long> createArmy( @RequestParam String name,
                                            @RequestParam String faction,
                                            @RequestParam Double points, Authentication authentication) {
    Long armyId = armyService.createNewArmy(authentication,name,faction,points);
        return ResponseEntity.ok(armyId);
    }

    @GetMapping("/templates")
    public ResponseEntity<List<ArmyDTO>> getUserTemplates(Authentication authentication) {
        try {
            List<ArmyDTO> templates = armyService.getUserTemplates(authentication);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/template/{id}")
    public ResponseEntity<String> deleteTemplate(@PathVariable Long id, Authentication authentication) {
        try {
            armyService.deleteTemplate(id, authentication);
            return ResponseEntity.ok("Template deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting template: " + e.getMessage());
        }
    }

    @GetMapping("/template/{id}")
    public ResponseEntity<ArmyDTO> getTemplate(@PathVariable Long id, Authentication authentication) {
        try {
            ArmyDTO template = armyService.loadTemplate(id, authentication);
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/army/{armyId}/units")
    public ResponseEntity<List<SelectedUnitDTO>> getUnits(@PathVariable Long armyId, Authentication authentication) {
       List<SelectedUnitDTO> selectedUnitDTOList = armyService.getArmySelectedUnitsList(armyId,authentication);
        return ResponseEntity.ok(selectedUnitDTOList);
    }

    @DeleteMapping("/army/{armyId}/units/{unitId}")
    public ResponseEntity<String> deleteUnit(@PathVariable Long armyId, @PathVariable Integer unitId, Authentication authentication) {
        armyService.deleteSelectedUnitFromArmyTemplate(armyId,unitId,authentication);
        return ResponseEntity.ok("Unit deleted successfully");
    }

    @PostMapping("/army/{armyId}/addUnit/{unitId}")
    public ResponseEntity<Void> addUnit(@PathVariable Long armyId, @PathVariable Integer unitId, Authentication authentication) {
        armyService.addUnit(armyId,unitId);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/army/{armyId}/getUpgrades/{selectedUnitId}")
    public ResponseEntity<List<SelectedUpgradeDTO>> showSelectedUnitUpgrades(@PathVariable long armyId, @PathVariable Integer selectedUnitId,Authentication authentication) {
       List<SelectedUpgradeDTO> selectedUpgradeDTOList = armyService.getSelectedUnitSelectedUpgradesList(armyId,selectedUnitId,authentication);
        return selectedUpgradeDTOList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(selectedUpgradeDTOList);
    }
}