package jasionowicz.warhammer.builder.Unit;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnitServiceTests {

    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UnitMapper unitMapper;
    @InjectMocks
    private UnitService unitService;
    @Mock
    private SelectedUnitService selectedUnitService;

    @Test
    public void CheckAllUnitsByNation() {
        Unit unit1 = new Unit();
        Unit unit2 = new Unit();
        String nation = "USRR";
        unit1.setId(99999);
        unit2.setId(99998);
        unit1.setNation(nation);
        unit2.setNation(nation);
        unit1.setUnitType("Core");
        unit2.setUnitType("Rare");
        List<Unit> unitList = List.of(unit1, unit2);

        UnitDTO unitDTO1 = new UnitDTO();
        UnitDTO unitDTO2 = new UnitDTO();

        unitDTO1.setId(unit1.getId());
        unitDTO2.setId(unit2.getId());

        unitDTO1.setNation(nation);
        unitDTO2.setNation(nation);

        unitDTO1.setUnitType("Core");
        unitDTO2.setUnitType("Rare");


        when(unitRepository.getAllByNation(nation)).thenReturn(unitList);
        when(unitMapper.unitToUnitDTO(unit1)).thenReturn(unitDTO1);
        when(unitMapper.unitToUnitDTO(unit2)).thenReturn(unitDTO2);
        List<UnitDTO> result = unitService.getAllUnitsByNation(nation);

        assertEquals("Core", result.get(0).getUnitType());
        assertEquals("Rare", result.get(1).getUnitType());

        verify(unitRepository).getAllByNation(nation);
        verify(unitMapper).unitToUnitDTO(unit1);
        verify(unitMapper).unitToUnitDTO(unit2);
    }

    @Test
    public void checkDelteById() {
        Unit unit1 = new Unit();
        unit1.setId(99199);
        unitService.deleteById(99199);
        verify(unitRepository).deleteById(99199);
    }

    @Test
    public void CheckIfGetAllUnits() {
        Unit unit1 = new Unit();
        Unit unit2 = new Unit();
        Unit unit3 = new Unit();
        Unit unit4 = new Unit();

        unit1.setId(99199);
        unit2.setId(99198);
        unit3.setId(99199);
        unit4.setId(99199);

        List<Unit> unitList = List.of(unit1, unit2, unit3, unit4);
        UnitDTO unitDTO1 = new UnitDTO();
        UnitDTO unitDTO2 = new UnitDTO();
        UnitDTO unitDTO3 = new UnitDTO();
        UnitDTO unitDTO4 = new UnitDTO();

        unitDTO1.setId(unit1.getId());
        unitDTO2.setId(unit2.getId());
        unitDTO3.setId(unit3.getId());
        unitDTO4.setId(unit4.getId());

        when(unitRepository.findAll()).thenReturn(unitList);
        when(unitMapper.unitToUnitDTO(unit1)).thenReturn(unitDTO1);
        when(unitMapper.unitToUnitDTO(unit2)).thenReturn(unitDTO2);
        when(unitMapper.unitToUnitDTO(unit3)).thenReturn(unitDTO3);
        when(unitMapper.unitToUnitDTO(unit4)).thenReturn(unitDTO4);
        List<UnitDTO> result = unitService.getAllUnits();
        verify(unitRepository).findAll();
        verify(unitMapper).unitToUnitDTO(unit1);
        verify(unitMapper).unitToUnitDTO(unit2);
        verify(unitMapper).unitToUnitDTO(unit3);
        verify(unitMapper).unitToUnitDTO(unit4);
        assertEquals(4, result.size());
        assertEquals(99199, result.get(0).getId());
    }

    @Test
    void getUnitByIdAndSendItToSelectedUnit_savesSelectedUnitBuiltFromFetchedUnit() {
            int unitId = 1000;
            UnitStats stats = new UnitStats();
            stats.setA(1);
            stats.setLd(1);
            stats.setM(1);
            stats.setBs(1);

            Unit unit = new Unit();
            unit.setId(unitId);
            unit.setUnitStats(stats);

            when(unitRepository.getReferenceById(unitId)).thenReturn(unit);

            ArgumentCaptor<SelectedUnit> captor = ArgumentCaptor.forClass(SelectedUnit.class);

            unitService.getUnitByIdAndSendItToSelectedUnit(unitId);

            verify(unitRepository).getReferenceById(unitId);
            verify(selectedUnitService).saveSelectedUnit(captor.capture());

            SelectedUnit saved = captor.getValue();
            assertNotNull(saved, "SelectedUnit przekazany do zapisu nie powinien być nullem");
            assertSame(unit, saved.getUnit(), "SelectedUnit powinien zawierać dokładnie ten sam Unit");
        }
    }
