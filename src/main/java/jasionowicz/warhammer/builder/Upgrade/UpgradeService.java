package jasionowicz.warhammer.builder.Upgrade;

import jasionowicz.warhammer.builder.Mapper.UpgradeMapper;
import org.springframework.stereotype.Service;

@Service
public class UpgradeService {
    private final UpgradeRepository upgradeRepository;
    private final UpgradeMapper upgradeMapper;

    public UpgradeService(UpgradeRepository upgradeRepository, UpgradeMapper upgradeMapper) {
        this.upgradeRepository = upgradeRepository;
        this.upgradeMapper = upgradeMapper;
    }

    public void addUpgrade(UpgradeDTO upgradeDTO) {
        Upgrade upgrade = upgradeMapper.dtoToUpgrade(upgradeDTO);
        upgradeRepository.save(upgrade);
    }

    public void deleteUpgradeById(Integer id) {
        upgradeRepository.deleteById(id);
    }

    public void updateUpgrade(Integer id, UpgradeDTO upgradeDTO) {
        Upgrade existing = upgradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Upgrade not found"));

        Upgrade fromDto = upgradeMapper.dtoToUpgrade(upgradeDTO);
        existing.setName(fromDto.getName());
        existing.setPointsCost(fromDto.getPointsCost());
        existing.setDescription(fromDto.getDescription());
        existing.setUpgradeType(fromDto.getUpgradeType());

        upgradeRepository.save(existing);
    }

}
