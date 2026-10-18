---
task: 5.5.8
title: Pulizia — preview duplicate, titolo QuickLogSheet, test unit nuovi ViewModel
status: in-progress
---

## Context Saturation Check
- DashboardViewModel: filteredPersons (combine persons/selectedId/query) — VERIFIED in source
- ProfileViewModel: authState mapping null/anon/auth — VERIFIED in source
- Preview duplicate: già rimosse in 5.5.2 (confermato in 04_verify.md di 5.5.2) — solo aggiornamento spec
- Titolo QuickLogSheet: già usa `$personName` alla linea 88 — solo aggiornamento spec
- Pattern test (mockk + UnconfinedTestDispatcher + JUnit 5): confermato in QuickLogViewModelTest
- Items unknown: 0 → proceed

## Sizing: Standard
Due nuovi file di test + aggiornamenti docs. Nessuna nuova dipendenza.

## Pre-mortem
1. **Rischio**: I test di DashboardViewModel falliscono perché `persons` viene inizializzato nel costruttore e richiede mock pre-setup — **Mitigazione**: setup mocks prima di creare il ViewModel nel @BeforeEach
2. **Rischio**: `combine(flows)` su lista vuota in `personColors` → NPE — **Mitigazione**: mock `observePersonColor(any())` con `flowOf(0)` sempre attivo

## Devil's Advocate
Alternativa: testare solo tramite Use Case fakes (senza mockk). Scartata: il pattern stabilito nel progetto usa mockk, cambiarlo richiederebbe refactor dei test esistenti.

## Deliverable
Test unit verdi per `DashboardViewModel.filteredPersons/toggleViewMode` e `ProfileViewModel.authState`; spec ux-rework.md con checkboxes 5.5.8 spuntati; 06-implementation-plan.md task 5.5.8 = done.
