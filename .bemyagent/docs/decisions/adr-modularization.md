# ADR: Rinvio della modularizzazione Gradle
**Data**: 2026-08 | **Status**: accepted

## Context
A ~850 LOC / 22 file / singola feature (auth), è stato valutato se suddividere il progetto in moduli Gradle separati per ridurre i tempi di build e migliorare la separazione delle responsabilità.

## Options Considered

| Opzione | Pro | Contro |
|---|---|---|
| Modularizzare subito (`:app`, `:di`, `:ui-common`) | Separazione esplicita fin dall'inizio | Overhead Gradle immediato, `:di` standalone è anti-pattern (dipendenze circolari), guadagni build-time nulli a questa scala |
| Mantenere monorepo + discipline di package | Zero overhead, build veloci, split meccanico futuro garantito dalla clean-arch già in place | Nessun confine fisico imposto dal compilatore |
| Modularizzare solo `:core:ui` subito | Isola il tema/design system | <5 componenti condivisi: overhead > beneficio |

## Decision

**Rinviare** la modularizzazione al milestone in cui viene sviluppata la 2a o 3a feature, oppure quando i tempi di build superano ~45 secondi.

Struttura target concordata al momento dello split:

```
:app                 ← shell, DI graph assembly, AppNavigation
:feature:auth
:feature:dashboard
:core:domain         ← use case, interfacce repository, domain model
:core:data           ← repository impl, Firebase, FCM
:core:ui             ← theme + Composable condivisi (trigger: ≥5 componenti)
```

**Regole vincolanti:**
- Nessun modulo `:di` standalone — i `@Module` Hilt restano nel modulo feature/data che li possiede.
- `:core:ui` si crea solo con ≥5 Composable condivisi oltre al tema.
- `:app` ospita `@HiltAndroidApp` e i `SingletonComponent` trasversali.

## Consequences

- **Positivo:** nessun debito di configurazione Gradle nella fase bootstrap; il refactor sarà meccanico grazie alla clean-arch già in place (data/domain/ui per package).
- **Positivo:** evitato l'anti-pattern `:di` centrale.
- **Da fare al trigger:** proporre la suddivisione come task preliminare del milestone che introduce la 2a feature, prima di scrivere nuovi schermi.