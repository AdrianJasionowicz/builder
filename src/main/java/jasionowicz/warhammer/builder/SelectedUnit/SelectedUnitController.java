package jasionowicz.warhammer.builder.SelectedUnit;


import jasionowicz.warhammer.builder.Army.Army;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SelectedUnitController {
    private SelectedUnitService selectedUnitService;

    public SelectedUnitController(SelectedUnitService selectedUnitService) {
        this.selectedUnitService = selectedUnitService;
    }


    @GetMapping("/getSelectedUnits")
    public List<SelectedUnitDTO> getSelectedUnits() {
        return selectedUnitService.convertListToDTO();

    }

    @PostMapping("/removeUnit")
    public void removeUnit(@RequestParam("id") Integer selectedId) {
        selectedUnitService.removeUnitById(selectedId);
    }

    @PostMapping("/increaseUnitQuantity")
    public void increaseUnitQuantity(@RequestParam("id") Integer selectedId) {
        selectedUnitService.increaseUnitQuantity(selectedId);
    }

    @PostMapping("/decreaseUnitQuantity")
    public void decreaseUnitQuantity(@RequestParam("id") Integer selectedId) {
        selectedUnitService.decreaseUnitQuantity(selectedId);
    }
}
