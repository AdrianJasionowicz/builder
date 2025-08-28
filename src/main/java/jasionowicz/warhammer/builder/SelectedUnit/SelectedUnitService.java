package jasionowicz.warhammer.builder.SelectedUnit;

import jakarta.transaction.Transactional;
import jasionowicz.warhammer.builder.Mapper.SelectedUnitMapper;
import jasionowicz.warhammer.builder.SelectedStats.SelectedStatsRepository;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeDTO;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeRepository;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeService;
import jasionowicz.warhammer.builder.Unit.UnitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SelectedUnitService {
    private final SelectedUnitRepository selectedUnitRepository;
    private final SelectedUpgradeService selectedUpgradeService;
    private final SelectedStatsRepository selectedStatsRepository;
    private final SelectedUpgradeRepository selectedUpgradeRepository;
    private final SelectedUnitMapper selectedUnitMapper;
    private SelectedUpgradeService selectdUpgradeService;

    public SelectedUnitService(SelectedStatsRepository selectedStatsRepository, SelectedUpgradeRepository selectedUpgradeRepository, SelectedUnitMapper selectedUnitMapper, UnitRepository unitRepository, SelectedUnitRepository selectedUnitRepository, SelectedUpgradeService selectedUpgradeService, SelectedUpgradeService selectdUpgradeService) {
        this.selectedStatsRepository = selectedStatsRepository;
        this.selectedUpgradeRepository = selectedUpgradeRepository;
        this.selectedUnitMapper = selectedUnitMapper;
        this.selectedUnitRepository = selectedUnitRepository;
        this.selectedUpgradeService = selectedUpgradeService;
        this.selectdUpgradeService = selectdUpgradeService;
    }


    public void removeUnitById(int id) {
        selectedUnitRepository.deleteById(id);
    }


        @Transactional
        public void increaseUnitQuantity(Integer id) {
        SelectedUnit selectedUnit = selectedUnitRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Unit not found " + id)) ;

            selectedUnit.setQuantity(selectedUnit.getQuantity() + 1);
            selectedUpgradeService.checkUpgradesQuantities(id, selectedUnit.getQuantity());
            selectedUnitRepository.save(selectedUnit);
            calculateTotalCostOfUnits();

        }



    @Transactional
    public ResponseEntity<String> decreaseUnitQuantity(Integer id) {
        double decrease = 1;
        Optional<SelectedUnit> optionalSelectedUnit = selectedUnitRepository.findById(id);

        if (optionalSelectedUnit.isPresent()) {
            SelectedUnit selectedUnit = optionalSelectedUnit.get();

            if (selectedUnit.getQuantity() > 0) {
                if (selectedUnit.getQuantity() == selectedUnit.getUnit().getMinQuantity()) {
                    return ResponseEntity.badRequest().body("Cant decrease quantity");
                }
                selectedUnit.setQuantity(selectedUnit.getQuantity() - decrease);
                selectedUnitRepository.save(selectedUnit);
                selectedUpgradeService.checkUpgradesQuantities(id, selectedUnit.getQuantity());
                calculateTotalCostOfUnits();

                return ResponseEntity.ok("Quantity decreased");
            } else {
                return ResponseEntity.badRequest().body("Quantity cannot be less than zero");
            }
        } else {
            return ResponseEntity.badRequest().body("Unit not found");
        }
    }

    public List<SelectedUnit> getSelectedUnits() {
        List<SelectedUnit> selectedUnits = selectedUnitRepository.findAll();
        if (selectedUnits.isEmpty()) {
            selectedUnits = new ArrayList<>();
        }
        return selectedUnits;
    }

    public void saveSelectedUnit(SelectedUnit selectedUnit) {

        if (selectedUnit.getSelectedStats() != null) {
            selectedStatsRepository.save(selectedUnit.getSelectedStats());
        }
        selectedUnitRepository.save(selectedUnit);

        List<SelectedUpgrade> selectedUpgradeList = selectedUnit.getSelectedUpgrades().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!selectedUpgradeList.isEmpty()) {
            selectedUpgradeService.addFreeUpgradesAndSpecialRaceUpgrades(selectedUnit.getId());
            selectedUpgradeRepository.saveAll(selectedUpgradeList);
        }
    }


    public List<SelectedUnitDTO> convertListToDTO() {
        calculateTotalCostOfUnits();
        List<SelectedUnit> entities = getSelectedUnits();
        List<SelectedUnitDTO> dtos = entities.stream()
                .map(selectedUnitMapper::selectedUnitToSelectedUnitDTO)
                .collect(Collectors.toList());
        return (dtos);
    }

    public void calculateTotalCostOfUnits() {
    List<SelectedUnit> selectedUnits = getSelectedUnits();
        for (SelectedUnit selectedUnit : selectedUnits) {
            if (selectedUnit.getUnit() == null) {
                System.err.println("⚠ Brak powiązanego Unit lub pointsCostPerUnit dla SelectedUnit ID = " + selectedUnit.getId());
                continue;
            }
            selectedUnit.setTotalCost(selectedUnit.getQuantity() * selectedUnit.getUnit().getPointsCostPerUnit());

            List<SelectedUpgrade> selectedUpgradeList = selectedUnit.getSelectedUpgrades();
            for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                if (selectedUpgrade.isSelected()) {
                    selectedUnit.setTotalCost(selectedUnit.getTotalCost() + selectedUpgrade.getQuantity()*selectedUpgrade.getUpgrade().getPointsCost());
                }
            }

            selectedUnitRepository.save(selectedUnit);
        }


    }





    public Object getUnitsGroupedByType(Long armyId) {
        return null;
    }


}

