# Spec — UX Rework: Dashboard, Navigazione, Persone

> Origine: feedback utente 2026-09-04. Stato: **draft — open questions da chiudere prima dell'EXECUTE.**

## Problemi rilevati

1. Il Quick Log non permette di scegliere la persona: usa `selectedPersonId ?: persons.first()` — con filtro "Tutti" l'evento va silenziosamente alla prima persona.
2. Non si può registrare l'assunzione di un farmaco dalla Dashboard: `ActivityLogType` non ha un tipo "farmaco"; le dosi si confermano solo da TherapyDetail.
3. Manca una pagina Account (tab Profilo è un placeholder `Text("Profilo")`).
4. Navigazione incoerente: tab radice (Persone, Impostazioni) mostrano freccia back; il CRUD Persona è spalmato su 3 schermate (PersonList / PersonDetail / EditPerson).
5. Le persone non sono distinguibili a colpo d'occhio (solo nome/nickname).
6. Il filtro persona in Home lascia visibili tutte le card (filtra solo i log).

## Requisiti

| # | Requisito | Note |
|---|---|---|
| R1 | Bottom bar 4 tab (Home, Persone, Profilo, Impostazioni) | **fatto** (shell `MainScreen`) |
| R2 | Quick Log per-persona: azione "+" dentro la card di ogni persona, sheet già vincolato a quella persona; header mostra nome (non l'id) | sostituisce il FAB globale (vedi OQ1) |
| R3 | Evento "💊 Farmaco" nel Quick Log: elenca dosi schedulate delle terapie attive della persona → tap conferma (`LogMedicationUseCase`, status TAKEN) | riusa `ObserveTherapiesUseCase` + `ScheduleCalculator` |
| R4 | Tab Profilo: se loggato → vedi/modifica profilo (displayName, email, provider); se anonimo → CTA "Accedi / Registrati" (riuso Login/Register, `linkWithCredential` preserva i dati) | |
| R5 | Tab radice senza freccia back; PersonDetail diventa l'unica pagina persona (edit inline + delete + terapie); EditPersonScreen assorbita; creazione da PersonList | |
| R6 | Distinzione visiva persone: colore scelto dall'utente e/o icona fascia d'età (bimbo/adulto/anziano) | colore = preferenza **locale per-utente** (DataStore/Room), NON sul documento `persons/` condiviso |
| R7 | Filtro Home: selezionata una persona si vede **solo** la sua card; ricerca per nome/nickname tramite search bar | |

## Open Questions (bloccanti per EXECUTE)

- **OQ1** — FAB globale: eliminarlo del tutto o tenerlo come scorciatoia con selezione persona esplicita nel sheet?
- **OQ2** — "Farmaco" nel Quick Log: solo conferma di dosi schedulate, o anche dose una tantum fuori schedule?
- **OQ3** — Icona fascia d'età: attributo condiviso del documento Person o preferenza locale come il colore?
- **OQ4** — Search bar: sostituisce i FilterChip o li affianca?
- **OQ5** — Profilo e Impostazioni: due tab separate o una sola tab "Profilo" con dentro le impostazioni (bottom bar a 3 tab)?

## Acceptance Criteria

- [ ] Da ogni card persona in Home si registra un evento in ≤ 2 tap, senza ambiguità sulla persona.
- [ ] Dal Quick Log si conferma una dose farmaco di una terapia attiva; il log compare in TherapyDetail.
- [ ] Header del Quick Log mostra nome/nickname della persona.
- [ ] Tab Profilo: utente anonimo vede CTA login; utente loggato vede e modifica il proprio profilo.
- [ ] Nessuna freccia back sulle 4 destinazioni radice.
- [ ] Creazione/modifica/cancellazione persona avvengono senza passare per una terza schermata dedicata.
- [ ] Ogni persona ha colore (locale) e/o icona distintiva visibile in card, chip e feed.
- [ ] Filtro su persona singola → in vista card resta solo la sua card; ricerca per nome/nick funzionante.
- [ ] Preview duplicate rimosse (`DashboardContentPreview2`, `DashboardContentPreviewQuickLog`); titolo temporaneo "Registra evento per $personId" sostituito.
- [ ] Test unit per i nuovi use case / ViewModel; `detekt`, `ktlintCheck`, `testDebugUnitTest` verdi.
