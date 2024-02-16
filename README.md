# Automaton core

Automaton core is about all core technical functionalities we may need to start a project.

This one should be agnostic of any environment, so it should run on the following examples of technology:
* CLI 
* backend of web app,
* frontend of web app,
* Android frontend,
* Android backend, ...

Features are:
* Execution graph 
  * Store what happened in a causal graph
* Small utility function helpers like: regular expression, strings, keywords, maps, sequences, uuids
* Logger
* Configuration management - with multiple environments
* Translation
* Exception fallback mechanism
* Persistence (databases)

License information can be found in [LICENSE file](LICENSE.md)
Copyright © 2020-2024 Anthony Caumond, Mateusz Mazurczak
