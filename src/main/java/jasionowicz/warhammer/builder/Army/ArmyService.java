package jasionowicz.warhammer.builder.Army;

import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import jasionowicz.warhammer.builder.LoginUser.LoginUserService;
import jasionowicz.warhammer.builder.Mapper.ArmyMapper;
import jasionowicz.warhammer.builder.Mapper.SelectedUnitMapper;
import jasionowicz.warhammer.builder.Mapper.UnitMapper;
import jasionowicz.warhammer.builder.Mapper.UpgradeMapper;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitRepository;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitService;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeDTO;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeRepository;
import jasionowicz.warhammer.builder.Unit.Unit;
import jasionowicz.warhammer.builder.Unit.UnitRepository;
import jasionowicz.warhammer.builder.Upgrade.Upgrade;
import jasionowicz.warhammer.builder.Upgrade.UpgradeRepository;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
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
    private SelectedUnitService selectedUnitService;
    private ArmyMapper armyMapper;
    private UnitMapper unitMapper;
    private UpgradeMapper upgradeMapper;
    private UnitRepository unitRepository;
private UpgradeRepository upgradeRepository;

    public ArmyService(SelectedUnit selectedUnit, SelectedUnitRepository selectedUnitRepository, SelectedUpgradeRepository selectedUpgradeRepository, ArmyRepository armyRepository, LoginUserService loginUserService, ArmyMapper armyMapper, UnitMapper unitMapper, UpgradeMapper upgradeMapper, UnitRepository unitRepository, UpgradeRepository upgradeRepository, SelectedUnitMapper selectedUnitMapper) {
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
    }


    public Map<String, Double> calculateDedicatedPoints(Long armyId) {
        Army army = armyRepository.findById(armyId).get();
        List<SelectedUnit> selectedUnitsList = army.getSelectedUnitsList();



        Map<String, Double> pointsByType = new HashMap<>();


        for (SelectedUnit selectedUnit : selectedUnitsList) {
            String unitType = selectedUnit.getUnit().getUnitType();
            double unitPoints = selectedUnit.getUnit().getPointsCostPerUnit() * selectedUnit.getQuantity();

            pointsByType.put(unitType, pointsByType.getOrDefault(unitType, 0.0) + unitPoints);
           List<SelectedUpgrade> selectedUpgradeList = selectedUnit.getSelectedUpgrades();
            for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                if (selectedUpgrade.isSelected()) {
                    double upgradePoints = selectedUpgrade.getQuantity() * selectedUpgrade.getUpgrade().getPointsCost();

                    pointsByType.put(unitType, pointsByType.getOrDefault(unitType, 0.0) + upgradePoints);
                }
            }

        }

        return pointsByType;
    }

    public Map<String, Double> calculatePointsLimitsByType(double pointsRestriction) {
        Map<String, Double> pointsLimitsByType = new HashMap<>();

        pointsLimitsByType.put("Lords", pointsRestriction * 0.5);
        pointsLimitsByType.put("Heroes", pointsRestriction * 0.5);
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

    public Long createNewArmy(Authentication authentication) {
        Army army = new Army();
        String username = authentication.getName();
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(username);
        army.setOwner(user);
        armyRepository.save(army);
        return army.getId();
    }

    public void addUnit(Long armyId, SelectedUnitDTO selectedUnitDTO) {
        Army army = armyRepository.getReferenceById(armyId);
        SelectedUnit selectedUnit = selectedUnitMapper.selectedUnitDToToSelectedUnit(selectedUnitDTO);
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
        for (SelectedUnit unit : selectedUnitList) {
          selectedUnitDTOList.add(selectedUnitMapper.selectedUnitToSelectedUnitDTO(selectedUnit));
        }
        return selectedUnitDTOList;
    }

    public void deleteSelectedUnitFromArmyTemplate(Long armyId, Long unitId, Authentication authentication) {
        String username = authentication.getName();
        LoginUser user = (LoginUser) loginUserService.loadUserByUsername(username);
        Army army = armyRepository.getReferenceById(armyId);
        if (!army.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
       List<SelectedUnit> selectedUnitList = army.getSelectedUnitsList();
        for (SelectedUnit unit : selectedUnitList) {
           if ( selectedUnit.getId().equals(unitId)) {
               selectedUnitList.remove(unit);
               break;
           }
        }
    }


}

