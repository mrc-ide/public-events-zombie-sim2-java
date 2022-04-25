# public-events-zombie-sim2-java

This is the Java front-end for running the Zombie Simulator II, a simple
illustration of spatial epidemiology modelling. 

# Pre-requisites

You need a Java Development Kit, version 8 or later, to build and
run the code. My favourite is 
[this one](https://www.azul.com/downloads/?version=java-8-lts&os=windows&architecture=x86-64-bit&package=jdk-fx)
which is a free open-source build of OpenJDK - which
happens to include JavaFX too, although we don't need it here.

# Compiling

See the `compile.sh` or `compile.bat` file for a one-liner to compile
the Zombie sim, and also the GUI kit that it uses to look the way
that it does.

In the `job` folder of this repo are precompiled executables of the
underlying C engine used to run the simulation. For infromation and
source code to modify or rebuild those executables, see 
[here](https://github.com/mrc-ide/public-events-zombie-sim-c)

# Running

See the `runthis.bat` or `runthis.sh` for another incredibly
complicated script - or just run `java com.mrc.zombie2.Z` from
the root directory.

# Arguments

* `/UNDEAD` will run in admin mode, with an extra button to dump
some XML to the console for your current configuration, which you
could paste into `z_conf.xml` and make it your default.

* `/PORT:8080` for example, will run a web-server in the background,
opening endpoints to control the simulator remotely - eg, from a
browser or android app (in development)

* `/MOVIE` writes PNGs of the entire app running, should you want
to generate an example movie of the Zombie Sim in action. (This
will make things quite slow...)

# Info, Thanks and Credits

* The public resources wiki page (which this repo is kind of
super-ceding) is [here](https://mrcdata.dide.ic.ac.uk/wiki/index.php/Zombie_Sim_II)

* Zombie II (2.2) is a simplified version of Zombie I, and was made 
simpler with invaluable help and insight from Harriet Mills, Rafal Nostowy 
and Diane Pople, who presented earlier versions of the game at different events.
Many thanks to others who also used this in the wild, and continue to do so.

* The original synthetic population generation code was by Pavlo Minayev as 
part of the Global Epidemic Simulator.

* The simulator itself, the Java zombie app, and the GKit is by Wes.

* The methods were all established by Prof Neil Ferguson - start [here](http://www.nature.com/nature/journal/v437/n7056/full/nature04017.html)
for example.
