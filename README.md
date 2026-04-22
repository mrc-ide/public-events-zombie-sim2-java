# public-events-zombie-sim2-java

This is the Java front-end for running the Zombie Simulator II, a simple
illustration of spatial epidemiology modelling. 

# Pre-requisites

You need a Java Development Kit, version 8 or later, to build and
run the code. My favourite is 
[this one](https://www.azul.com/downloads/?version=java-8-lts&os=windows&architecture=x86-64-bit&package=jdk-fx)
which is a free open-source build of OpenJDK - which
happens to include JavaFX too, although we don't need it here.


## On Mac OS

### Install OpenJDK

In a terminal, see if `java --version` and `javac --version` do anything. If they don't, then we
need to install OpenJDK. I use Homebrew - if `brew` in the terminal doesn't do anything useful,
then use the next section to install `brew` - and then:-

```
brew install openjdk
sudo ln -sfn /usr/local/opt/openjdk/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk.jdk
echo 'export JAVA_HOME="/usr/local/opt/openjdk"' >> ~/.zshrc
```

And after this, hopefully the `java` and `javac` commands work.

### Install Homebrew

In a terminal, see if `brew` does anything. If it doesn't, then:-
```
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

It will ask for your root password, and then for me it took about 20 minutes to install
Homebrew, and the Xcode command-line tools.

### Check for gcc

Hopefully by now, `gcc --version` in the terminal will report something - if it doesn't, I
think installing homebrew above is enough to make it work.


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
