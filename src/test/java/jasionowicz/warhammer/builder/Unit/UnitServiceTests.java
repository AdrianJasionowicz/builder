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
    public void checkDelteById() {
        Unit unit1 = new Unit();
        unit1.setId(99199);
        unitService.deleteById(99199);
        verify(unitRepository).deleteById(99199);
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
