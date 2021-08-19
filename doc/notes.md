
# Table of Contents

1.  [Prototyp v0 (Simulator)](#orgbc6aea0)
2.  [Regeln für Navigationsanweisungen](#org77fbee5)
3.  [Fahrplan Implementierung](#org0f35f87)
    1.  [Szenario Navigation](#org2690bee)
        1.  [Version 1](#orged56578)
        2.  [Version 2](#org0e6f2a0)
        3.  [Version 3 (optionale Erweiterungen)](#org101f41f)
    2.  [Szenario Autonomes Fahren / Episodische Begegnungen](#orgf949dac)


<a id="orgbc6aea0"></a>

# Prototyp v0 (Simulator)

1.  Ontologie checken
2.  Minimale Karte (mit allen Vorkommnissen)
3.  Weg durch die Karte (Simulation der Navigation)
4.  Simulation fahren durch die Karte entlang der Route (oder auch nicht?)
    -   Rollator in der Karte plazieren
    -   Zu einem der nächsten Connectionpoints fahren
    -   Mini-Gui(?) oder textuelles Interface


<a id="org77fbee5"></a>

# Regeln für Navigationsanweisungen

-   Rollator am Startpunkt aufsetzen: Begrüßung
-   Unterwegs Navigationsanweisungen gemäss lokaler Daten
    (und Historie/Ausblick)
-   Ankommen am Zielpunkt: Verabschiedung


<a id="org0f35f87"></a>

# Fahrplan Implementierung


<a id="org2690bee"></a>

## Szenario Navigation


<a id="orged56578"></a>

### Version 1

Funktionalität: Navigation ohne Rückkanal, keine Landmarken

Voraussetzungen:

+   Semantische Karte: Testkarte DFKI done, Saarschleife in Arbeit (done)

+ eingehende Lokalisationsdaten: Wie funktioniert das?

-+   Verarbeitung eingehender Lokalisationsdaten und in Relation zur Karte setzen
    -   Idee:
        Winkel derzeitiger bewegungsvector zu "demnächst" Bewegungsvektor
        in acht sektoren aufteilen

+ Route / Routenplanung
+ Agentenregeln zur Triggerung von Aktionen an den richtigen Stellen
+ Generierung von (multimodalen) Reaktionen für die Aktionen
Wünschenswert:

-   Simulator zum Einspeisen von Lokalisationsdaten (?)


<a id="org0e6f2a0"></a>

### Version 2

Funktionalität: Einbeziehen von Landmarken und anderen Merkmalen in der Karte

Vorraussetzungen:

-   Landmarken etc. in der Karte
-   Bestimmen der relevanten Landmarken
-   Anpassen der Generierung

Funktionalität: Route / Anweisung wird nicht beachtet

-   Nachfragen: nicht verstanden? (sehr einfacher Rückkanal)
-   Route umplanen


<a id="org101f41f"></a>

### Version 3 (optionale Erweiterungen)

-   Dynamische Landmarken aus Objekterkennung
    -   Muss unterscheiden zw. statischen Objekten und Personen
-   Rückkanal: Interpretation
    -   Andere Route gewünscht
-   Nicht routenrelevante Informationen aus Karte generieren u darüber sprechen
-   Weitere "Smalltalk" Szenarien


<a id="orgf949dac"></a>

## Szenario Autonomes Fahren / Episodische Begegnungen

-   Auswerten der Vorstudie!!!
