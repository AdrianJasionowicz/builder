package jasionowicz.warhammer.builder.Unit;

import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.Army.ArmyRepository;
import jasionowicz.warhammer.builder.Mapper.UnitMapper;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitRepository;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitService;
import jasionowicz.warhammer.builder.UnitStats.UnitStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnitServiceTests {

    @Mock
    private ArmyRepository armyRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UnitMapper unitMapper;
    @InjectMocks
    private UnitService unitService;


    @Test
    public void testIfGetAllUnitsByNation() {
        Long armyId = 1L;
        Army army = new Army();
        army.setFactionName("Berlin");
        when(armyRepository.getReferenceById(armyId)).thenReturn(army);
        Unit unit1 = new Unit();
        Unit unit2 = new Unit();
        unit1.setNation("Berlin");
        unit2.setNation("Berlin");
        List<Unit> units = Arrays.asList(unit1, unit2);
        when(unitRepository.getAllByNation("Berlin")).thenReturn(units);
        UnitDTO dto1 = new UnitDTO();
        UnitDTO dto2 = new UnitDTO();
        when(unitMapper.unitToUnitDTO(unit1)).thenReturn(dto1);
        when(unitMapper.unitToUnitDTO(unit2)).thenReturn(dto2);
        List<UnitDTO> result = unitService.getAllUnitsByNation(armyId);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

    }




    }
