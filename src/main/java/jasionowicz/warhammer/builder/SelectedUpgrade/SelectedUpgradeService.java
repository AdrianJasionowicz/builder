package jasionowicz.warhammer.builder.SelectedUpgrade;

import jakarta.annotation.PostConstruct;
import jasionowicz.warhammer.builder.Exceptions.MagicItemsException;
import jasionowicz.warhammer.builder.Exceptions.StandardBannerCannotTakeMagicWeapons;
import jasionowicz.warhammer.builder.Exceptions.UpgradeAlreadySelectedException;
import jasionowicz.warhammer.builder.Exceptions.WeaponTeamException;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SelectedUpgradeService {
    private final SelectedUpgradeRepository selectedUpgradeRepository;
    private final SelectedUnitRepository selectedUnitRepository;

    public SelectedUpgradeService(SelectedUpgradeRepository selectedUpgradeRepository, SelectedUnitRepository selectedUnitRepository) {
        this.selectedUpgradeRepository = selectedUpgradeRepository;
        this.selectedUnitRepository = selectedUnitRepository;
    }

    @PostConstruct
    public void clear() {
        selectedUpgradeRepository.deleteAll();
    }



    public boolean checkChieftainBattleStandard(Integer selectedId,Integer upgradeId ) {
        List<SelectedUpgrade> upgrades = selectedUpgradeRepository.findAllBySelectedUnitId(selectedId);
        SelectedUpgrade chosenUpgrade = selectedUpgradeRepository.findById(upgradeId).orElse(null);

        if (chosenUpgrade == null) return false;

        boolean hasBattleStandard = upgrades.stream()
                .anyMatch(u -> u.isSelected() && "Battle Standard".equalsIgnoreCase(u.getUpgrade().getName()));

        boolean hasMagicBanner = upgrades.stream()
                .anyMatch(u -> u.isSelected() && "Magic Banner".equalsIgnoreCase(u.getUpgrade().getUpgradeType()));

        if (!hasBattleStandard && chosenUpgrade.getUpgrade().getUpgradeType().equalsIgnoreCase("Magic Banner")) {
            return true;
        }
        if (chosenUpgrade.getUpgrade().getUpgradeType().equalsIgnoreCase("Weapon")) {
            return false;
        }
        if (!hasBattleStandard) {
            return false;
        }
        if (!hasMagicBanner) {
            return false;
        }
        return true;
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

    public Boolean checkLordsUpgrades(Integer selectedId) {
        List<SelectedUpgrade> selectedUpgrades = selectedUpgradeRepository.findAllBySelectedUnitId(selectedId);
        double upgradeLimit = 0;
        for (SelectedUpgrade selectedUpgrade : selectedUpgrades) {
               if (selectedUpgrade.isSelected() && selectedUpgrade.getUpgrade().getUpgradeType().equalsIgnoreCase("Magic Weapon") || selectedUpgrade.getUpgrade().getUpgradeType().equalsIgnoreCase("The Scavenge-Pile")) {
                  upgradeLimit += selectedUpgrade.getUpgrade().getPointsCost();
               }
        }
        if ( upgradeLimit >= 100) {
            return false;
        }
        return true;
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

    public SelectedUnit addFreeUpgradesAndSpecialRaceUpgrades(SelectedUnit selectedUnit) {
        List<SelectedUpgrade> selectedUpgradeList = selectedUnit.getSelectedUpgrades();
        if (!selectedUpgradeList.isEmpty()) {
            for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                if (selectedUpgrade.getUpgrade().getUpgradeType().equalsIgnoreCase("Free upgrade")) {
                    selectedUpgrade.setSelected(true);

                }
                if (selectedUpgrade.getUpgrade().getUpgradeType().equalsIgnoreCase("Free")) {
                    selectedUpgrade.setSelected(true);


                }
                if (selectedUpgrade.getUpgrade().getUpgradeType().equalsIgnoreCase("Race special rule")) {
                    selectedUpgrade.setSelected(true);
                }
            }
        }
        return selectedUnit;
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

        if (selectedUnit.getUnit().getUnitType().equals("Lords")) {
           boolean areUpgradesLessThan100Point = checkLordsUpgrades(unitId);
           if (!areUpgradesLessThan100Point) {
               selectedUpgrade.setSelected(false);
               throw new MagicItemsException("Unit can take only one Weapon team");
           }
        }
        if (selectedUnit.getUnit().getUnitType().equals("Hero")) {
            boolean areUpgradesLessThan50Point = checkLordsUpgrades(unitId);
            if (!areUpgradesLessThan50Point) {
                selectedUpgrade.setSelected(false);
                throw new MagicItemsException("Unit can take only one Weapon team");
            }
        }
        if (selectedUnit.getUnit().getUnitType().equals("Hero")) {
            if (checkChieftainBattleStandard(unitId,upgradeId)) {
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
        if (upgradeType.equals("Weapon Team") || upgradeType.equals("SingleBuy") || upgradeType.equals("Champion")) {
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
