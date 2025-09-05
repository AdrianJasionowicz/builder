package jasionowicz.warhammer.builder.SelectedUpgrade;

import jakarta.annotation.PostConstruct;
import jasionowicz.warhammer.builder.Exceptions.StandardBannerCannotTakeMagicWeapons;
import jasionowicz.warhammer.builder.Exceptions.UpgradeAlreadySelectedException;
import jasionowicz.warhammer.builder.Exceptions.WeaponTeamException;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SelectedUpgradeService {
    private SelectedUpgradeRepository selectedUpgradeRepository;
    private SelectedUnitRepository selectedUnitRepository;

    public SelectedUpgradeService(SelectedUpgradeRepository selectedUpgradeRepository, SelectedUnitRepository selectedUnitRepository) {
        this.selectedUpgradeRepository = selectedUpgradeRepository;
        this.selectedUnitRepository = selectedUnitRepository;
    }

    @PostConstruct
    public void clear() {
        selectedUpgradeRepository.deleteAll();
    }



    public boolean checkChieftainBattleStandard(Integer selectedId) {
        List<SelectedUpgrade> checkUpgrades = selectedUpgradeRepository.findAllBySelectedUnitId(selectedId);
        for (SelectedUpgrade checkUpgrade : checkUpgrades) {
            if (checkUpgrade.isSelected() && "Battle Standard".equals(checkUpgrade.getUpgrade().getUpgradeType())) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<String> checkHeroUpgrades(Integer selectedId) {
        List<SelectedUpgrade> checkUpgrades = selectedUpgradeRepository.findAllBySelectedUnitId(selectedId);
        double upgradeLimit = 0;
        for (SelectedUpgrade checkUpgrade : checkUpgrades) {
            if (checkUpgrade.isSelected() && "Magic Weapon".equals(checkUpgrade.getUpgrade().getUpgradeType())) {
                upgradeLimit += checkUpgrade.getUpgrade().getPointsCost();
            }
        }
        if (upgradeLimit > 100) {
            return ResponseEntity.badRequest().body("Upgrade limit exceeded");
        }
        return ResponseEntity.ok("Done");
    }

    public boolean checkWeaponTeams(Integer selectedId) {
        List<SelectedUpgrade> selectedUpgrades = selectedUpgradeRepository.findAllBySelectedUnitId(selectedId);
        for (SelectedUpgrade selectedUpgrade : selectedUpgrades) {
            if (selectedUpgrade.getUpgrade().getUpgradeType().equals("Weapon Team") && selectedUpgrade.isSelected()) {

                return true;

            }



        }
        return false;
    }

    public ResponseEntity<String> checkLordsUpgrades(Integer selectedId) {
        Optional<SelectedUnit> optionalSelectedUnit = selectedUnitRepository.findById(selectedId);
        if (optionalSelectedUnit.isPresent()) {
            SelectedUnit selectedUnit = optionalSelectedUnit.get();
            String isSelectedUnitALord = selectedUnit.getUnit().getUnitType();

            if (isSelectedUnitALord.equals("Lords")) {
                List<SelectedUpgrade> upgradeList = selectedUpgradeRepository.findAllBySelectedUnitId(selectedId);
                double upgradeLimit = 0;
                for (SelectedUpgrade upgrade : upgradeList) {
                    if (upgrade.isSelected() && "Magic weapon".equals(upgrade.getUpgrade().getUpgradeType())) {
                        upgradeLimit += upgrade.getUpgrade().getPointsCost();
                    }
                }
                if (upgradeLimit > 100) {
                    return ResponseEntity.badRequest().body("Upgrade limit exceeded");
                }
                return ResponseEntity.ok("Done");
            }
        }

        return ResponseEntity.ok().body("Done");
    }

    public ResponseEntity<String> checkAmmountOfSBattleStandardsInArmy() {
        int bsbsInArmy = 0;
        List<SelectedUpgrade> selectedUpgradeList = selectedUpgradeRepository.findAll();
        for (SelectedUpgrade upgrade : selectedUpgradeList) {
            if (upgrade.isSelected() && upgrade.getUpgrade().getUpgradeType().equals("Battle Standard")) {
                bsbsInArmy++;
            }
        }
        if (bsbsInArmy > 1) {
            return ResponseEntity.badRequest().body("Army can hold only one bsb");
        }
        return ResponseEntity.ok().body("Army hold one bsb");
    }


    public void checkUpgradesQuantities(Integer id, double quantity) {
        List<SelectedUpgrade> selectedUpgradeList = selectedUpgradeRepository.findAllBySelectedUnitId(id);
        for (SelectedUpgrade upgrade : selectedUpgradeList) {
            if (upgrade.isSelected() && upgrade.getUpgrade().getUpgradeType().equals("Weapon")) {
                upgrade.setQuantity(quantity);
                selectedUpgradeRepository.save(upgrade);
            }
        }
    }

    public void addFreeUpgradesAndSpecialRaceUpgrades(int unitId) {
        List<SelectedUpgrade> selectedUpgradeList = selectedUpgradeRepository.findAllBySelectedUnitId(unitId);
        if (!selectedUpgradeList.isEmpty()) {
            for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                if (selectedUpgrade.getUpgrade().getUpgradeType().equals("Free upgrade")) {
                    selectedUpgrade.setSelected(true);
                    selectedUpgradeRepository.save(selectedUpgrade);

                }
                if (selectedUpgrade.getUpgrade().getUpgradeType().equals("Race special rule")) {
                    selectedUpgrade.setSelected(true);
                    selectedUpgradeRepository.save(selectedUpgrade);
                }
            }
        }
    }

    public void removeSelectedUpgrade(int upgradeId) {
        Optional<SelectedUpgrade> optionalSelectedUpgrade = selectedUpgradeRepository.findById(upgradeId);
        if (optionalSelectedUpgrade.isPresent()) {
            SelectedUpgrade selectedUpgrade = optionalSelectedUpgrade.get();
            selectedUpgrade.setSelected(false);
            selectedUpgradeRepository.save(selectedUpgrade);
        }
    }

    public void addUpgrade(Integer upgradeId) {
        Optional<SelectedUpgrade> optionalSelectedUpgrade = selectedUpgradeRepository.findById(upgradeId);
        if (optionalSelectedUpgrade.isEmpty()) {
            throw new RuntimeException("Upgrade not found");
        }

        SelectedUpgrade selectedUpgrade = optionalSelectedUpgrade.get();
        SelectedUnit selectedUnit = selectedUpgrade.getSelectedUnit();
        String upgradeType = selectedUpgrade.getUpgrade().getUpgradeType();
        int unitId = selectedUnit.getId();

        if (upgradeType.equals("Weapon Team")) {
            if (checkWeaponTeams(unitId)) {

                selectedUpgrade.setSelected(false);
                selectedUpgradeRepository.save(selectedUpgrade);
                throw new WeaponTeamException("Unit can take only one Weapon team");
            }
        }
        Long id = selectedUpgrade.getSelectedUnit().getArmy().getId();

        if (selectedUnit.getUnit().getUnitType().equals("Lords")) {
            checkLordsUpgrades(unitId);
        } else if (selectedUnit.getUnit().getUnitType().equals("Hero")) {
            if (checkChieftainBattleStandard(unitId)) {
                throw new StandardBannerCannotTakeMagicWeapons("Hero with Standard banner cannot take Magic items");
            }
            checkHeroUpgrades(unitId);
        }

        if (selectedUpgrade.isSelected()) {
            throw new UpgradeAlreadySelectedException("Upgrade already selected");
        }

        selectedUpgrade.setSelected(true);
        updateSelectedUpgradeQuantity(selectedUpgrade, selectedUnit);
        selectedUpgradeRepository.save(selectedUpgrade);

    }

    private void updateSelectedUpgradeQuantity(SelectedUpgrade selectedUpgrade, SelectedUnit selectedUnit) {
        String upgradeType = selectedUpgrade.getUpgrade().getUpgradeType();
        if (upgradeType.equals("Weapon Team") || upgradeType.equals("SingleBuy")) {
            selectedUpgrade.setQuantity(1);
        } else {
            SelectedUnit selectedUnitForQuantity = selectedUnitRepository.findById(selectedUnit.getId()).orElseThrow();
            selectedUpgrade.setQuantity(selectedUnitForQuantity.getQuantity());
        }
    }


    public List<SelectedUpgradeDTO> getSelectedUpgradesBySelectedUnitId(Integer id) {
        SelectedUnit selectedUnit = selectedUnitRepository.findById(id).orElseThrow();
        if ( selectedUnit == null ) {
            throw new RuntimeException("Unit not found");
        }
        List<SelectedUpgrade> selectedUpgradesList = selectedUnit.getSelectedUpgrades();
        if (selectedUpgradesList.isEmpty()) {
            throw new RuntimeException("No selected upgrade found");
        }
        return selectedUpgradesList.stream().map(selectedUpgrade -> new SelectedUpgradeDTO(selectedUpgrade)).collect(Collectors.toList());
    }




}
