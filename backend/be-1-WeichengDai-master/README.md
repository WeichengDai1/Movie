# be-1-WeichengDai
be-1-WeichengDai created by GitHub Classroom

# This is added by Weicheng Dai
  Here I set "/api/basic" to be the config path. There are two reasons:
    1. The name of this microservice is "BasicService".
    2. After "/basic", each of the service (math, reversed string, hello) would take a java class. 
      If "/basic" is set to be the outer path, then all of the 3 would be mapped in one java class, which is obviously burdensome for future modification.
