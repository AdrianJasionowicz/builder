package jasionowicz.warhammer.builder.Army;

import jakarta.persistence.EntityNotFoundException;
import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import jasionowicz.warhammer.builder.LoginUser.LoginUserService;
import jasionowicz.warhammer.builder.Mapper.*;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitRepository;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitService;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeDTO;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeRepository;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeService;
import jasionowicz.warhammer.builder.Unit.Unit;
import jasionowicz.warhammer.builder.Unit.UnitRepository;
import jasionowicz.warhammer.builder.Upgrade.UpgradeRepository;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArmyService {

    private final SelectedUnit selectedUnit;
    @Getter
    private final Army army = new Army();
    private final SelectedUnitRepository selectedUnitRepository;
    private final SelectedUpgradeRepository selectedUpgradeRepository;
    private final ArmyRepository armyRepository;
    private final LoginUserService loginUserService;
    private final SelectedUnitMapper selectedUnitMapper;
    private final SelectedUpgradeMapper selectedUpgradeMapper;
    private final SelectedUpgradeService selectedUpgradeService;
    private final SelectedUnitService selectedUnitService;
    private final ArmyMapper armyMapper;
    private final UnitMapper unitMapper;
    private final UpgradeMapper upgradeMapper;
    private final UnitRepository unitRepository;
    private final UpgradeRepository upgradeRepository;

    public ArmyService(SelectedUnit selectedUnit, SelectedUnitRepository selectedUnitRepository, SelectedUpgradeRepository selectedUpgradeRepository, ArmyRepository armyRepository, LoginUserService loginUserService, ArmyMapper armyMapper, UnitMapper unitMapper, UpgradeMapper upgradeMapper, UnitRepository unitRepository, UpgradeRepository upgradeRepository, SelectedUnitMapper selectedUnitMapper, SelectedUnitService selectedUnitService, SelectedUpgradeMapper selectedUpgradeMapper, SelectedUpgradeService selectedUpgradeService) {
        this.selectedUnit = selectedUnit;
        this.selectedUnitRepository = selectedUnitRepository;
        this.selectedUpgradeRepository = selectedUpgradeRepository;
        this.armyRepository = armyRepository;
        this.loginUserService = loginUserService;
        this.armyMapper = armyMapper;
        this.unitMapper = unitMapper;
        this.upgradeMapper = upgradeMapper;
        this.unitRepository = unitRepository;
        this.upgradeRepository = upgradeRepository;
        this.selectedUnitMapper = selectedUnitMapper;
        this.selectedUnitService = selectedUnitService;
        this.selectedUpgradeMapper = selectedUpgradeMapper;
        this.selectedUpgradeService = selectedUpgradeService;
    }


    public Map<String, Double> calculateDedicatedPoints(Long armyId) {
        Army army = armyRepository.findById(armyId).orElseThrow(() -> new EntityNotFoundException("Army not found"));
        List<SelectedUnit> selectedUnitsList = army.getSelectedUnitsList();

        Map<String, Double> pointsByType = new HashMap<>();
        double totalPoints =0;

        for (SelectedUnit selectedUnit : selectedUnitsList) {
            String unitType = selectedUnit.getUnit().getUnitType();
            double unitPoints = selectedUnit.getUnit().getPointsCostPerUnit() * selectedUnit.getQuantity();
            double upgradesPoints = selectedUnit.getSelectedUpgrades().stream()
                    .filter(SelectedUpgrade::isSelected)
                    .mapToDouble(upg -> upg.getQuantity() * upg.getUpgrade().getPointsCost())
                    .sum();

            pointsByType.put(unitType, pointsByType.getOrDefault(unitType, 0.0) + unitPoints + upgradesPoints);
            totalPoints += unitPoints +  upgradesPoints;

        }



        army.setLordPointsUsed(pointsByType.get("Lords"));
        army.setHeroPointsUsed(pointsByType.get("Hero"));
        army.setCorePointsUsed(pointsByType.get("Core"));
        army.setSpecialPointsUsed(pointsByType.get("Special"));
        army.setRarePointsUsed(pointsByType.get("Rare"));
        army.setPointsUsed(totalPoints);
        armyRepository.save(army);
        return pointsByType;
    }

    public Map<String, Double> calculatePointsLimitsByType(double pointsRestriction) {
        Map<String, Double> pointsLimitsByType = new HashMap<>();

        pointsLimitsByType.put("Lords", pointsRestriction * 0.5);
        pointsLimitsByType.put("Hero", pointsRestriction * 0.5);
        pointsLimitsByType.put("Core", pointsRestriction * 0.25);
        pointsLimitsByType.put("Special", pointsRestriction * 0.5);
        pointsLimitsByType.put("Rare", pointsRestriction * 0.25);

        pointsLimitsByType.put("Total", pointsRestriction);
        return pointsLimitsByType;
    }


    public List<ArmyDTO> getUserTemplates(Authentication authentication) {
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(authentication.getName());
        List<ArmyDTO> armyDTOs = new ArrayList<>();
        List<Army> armyList = armyRepository.findByOwner(user);
        for (Army template : armyList) {
            armyDTOs.add(armyMapper.armyToArmyDTO(template));
        }
        return armyDTOs;
    }

    public void deleteTemplate(Long templateId, Authentication authentication) {
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(authentication.getName());
        Army template = armyRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        if (!template.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        armyRepository.delete(template);
    }


    public Long createNewArmy(Authentication authentication,String name,String faction, Double points) {
        Army army = new Army();
        String username = authentication.getName();
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(username);
        army.setLordPointsLimit(points * 0.5);
        army.setHeroPointsLimit(points * 0.5);
        army.setCorePointsLimit(points * 0.25);
        army.setSpecialPointsLimit(points * 0.5);
        army.setRarePointsLimit(points * 0.25);

        army.setOwner(user);
        army.setFactionName(faction);
        army.setName(name);
        army.setPointsLimit(points);
        armyRepository.save(army);
        return army.getId();
    }

    public void addUnit(Long armyId, Integer unitId) {
        Army army = armyRepository.findById(armyId).orElseThrow(() -> new RuntimeException("Army not found"));

        if (unitId == null) {
            throw new RuntimeException("Unit id is required");
        }

        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new RuntimeException("Unit not found"));

        SelectedUnit selectedUnit = new SelectedUnit(unit);
        selectedUpgradeService.addFreeUpgradesAndSpecialRaceUpgrades(selectedUnit);
        selectedUnit.setArmy(army);
        army.getSelectedUnitsList().add(selectedUnit);
        armyRepository.save(army);

    }

    public List<SelectedUnitDTO> getArmySelectedUnitsList(Long armyId, Authentication authentication) {
       String loginUsername = authentication.getName();
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(loginUsername);
        Army army = armyRepository.getReferenceById(armyId);
        if (!army.getOwner().getId().equals(user.getId())) {
            return Collections.emptyList();
        }
        List<SelectedUnit> selectedUnitList = army.getSelectedUnitsList();
        List<SelectedUnitDTO> selectedUnitDTOList = new ArrayList<>();
        selectedUnitList.stream()
                .map(selectedUnitMapper::selectedUnitToSelectedUnitDTO)
                .forEach(selectedUnitDTOList::add);
        selectedUnitService.calculateTotalCostOfUnits(armyId);
        return selectedUnitDTOList;
    }


    public void deleteSelectedUnitFromArmyTemplate(Long armyId, Integer unitId, Authentication authentication) {
        String username = authentication.getName();
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(username);
        Army army = armyRepository.getReferenceById(armyId);
        if (!army.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        List<SelectedUnit> selectedUnitList = army.getSelectedUnitsList();
        selectedUnitList.removeIf(unit -> unit.getId().equals(unitId));
        armyRepository.save(army);
    }

    public List<SelectedUpgradeDTO> getSelectedUnitSelectedUpgradesList(long armyId, Integer selectedUnitId, Authentication authentication) {
        Army army = armyRepository.findById(armyId).orElseThrow(() -> new RuntimeException("Army not found"));

        if (selectedUnitId == null) {
            throw new RuntimeException("Unit id is required");
        }

        String loginUsername = authentication.getName();

        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(loginUsername);
        if (!army.getOwner().getId().equals(user.getId())) {
            return Collections.emptyList();
        }

        List<SelectedUpgradeDTO> selectedUpgradeList = selectedUpgradeService.getSelectedUpgradesBySelectedUnitId(selectedUnitId);


        return selectedUpgradeList;
    }


    public Boolean isArmyValid(Long armyId) {
        boolean isGeneralPickedUp = true;
        boolean minimalAmmountOfCoreTaken = true;
        boolean areLordsValid = true;
        boolean areHeroValid = true;
        boolean areSpecialValid = true;
        boolean areRareValid = true;
        boolean noDuplicateOfMagicWeapon = true;
        boolean maxAmmoutOfDuplicationInSpecialAndRare = true;

        Army army = armyRepository.getReferenceById(armyId);
        List<SelectedUnit> selectedUnitList = army.getSelectedUnitsList();
        List<SelectedUpgrade> selectedUpgradeList = army.getSelectedUnitsList().stream()
                .flatMap(unit -> unit.getSelectedUpgrades().stream())
                .filter(SelectedUpgrade::isSelected)
                .toList();

        isGeneralPickedUp = isGeneralPickedUp(selectedUpgradeList);
        minimalAmmountOfCoreTaken = minimalAmmountOfCoreTaken(army);
        areLordsValid = areLordsValid(army);
        areHeroValid = areHeroValid(army);
        areSpecialValid = areSpecialValid(army);
        areRareValid = areRareValid(army);
        noDuplicateOfMagicWeapon = noDuplicateOfMagicWeapon(selectedUpgradeList);
        maxAmmoutOfDuplicationInSpecialAndRare = maxAmmoutOfDuplicationInSpecialAndRare(selectedUnitList,army);

        return isGeneralPickedUp &&
                minimalAmmountOfCoreTaken &&
                areLordsValid &&
                areHeroValid &&
                areSpecialValid &&
                areRareValid &&
                noDuplicateOfMagicWeapon &&
                maxAmmoutOfDuplicationInSpecialAndRare;
    }

    public boolean maxAmmoutOfDuplicationInSpecialAndRare(List<SelectedUnit> selectedUnitList,Army army) {
       boolean maxAmmoutOfDuplicationInSpecialAndRare = true;

        Map<String, Long> unitCounts = selectedUnitList.stream()
                .filter(u -> u.getUnit().getUnitType().equalsIgnoreCase("Special")
                        || u.getUnit().getUnitType().equalsIgnoreCase("Rare"))
                .collect(Collectors.groupingBy(u -> u.getUnit().getName(), Collectors.counting()));

        for (long count : unitCounts.values()) {
            if ((army.getPointsLimit() < 3000 && count > 3) ||
                    (army.getPointsLimit() >= 3000 && count > 6)) {
                maxAmmoutOfDuplicationInSpecialAndRare = false;
                break;
            }
        }

        return maxAmmoutOfDuplicationInSpecialAndRare;
    }

    public boolean noDuplicateOfMagicWeapon(List<SelectedUpgrade> selectedUpgradeList) {
        boolean noDuplicateOfMagicWeapon = selectedUpgradeList.stream()
                .filter(SelectedUpgrade::isSelected)
                .filter(upg -> "Magic Weapon".equalsIgnoreCase(upg.getUpgrade().getName()))
                .count() <= 1;
        return noDuplicateOfMagicWeapon;
    }

    public boolean areRareValid(Army army) {
        if (army.getRarePointsUsed() != null) {
           return army.getRarePointsUsed() <= army.getRarePointsLimit();
        } else {
            return true;
        }
    }


    public boolean areSpecialValid(Army army) {
        if (army.getSpecialPointsUsed() != null) {
           return army.getSpecialPointsUsed() <= army.getSpecialPointsLimit();
        } else {
            return true;
        }
    }

    public boolean areHeroValid(Army army) {
        if (army.getHeroPointsUsed() != null) {
            return army.getHeroPointsUsed() <= army.getHeroPointsLimit();
        } else {
            return true;
        }
    }

    public boolean areLordsValid(Army army) {

        if (army.getLordPointsUsed() != null) {
            return army.getLordPointsUsed() <= army.getLordPointsLimit();
        } else {
            return true;
        }
    }

    public boolean minimalAmmountOfCoreTaken(Army army) {
        if (army.getCorePointsUsed() != null) {
            return army.getCorePointsUsed() > army.getCorePointsLimit() || army.getCorePointsUsed() < army.getPointsLimit();
        } else {
            return false;
        }
    }


    public boolean isGeneralPickedUp(List<SelectedUpgrade> selectedUpgradeList) {
        int ammountOfGenerals = 0;
        for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
            if (selectedUpgrade.getUpgrade().getName().equalsIgnoreCase("General")) {
                ammountOfGenerals++;
                return ammountOfGenerals == 1;
            }
        }
        return false;
    }

}

