package jasionowicz.warhammer.builder.Unit;

import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.Army.ArmyRepository;
import jasionowicz.warhammer.builder.Mapper.UnitMapper;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UnitService {
   private final UnitRepository unitRepository;
   private final UnitMapper unitMapper;
   private final SelectedUnitService selectedUnitService;
    private final ArmyRepository armyRepository;

    public UnitService(UnitRepository unitRepository, UnitMapper unitMapper, SelectedUnitService selectedUnitService, ArmyRepository armyRepository) {
        this.unitRepository = unitRepository;
        this.unitMapper = unitMapper;
        this.selectedUnitService = selectedUnitService;
        this.armyRepository = armyRepository;
    }



    public List<UnitDTO> getAllUnitsByNation(Long armyId) {

        String nation;
        Army army = armyRepository.getReferenceById(armyId);
        nation = army.getFactionName();
        List<Unit> unitListByNation = unitRepository.getAllByNation(nation);

        return unitListByNation.stream()
                .map(unitMapper::unitToUnitDTO)
                .toList();
    }

    public void deleteById(Integer id) {
        unitRepository.deleteById(id);
    }


    public void getUnitByIdAndSendItToSelectedUnit(Integer unitId) {
       Unit unit = unitRepository.getReferenceById(unitId);
        SelectedUnit selectedUnit = new SelectedUnit(unit);
        selectedUnitService.saveSelectedUnit(selectedUnit);

    }
}

