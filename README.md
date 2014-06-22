# PIPEMarkovChain
[![Build Status](https://travis-ci.org/sarahtattersall/PIPEMarkovChain.png?branch=master)](https://travis-ci.org/sarahtattersall/PIPEMarkovChain)
## About ##
A collection of classes that are useful for Markov Chain analysis of Petri nets. 

## Installation ##
Simply install via maven:

```
  mvn install
```

## Maven integration
To use this library in Maven projects add this GitHub project as an external repository:

```
<repositories>
    <repository>
        <id>PIPEMarkovChain-mvn-repo</id>
        <url>https://raw.github.com/sarahtattersall/PIPEMarkovChain/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

Then either include the SNAPSHOT or latest release version in your dependencies:
```
<dependencies>
    <dependency>
        <groupId>uk.ac.imperial</groupId>
        <artifactId>pipe-markov-chain</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

