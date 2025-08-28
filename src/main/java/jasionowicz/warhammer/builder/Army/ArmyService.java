package jasionowicz.warhammer.builder.Army;

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
    private SelectedUnitService selectedUnitService;
    private ArmyMapper armyMapper;
    private UnitMapper unitMapper;
    private UpgradeMapper upgradeMapper;
    private UnitRepository unitRepository;
private UpgradeRepository upgradeRepository;

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
        Army army = armyRepository.findById(armyId).get();
        List<SelectedUnit> selectedUnitsList = army.getSelectedUnitsList();



        Map<String, Double> pointsByType = new HashMap<>();


        for (SelectedUnit selectedUnit : selectedUnitsList) {
            String unitType = selectedUnit.getUnit().getUnitType();
            double unitPoints = selectedUnit.getUnit().getPointsCostPerUnit() * selectedUnit.getQuantity();

            double upgradesPoints = selectedUnit.getSelectedUpgrades().stream()
                    .filter(SelectedUpgrade::isSelected)
                    .mapToDouble(upg -> upg.getQuantity() * upg.getUpgrade().getPointsCost())
                    .sum();

            pointsByType.put(unitType, pointsByType.getOrDefault(unitType, 0.0) + unitPoints + upgradesPoints);
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

    public ArmyDTO loadTemplate(Long templateId, Authentication authentication) {
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(authentication.getName());
        Army template = armyRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        if (!template.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return armyMapper.armyToArmyDTO(template);
    }



    public void updateTemplate(Long templateId, ArmyDTO armyDTO, Authentication authentication) {
        String username = authentication.getName();
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(username);

        Army existingTemplate = armyRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (!existingTemplate.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        existingTemplate.setName(armyDTO.getName());
        existingTemplate.setDescription(armyDTO.getDescription());
        existingTemplate.setFactionName(armyDTO.getFactionName());
        List<SelectedUnitDTO> selectedUnitList = armyDTO.getSelectedUnitsList();
        for (SelectedUnitDTO selectedUnitDTO : selectedUnitList) {
            selectedUnitDTO.getUnit().getNation();
            existingTemplate.setFactionName(selectedUnitDTO.getUnit().getNation());
            if (existingTemplate.getFactionName().equals(selectedUnitDTO.getUnit().getNation()))
                break;

        }


        armyRepository.save(existingTemplate);
    }

    public Long createNewArmy(Authentication authentication,String name,String faction, Double points) {
        Army army = new Army();
        String username = authentication.getName();
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(username);
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

}

