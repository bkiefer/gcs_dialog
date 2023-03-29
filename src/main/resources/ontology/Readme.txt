Dieses Dokument beschreibt im Groben die Struktur und Funktion des Intuitiv Ontology Drafts und wird im Laufe der Zeit weiter ausgebaut.

Die Haupt-Ontology Intuitiv.owl importiert und verbindet mehrere Ontologien von denen jede einen eigene Domain beschreibt.
1) Domain: Diese Ontology enthaelt Konzepte und Relationen die spezifisch auf die Use-Cases im INTUITIV Projekt ausgelegt sind. Dies beinhaltet Events, welche dazu genutzt werden Begegnungen mit Patienten, beispielsweise auf dem Flur, darzustellen. Ausserdem enthaelt sie Konzepte um Patienteninformationen zu repraesentieren. Beispiele hierfuer sind die einem Patienten zugeordneten Zimmer, Zimmernummer, der Zeitraum seines Aufenthalts, Alter, Vorlieben, etc.
2) Indoor: Diese Ontology erlaubt es uns den Grundriss des Gebaeudes zu repraesentieren und so die Position des Roboters/Patienten im Gebaeude zu bestimmen. Dies erlaubt es uns dann, in Kombination mit sogenannten Landmarks, korrekte Hinweise ueber anstehende Richtungsaenderungen dem Patienten mitzuteilen.
3) Navigation: Diese Ontology kann in Kombination mit der Indoor Ontology dazu genutzt werden um einen sehr einfachen Navigationgraphen aufzubauen.
4) Actor: Eine allgemeine Ontology zur Repraesenation von Aktoren, sowohl Menschen, als auch Roboter, mit ihren Eigenschaften.
5) Dialogue: Diese Ontology beschreibt die wichtigsten Dialog-Akte entsprechend der DIT++ Taxonomy.

Bei der Ontology handelt es sich nur um einen vorlaeufigen Entwurf. Anregungen & Verbesserungsvorschlaege koennen gerne an mich (christian.willms@dfki.de) geschickt werden.
