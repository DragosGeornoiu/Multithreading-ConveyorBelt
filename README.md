## Prerequisites
- java 1.8 
- maven

## Running the tests
```
mvn test
```

## What was tested
1. AcmeFactoryTest
    - testQueueCapacityLimit: the conveyor belt cannot have more than 10 elements.
    - testInvalidRobotTypeException: creating a worker with null as type will throw InvalidRobotTypeException.
2. FactorySupplierTest
    - testQueueLimitWithOneProducer: one producer will not add more than 10 elements on the conveyor belt.
    - testQueueLimitWithMultipleProducers: more than one producer will not add more than 10 elements on the conveyor belt.
    - testProducerAddsToQueueAtOneSecondInterval: producer adds elements to queue at a one second interval.
3. WorkerTest
    - testDryRobotComponentsNeeded: verifies that a worker which builds DRY2000 robots needs a MainUnit and two BROOM 
    components for each robot.
    - testWetRobotComponentsNeeded: verifies that a worker which builds WET2000 robots needs a MainUnit and two MOP
    components for each robot.
    - testWorkersCannotCompleteRobotWithOnlyBroomElements: verifies that using only BROOM components, neither the worker
    that builds DRY2000 robots, neither the worker that builds WET2000 components can complete a robot.
    - testWorkersCannotCompleteRobotWithOnlyMopElements: verifies that using only MOP components, neither the worker
    that builds DRY2000 robots, neither the worker that builds WET2000 components can complete a robot.
    - testWorkersCannotCompleteRobotWithOnlyMainUnitElements: verifies that using only MainUnit components, neither 
    the worker that builds DRY2000 robots, neither the worker that builds WET2000 components can complete a robot.
    - testDryWorkerCanCompleteRobot: tests that a Worker that builds DRY2000 robots can complete a robot using one
    MainUnit component and two BROOM components.
    - testWetWorkerCanCompleteRobot: tests that a Worker that builds WET2000 robots can complete a robot using one
    MainUnit component and two MOP components.
    - testBothWorkersCanCompleteRobot: tests that if the queue has two MainUnit components, two BROOM components and two
    MOP components, both the Worker that builds the WET2000 robots and the Worker that builds the DRY2000 robots will be 
    able to complete a single robot.
    - testBothWorkersNeedMainUnitToCompleteRobot: tests that if no MainUnit component is on the queue, no Worker can 
    complete assembling a robot.

## Running the application
```
mvn clean package -DskipTests=true
java -jar ./target/Multithreading-ConveyorBelt-1.0-SNAPSHOT-jar-with-dependencies.jar arg1 arg2 arg3
```

**Where**
- arg1 - the number of Workers that bulid DRY2000 robots
- arg2 - the number of Workers that build WET2000 robots
- arg3 - the number of seconds that the aplication should run

**Example:**
```
mvn clean package -DskipTests=true
java -jar ./target/Multithreading-ConveyorBelt-1.0-SNAPSHOT-jar-with-dependencies.jar 2 2 120
```

## Problem
There's a factory called "ACME" that assembles cleaning robots. They require three different components to  be delivered 
to them: main unit, mop and broom. There are two types of robots they produce: "Dry-2000" and "Wet-2000". They are built 
by two different workers and each one of them knows only how to assemble  one of the robots. To make the "Dry-2000" it's 
required to have one main unit and two broom. To assemble the "Wet-2000" it's required to have one main unit and two mops.

Unfortunately, the factory supplier is not very reliable so it only delivers one of the components every second. The 
component it delivers is completely random. The delivered components are placed on a conveyer belt and transported this 
way to the room where both workers wait for them. They want to grab all the components they need, but only one at a time! 
It has to be the element at the end of the belt - worker can't check other items on the belt to pick the one he needs. 
Once they have them, they go to the assembly room to put things together to create a cleaning robot - it takes exactly 3 
seconds. Once it's done they just shout how many robots they have assembled in their lifetime and immediately go back to 
the conveyer belt to wait for the components they need to assemble the next robot.Conveyer belt has a size limit - it can 
fit at most 10 items. If it's full and the items can't be picked up from the conveyer belt by workers (because they're 
assembling robot at the moment), supplier will not put another one on it. However, it's still possible that the item 
available at the end of the conveyer belt in a given moment will not suit any of the workers, so they get stuck - to 
prevent them waiting forever, if supplier was unable to put an item on the conveyor belt for more than 10 seconds, he can 
just go to the room where the conveyor belt ends and destroy the last item.  

Your task is to write a command line, multithreaded Java application that simulates this factory. 

Make the output of the program easy to track in a real time: each action taken by supplier or workers should be printed out. 
You can also print additional information if you find it useful, but don't make the output noisy. If something is not clear - 
make sensible assumptions, justify them and implement the application logic according to them.

## Solution

The problem was solved using the Producer-Consumer pattern. The factory supplier is the producer, which puts components on 
the conveyor belt, and the Consumers are the workers which build DRY2000 or WET2000 robots.

The FactorySupplier is a thread which puts components on the conveyor belt at a one second interval. If the conveyor belt 
is full (already has 10 components) it will not add any more. After 10 seconds have passed and the FactorySupplier did not
add any more components, it will remove the first component on the conveyor belt. When it checks the size of the queue, 
tries to add a new component or tries to remove the first component from the conveyor belt, it will first try to 
aquire the lock on the conveyor belt.

The Worker can build either DRY2000 or WET2000 robots, it has as a map of Component-RobotComponentsPair which holds the 
components it needs in order to build the robot. The worker will try to look at the first component on the queue, check 
if it needs that type of component to assemble the robot and take it from the conveyor belt if it needs it, otherwise 
wait for another component that it needs. If it manages to gather the components needed to build a robot, it will print 
the number of robots  it assembled in his entire lifetime. The worker will try to aquire the lock on the conveyor belt 
when it peeks on the last component of the conveyor belt and when it takes the component from the conveyor belt.

The RobotComponentsPair is designed to hold the number of components needed and the number of components each worker 
currently has.

All the objects needed to run the applications should be retrieved using the ACMEFactory, which is a Factory Pattern 
implementation, hiding from the user all the logic needed to build the FactorySupplier or Worker. 

The conveyor belt is retrieved from the QueueStorage class, which is a Singleton implementation, more exactly a 
Double Checked Locking of Singleton, to not allow the creation of multiple conveyor belts if called by 
more than one thread in parallel. 


