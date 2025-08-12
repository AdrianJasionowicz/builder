package jasionowicz.warhammer.builder.Army;

import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitRepository;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitService;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArmyService {

    private final SelectedUnit selectedUnit;
    @Getter
    private final Army army = new Army();
    private final SelectedUnitRepository selectedUnitRepository;
    private final SelectedUpgradeRepository selectedUpgradeRepository;
    private final ArmyRepository armyCompositionRepository;
    private SelectedUnitService selectedUnitService;

    public ArmyService(SelectedUnit selectedUnit, SelectedUnitRepository selectedUnitRepository, SelectedUpgradeRepository selectedUpgradeRepository, ArmyRepository armyCompositionRepository) {
        this.selectedUnit = selectedUnit;
        this.selectedUnitRepository = selectedUnitRepository;
        this.selectedUpgradeRepository = selectedUpgradeRepository;
        this.armyCompositionRepository = armyCompositionRepository;
    }


    public Map<String, Double> calculateDedicatedPoints() {
        List<SelectedUnit> selectedUnitsList = selectedUnitRepository.findAll();
        List<SelectedUpgrade> selectedUpgradeList = selectedUpgradeRepository.findAll();

        Map<String, Double> pointsByType = new HashMap<>();


        for (SelectedUnit selectedUnit : selectedUnitsList) {
            String unitType = selectedUnit.getUnit().getUnitType();
            double unitPoints = selectedUnit.getUnit().getPointsCostPerUnit() * selectedUnit.getQuantity();

            pointsByType.put(unitType, pointsByType.getOrDefault(unitType, 0.0) + unitPoints);
        }
        for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
            if (selectedUpgrade.isSelected()) {
                String unitType = selectedUpgrade.getSelectedUnit().getUnit().getUnitType();
                double upgradePoints = selectedUpgrade.getUpgrade().getPointsCost() * selectedUpgrade.getQuantity();
                pointsByType.put(unitType, pointsByType.getOrDefault(unitType, 0.0) + upgradePoints);
            }
        }
        return pointsByType;
    }

    public Map<String, Double> calculatePointsLimitsByType(double pointsRestriction) {
        Map<String, Double> pointsLimitsByType = new HashMap<>();
        pointsLimitsByType.put("Lords", pointsRestriction * 0.5);
        pointsLimitsByType.put("Hero", pointsRestriction * 0.5);
        pointsLimitsByType.put("Core", pointsRestriction * 0.25);
        pointsLimitsByType.put("Special", pointsRestriction * 0.5);
        pointsLimitsByType.put("Rare", pointsRestriction * 0.25);
        return pointsLimitsByType;
    }













}
