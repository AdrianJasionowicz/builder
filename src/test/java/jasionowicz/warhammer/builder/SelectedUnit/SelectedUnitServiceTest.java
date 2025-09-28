package jasionowicz.warhammer.builder.SelectedUnit;

import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.Army.ArmyRepository;
import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import jasionowicz.warhammer.builder.LoginUser.LoginUserRepository;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeService;
import jasionowicz.warhammer.builder.Unit.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectedUnitServiceTest {



    @Mock
    private SelectedUnitRepository selectedUnitRepository;
    @Mock
    private LoginUserRepository loginUserRepository;
    @Mock
    private SelectedUpgradeService selectedUpgradeService;
    @InjectMocks
    private SelectedUnitService selectedUnitService;
    @Mock
    private ArmyRepository armyRepository;

    @Test
    void TestIncreaseUnitQuantity_HappyPath() {
        Integer unitId = 1;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        LoginUser loginUser = new LoginUser();
        loginUser.setId(10L);
        loginUser.setUsername("testUser");
        when(loginUserRepository.findByUsername("testUser")).thenReturn(Optional.of(loginUser));

        Army army = new Army();
        army.setId(100L);
        army.setOwner(loginUser);

        Unit unit = new Unit();

        SelectedUnit selectedUnit = new SelectedUnit();
        selectedUnit.setId(unitId);
        selectedUnit.setQuantity(5.0);
        selectedUnit.setArmy(army);
        selectedUnit.setUnit(unit);

        when(selectedUnitRepository.findById(unitId)).thenReturn(Optional.of(selectedUnit));
        when(armyRepository.findById(army.getId())).thenReturn(Optional.of(army));

        selectedUnitService.increaseUnitQuantity(unitId, authentication);

        assertEquals(6.0, selectedUnit.getQuantity());
        verify(selectedUpgradeService).checkUpgradesQuantities(unitId, 6.0);
        verify(selectedUnitRepository).save(selectedUnit);
    }

    @Test
    void TestIncreaseUnitQuantity_UnauthorizedUser() {
        Integer unitId = 1;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("wrongUser");

        LoginUser loginUser = new LoginUser();
        loginUser.setId(10L);
        loginUser.setUsername("wrongUser");
        when(loginUserRepository.findByUsername("wrongUser")).thenReturn(Optional.of(loginUser));

        Army army = new Army();
        army.setId(100L);

        LoginUser armyOwner = new LoginUser();
        armyOwner.setId(99L);
        army.setOwner(armyOwner);

        Unit unit = new Unit();

        SelectedUnit selectedUnit = new SelectedUnit();
        selectedUnit.setId(unitId);
        selectedUnit.setQuantity(5.0);
        selectedUnit.setArmy(army);
        selectedUnit.setUnit(unit);

        when(selectedUnitRepository.findById(unitId)).thenReturn(Optional.of(selectedUnit));

        selectedUnitService.increaseUnitQuantity(unitId, authentication);

        assertEquals(5.0, selectedUnit.getQuantity());

        verify(selectedUpgradeService, never()).checkUpgradesQuantities(anyInt(), anyDouble());
        verify(selectedUnitRepository, never()).save(any(SelectedUnit.class));
    }


    @Test
    void TestDecreaseUnitQuantity_HappyPath() {
        Integer unitId = 1;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        LoginUser loginUser = new LoginUser();
        loginUser.setId(10L);
        loginUser.setUsername("testUser");
        when(loginUserRepository.findByUsername("testUser")).thenReturn(Optional.of(loginUser));

        Army army = new Army();
        army.setId(100L);
        army.setOwner(loginUser);

        Unit unit = new Unit();

        SelectedUnit selectedUnit = new SelectedUnit();
        selectedUnit.setId(unitId);
        selectedUnit.setQuantity(5.0);
        selectedUnit.setArmy(army);
        selectedUnit.setUnit(unit);

        when(selectedUnitRepository.findById(unitId)).thenReturn(Optional.of(selectedUnit));
        when(armyRepository.findById(army.getId())).thenReturn(Optional.of(army));

        selectedUnitService.decreaseUnitQuantity(unitId, authentication);

        assertEquals(4.0, selectedUnit.getQuantity());
        verify(selectedUpgradeService).checkUpgradesQuantities(unitId, 4.0);
        verify(selectedUnitRepository).save(selectedUnit);
    }

    @Test
    void TestDecreaseUnitQuantity_UnauthorizedUser() {
        Integer unitId = 1;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("wrongUser");

        LoginUser loginUser = new LoginUser();
        loginUser.setId(10L);
        loginUser.setUsername("wrongUser");
        when(loginUserRepository.findByUsername("wrongUser")).thenReturn(Optional.of(loginUser));

        Army army = new Army();
        army.setId(100L);

        LoginUser armyOwner = new LoginUser();
        armyOwner.setId(99L);
        army.setOwner(armyOwner);

        Unit unit = new Unit();

        SelectedUnit selectedUnit = new SelectedUnit();
        selectedUnit.setId(unitId);
        selectedUnit.setQuantity(5.0);
        selectedUnit.setArmy(army);
        selectedUnit.setUnit(unit);

        when(selectedUnitRepository.findById(unitId)).thenReturn(Optional.of(selectedUnit));

        selectedUnitService.decreaseUnitQuantity(unitId, authentication);

        assertEquals(5.0, selectedUnit.getQuantity());

        verify(selectedUpgradeService, never()).checkUpgradesQuantities(anyInt(), anyDouble());
        verify(selectedUnitRepository, never()).save(any(SelectedUnit.class));
    }


    @Test
    void CheckIfCalculateTotalCostOfUnits() {
        Army army = new Army();
        army.setId(100L);
        army.setOwner(null);

        Unit unit = new Unit();
        unit.setPointsCostPerUnit(5.0);

        SelectedUnit selectedUnit = new SelectedUnit();
        selectedUnit.setId(1);
        selectedUnit.setQuantity(5.0);
        selectedUnit.setArmy(army);
        selectedUnit.setUnit(unit);

        army.setSelectedUnitsList(List.of(selectedUnit));

        when(armyRepository.findById(army.getId())).thenReturn(Optional.of(army));

        selectedUnitService.calculateTotalCostOfUnits(100L);

        assertEquals(25.0, selectedUnit.getTotalCost());

        verify(selectedUnitRepository).save(selectedUnit);
    }
}