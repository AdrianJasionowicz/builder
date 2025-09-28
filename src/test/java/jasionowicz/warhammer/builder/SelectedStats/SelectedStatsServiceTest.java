package jasionowicz.warhammer.builder.SelectedStats;

import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.UnitStats.UnitStats;
import jasionowicz.warhammer.builder.UnitStats.UnitStatsRepository;
import jasionowicz.warhammer.builder.Upgrade.Upgrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

class SelectedStatsServiceTest {

    @Mock
    private SelectedStatsRepository selectedStatsRepository;

    @InjectMocks
    private SelectedStatsService selectedStatsService;
@Mock
private UnitStatsRepository unitStatsRepository;

    private final SelectedStatsService service = new SelectedStatsService(
            null, null, null, null, null, null
    );

    @Test
    void setSkillsStats_AppliesAllRelevantUpgrades() {
        SelectedStats stats = new SelectedStats();
        stats.setA(1);
        stats.setBasicSave(7);
        stats.setWardSave(7);

        SelectedUpgrade frenzy = mock(SelectedUpgrade.class);
        SelectedUpgrade dodge = mock(SelectedUpgrade.class);
        SelectedUpgrade armourSave = mock(SelectedUpgrade.class);

        Upgrade frenzyUpg = mock(Upgrade.class);
        Upgrade dodgeUpg = mock(Upgrade.class);
        Upgrade armourUpg = mock(Upgrade.class);

        when(frenzy.getUpgrade()).thenReturn(frenzyUpg);
        when(dodge.getUpgrade()).thenReturn(dodgeUpg);
        when(armourSave.getUpgrade()).thenReturn(armourUpg);

        when(frenzyUpg.getName()).thenReturn("Frenzy");
        when(dodgeUpg.getName()).thenReturn("Dodge (6+)");
        when(armourUpg.getName()).thenReturn("Armour Save(4+)");

        when(frenzy.isSelected()).thenReturn(true);
        when(dodge.isSelected()).thenReturn(true);
        when(armourSave.isSelected()).thenReturn(true);

        SelectedUnit unit = mock(SelectedUnit.class);
        when(unit.getSelectedUpgrades()).thenReturn(List.of(frenzy, dodge, armourSave));
        when(unit.getId()).thenReturn(1);

        when(selectedStatsRepository.getReferenceById(1)).thenReturn(stats);

        selectedStatsService.setSkillsStats(unit);

        assertEquals(2, stats.getA());
        assertEquals(6, stats.getWardSave());
        assertEquals(4, stats.getBasicSave());

        verify(selectedStatsRepository).save(stats);
    }



    @Test
    void safeDecrement_NullValue_UsesDefault() throws Exception {
        Method method = SelectedStatsService.class.getDeclaredMethod("safeDecrement", Integer.class, int.class, int.class);
        method.setAccessible(true);

        int result = (int) method.invoke(service, null, 2, 10);
        assertEquals(8, result);
    }

    @Test
    void safeDecrement_NonNullValue_Subtracts() throws Exception {
        Method method = SelectedStatsService.class.getDeclaredMethod("safeDecrement", Integer.class, int.class, int.class);
        method.setAccessible(true);

        int result = (int) method.invoke(service, 5, 2, 10);
        assertEquals(3, result);
    }

    @Test
    void safeCalculate_NullValue_Returns6() throws Exception {
        Method method = SelectedStatsService.class.getDeclaredMethod("safeCalculate", Integer.class, int.class);
        method.setAccessible(true);

        int result = (int) method.invoke(service, null, 3);
        assertEquals(6, result);
    }

    @Test
    void safeCalculate_NonNullValue_Subtracts() throws Exception {
        Method method = SelectedStatsService.class.getDeclaredMethod("safeCalculate", Integer.class, int.class);
        method.setAccessible(true);

        int result = (int) method.invoke(service, 10, 3);
        assertEquals(7, result);
    }


    @Test
    void setWeaponsStats_AppliesMultipleUpgrades() {
        SelectedStats stats = new SelectedStats();
        stats.setS(5);
        stats.setA(2);
        stats.setBs(3);
        stats.setWs(4);
        stats.setI(1);

        SelectedUnit unit = mock(SelectedUnit.class);
        when(unit.getId()).thenReturn(1);

        when(selectedStatsRepository.getReferenceById(1)).thenReturn(stats);

        SelectedUpgrade upgrade1 = mock(SelectedUpgrade.class);
        Upgrade upg1 = mock(Upgrade.class);
        when(upg1.getName()).thenReturn("Fellblade");
        when(upgrade1.getUpgrade()).thenReturn(upg1);
        when(upgrade1.isSelected()).thenReturn(true);

        SelectedUpgrade upgrade2 = mock(SelectedUpgrade.class);
        Upgrade upg2 = mock(Upgrade.class);
        when(upg2.getName()).thenReturn("Warlock-Augumented Weapon");
        when(upgrade2.getUpgrade()).thenReturn(upg2);
        when(upgrade2.isSelected()).thenReturn(true);

        when(unit.getSelectedUpgrades()).thenReturn(List.of(upgrade1, upgrade2));

        selectedStatsService.setWeaponsStats(unit);

        assertEquals(11, stats.getS());
        assertEquals(3, stats.getA());
        verify(selectedStatsRepository).save(stats);
    }

    @Test
    void setArmourStats_AppliesCorrectly() {
        SelectedStats stats = new SelectedStats();
        stats.setBasicSave(7);
        stats.setWardSave(7);

        SelectedUnit unit = mock(SelectedUnit.class);
        when(unit.getId()).thenReturn(1);

        when(selectedStatsRepository.getReferenceById(1)).thenReturn(stats);

        SelectedUpgrade upgrade1 = mock(SelectedUpgrade.class);
        Upgrade upg1 = mock(Upgrade.class);
        when(upg1.getName()).thenReturn("Warpstone armour");
        when(upgrade1.getUpgrade()).thenReturn(upg1);
        when(upgrade1.isSelected()).thenReturn(true);

        SelectedUpgrade upgrade2 = mock(SelectedUpgrade.class);
        Upgrade upg2 = mock(Upgrade.class);
        when(upg2.getName()).thenReturn("Talisman of Protection");
        when(upgrade2.getUpgrade()).thenReturn(upg2);
        when(upgrade2.isSelected()).thenReturn(true);

        when(unit.getSelectedUpgrades()).thenReturn(List.of(upgrade1, upgrade2));

        try {
            Method method = SelectedStatsService.class.getDeclaredMethod("setArmourStats", SelectedUnit.class);
            method.setAccessible(true);
            method.invoke(selectedStatsService, unit);
        } catch (Exception e) {
            fail("Nie udało się wywołać metody setArmourStats: " + e.getMessage());
        }

        assertEquals(4, stats.getBasicSave());
        assertEquals(6, stats.getWardSave());
        verify(selectedStatsRepository).save(stats);
    }

    @Test
    void setBasicStatsBeforeAddUpgradeBuffs_CopiesStatsCorrectly() {
        UnitStats basicStats = new UnitStats();
        basicStats.setS(5);
        basicStats.setA(3);
        basicStats.setWs(4);
        basicStats.setBs(4);
        basicStats.setI(2);
        basicStats.setT(3);
        basicStats.setW(3);
        basicStats.setM(6);
        basicStats.setLd(7);
        basicStats.setBasicSave(6);
        basicStats.setWardSave(5);

        SelectedStats selectedStats = new SelectedStats();
        selectedStats.setS(0);

        SelectedUnit unit = mock(SelectedUnit.class);
        when(unit.getSelectedStats()).thenReturn(selectedStats);
        when(unit.getUnit()).thenReturn(mock(jasionowicz.warhammer.builder.Unit.Unit.class));
        when(unit.getUnit().getUnitStats()).thenReturn(basicStats);

        when(unitStatsRepository.findById(basicStats.getId())).thenReturn(Optional.of(basicStats));
        when(selectedStatsRepository.getReferenceById(selectedStats.getId())).thenReturn(selectedStats);

        selectedStatsService.setBasicStatsBeforeAddUpgradeBuffs(unit);

        assertEquals(5, selectedStats.getS());
        assertEquals(3, selectedStats.getA());
        assertEquals(4, selectedStats.getWs());
        assertEquals(4, selectedStats.getBs());
        assertEquals(2, selectedStats.getI());
        assertEquals(3, selectedStats.getT());
        assertEquals(3, selectedStats.getW());
        assertEquals(6, selectedStats.getM());
        assertEquals(7, selectedStats.getLd());
        assertEquals(6, selectedStats.getBasicSave());
        assertEquals(5, selectedStats.getWardSave());

        verify(selectedStatsRepository).save(selectedStats);
    }

}

