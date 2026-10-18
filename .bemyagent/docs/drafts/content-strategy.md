# Content Strategy — Medium / LinkedIn

> Promuovere Carezze come portfolio tecnico e riflessione pubblica.
> Temi trasversali da richiamare in OGNI post: open source, nessuna registrazione obbligatoria, privacy by design.

## Problema / Opportunità

Il progetto nasce da un'esigenza personale reale (nascita della figlia del maintainer) ed è costruito con architettura professionale (MVVM, TDD, GitFlow, AI-assisted). Tre angoli narrativi forti da intrecciare in ogni post:

1. **La storia umana** — un genitore che costruisce lo strumento che avrebbe voluto avere
2. **Le scelte tecniche** — decisioni non ovvie con trade-off espliciti
3. **I principi** — privacy, open source, zero barriere all'uso

## Temi Fissi (ripetere in ogni post, con angolo diverso)

| Tema | Messaggio chiave | Come dirlo |
|---|---|---|
| **Open source** | Il codice è pubblico su GitHub — puoi leggerlo, forkarlo, contribuire | Link al repo in ogni post; screenshot di codice reale |
| **Nessuna registrazione** | Puoi usare l'app senza creare un account. I tuoi dati rimangono sul tuo telefono finché non vuoi condividerli | Mostrare il flusso anonimo → opzionalmente account |
| **Privacy & sicurezza** | Dati in Europa (GDPR), inviti monouso con scadenza, nessun dato condiviso senza consenso esplicito | Essere concreti: regione Firebase, codice Security Rules, spiegare cosa NON viene salvato |

---

## Struttura: 5 Post Tematici

### Post 1 — Inception: "Ho avuto una figlia. Ho scritto un'app."
**Milestone coperte**: pre-M1 (fase di design)
**Focus**: motivazione personale + problema reale + principi fondanti

**Struttura**:
- Aneddoto personale (notte, pannolino, "l'ho già dato il farmaco?")
- Il problema: frammentazione delle informazioni in famiglia
- La soluzione: Carezze — open source, zero obblighi di registrazione, privacy-first
- Preview dell'architettura (diagramma) e dello schema Firestore
- Invito a seguire il journey

**Hook LinkedIn**: "Ho avuto una figlia a [mese]. Tre settimane dopo stavo disegnando uno schema Firestore alle 2 di notte."

---

### Post 2 — Le Fondamenta: Firebase, Offline-First e il Problema dell'Identità
**Milestone coperte**: M1 + M2
**Focus**: setup tecnico + decisioni non ovvie + privacy

**Struttura**:
- Perché offline-first non è "nice to have" per un'app medica (connessione ospedaliera, roaming)
- Firebase Anonymous Auth: come si usa l'app senza account e cosa succede se poi decidi di registrarti
- Doppio layer Room + Firestore: la scelta e il trade-off
- Dati in `europe-west1`: perché la regione non è un dettaglio
- Link al codice (auth flow, migration use case)

**Angolo privacy**: "I tuoi dati non escono dal tuo telefono finché non sei tu a sceglierlo."

---

### Post 3 — Il Cuore: Terapie, Pannolini e UX a Una Mano
**Milestone coperte**: M3 + M4 + M5
**Focus**: feature building con TDD, sealed class Kotlin, UX 1-tap

**Struttura**:
- Il domain model: perché `sealed class ActivityLog` e non una tabella piatta
- TDD su use case: mostrare un test vero scritto prima del codice
- La sfida UX: registrare un pasto con una mano mentre tieni il bambino con l'altra
- Quick Log Bottom Sheet: come si progetta per l'urgenza
- Progresso terapia: barra + calendario + contatore (tre rappresentazioni, un dato)

**Angolo open source**: "Ogni scelta di design è visibile nel codice — puoi vedere perché ho preferito X a Y leggendo il commit."

---

### Post 4 — Condividere Senza Perdere il Controllo: Real-Time e Sicurezza
**Milestone coperte**: M6 + M7
**Focus**: la parte "wow" tecnica + sicurezza come feature, non come nota a piè di pagina

**Struttura**:
- Il problema della condivisione: come condivido le terapie di mia figlia col pediatra senza dargli accesso ai suoi pannolini?
- Inviti monouso: 8 caratteri, 24 ore, un solo uso — e perché un link permanente sarebbe stato sbagliato
- Firestore transaction atomica: perché il client *può* fare questa cosa — e perché è la scelta giusta (vedi angolo extra sotto)
- Firestore Security Rules: il codice che protegge i dati (mostrare snippet reale)
- FCM e conferma familiare: "Papà ha già dato l'antibiotico" — real-time, su tutti i dispositivi; notifiche schedulate con WorkManager on-device

**Angolo sicurezza**: "La sicurezza non è un layer aggiunto dopo — è nelle regole Firestore, nella transaction, nel codice invito."

**Angolo extra (post a sé o sezione dedicata)**: "Zero Cloud Functions — non solo per il piano gratuito"
- Il vincolo economico (Spark plan) ha forzato la domanda: *serve davvero un server?*
- Risposta: no. Firestore transactions danno la stessa atomicità; WorkManager schedula le notifiche on-device senza cold start
- Vantaggio inaspettato: l'app funziona offline completo, nessuna dipendenza da infrastruttura esterna, domain layer puro e testabile
- La lezione: i vincoli di budget sono spesso vincoli di design in disguise — il costo zero ha portato a un'architettura più robusta

---

### Post 5 — Lancio e Retrospettiva: Widget, TDD e AI-Assisted Development
**Milestone coperte**: M8 + M9 + M10
**Focus**: conclusione + lezioni + il processo di sviluppo con agenti AI

**Struttura**:
- I widget Glance: registrare un pannolino senza sbloccare il telefono
- Localizzazione IT/EN: perché l'app deve parlare la lingua dell'utente
- TDD in pratica: coverage 80%+, cosa ha trovato che non avrei trovato manualmente
- GitFlow + agenti AI: come più agenti hanno lavorato in parallelo su worktree separati
- Cosa ho imparato costruendo uno strumento per mia figlia
- Roadmap futura + call to contribute (open source)

**Angolo community**: "Il repo è pubblico. Se hai una figlia, un genitore anziano o una terapia lunga, questo progetto è anche tuo."

---

## Tone of Voice
- Prima persona, riflessivo ma tecnico
- Onesto sui trade-off e sulle cose che non hanno funzionato
- Snippet di codice reali (non pseudocodice)
- Collegare sempre il problema tecnico alla motivazione umana

## Canali e Formato

| Canale | Formato | Lunghezza | Quando |
|---|---|---|---|
| **Medium** | Post lungo con diagrammi e snippet | 2000–3500 parole | Al completamento di ogni gruppo di milestone |
| **LinkedIn** | Versione condensata + 1 immagine chiave | 300–500 parole | Stesso giorno del post Medium |
| **GitHub README** | Badge + link ai post Medium | — | Aggiornare dopo ogni post |

## Open Questions
- Pubblicare in italiano o inglese? → **Inglese** per massima reach; nota personale in italiano nell'intro
- Link al repo da subito (post 1) o aspettare M3 con codice sostanzioso? → da subito, anche con repo quasi vuoto — il journey è il contenuto
- Cross-postare su dev.to? → valutare dopo post 1 in base al feedback

## Note
Pubblicare durante lo sviluppo, non alla fine. La storia in progress è più coinvolgente di un annuncio di lancio.
