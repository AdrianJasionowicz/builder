package jasionowicz.warhammer.builder.Unit;

import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UnitController {
    private UnitService unitService;
    private SelectedUnitService selectedUnitService;


    public UnitController(UnitService unitService, SelectedUnitService selectedUnitService) {
        this.unitService = unitService;
        this.selectedUnitService = selectedUnitService;
    }

    @GetMapping("/getAllUnits")
    public ResponseEntity<List<UnitDTO>> getAllUnits(@RequestParam String nation) {
        List<UnitDTO> units = unitService.getAllUnitsByNation(nation);
        return units.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(units);
    }


    @DeleteMapping("/admin/unit/{id}")
     @PreAuthorize("hasRole('ADMIN')")
    public void deleteUnit(@PathVariable Integer id) {
        unitService.deleteById(id);
    }

    @GetMapping("/getAvailableUnits")
 //   @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UnitDTO>> getAvailableUnits(Authentication authentication) {
        List<UnitDTO> unitDTOList = unitService.getAllUnits();
        return unitDTOList.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(unitDTOList, HttpStatus.OK);
    }


        @PostMapping("/addUnit")
    public void addUnit(@RequestParam("unitId") Integer unitId) {
                unitService.getUnitByIdAndSendItToSelectedUnit(unitId);
        }



    @GetMapping("/army/{armyId}/unitsByType")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUnitsGroupedByType(@PathVariable Long armyId) {
        var groupedUnits = selectedUnitService.getUnitsGroupedByType(armyId);
        return ResponseEntity.ok(groupedUnits);
    }
}


