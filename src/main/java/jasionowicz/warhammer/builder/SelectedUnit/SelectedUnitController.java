package jasionowicz.warhammer.builder.SelectedUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SelectedUnitController {
    private final SelectedUnitService selectedUnitService;

    public SelectedUnitController(SelectedUnitService selectedUnitService) {
        this.selectedUnitService = selectedUnitService;
    }

    @PostMapping("/increaseUnitQuantity")
    public ResponseEntity<?> increaseUnitQuantity(@RequestParam("id") Integer selectedId,Authentication authentication) {
        selectedUnitService.increaseUnitQuantity(selectedId,authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decreaseUnitQuantity")
    public void decreaseUnitQuantity(@RequestParam("id") Integer selectedId,Authentication authentication) {
        selectedUnitService.decreaseUnitQuantity(selectedId,authentication);
    }
}
