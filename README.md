<div align="center">
  <img src="static/images/prediction-image.jpg" alt="Prediction Image">
</div>

<h1 align="center">Predictions</h1>
<p align="center"><strong>Graphic Edition - by Amit Cohen & Aviv Galily</strong>
<br>Students Project for Java Course - The Academic College of Tel Aviv - Yaffo</p>

<h2>About</h2>

The described system is a simulation framework designed to model the effects of proposed laws and changes in various scenarios on a simulated population. The system's motivation lies in providing decision-makers, like lawmakers, with the tools to predict the potential outcomes of policy changes before they are implemented. The system is centered around the Knesset's research institute, which employs various analysis techniques to forecast the effects of new bills on the state and its citizens.

Key Components:

1. **World:** The central component of each simulation, the world defines entities and their relationships. The population consists of various types of entities, each with defined properties. A timeline advances in discrete steps, allowing for the application of laws and changes to entities over time.

2. **Entities:** Representing individuals in the simulated world, entities possess names, properties, and specified quantities. Properties differentiate entities, and each entity type appears a set number of times within the population.

3. **Laws, Actions, Functions:** Laws contain sets of instructions and actions that modify entities' properties. Actions can be simple, like changing property values, or complex, incorporating conditions and multi-entity interactions. Activation timing for laws is determined by ticks and probability.

4. **Activation Timing:** Laws are triggered based on specified tick intervals and probabilities, allowing for controlled and probabilistic application.

5. **Environment Variables:** Similar to entity properties, environment variables are properties that affect the simulation at large.

6. **Termination Conditions:** The simulation includes predefined conditions that trigger its termination, indicating when the simulation has achieved its goals or a specific scenario has unfolded.

7. **Graduation Process:** The simulation progresses through iterations, applying actions and laws to entities according to the defined timeline until termination conditions are met. Errors are detected and reported, halting the simulation and providing information about the nature and context of the error.

8. **Simulation Results:** Upon completion, the operator can obtain insights about the simulation, including entity statistics, average property values, and the consistency of specific properties over time.

The system is designed to facilitate the creation and execution of simulations that model real-world scenarios affected by laws and changes. By providing a flexible framework with tools for defining entities, laws, and simulation parameters, the system empowers users to explore the potential impacts of policy decisions and gain insights into the complex relationships between different variables.

<h2>Implements By:</h2>

- Polymorphism 
- Collections
- Interfaces
- Exceptions
- JAXB (Java Architecture for XML Binding)
- Java I/O
- Threads (Parallel Programming)
- JavaFX (Graphic App)
- Scene Builder

<h2>Upgrades from Consule Edition</h2>
1. Logic expansion - add actions and functions for simulation running
2. Graphic design
3. Parallel Programming - running several simulations at the same time

<h2>How to Run a Simulation?</h2>

1. Clone this repository
2. Open the CMD with the folder path 'submit'
3. Write the command:

  ```run
  run.bat
  ```
  
4. load XML files from the folder 'test-files' to run simulations.


<h2>Team Members</h2>

* [Amit Cohen](https://github.com/amitCohen2)
* [Aviv Galily](https://github.com/AvivGalily)

<h2>Project status</h2>

Completed


<h2>Credits</h2>

- Lecturer: <a href="https://www.linkedin.com/in/aviad-cohen/" target="_blank">Aviad Cohen</a>
- <a href="https://www.mta.ac.il/" target="_blank">The Academic College of Tel Aviv - Yaffo</a>
- Logo & Design: <a href="https://www.freepik.com/free-vector/vision-statement-concept-illustration_18771529.htm#query=prediction&position=3&from_view=search&track=sph">Image by storyset - on Freepik</a> 


<h2>License</h2>

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
