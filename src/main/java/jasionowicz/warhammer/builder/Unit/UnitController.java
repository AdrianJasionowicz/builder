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
    private final UnitService unitService;
    private final SelectedUnitService selectedUnitService;


    public UnitController(UnitService unitService, SelectedUnitService selectedUnitService) {
        this.unitService = unitService;
        this.selectedUnitService = selectedUnitService;
    }

    @GetMapping("/getAllUnits/{armyId}")
    public ResponseEntity<List<UnitDTO>> getAllUnits(@PathVariable Long armyId) {
        List<UnitDTO> units = unitService.getAllUnitsByNation(armyId);
        return units.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(units);
    }


}


