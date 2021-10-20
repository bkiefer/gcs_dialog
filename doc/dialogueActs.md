#Dialogue acts provided by the NLU

*Description*: This document describes the dialogue acts which can be recognized by our Intuitiv Interpretation Module(Cerence Mix NLU). Meaning this is the list of potential user Inputs we are able to recognize.

* **Instruct(Stop)**: Control command, safely stop all actions
  * Example: "Halt, Stop".
* **Instruct(EmergencyStop)**:   Control command, emergency stop -> robot immediately stops all actions and movements
* **Instruct(SpeedUp)**: Control the robot's speed; increase the speed; enter new speed preference into the information state
  * Example: "Etwas schneller."
* **Instruct(SlowDown)**: Control the robot's speed; decrease the speed; enter new speed preference into the information state
    * Example: "Langsamer." oder "Nicht so schnell"
* **Disagreement(NotParse)**: Indicates that an input could not be parsed by the NLU Modul
* **Confirm(_)**, e.g. Confirm(Confirm) or Confirm(CloseBy): Every kind of confirmation. Confirm(Confirm) subsumes all other confirmations.
    * Example: "Ja" oder "Ja, hier bin ich."
* **Disconfirm(_)**, e.g. Disconfirm(Disconfirm) or Disconfirm(CloseBy): Every kind of confirmation. Confirm(Confirm) subsumes all other confirmations.
    * Example: "Nein" oder "Nope."
* **Instruct(Greet)**: The robot is asked to greet a person/client. *This input is not used, yet.*
    * Example: "Rolli, sag hallo zu ... ."
* **Instruct(Introduction)**: The robot ist asked to introduce itself to a person/client. *This input is not used, yet.*
    * Example: "Koffi, stell dich bitte vor."
* **Instruct(Instruction)**: The robot ist asked to instruct the user in how to interact with the robot. *This input is not used, yet.*
    * Example: "Koffi, stell dich bitte vor."
* **InitialGreeting(Greet)** or **ReturnGreeting(Greet)**: The robot was greeted by a person, either as an intial greeting or as a response of a greeting uttered by the robot.
    * Example: "Hallo, Koffi". or "Schön dich zu sehen Koffi."

<mark> For more information on the supported control commands for the robotic arm please have a look at the '/doc' folder of the developer-arm branch.</mark>