Movie Lens with Gremlin
=======================

* Marko's presentation from NoSQL Now 2015 http://www.slideshare.net/slidarko/the-gremlin-traversal-language

# Prerequisites

* TinkerPop 3.0 http://tinkerpop.incubator.apache.org/
* Titan 1.0 http://thinkaurelius.github.io/titan/

# Download MovieLens Data Set

http://grouplens.org/datasets/movielens/

```
curl -O http://files.grouplens.org/datasets/movielens/ml-1m.zip
unzip ml-1m.zip
```

# Generate Gryo file

```
$TITAN_HOME/bin/gremlin.sh

:load movielens.groovy
:q
```

# Load Gryo file into Titan

```
$TITAN_HOME/bin/gremlin.sh

graph = TitanFactory.open('titan.properties')
graph.io(gryo()).readGraph('./graphdata/movielens.gryo')
g = graph.traversal()
g.V().count()
g.E().count()
g.V().label().groupCount()
g.E().label().groupCount()
```
