package jasionowicz.warhammer.builder.SelectedUnit;

import jakarta.transaction.Transactional;
import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import jasionowicz.warhammer.builder.LoginUser.LoginUserRepository;
import jasionowicz.warhammer.builder.Mapper.SelectedUnitMapper;
import jasionowicz.warhammer.builder.SelectedStats.SelectedStatsRepository;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeRepository;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeService;
import jasionowicz.warhammer.builder.Unit.UnitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final LoginUserRepository loginUserRepository;
    private SelectedUpgradeService selectdUpgradeService;

    public SelectedUnitService(SelectedStatsRepository selectedStatsRepository, SelectedUpgradeRepository selectedUpgradeRepository, SelectedUnitMapper selectedUnitMapper, UnitRepository unitRepository, SelectedUnitRepository selectedUnitRepository, SelectedUpgradeService selectedUpgradeService, SelectedUpgradeService selectdUpgradeService, LoginUserRepository loginUserRepository) {
        this.selectedStatsRepository = selectedStatsRepository;
        this.selectedUpgradeRepository = selectedUpgradeRepository;
        this.selectedUnitMapper = selectedUnitMapper;
        this.selectedUnitRepository = selectedUnitRepository;
        this.selectedUpgradeService = selectedUpgradeService;
        this.selectdUpgradeService = selectdUpgradeService;
        this.loginUserRepository = loginUserRepository;
    }


    public void removeUnitById(int id, Authentication authentication) {
        String username = authentication.getName();
        LoginUser loginUser = loginUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        SelectedUnit selectedUnit = selectedUnitRepository.findById(id).orElseThrow(() -> new RuntimeException("Selected unit not found"));
        LoginUser loginUserFromSelectedUnit = selectedUnit.getArmy().getOwner();
        if (loginUserFromSelectedUnit.getId().equals(loginUser.getId())) {
            selectedUnitRepository.deleteById(id);
        }
    }


    @Transactional
    public void increaseUnitQuantity(Integer id, Authentication authentication) {
        SelectedUnit selectedUnit = selectedUnitRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Unit not found " + id));
        String username = authentication.getName();
        LoginUser loginUser = loginUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        LoginUser loginUserFromSelectedUnit = selectedUnit.getArmy().getOwner();
        if (loginUserFromSelectedUnit.getId().equals(loginUser.getId())) {
            selectedUnit.setQuantity(selectedUnit.getQuantity() + 1);
            selectedUpgradeService.checkUpgradesQuantities(id, selectedUnit.getQuantity());
            selectedUnitRepository.save(selectedUnit);
            calculateTotalCostOfUnits();
        }
    }


    @Transactional
    public ResponseEntity<String> decreaseUnitQuantity(Integer id,Authentication authentication) {
        double decrease = 1;
        Optional<SelectedUnit> optionalSelectedUnit = selectedUnitRepository.findById(id);

        String username = authentication.getName();
        LoginUser loginUser = loginUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (optionalSelectedUnit.isPresent()) {
            SelectedUnit selectedUnit = optionalSelectedUnit.get();
            LoginUser loginUserFromSelectedUnit = selectedUnit.getArmy().getOwner();

            if (loginUserFromSelectedUnit.getId().equals(loginUser.getId())) {
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
        return ResponseEntity.badRequest().body("You have no permission to decrease quantity");
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
                    selectedUnit.setTotalCost(selectedUnit.getTotalCost() + selectedUpgrade.getQuantity() * selectedUpgrade.getUpgrade().getPointsCost());
                }
            }

            selectedUnitRepository.save(selectedUnit);
        }


    }


    public Object getUnitsGroupedByType(Long armyId) {
        return null;
    }


}

