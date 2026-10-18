# TASK — 4.5.2
Delivers: `TherapyRepositoryImpl` supporta update completo, terminate con endDate, delete con cascade medicationLogs; `MedicationLogRepositoryImpl` persiste e legge `isManual`.

## CDM
- ✅ Validation: `./gradlew :app:compileDebugKotlin` → BUILD SUCCESSFUL (exit 0)

## Checklist
- [ ] `TherapyRepositoryImpl.updateTherapy` — payload include `startDate` + `endDate` (null → omesso con filterValues)
- [ ] `TherapyRepositoryImpl.deleteTherapy` — cascade: batch-delete `medicationLogs`, poi delete therapy doc
- [ ] `TherapyRepositoryImpl.toDomain` — legge `endDate` (Timestamp?, nullable LocalDate)
- [ ] `MedicationLogRepositoryImpl.toDomain` — legge `isManual` (Boolean, default false)
- [ ] Build verde: `compileDebugKotlin` PASS
