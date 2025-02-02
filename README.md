# ToonWorld

Progress management tool for Player and Guild

- Retrieve every x hours the current stats of a player
- Visualize the progress of a player via graphs/charts
- In-depth opposition research for fleet shard, tw and gac

## Tech

- Spring Boot
- Kotlin
- Discord4J
- Comlink
- Angular Webapp for later?

## TODO

nice info: how many under-leveled / non-matching mods you have

## User Stories

### Enable player tracking

1. I want to track someone's progress (toon build, gp gain)
2. I call something like `/track <allyCode>`
3. Therefore, I provide an `allyCode` of the target player
4. The player will be retrieved to check whether it's a valid `allyCode`
5. Now every `[6 hours]` the player will be synced again
6. Therefore, a job is running with a delay to the next player
7. The job gets automatically started on application start and calculates when the next player needs to be synced
8. _TO BE CONSIDERED_: auto-tracking when linked/unlinked?

### Disable player tracking

1. I want to be able to untrack a player if I am no longer interested
2. I call something like `/untrack <allyCode>`
3. The tracking of the player is disabled and the job checks if it needs to reschedule

### Scouting

#### GAC 3v3 / 5v5

- what did the opponent put down for the past `[3]` rounds of GAC?
- against what did the opponent drop battles in attack?
- what datacrons does the opponent have

- recent brackets: (https://swgoh.gg/api/v2/gac/brackets/recent/154962211/)
- configs: (https://swgoh.gg/api/v1/gac/config-data/)
- https://github.com/fleeksoft/ksoup or directly https://github.com/jhy/jsoup for scraping

#### TW Guild

#### Recruitment

## Ideas

- Account timeline
- GAC: track history, what the player used against someone else, counters that match your account
    - based on https://youtu.be/wkcap-W5kTc
- Fleet Arena: track position, observe shard to see their progress regarding fleet + changes
- Gear towards farming.
- modding advice regarding swgoh.gg best mods for character, based on own characters
- define squads for teams for 3v3 and 5v5, track farming progress
- conquest: show feats, suggest teams based on roster, track impossible feats etc
- guid features:
    - track progress of each player per time period
    - tw counters
    - tb missions
    - assign roles based on unlocked toons
- track progress on assault battles, suggested squads and their progress. also ability to mark which done
- proving grounds: suggested squads and the player's progress. also ability to mark which are done
- ship counter planner (mods to increase speed etc) [compared to fleet shard?]
- notification for fleet arena movement, GL + Journey unlocks, ...

## Bugs

_nothing known_

- letting it join a new discord server does not automatically register commands. only a restart does

## Snippets

Blueprint for account review?!

```
\```yaml
- Name: alonmower
- AllyCode: 727216426
- Fleet Rank: 16 with Exec
- GP: 7.4m
- Account age: ~2.1 years old
- Current Guild avg daily tickets: 482
- Total avg daily tickets: 489
- Raid average: 1.2m - 2.6m
- GLs: JML SEE SLKR
- GL Ships: Prof Exec
- TB:
  - Reva ready
  - Zeffo not ready
- Conquest Units:
  - ooozzzBane R7
  - ozLuthen R5
  - Bald Boba G12
- Omicrons:
  - GAC: Bane Raddus Rex Luthen Wampa QGJ
  - TW: Poggle
- Notable Toons:
  - Merrin G12 no omi
  - Wat G12
  - Padme G9
  - Iden G12 no omi
  - GG G12 no zeta
  - MJ G12 no zeta/omi
  - AT G12 no zeta
  - MQG & POW G8 7*
  - Nihilus G2 3*, but Traya R6 ?! Savage G1 7*
  - Scythe 7*
  - Punishing One 5*
  - KAM R5
  - CLS squad
- Mods:
  - Lacks fast characters, only 6 are 300+
  - declines super fast
- Journeys:
  - somewhat close to Rey (BB8, Finn, ScavRey, VetChewy missing)
  - somewhat close to SK (Talon, MJ missing)
  - rest not worth mentioning
\```
```

```
\```yaml
- Name: KingSauce31
- AllyCode: 842387166
- Fleet Rank: 1 with Exec
- GP: 10.4m
- Account age: >3 years old
- Total avg daily tickets: 523
- Raid average: 368k - 4.8m
- GLs: JMK LV Jabba Rey SEE SLKR JML
- GL Ships: Exec
- TB:
  - Reva ready
  - Zeffo ready
- Conquest Units:
  - zzzCAT R7
  - oozzBane R7  
  - Bald Boba R7
  - ozzMalgus G9
  - ooozzzMalicos R5
  - zzzoooBen Solo R6
  - zzzMaul R6
  - Trench G8
  - zzzDTMG R5
  - zzQuadme R8
- Notable Toons:
  - BAM R7
  - MQG & POW R9
  - Piett R8
  - GBA R7 o_O
  - Traya R7
  - KAM R7
  - Talzin R7
  - Snips R7
  - JTR no zetas?
  - HYoda R7
  - Fives R3 :o
  - JKR G12
  - TripleZero R7
  - Karga no zeta
  - Merrin G12
  - zzMalak G10
  - Slow BB Echo
  - zDR G11
  - Aphra G8
- Missing Zetas:
  - CLS leadership
  - Daka unique
  - Bossk lead
  - Karga Special 2
  - MJ Special 2
- Missing Omis:
  - Phasma for TW
  - MJ for TW
- Journeys:
  - Prof might be the next smart move
  - GL Leia is in sight
- Mods:
  - mods are a major factor
  - 4 characters above 300 speed
  - even alonmower got almost equal moddings, and his were... ehm... improvable
  - why does Kanan have a +20 speed mod? For what do you need Mob Enforcer with 2 of your fastest mods?
  - Snips doesn't need to be super fast as she almost always assists anyway
  - but overall said, best mods are mostly where they should be... though you need better mods...
\```
```

```
\```yaml
- Name: Xela
- AllyCode: 461937966
- Fleet Rank: 1st with Levi
- GP: 10.9m
- Account age: 4.1+ years old
- Avg daily tickets: 583
- Raid average: 432k - 1.3m
- GLs: Jabba JMK LV SLKR SEE JML Leia Rey
- GL Ships: Exec Levi Prof
- TB:
  - Reva ready
  - Zeffo ready
- Conquest Units:
  - CAT R7
  - Bane R8
  - Bald Boba R6
  - Malgus R7
  - Malicos G8 no zeta/omi
  - Ben Solo R6
  - Maul R8
  - Trench R7
  - DTMG G6 no zeta/omi
  - Quadme G3 no zeta/omi
- Notable Toons:
  - JKCK R7
  - GI No omi
  - Aphra
  - SK
  - JTR 1 zeta only
  - Dark Trooper R7
  - Krrsantan R5
  - Fennec no zeta
  - Wampa R3
  - Zorii G12
  - Mando R1
  - DDK G12 no omi
- Missing Zetas:
  - none essentials
- Missing Omis:
  - Embo for TW
- Mods:
  - 24 toons over 300 speed is good!
  - generally speaking good mod quality
  - though I have the feeling they are a little bit too spread across various toons
  - some mediocre mods are 6* mods (BB Echo, JML triangle, SLKR triangle, Han Solo)
\```
```

```
\```yaml
- Name: Valkart8t2
- AllyCode: 294626814
- Fleet Rank: 14th with Exec
- GP: 8.888m
- Account age: 3.7+ years old
- Avg daily tickets: 551
- Raid average: 235k - 1.0m
- GLs: JMK Rey SLKR SEE JML Jabba
- GL Ships: Exec
- TB:
  - Reva NOT ready
  - Zeffo NOT ready
- Conquest Units:
  - CAT R7
  - Bane R9
  - Bald Boba G1
  - Ben Solo G9
  - Maul R3
  - Ezra G11
- Notable Toons:
  - Jabba G10 6zetas
  - Thrawn R7
  - JKR R3
  - Ezra Ezile G11 all zetas/omis
  - SK R5 only 1 zeta
  - GM R3, no morgan though
  - MM R6 without any zeta
  - CRex R1
  - Boushh R5 no zeta
  - MJ R5 no zeta
  - Old Daka R3 no zeta
  - GI G10 no zeta
  - Enoch G10 all zeta
  - Zorii G9
  - Echo G9
  - ARC G5 4*
- Missing Zetas:
  - Boushh zeta is the most crucial for Jabba, probably even more than the 1st GL zeta itself, and Jabba got all 6...
  - SK basic zeta is important to give you an AOE on his turn
  - Daka is useless without her zeta
  - MM needs at least her lead zeta to be useful, preferably both
  - MJs Special2 zeta is really nice
  - GI urgently needs his zetas
- Missing Omis:
  - Ben Solo's 2nd unique.
  - MJ is strong too
  - GI Special
- Journey Guide:
  - super close to JKCK
  - Baylan might be a nice target with the investment into Thrawn and GM already
- Mods:
  - 12 characters above 300 speed is ok
  - you need at least a 3rd Tusken modded to make a decent team. 2 are not enough
  - better mods than King (is that an achievement by now?)
  - need better focus on fastest mods on faster characters (e.g. CAT circle, 3 mods of JMK, )
  - primaries matter (JMK Defense cross, Prot triangle)
  - Speedy characters want speed sets (Hux has no complete set, )
\```
```

```
\```yaml
- Name: Bantha Brew
- AllyCode: 886465842
- Fleet Rank: 7th with Exec
- GP: 9.45m
- Account age: 3.9+ years old
- Avg daily tickets: 575
- Raid average: 800k - 1.0m
- GLs: Jabba JML Leia SEE SLKR
- GL Ships: Exec
- TB:
  - Reva NOT ready
  - Zeffo ready
- Conquest Units:
  - CAT R5
  - Maul R7
  - Bane R8
  - Bald Boba R6 all omi
  - Malicos R7
  - Malgus R7
  - DTMG R7 no omi
- Notable Toons:
  - Jabba R9 while JML R8
  - JKCK R7
  - Dengar R7
  - Krennic omicron :o
  - Zorii G9 no omi
- Missing Zetas:
  - JTR has no zeta
  - Greef Special 2
  - Mando Unique
  - Cal Kestis Unique
  - Rex Unique
  - Mon Mothma has no zeta
  - Scout Trooper Unique
  - Talzin Unique
- Missing Omis:
  - Cere Lead
  - Malicos Special
  - Zorii
  - Malgus
- Journey Guide:
  - 3 characters away from Rey (BB8, Scav Rey, VetChewy)
  - really close to JMK
  - reasonable close to Levi
  - Aphra reqs done
- Mods:
  - 19 characters above 300 speed is good
  - lovely speed mods at the right characters
  - primaries are sometimes a bit weird (Jabba, Malgus, Malak, KRU, ...)
  - CLS does not need that uber fast mods as he has plenty of speed gaining mechanics
  - FOO wants speed, preferably 1 less than Hux
  - SET is missing a mod
\```
```
