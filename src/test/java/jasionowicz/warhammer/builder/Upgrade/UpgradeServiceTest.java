package jasionowicz.warhammer.builder.Upgrade;

import jasionowicz.warhammer.builder.Mapper.UpgradeMapper;
import jasionowicz.warhammer.builder.Unit.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpgradeServiceTest {

    @Mock
    UpgradeRepository upgradeRepository;
    @Mock
    UpgradeMapper upgradeMapper;
    @InjectMocks
    UpgradeService upgradeService;




    @Test
    void updateUpgrade_mapsDtoAndSavesMergedEntity() {
        Integer id = 42;

        UpgradeDTO dto = new UpgradeDTO();
        dto.setId(777);
        dto.setName("New Name");
        dto.setDescription("New Desc");
        dto.setPointsCost(123.0);
        dto.setUpgradeType("WEAPON");

        Upgrade existing = new Upgrade();
        existing.setId(id);
        existing.setName("Old Name");
        existing.setDescription("Old Desc");
        existing.setPointsCost(10.0);
        existing.setUpgradeType("ARMOR");

        Upgrade mapped = new Upgrade();
        mapped.setId(dto.getId());
        mapped.setName(dto.getName());
        mapped.setDescription(dto.getDescription());
        mapped.setPointsCost(dto.getPointsCost());
        mapped.setUpgradeType(dto.getUpgradeType());

        when(upgradeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(upgradeMapper.dtoToUpgrade(dto)).thenReturn(mapped);

        ArgumentCaptor<Upgrade> captor = ArgumentCaptor.forClass(Upgrade.class);

        upgradeService.updateUpgrade(id, dto);

        verify(upgradeRepository).findById(id);
        verify(upgradeMapper).dtoToUpgrade(dto);
        verify(upgradeRepository).save(captor.capture());

        Upgrade saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(id, saved.getId());
        assertEquals("New Name", saved.getName());
        assertEquals("New Desc", saved.getDescription());
        assertEquals(123.0, saved.getPointsCost());
        assertEquals("WEAPON", saved.getUpgradeType());
    }

    @Test
    void updateUpgrade_throwsWhenNotFound() {
        Integer id = 13;
        UpgradeDTO dto = new UpgradeDTO();
        when(upgradeRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> upgradeService.updateUpgrade(id, dto));
        assertTrue(ex.getMessage().contains("Upgrade not found"));

        verify(upgradeRepository).findById(id);
        verifyNoInteractions(upgradeMapper);
        verify(upgradeRepository, never()).save(any());
    }
}