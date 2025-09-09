package jasionowicz.warhammer.builder.SelectedUnit;

import jakarta.transaction.Transactional;
import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.Army.ArmyRepository;
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
    private final ArmyRepository armyRepository;
    private final SelectedUpgradeService selectdUpgradeService;

    public SelectedUnitService(SelectedStatsRepository selectedStatsRepository, SelectedUpgradeRepository selectedUpgradeRepository, SelectedUnitMapper selectedUnitMapper, UnitRepository unitRepository, SelectedUnitRepository selectedUnitRepository, SelectedUpgradeService selectedUpgradeService, SelectedUpgradeService selectdUpgradeService, LoginUserRepository loginUserRepository, ArmyRepository armyRepository) {
        this.selectedStatsRepository = selectedStatsRepository;
        this.selectedUpgradeRepository = selectedUpgradeRepository;
        this.selectedUnitMapper = selectedUnitMapper;
        this.selectedUnitRepository = selectedUnitRepository;
        this.selectedUpgradeService = selectedUpgradeService;
        this.selectdUpgradeService = selectdUpgradeService;
        this.loginUserRepository = loginUserRepository;
        this.armyRepository = armyRepository;
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
            calculateTotalCostOfUnits(selectedUnit.getArmy().getId());
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
                    calculateTotalCostOfUnits(selectedUnit.getArmy().getId());

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

    public void calculateTotalCostOfUnits(Long armyId) {
        Army army = armyRepository.findById(armyId).orElseThrow(() -> new RuntimeException("Army not found " + armyId));

        List<SelectedUnit> selectedUnits = army.getSelectedUnitsList();

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

}

