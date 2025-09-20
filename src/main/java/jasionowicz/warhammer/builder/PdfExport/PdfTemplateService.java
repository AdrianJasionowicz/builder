package jasionowicz.warhammer.builder.PdfExport;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.Army.ArmyRepository;
import jasionowicz.warhammer.builder.Army.ArmyService;
import jasionowicz.warhammer.builder.SelectedStats.SelectedStatsService;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeRepository;
import jasionowicz.warhammer.builder.Unit.Unit;
import jasionowicz.warhammer.builder.Unit.UnitRepository;
import jasionowicz.warhammer.builder.UnitStats.UnitStats;
import jasionowicz.warhammer.builder.UnitStats.UnitStatsRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PdfTemplateService {


    private final ArmyService armyService;
    private final UnitRepository unitRepository;
    private final UnitStatsRepository unitStatsRepository;
    private final SelectedUnit selectedUnit;
    private final SelectedUpgradeRepository selectedUpgradeRepository;
    private ArmyRepository armyRepository;
    private SelectedStatsService selectedStatsService;

    public PdfTemplateService(ArmyRepository armyRepository, ArmyService armyService, SelectedStatsService selectedStatsService, UnitRepository unitRepository, UnitStatsRepository unitStatsRepository, SelectedUnit selectedUnit, SelectedUpgradeRepository selectedUpgradeRepository) {
        this.armyRepository = armyRepository;
        this.armyService = armyService;
        this.selectedStatsService = selectedStatsService;
        this.unitRepository = unitRepository;
        this.unitStatsRepository = unitStatsRepository;
        this.selectedUnit = selectedUnit;
        this.selectedUpgradeRepository = selectedUpgradeRepository;
    }

    public byte[] generateArmyPdf(String armyName, Long armyId) {
        selectedStatsService.adjustArmyStatsToSelectedUpgrades(armyId);
        Army army = armyRepository.getReferenceById(armyId);
        List<SelectedUnit> selectedUnitList = army.getSelectedUnitsList();
        List<SelectedUnit> selectedUnitListLords = selectedUnitList.stream()
                .filter(unit ->unit.getUnitType().equalsIgnoreCase("Lords"))
                .toList();
        List<SelectedUnit> selectedUnitListHeroes = selectedUnitList.stream()
                .filter(unit->unit.getUnitType().equalsIgnoreCase("Hero"))
                .toList();
        List<SelectedUnit> selectedUnitListCores = selectedUnitList.stream()
                .filter(unit->unit.getUnitType().equalsIgnoreCase("Core"))
                .toList();
        List<SelectedUnit> selectedUnitListSpecials = selectedUnitList.stream()
                .filter(unit ->unit.getUnitType().equalsIgnoreCase("Special"))
                .toList();
        List<SelectedUnit> selectedUnitsListRares = selectedUnitList.stream()
                .filter(unit->unit.getUnitType().equalsIgnoreCase("Rare"))
                .toList();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Warhammer Army: " + armyName).setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Army: " + army.getFactionName() + " " + army.getPointsUsed() + " / "  + army.getPointsLimit() + " pts").setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph().setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Is Army valid: " + armyService.isArmyValid(armyId)).setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            // Lords Main Table
            Table table = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell cell = new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Paragraph p = new Paragraph();
            p.addTabStops(new TabStop(520, TabAlignment.RIGHT));
            p.add("Lords: ").setBold();
            p.add(new Tab());
            p.add(String.valueOf(army.getLordPointsUsed() + " pts"));
            cell.add(p);
            table.addCell(cell);
            document.add(table);

            // Lords
            for (SelectedUnit selectedUnitListLord : selectedUnitListLords) {
                Table table2 = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Paragraph p2 = new Paragraph();
                p2.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
                p2.add(new Text(selectedUnitListLord.getUnit().getName() + " Squad"));
                p2.add(new com.itextpdf.layout.element.Tab());
                p2.add(new Text(String.valueOf(selectedUnitListLord.getTotalCost()) + " pts"));

                Cell cell2 = new Cell().add(p2);
                table2.addCell(cell2);
                document.add(table2);

                Table statsTable = new Table(13).setWidth(UnitValue.createPercentValue(100));
                String[] headers = {"Unit", "Qty", "M", "Ws", "Bs", "S", "T", "W", "I", "A", "Ld", "Save", "Ward Save"};
                for (String header : headers) {
                    statsTable.addCell(
                            new Cell().add(new Paragraph(header))
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setTextAlignment(TextAlignment.CENTER)

                    );
                }
                // Stats
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getUnit().getName()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(selectedUnitListLord.getQuantity())))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getM()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getWs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getBs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getS()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getT()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getW()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getI()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getA()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getLd()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListLord.getSelectedStats().getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                //Upg
                List<SelectedUpgrade> selectedUpgradeList = selectedUnitListLord.getSelectedUpgrades().stream()
                        .filter(SelectedUpgrade::isSelected)
                        .toList();
                for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                    String type = selectedUpgrade.getUpgrade().getUpgradeType();

                    if (type.equalsIgnoreCase("Mount") || type.equalsIgnoreCase("Crew")) {
                        Unit unit = unitRepository.findByName(selectedUpgrade.getUpgrade().getName());
                        UnitStats stats = unitStatsRepository.getReferenceById(unit.getUnitStats().getId());
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit.getName())))).setTextAlignment(TextAlignment.CENTER);
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getM()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getS()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getT()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getW()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getI()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getA()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getLd()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        Unit unit1 = unitRepository.findByName(selectedUpgrade.getUpgrade().getName() + " Crew");
                        if (unit1 != null) {
                            UnitStats stats2 = unitStatsRepository.getReferenceById(unit1.getUnitStats().getId());
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit1.getNation())))).setTextAlignment(TextAlignment.CENTER);
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getM()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getS()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getT()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getW()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getI()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getA()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getLd()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        }



                    }

                    }
                document.add(statsTable);


                Table upgradesTable = new Table(3).setWidth(UnitValue.createPercentValue(100));

                for (int i = 0; i < selectedUpgradeList.size(); i += 3) {

                    Cell cell9 = new Cell().add(new Paragraph(
                            selectedUpgradeList.get(i).getUpgrade().getName() +
                                    " (" + selectedUpgradeList.get(i).getUpgrade().getPointsCost() * selectedUpgradeList.get(i).getQuantity() + " pkt)"
                    )).setBorder(Border.NO_BORDER);
                    upgradesTable.addCell(cell9);

                    if (i + 1 < selectedUpgradeList.size()) {
                        Cell cell10 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 1).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 1).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 1).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell10);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }

                    if (i + 2 < selectedUpgradeList.size()) {
                        Cell cell11 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 2).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 2).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 2).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell11);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }
                }


                document.add(new Paragraph("Upgrades:").setFontSize(10).setMarginTop(10));
                document.add(upgradesTable);


            }

            // Hero Main Table
            Table heroTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell heroCell = new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Paragraph heroParagraph = new Paragraph();
            heroParagraph.addTabStops(new TabStop(520, TabAlignment.RIGHT));
            heroParagraph.add("Heroes: ").setBold();
            heroParagraph.add(new Tab());
            heroParagraph.add(String.valueOf(army.getHeroPointsUsed() + " pts"));
            heroCell.add(heroParagraph);
            heroTable.addCell(heroCell);
            document.add(heroTable);

            // hero
            for (SelectedUnit selectedUnitListHero : selectedUnitListHeroes) {
                Table heroTable2 = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Paragraph heroParagraph2 = new Paragraph();
                heroParagraph2.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
                heroParagraph2.add(new Text(selectedUnitListHero.getUnit().getName() + " Squad"));
                heroParagraph2.add(new com.itextpdf.layout.element.Tab());
                heroParagraph2.add(new Text(String.valueOf(selectedUnitListHero.getTotalCost()) + " pts"));

                Cell heroCell2 = new Cell().add(heroParagraph2);
                heroTable2.addCell(heroCell2);
                document.add(heroTable2);

                Table statsTable = new Table(13).setWidth(UnitValue.createPercentValue(100));
                String[] headers = {"Unit", "Qty", "M", "Ws", "Bs", "S", "T", "W", "I", "A", "Ld", "Save", "Ward Save"};
                for (String header : headers) {
                    statsTable.addCell(
                            new Cell().add(new Paragraph(header))
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setTextAlignment(TextAlignment.CENTER)

                    );
                }
                // Stats
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getUnit().getName()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(selectedUnitListHero.getQuantity())))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getM()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getWs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getBs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getS()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getT()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getW()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getI()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getA()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getLd()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListHero.getSelectedStats().getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                //Upg
                List<SelectedUpgrade> selectedUpgradeList = selectedUnitListHero.getSelectedUpgrades().stream()
                        .filter(SelectedUpgrade::isSelected)
                        .toList();
                for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                    String type = selectedUpgrade.getUpgrade().getUpgradeType();

                    if (type.equalsIgnoreCase("Mount") || type.equalsIgnoreCase("Crew")) {
                        Unit unit = unitRepository.findByName(selectedUpgrade.getUpgrade().getName());
                        UnitStats stats = unitStatsRepository.getReferenceById(unit.getUnitStats().getId());
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit.getName())))).setTextAlignment(TextAlignment.CENTER);
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getM()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getS()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getT()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getW()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getI()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getA()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getLd()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        Unit unit1 = unitRepository.findByName(selectedUpgrade.getUpgrade().getName() + " Crew");
                        if (unit1 != null) {
                            UnitStats stats2 = unitStatsRepository.getReferenceById(unit1.getUnitStats().getId());
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit1.getNation())))).setTextAlignment(TextAlignment.CENTER);
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getM()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getS()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getT()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getW()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getI()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getA()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getLd()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        }



                    }

                }
                document.add(statsTable);


                Table upgradesTable = new Table(3).setWidth(UnitValue.createPercentValue(100));

                for (int i = 0; i < selectedUpgradeList.size(); i += 3) {

                    Cell cell9 = new Cell().add(new Paragraph(
                            selectedUpgradeList.get(i).getUpgrade().getName() +
                                    " (" + selectedUpgradeList.get(i).getUpgrade().getPointsCost() * selectedUpgradeList.get(i).getQuantity() + " pkt)"
                    )).setBorder(Border.NO_BORDER);
                    upgradesTable.addCell(cell9);

                    if (i + 1 < selectedUpgradeList.size()) {
                        Cell cell10 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 1).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 1).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 1).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell10);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }

                    if (i + 2 < selectedUpgradeList.size()) {
                        Cell cell11 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 2).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 2).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 2).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell11);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }
                }


                document.add(new Paragraph("Upgrades:").setFontSize(10).setMarginTop(10));
                document.add(upgradesTable);


            }

            // Core Main Table
            Table coreTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell coreCell = new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Paragraph coreParagraph = new Paragraph();
            coreParagraph.addTabStops(new TabStop(520, TabAlignment.RIGHT));
            coreParagraph.add("Core: ").setBold();
            coreParagraph.add(new Tab());
            coreParagraph.add(String.valueOf(army.getCorePointsUsed() + " pts"));
            coreCell.add(coreParagraph);
            coreTable.addCell(coreParagraph);
            document.add(coreTable);

            // core
            for (SelectedUnit selectedUnitListCore : selectedUnitListCores) {
                Table coreTable2 = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Paragraph coreParagraph2 = new Paragraph();
                coreParagraph2.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
                coreParagraph2.add(new Text(selectedUnitListCore.getUnit().getName() + " Squad"));
                coreParagraph2.add(new com.itextpdf.layout.element.Tab());
                coreParagraph2.add(new Text(String.valueOf(selectedUnitListCore.getTotalCost()) + " pts"));

                Cell coreCell2 = new Cell().add(coreParagraph2);
                coreTable2.addCell(coreCell2);
                document.add(coreTable2);

                Table statsTable = new Table(13).setWidth(UnitValue.createPercentValue(100));
                String[] headers = {"Unit", "Qty", "M", "Ws", "Bs", "S", "T", "W", "I", "A", "Ld", "Save", "Ward Save"};
                for (String header : headers) {
                    statsTable.addCell(
                            new Cell().add(new Paragraph(header))
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setTextAlignment(TextAlignment.CENTER)

                    );
                }
                // Stats
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getUnit().getName()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(selectedUnitListCore.getQuantity())))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getM()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getWs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getBs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getS()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getT()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getW()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getI()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getA()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getLd()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                //Upg
                List<SelectedUpgrade> selectedUpgradeList = selectedUnitListCore.getSelectedUpgrades().stream()
                        .filter(SelectedUpgrade::isSelected)
                        .toList();
                for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                    String type = selectedUpgrade.getUpgrade().getUpgradeType();

                    if (type.equalsIgnoreCase("Mount") || type.equalsIgnoreCase("Crew") || type.equalsIgnoreCase("Champion")) {
                        Unit unit = unitRepository.findByName(selectedUpgrade.getUpgrade().getName());
                        UnitStats stats = unitStatsRepository.getReferenceById(unit.getUnitStats().getId());
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit.getName())))).setTextAlignment(TextAlignment.CENTER);
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getM()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getS()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getT()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getW()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getI()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getA()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getLd()))).setTextAlignment(TextAlignment.CENTER));
                        if (type.equalsIgnoreCase("Champion")) {
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListCore.getSelectedStats().getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        } else {
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        }
                        Unit unit1 = unitRepository.findByName(selectedUpgrade.getUpgrade().getName() + " Crew");
                        if (unit1 != null) {
                            UnitStats stats2 = unitStatsRepository.getReferenceById(unit1.getUnitStats().getId());
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit1.getNation())))).setTextAlignment(TextAlignment.CENTER);
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getM()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getS()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getT()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getW()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getI()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getA()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getLd()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        }



                    }

                }
                document.add(statsTable);


                Table upgradesTable = new Table(3).setWidth(UnitValue.createPercentValue(100));

                for (int i = 0; i < selectedUpgradeList.size(); i += 3) {

                    Cell cell9 = new Cell().add(new Paragraph(
                            selectedUpgradeList.get(i).getUpgrade().getName() +
                                    " (" + selectedUpgradeList.get(i).getUpgrade().getPointsCost() * selectedUpgradeList.get(i).getQuantity() + " pkt)"
                    )).setBorder(Border.NO_BORDER);
                    upgradesTable.addCell(cell9);

                    if (i + 1 < selectedUpgradeList.size()) {
                        Cell cell10 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 1).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 1).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 1).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell10);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }

                    if (i + 2 < selectedUpgradeList.size()) {
                        Cell cell11 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 2).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 2).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 2).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell11);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }
                }


                document.add(new Paragraph("Upgrades:").setFontSize(10).setMarginTop(10));
                document.add(upgradesTable);


            }
// Special Main Table
            Table specialTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell specialCell = new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Paragraph specialParagraph = new Paragraph();
            specialParagraph.addTabStops(new TabStop(520, TabAlignment.RIGHT));
            specialParagraph.add("Special: ").setBold();
            specialParagraph.add(new Tab());
            specialParagraph.add(String.valueOf(army.getSpecialPointsUsed() + " pts"));
            specialCell.add(specialParagraph);
            specialTable.addCell(specialParagraph);
            document.add(specialTable);

            // special
            for (SelectedUnit selectedUnitListSpecial : selectedUnitListSpecials) {
                Table specialTable2 = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Paragraph specialParagraph2 = new Paragraph();
                specialParagraph2.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
                specialParagraph2.add(new Text(selectedUnitListSpecial.getUnit().getName() + " Squad"));
                specialParagraph2.add(new com.itextpdf.layout.element.Tab());
                specialParagraph2.add(new Text(String.valueOf(selectedUnitListSpecial.getTotalCost()) + " pts"));

                Cell specialCell2 = new Cell().add(specialParagraph2);
                specialTable2.addCell(specialCell2);
                document.add(specialCell2);

                Table statsTable = new Table(13).setWidth(UnitValue.createPercentValue(100));
                String[] headers = {"Unit", "Qty", "M", "Ws", "Bs", "S", "T", "W", "I", "A", "Ld", "Save", "Ward Save"};
                for (String header : headers) {
                    statsTable.addCell(
                            new Cell().add(new Paragraph(header))
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setTextAlignment(TextAlignment.CENTER)

                    );
                }
                // Stats
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getUnit().getName()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(selectedUnitListSpecial.getQuantity())))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getM()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getWs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getBs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getS()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getT()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getW()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getI()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getA()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getLd()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListSpecial.getSelectedStats().getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                //Upg
                List<SelectedUpgrade> selectedUpgradeList = selectedUnitListSpecial.getSelectedUpgrades().stream()
                        .filter(SelectedUpgrade::isSelected)
                        .toList();
                for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                    String type = selectedUpgrade.getUpgrade().getUpgradeType();

                    if (type.equalsIgnoreCase("Mount") || type.equalsIgnoreCase("Crew") || type.equalsIgnoreCase("Champion")) {
                        Unit unit = unitRepository.findByName(selectedUpgrade.getUpgrade().getName());
                        UnitStats stats = unitStatsRepository.getReferenceById(unit.getUnitStats().getId());
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit.getName())))).setTextAlignment(TextAlignment.CENTER);
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getM()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getS()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getT()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getW()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getI()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getA()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getLd()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        Unit unit1 = unitRepository.findByName(selectedUpgrade.getUpgrade().getName() + " Crew");
                        if (unit1 != null) {
                            UnitStats stats2 = unitStatsRepository.getReferenceById(unit1.getUnitStats().getId());
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit1.getNation())))).setTextAlignment(TextAlignment.CENTER);
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getM()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getS()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getT()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getW()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getI()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getA()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getLd()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        }



                    }

                }
                document.add(statsTable);


                Table upgradesTable = new Table(3).setWidth(UnitValue.createPercentValue(100));

                for (int i = 0; i < selectedUpgradeList.size(); i += 3) {

                    Cell cell9 = new Cell().add(new Paragraph(
                            selectedUpgradeList.get(i).getUpgrade().getName() +
                                    " (" + selectedUpgradeList.get(i).getUpgrade().getPointsCost() * selectedUpgradeList.get(i).getQuantity() + " pkt)"
                    )).setBorder(Border.NO_BORDER);
                    upgradesTable.addCell(cell9);

                    if (i + 1 < selectedUpgradeList.size()) {
                        Cell cell10 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 1).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 1).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 1).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell10);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }

                    if (i + 2 < selectedUpgradeList.size()) {
                        Cell cell11 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 2).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 2).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 2).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell11);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }
                }


                document.add(new Paragraph("Upgrades:").setFontSize(10).setMarginTop(10));
                document.add(upgradesTable);


            }
            // Rare Main Table
            Table rareTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell rareCell = new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Paragraph rareParagraph = new Paragraph();
            rareParagraph.addTabStops(new TabStop(520, TabAlignment.RIGHT));
            rareParagraph.add("Rare: ").setBold();
            rareParagraph.add(new Tab());
            rareParagraph.add(String.valueOf(army.getRarePointsUsed() + " pts"));
            rareCell.add(rareParagraph);
            rareTable.addCell(rareParagraph);
            document.add(rareTable);

            // rare
            for (SelectedUnit selectedUnitListRare : selectedUnitsListRares) {
                Table rareTable2 = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Paragraph rareParagraph2 = new Paragraph();
                rareParagraph2.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
                rareParagraph2.add(new Text(selectedUnitListRare.getUnit().getName() + " Squad"));
                rareParagraph2.add(new com.itextpdf.layout.element.Tab());
                rareParagraph2.add(new Text(String.valueOf(selectedUnitListRare.getTotalCost()) + " pts"));

                Cell rareCell2 = new Cell().add(rareParagraph2);
                rareTable2.addCell(rareCell2);
                document.add(rareTable2);

                Table statsTable = new Table(13).setWidth(UnitValue.createPercentValue(100));
                String[] headers = {"Unit", "Qty", "M", "Ws", "Bs", "S", "T", "W", "I", "A", "Ld", "Save", "Ward Save"};
                for (String header : headers) {
                    statsTable.addCell(
                            new Cell().add(new Paragraph(header))
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setTextAlignment(TextAlignment.CENTER)

                    );
                }
                // Stats
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getUnit().getName()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(selectedUnitListRare.getQuantity())))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getM()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getWs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getBs()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getS()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getT()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getW()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getI()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getA()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getLd()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitListRare.getSelectedStats().getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                //Upg
                List<SelectedUpgrade> selectedUpgradeList = selectedUnitListRare.getSelectedUpgrades().stream()
                        .filter(SelectedUpgrade::isSelected)
                        .toList();
                for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                    String type = selectedUpgrade.getUpgrade().getUpgradeType();

                    if (type.equalsIgnoreCase("Mount") || type.equalsIgnoreCase("Crew") || type.equalsIgnoreCase("Champion")) {
                        Unit unit = unitRepository.findByName(selectedUpgrade.getUpgrade().getName());
                        UnitStats stats = unitStatsRepository.getReferenceById(unit.getUnitStats().getId());
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit.getName())))).setTextAlignment(TextAlignment.CENTER);
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getM()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBs()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getS()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getT()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getW()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getI()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getA()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getLd()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        Unit unit1 = unitRepository.findByName(selectedUpgrade.getUpgrade().getName() + " Crew");
                        if (unit1 != null) {
                            UnitStats stats2 = unitStatsRepository.getReferenceById(unit1.getUnitStats().getId());
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit1.getNation())))).setTextAlignment(TextAlignment.CENTER);
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int)Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getM()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getS()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getT()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getW()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getI()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getA()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getLd()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                        }



                    }

                }
                document.add(statsTable);


                Table upgradesTable = new Table(3).setWidth(UnitValue.createPercentValue(100));

                for (int i = 0; i < selectedUpgradeList.size(); i += 3) {

                    Cell cell9 = new Cell().add(new Paragraph(
                            selectedUpgradeList.get(i).getUpgrade().getName() +
                                    " (" + selectedUpgradeList.get(i).getUpgrade().getPointsCost() * selectedUpgradeList.get(i).getQuantity() + " pkt)"
                    )).setBorder(Border.NO_BORDER);
                    upgradesTable.addCell(cell9);

                    if (i + 1 < selectedUpgradeList.size()) {
                        Cell cell10 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 1).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 1).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 1).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell10);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }

                    if (i + 2 < selectedUpgradeList.size()) {
                        Cell cell11 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(i + 2).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(i + 2).getUpgrade().getPointsCost() * selectedUpgradeList.get(i + 2).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell11);
                    } else {
                        upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                    }
                }


                document.add(new Paragraph("Upgrades:").setFontSize(10).setMarginTop(10));
                document.add(upgradesTable);

            }
            Table upgradesNewTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
            Set<String> added = new HashSet<>();

            selectedUnitList.stream()
                    .flatMap(unit -> unit.getSelectedUpgrades().stream())
                    .filter(SelectedUpgrade::isSelected)
                    .map(SelectedUpgrade::getUpgrade)
                    .filter(upgrade -> added.add(upgrade.getName()))
                    .forEach(upgrade -> {
                        upgradesNewTable.addCell(new Cell().add(new Paragraph(upgrade.getName())));
                        upgradesNewTable.addCell(new Cell().add(new Paragraph(upgrade.getDescription())));
                    });
            document.add(upgradesNewTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Błąd przy generowaniu PDF: " + e.getMessage(), e);
        }
    }
}




