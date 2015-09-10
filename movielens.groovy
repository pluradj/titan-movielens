
graph = TinkerGraph.open()
g = graph.traversal()

//dir = '/tmp/movielen/'
dir = './'
inputDir = dir + 'ml-1m/'
outputDir = dir + 'graphdata/'

occupations = [0:'other', 1:'academic/educator', 2:'artist', \
  3:'clerical/admin', 4:'college/grad student', 5:'customer service', \
  6:'doctor/health care', 7:'executive/managerial', 8:'farmer', \
  9:'homemaker', 10:'K-12 student', 11:'lawyer', 12:'programmer', \
  13:'retired', 14:'sales/marketing', 15:'scientist', 16:'self-employed', \
  17:'technician/engineer', 18:'tradesman/craftsman', 19:'unemployed', 20:'writer']

// local caches to speed up edge creation
movieCache = [:]
userCache = [:]

new File(inputDir + 'movies.dat').eachLine { line ->
  components = line.split('::')
  // split out title and year
  idx = components[1].length()-7
  title = components[1].substring(0, idx)
  year = components[1].substring(idx+2, idx+6)
  println '' + year + ' - ' + title
  movieVertex = graph.addVertex(T.label, 'movie', 'movieId', components[0].toInteger(), 'name', title, 'year', year.toInteger())
  movieCache[components[0].toInteger()] = movieVertex.id()
  components[2].split('\\|').each { genera ->
    hits = g.V().hasLabel('category').has('name', genera)
    generaVertex = hits.hasNext() ? hits.next() : graph.addVertex(T.label, 'category', 'name', genera)
    movieVertex.addEdge('category', generaVertex)
  }
}

new File(inputDir + 'users.dat').eachLine { line ->
  components = line.split('::')
  userVertex = graph.addVertex(T.label, 'user', 'userId', components[0].toInteger(), 'gender', components[1], 'age', components[2].toInteger())
  userCache[components[0].toInteger()] = userVertex.id()
  occupation = occupations[components[3].toInteger()]
  hits = g.V().hasLabel('occupation').has('name', occupation)
  occupationVertex = hits.hasNext() ? hits.next() : graph.addVertex(T.label, 'occupation', 'name', occupation)
  userVertex.addEdge('occupation', occupationVertex)
}

count = 0;
start = System.currentTimeMillis()
new File(inputDir + 'ratings.dat').eachLine { line ->
  components = line.split('::');
  userVertex = g.V(userCache[components[0].toInteger()])
  movieVertex = g.V(movieCache[components[1].toInteger()])
  if (userVertex.hasNext() && movieVertex.hasNext()) {
    count++
    if (count%50000 == 0) println count
    ratedEdge = userVertex.next().addEdge('rated', movieVertex.next())
    ratedEdge.property('stars', components[2].toInteger())
  }
}
println 'loaded ' + count + ' ratings in ' + (System.currentTimeMillis()-start) + ' ms'

new File(outputDir).mkdirs()
//graph.io(graphson()).writeGraph(outputDir + 'movielens.graphson')
//graph.io(graphml()).writeGraph(outputDir + 'movielens.graphml')
graph.io(gryo()).writeGraph(outputDir + 'movielens.gryo')
