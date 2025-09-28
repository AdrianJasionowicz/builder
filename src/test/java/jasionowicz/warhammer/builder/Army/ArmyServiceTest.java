package jasionowicz.warhammer.builder.Army;

import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import jasionowicz.warhammer.builder.LoginUser.LoginUserService;
import jasionowicz.warhammer.builder.Mapper.ArmyMapper;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeDTO;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeService;
import jasionowicz.warhammer.builder.Unit.Unit;
import jasionowicz.warhammer.builder.Unit.UnitRepository;
import jasionowicz.warhammer.builder.UnitStats.UnitStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArmyServiceTest {
    @Mock
    private ArmyMapper armyMapper;
    @Mock
    private ArmyRepository armyRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private SelectedUpgradeService selectedUpgradeService;

    @Mock
    private LoginUserService loginUserService; // tylko mock

    @InjectMocks
    private ArmyService armyService;

    @Mock
    private SelectedUpgrade selectedUpgrade;

    @Test
    void addUnit_HappyPath() {
        Long armyId = 1L;
        Integer unitId = 42;

        Army army = new Army();
        army.setId(armyId);
        army.setSelectedUnitsList(new ArrayList<>());

        Unit unit = new Unit();
        unit.setId(unitId);

        UnitStats unitStats = new UnitStats();
        unitStats.setWs(3);
        unitStats.setBs(3);
        unitStats.setS(3);
        unitStats.setT(3);
        unitStats.setW(1);
        unitStats.setA(1);
        unitStats.setLd(5);
        unitStats.setBasicSave(6);
        unitStats.setWardSave(0);
        unitStats.setI(3);
        unitStats.setM(5);
        unit.setUnitStats(unitStats);

        when(armyRepository.findById(armyId)).thenReturn(Optional.of(army));
        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));

        armyService.addUnit(armyId, unitId);

        ArgumentCaptor<Army> captor = ArgumentCaptor.forClass(Army.class);
        verify(armyRepository).save(captor.capture());

        Army savedArmy = captor.getValue();

        assertEquals(1, savedArmy.getSelectedUnitsList().size());

        SelectedUnit added = savedArmy.getSelectedUnitsList().get(0);
        assertEquals(unit, added.getUnit());
        assertEquals(savedArmy, added.getArmy());

        verify(selectedUpgradeService).addFreeUpgradesAndSpecialRaceUpgrades(added);
    }

    @Test
    void createNewArmy_HappyPath() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        LoginUser user = new LoginUser();
        user.setId(10L);
        when(loginUserService.loadUserByUsername("testUser")).thenReturn(user);

        String name = "Test Army";
        String faction = "Orcs";
        Double points = 100.0;

        ArgumentCaptor<Army> captor = ArgumentCaptor.forClass(Army.class);

        Long returnedId = armyService.createNewArmy(authentication, name, faction, points);

        verify(armyRepository).save(captor.capture());
        Army savedArmy = captor.getValue();

        assertEquals(name, savedArmy.getName());
        assertEquals(faction, savedArmy.getFactionName());
        assertEquals(user, savedArmy.getOwner());
        assertEquals(points, savedArmy.getPointsLimit());
        assertEquals(points * 0.5, savedArmy.getLordPointsLimit());
        assertEquals(points * 0.5, savedArmy.getHeroPointsLimit());
        assertEquals(points * 0.25, savedArmy.getCorePointsLimit());
        assertEquals(points * 0.5, savedArmy.getSpecialPointsLimit());
        assertEquals(points * 0.25, savedArmy.getRarePointsLimit());

        assertEquals(savedArmy.getId(), returnedId);
    }

    @Test
    void deleteTemplate_HappyPath() {
        Long templateId = 1L;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        LoginUser user = new LoginUser();
        user.setId(10L);
        when(loginUserService.loadUserByUsername("testUser")).thenReturn(user);

        Army template = new Army();
        template.setId(templateId);
        template.setOwner(user);

        when(armyRepository.findById(templateId)).thenReturn(Optional.of(template));

        armyService.deleteTemplate(templateId, authentication);

        verify(armyRepository).delete(template);
    }

    @Test
    void deleteTemplate_TemplateNotFound() {
        Long templateId = 1L;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(armyRepository.findById(templateId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                armyService.deleteTemplate(templateId, authentication)
        );

        assertEquals("Template not found", ex.getMessage());
    }

    @Test
    void deleteTemplate_AccessDenied() {
        Long templateId = 1L;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        LoginUser user = new LoginUser();
        user.setId(10L);
        when(loginUserService.loadUserByUsername("testUser")).thenReturn(user);

        LoginUser otherUser = new LoginUser();
        otherUser.setId(99L);

        Army template = new Army();
        template.setId(templateId);
        template.setOwner(otherUser);

        when(armyRepository.findById(templateId)).thenReturn(Optional.of(template));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                armyService.deleteTemplate(templateId, authentication)
        );

        assertEquals("Access denied", ex.getMessage());

        verify(armyRepository, never()).delete(any());
    }

    @Test
    void getUserTemplates_HappyPath() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        LoginUser user = new LoginUser();
        user.setId(10L);
        when(loginUserService.loadUserByUsername("testUser")).thenReturn(user);

        Army army1 = new Army();
        army1.setId(1L);
        Army army2 = new Army();
        army2.setId(2L);

        when(armyRepository.findByOwner(user)).thenReturn(List.of(army1, army2));

        ArmyDTO dto1 = new ArmyDTO();
        dto1.setId(1L);
        ArmyDTO dto2 = new ArmyDTO();
        dto2.setId(2L);

        when(armyMapper.armyToArmyDTO(army1)).thenReturn(dto1);
        when(armyMapper.armyToArmyDTO(army2)).thenReturn(dto2);

        List<ArmyDTO> result = armyService.getUserTemplates(authentication);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));

        verify(armyMapper).armyToArmyDTO(army1);
        verify(armyMapper).armyToArmyDTO(army2);
    }

    @Test
    void noDuplicateOfMagicWeapon_HappyPath() {
        SelectedUpgrade upg1 = mock(SelectedUpgrade.class);
        when(upg1.isSelected()).thenReturn(true);
        when(upg1.getUpgrade()).thenReturn(mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class));
        when(upg1.getUpgrade().getName()).thenReturn("Magic Weapon");

        SelectedUpgrade upg2 = mock(SelectedUpgrade.class);
        when(upg2.isSelected()).thenReturn(false);

        List<SelectedUpgrade> upgrades = List.of(upg1, upg2);

        boolean result = armyService.noDuplicateOfMagicWeapon(upgrades);
        assertTrue(result);
    }

    @Test
    void isArmyValid_ReturnsTrue_WhenAllChecksPass() {
        Army army = mock(Army.class);
        when(armyRepository.getReferenceById(1L)).thenReturn(army);

        when(army.getSelectedUnitsList()).thenReturn(new ArrayList<>());


        assertDoesNotThrow(() -> armyService.isArmyValid(1L));
    }

    @Test
    void getSelectedUnitSelectedUpgradesList_HappyPath() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");

        LoginUser user = new LoginUser();
        user.setId(1L);
        when(loginUserService.loadUserByUsername("user")).thenReturn(user);

        Army army = new Army();
        army.setOwner(user);
        when(armyRepository.findById(1L)).thenReturn(Optional.of(army));

        SelectedUpgradeDTO dto = new SelectedUpgradeDTO();
        when(selectedUpgradeService.getSelectedUpgradesBySelectedUnitId(10)).thenReturn(List.of(dto));

        List<SelectedUpgradeDTO> result = armyService.getSelectedUnitSelectedUpgradesList(1L, 10, auth);

        assertEquals(1, result.size());
        assertTrue(result.contains(dto));
    }

    @Test
    void deleteSelectedUnitFromArmyTemplate_HappyPath() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");

        LoginUser user = new LoginUser();
        user.setId(1L);
        when(loginUserService.loadUserByUsername("user")).thenReturn(user);

        Army army = new Army();
        army.setOwner(user);
        SelectedUnit unit1 = new SelectedUnit();
        unit1.setId(100);
        SelectedUnit unit2 = new SelectedUnit();
        unit2.setId(101);
        army.setSelectedUnitsList(new ArrayList<>(List.of(unit1, unit2)));

        when(armyRepository.getReferenceById(1L)).thenReturn(army);

        armyService.deleteSelectedUnitFromArmyTemplate(1L, 100, auth);

        ArgumentCaptor<Army> captor = ArgumentCaptor.forClass(Army.class);
        verify(armyRepository).save(captor.capture());
        Army saved = captor.getValue();

        assertEquals(1, saved.getSelectedUnitsList().size());
        assertEquals(101, saved.getSelectedUnitsList().get(0).getId());
    }

    @Test
    void isGeneralPickedUp_HappyPath_OneGeneral() {
        SelectedUpgrade generalUpgrade = mock(SelectedUpgrade.class);
        jasionowicz.warhammer.builder.Upgrade.Upgrade upgrade = mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class);
        when(upgrade.getName()).thenReturn("General");
        when(generalUpgrade.getUpgrade()).thenReturn(upgrade);

        List<SelectedUpgrade> upgrades = List.of(generalUpgrade);

        boolean result = armyService.isGeneralPickedUp(upgrades);

        assertTrue(result);
    }

    @Test
    void isGeneralPickedUp_MultipleGenerals_ReturnsFalse() {
        SelectedUpgrade general1 = mock(SelectedUpgrade.class);
        SelectedUpgrade general2 = mock(SelectedUpgrade.class);
        jasionowicz.warhammer.builder.Upgrade.Upgrade upgrade1 = mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class);
        jasionowicz.warhammer.builder.Upgrade.Upgrade upgrade2 = mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class);
        when(upgrade1.getName()).thenReturn("General");
        when(upgrade2.getName()).thenReturn("General");
        when(general1.getUpgrade()).thenReturn(upgrade1);
        when(general2.getUpgrade()).thenReturn(upgrade2);

        List<SelectedUpgrade> upgrades = List.of(general1, general2);

        boolean result = armyService.isGeneralPickedUp(upgrades);

        assertFalse(result);
    }

    @Test
    void isGeneralPickedUp_NoGeneral_ReturnsFalse() {
        SelectedUpgrade someUpgrade = mock(SelectedUpgrade.class);
        jasionowicz.warhammer.builder.Upgrade.Upgrade upgrade = mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class);
        when(upgrade.getName()).thenReturn("Magic Weapon");
        when(someUpgrade.getUpgrade()).thenReturn(upgrade);

        List<SelectedUpgrade> upgrades = List.of(someUpgrade);

        boolean result = armyService.isGeneralPickedUp(upgrades);

        assertFalse(result);
    }

    @Test
    void minimalAmmountOfCoreTaken_CorePointsNull_ReturnsFalse() {
        Army army = new Army();
        army.setCorePointsUsed(null);
        army.setCorePointsLimit(50.0);
        army.setPointsLimit(100.0); // <-- trzeba ustawić

        boolean result = armyService.minimalAmmountOfCoreTaken(army);

        assertFalse(result);
    }

    @Test
    void minimalAmmountOfCoreTaken_CorePointsLessThanLimit_ReturnsFalse() {
        Army army = new Army();
        army.setCorePointsUsed(40.0);
        army.setCorePointsLimit(50.0);
        army.setPointsLimit(100.0);

        boolean result = armyService.minimalAmmountOfCoreTaken(army);

        assertFalse(result);
    }

    @Test
    void minimalAmmountOfCoreTaken_CorePointsEqualOrAboveLimit_ReturnsTrue() {
        Army army = new Army();
        army.setCorePointsUsed(50.0);
        army.setCorePointsLimit(50.0);
        army.setPointsLimit(100.0);

        boolean result = armyService.minimalAmmountOfCoreTaken(army);
        assertTrue(result);

        army.setCorePointsUsed(60.0);
        result = armyService.minimalAmmountOfCoreTaken(army);
        assertTrue(result);
    }

    @Test
    void areLordsValid_NullPoints_ReturnsTrue() {
        Army army = new Army();
        army.setLordPointsUsed(null);
        army.setLordPointsLimit(50.0);

        boolean result = armyService.areLordsValid(army);

        assertTrue(result);
    }

    @Test
    void areLordsValid_UsedPointsLessThanLimit_ReturnsTrue() {
        Army army = new Army();
        army.setLordPointsUsed(40.0);
        army.setLordPointsLimit(50.0);

        boolean result = armyService.areLordsValid(army);

        assertTrue(result);
    }

    @Test
    void areLordsValid_UsedPointsEqualToLimit_ReturnsTrue() {
        Army army = new Army();
        army.setLordPointsUsed(50.0);
        army.setLordPointsLimit(50.0);

        boolean result = armyService.areLordsValid(army);

        assertTrue(result);
    }

    @Test
    void areLordsValid_UsedPointsAboveLimit_ReturnsFalse() {
        Army army = new Army();
        army.setLordPointsUsed(60.0);
        army.setLordPointsLimit(50.0);

        boolean result = armyService.areLordsValid(army);

        assertFalse(result);
    }
    @Test
    void areHeroValid_NullPoints_ReturnsTrue() {
        Army army = new Army();
        army.setHeroPointsUsed(null);
        army.setHeroPointsLimit(50.0);

        boolean result = armyService.areHeroValid(army);

        assertTrue(result);
    }

    @Test
    void areHeroValid_UsedPointsLessThanLimit_ReturnsTrue() {
        Army army = new Army();
        army.setHeroPointsUsed(40.0);
        army.setHeroPointsLimit(50.0);

        boolean result = armyService.areHeroValid(army);

        assertTrue(result);
    }

    @Test
    void areHeroValid_UsedPointsEqualToLimit_ReturnsTrue() {
        Army army = new Army();
        army.setHeroPointsUsed(50.0);
        army.setHeroPointsLimit(50.0);

        boolean result = armyService.areHeroValid(army);

        assertTrue(result);
    }

    @Test
    void areHeroValid_UsedPointsAboveLimit_ReturnsFalse() {
        Army army = new Army();
        army.setHeroPointsUsed(60.0);
        army.setHeroPointsLimit(50.0);

        boolean result = armyService.areHeroValid(army);

        assertFalse(result);
    }
    @Test
    void areSpecialValid_NullPoints_ReturnsTrue() {
        Army army = new Army();
        army.setSpecialPointsUsed(null);
        army.setSpecialPointsLimit(50.0);

        boolean result = armyService.areSpecialValid(army);

        assertTrue(result);
    }

    @Test
    void areSpecialValid_UsedPointsLessThanLimit_ReturnsTrue() {
        Army army = new Army();
        army.setSpecialPointsUsed(40.0);
        army.setSpecialPointsLimit(50.0);

        boolean result = armyService.areSpecialValid(army);

        assertTrue(result);
    }

    @Test
    void areSpecialValid_UsedPointsEqualToLimit_ReturnsTrue() {
        Army army = new Army();
        army.setSpecialPointsUsed(50.0);
        army.setSpecialPointsLimit(50.0);

        boolean result = armyService.areSpecialValid(army);

        assertTrue(result);
    }

    @Test
    void areSpecialValid_UsedPointsAboveLimit_ReturnsFalse() {
        Army army = new Army();
        army.setSpecialPointsUsed(60.0);
        army.setSpecialPointsLimit(50.0);

        boolean result = armyService.areSpecialValid(army);

        assertFalse(result);
    }
    @Test
    void areRareValid_NullPoints_ReturnsTrue() {
        Army army = new Army();
        army.setRarePointsUsed(null);
        army.setRarePointsLimit(50.0);

        boolean result = armyService.areRareValid(army);

        assertTrue(result);
    }

    @Test
    void areRareValid_UsedPointsLessThanLimit_ReturnsTrue() {
        Army army = new Army();
        army.setRarePointsUsed(40.0);
        army.setRarePointsLimit(50.0);

        boolean result = armyService.areRareValid(army);

        assertTrue(result);
    }

    @Test
    void areRareValid_UsedPointsEqualToLimit_ReturnsTrue() {
        Army army = new Army();
        army.setRarePointsUsed(50.0);
        army.setRarePointsLimit(50.0);

        boolean result = armyService.areRareValid(army);

        assertTrue(result);
    }

    @Test
    void areRareValid_UsedPointsAboveLimit_ReturnsFalse() {
        Army army = new Army();
        army.setRarePointsUsed(60.0);
        army.setRarePointsLimit(50.0);

        boolean result = armyService.areRareValid(army);

        assertFalse(result);
    }
    @Test
    void noDuplicateOfMagicWeapon_NoDuplicates_ReturnsTrue() {
        SelectedUpgrade upgrade = mock(SelectedUpgrade.class);
        jasionowicz.warhammer.builder.Upgrade.Upgrade upg = mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class);
        when(upg.getName()).thenReturn("Magic Weapon");
        when(upgrade.getUpgrade()).thenReturn(upg);
        when(upgrade.isSelected()).thenReturn(true);

        boolean result = armyService.noDuplicateOfMagicWeapon(List.of(upgrade));

        assertTrue(result);
    }

    @Test
    void noDuplicateOfMagicWeapon_DuplicateExists_ReturnsFalse() {
        SelectedUpgrade upgrade1 = mock(SelectedUpgrade.class);
        SelectedUpgrade upgrade2 = mock(SelectedUpgrade.class);
        jasionowicz.warhammer.builder.Upgrade.Upgrade upg1 = mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class);
        jasionowicz.warhammer.builder.Upgrade.Upgrade upg2 = mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class);

        when(upg1.getName()).thenReturn("Magic Weapon");
        when(upg2.getName()).thenReturn("Magic Weapon");

        when(upgrade1.getUpgrade()).thenReturn(upg1);
        when(upgrade2.getUpgrade()).thenReturn(upg2);

        when(upgrade1.isSelected()).thenReturn(true);
        when(upgrade2.isSelected()).thenReturn(true);

        boolean result = armyService.noDuplicateOfMagicWeapon(List.of(upgrade1, upgrade2));

        assertFalse(result);
    }

    @Test
    void noDuplicateOfMagicWeapon_UnselectedDuplicates_Ignored_ReturnsTrue() {
        SelectedUpgrade upgrade1 = mock(SelectedUpgrade.class);
        jasionowicz.warhammer.builder.Upgrade.Upgrade upg1 = mock(jasionowicz.warhammer.builder.Upgrade.Upgrade.class);

        when(upg1.getName()).thenReturn("Magic Weapon");
        when(upgrade1.getUpgrade()).thenReturn(upg1);
        when(upgrade1.isSelected()).thenReturn(true);

        SelectedUpgrade upgrade2 = mock(SelectedUpgrade.class);
        when(upgrade2.isSelected()).thenReturn(false);

        boolean result = armyService.noDuplicateOfMagicWeapon(List.of(upgrade1, upgrade2));
        assertTrue(result);
    }
    @Test
    void maxAmmoutOfDuplicationInSpecialAndRare_LessThanLimit_ReturnsTrue() {
        Army army = new Army();
        army.setPointsLimit(2000.0); // < 3000, limit max 3

        SelectedUnit unit1 = mock(SelectedUnit.class);
        SelectedUnit unit2 = mock(SelectedUnit.class);
        SelectedUnit unit3 = mock(SelectedUnit.class);

        Unit u1 = mock(Unit.class);
        when(u1.getUnitType()).thenReturn("Special");
        when(u1.getName()).thenReturn("Spearman");
        when(unit1.getUnit()).thenReturn(u1);
        when(unit2.getUnit()).thenReturn(u1);
        when(unit3.getUnit()).thenReturn(u1);

        boolean result = armyService.maxAmmoutOfDuplicationInSpecialAndRare(List.of(unit1, unit2, unit3), army);
        assertTrue(result);
    }

    @Test
    void maxAmmoutOfDuplicationInSpecialAndRare_AboveLimit_ReturnsFalse() {
        Army army = new Army();
        army.setPointsLimit(2000.0);

        SelectedUnit unit1 = mock(SelectedUnit.class);
        SelectedUnit unit2 = mock(SelectedUnit.class);
        SelectedUnit unit3 = mock(SelectedUnit.class);
        SelectedUnit unit4 = mock(SelectedUnit.class);

        Unit u1 = mock(Unit.class);
        when(u1.getUnitType()).thenReturn("Special");
        when(u1.getName()).thenReturn("Spearman");
        when(unit1.getUnit()).thenReturn(u1);
        when(unit2.getUnit()).thenReturn(u1);
        when(unit3.getUnit()).thenReturn(u1);
        when(unit4.getUnit()).thenReturn(u1);

        boolean result = armyService.maxAmmoutOfDuplicationInSpecialAndRare(List.of(unit1, unit2, unit3, unit4), army);
        assertFalse(result);
    }

    @Test
    void maxAmmoutOfDuplicationInSpecialAndRare_LargeArmyLimit_ReturnsTrueOrFalse() {
        Army army = new Army();
        army.setPointsLimit(5000.0);

        SelectedUnit unit = mock(SelectedUnit.class);
        Unit u = mock(Unit.class);
        when(u.getUnitType()).thenReturn("Rare");
        when(u.getName()).thenReturn("Dragon");
        when(unit.getUnit()).thenReturn(u);

        List<SelectedUnit> units = new ArrayList<>();
        for (int i = 0; i < 6; i++) units.add(unit);

        boolean result = armyService.maxAmmoutOfDuplicationInSpecialAndRare(units, army);
        assertTrue(result);

        units.add(unit);
        result = armyService.maxAmmoutOfDuplicationInSpecialAndRare(units, army);
        assertFalse(result);
    }

    @Test
    void isArmyValid_AllChecksPass_ReturnsTrue() {
        Long armyId = 1L;

        Army army = mock(Army.class);
        SelectedUnit unit = mock(SelectedUnit.class);
        SelectedUpgrade upgrade = mock(SelectedUpgrade.class);

        when(army.getSelectedUnitsList()).thenReturn(List.of(unit));
        when(unit.getSelectedUpgrades()).thenReturn(List.of(upgrade));
        when(upgrade.isSelected()).thenReturn(true);

        when(armyRepository.getReferenceById(armyId)).thenReturn(army);

        ArmyService spyService = spy(armyService);
        doReturn(true).when(spyService).isGeneralPickedUp(anyList());
        doReturn(true).when(spyService).minimalAmmountOfCoreTaken(army);
        doReturn(true).when(spyService).areLordsValid(army);
        doReturn(true).when(spyService).areHeroValid(army);
        doReturn(true).when(spyService).areSpecialValid(army);
        doReturn(true).when(spyService).areRareValid(army);
        doReturn(true).when(spyService).noDuplicateOfMagicWeapon(anyList());
        doReturn(true).when(spyService).maxAmmoutOfDuplicationInSpecialAndRare(anyList(), eq(army));

        Boolean result = spyService.isArmyValid(armyId);

        assertTrue(result);
    }

    @Test
    void isArmyValid_OneCheckFails_ReturnsFalse() {
        Long armyId = 1L;

        Army army = mock(Army.class);
        SelectedUnit unit = mock(SelectedUnit.class);
        SelectedUpgrade upgrade = mock(SelectedUpgrade.class);

        when(army.getSelectedUnitsList()).thenReturn(List.of(unit));
        when(unit.getSelectedUpgrades()).thenReturn(List.of(upgrade));
        when(upgrade.isSelected()).thenReturn(true);

        when(armyRepository.getReferenceById(armyId)).thenReturn(army);

        ArmyService spyService = spy(armyService);
        doReturn(false).when(spyService).isGeneralPickedUp(anyList());
        doReturn(true).when(spyService).minimalAmmountOfCoreTaken(army);
        doReturn(true).when(spyService).areLordsValid(army);
        doReturn(true).when(spyService).areHeroValid(army);
        doReturn(true).when(spyService).areSpecialValid(army);
        doReturn(true).when(spyService).areRareValid(army);
        doReturn(true).when(spyService).noDuplicateOfMagicWeapon(anyList());
        doReturn(true).when(spyService).maxAmmoutOfDuplicationInSpecialAndRare(anyList(), eq(army));

        Boolean result = spyService.isArmyValid(armyId);

        assertFalse(result);
    }

}
