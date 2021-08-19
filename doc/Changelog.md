# Changelog

Changelog
1. Renamed Rolli.rudi to Intuitiv.rudi and Rolli.java to IntuitivAgent.java. This means that you should use intuitiv.\<relation> instead of rolli.\<relation> in your new .rudi files. Existing ones were already updated.
2. new entry in the information state "**robot.hasNewTask**": this is now used to initiate the autonomous driving behavior. A corresponding new GUI entry setTask(clientName, startPOIName, endPOIName, description) has been added. SetDestination, and the "old" setTask are now deprecated. Added a set of predefined POIs 
   + POI_1: (12,33, 0)
   + POI_2: (17,3, 0)
   + POI_3: (17, 3, 1)
   + POI_4: (42, -1, 1)
3. new entry in the InformationState "**robot.reachedDestination**": this entry is updated by the robotic systems when they have reached the start or end POI specified in the task. We now use this state for the conditionals in arrived at start and goal_waypoint. 
4. added new GUI methods
   - reachedPOI(): robot.reachedDestination = true;
   - public void speakText(String text): output given text with TTS (works only if connected to ros)
   - cancelKoffi() cancel Koffi (works only if connected to ros) 
   - public void memorizePerson(): trigger dialog "memorize person" and Koffi behavior (works only if connected to ros)
   - followMemorizedPerson(): koffi follows the memorized person (works only if connected to ros)
   - goHome(): koffi goes to the charging station (works only if connected to ros)
   - goTo(String poi, String withPatient): koffi accompanies the person to the given POI
5. removedKoffi.java: use public field rolli.isLuggageBot instead to separate behaviours.
6. implemented basic dialogue stump for memorize person dialogue. Can be found in Rolli.rudi