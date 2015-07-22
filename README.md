# cgs-enumeration
Algorithms for enumerating the consistent global states of a distributed computation.


=== The first run ===
The defalt target of ant would compile and run the program with the poset: d-100. 


=== Run with different settings ===
After the code is compiled, use "ant run" to execute the program. The command "ant run" can take four arguments, which are listed as follows:
1. -Dtraverser=traverserName: For example, -Dtraverser=BFS will start the enumeration using the BFS algorithm. The available algorithms are BFS and Lex (case-sensitive on Mac OS).
2. -Dtest=testName: The argument testName is the path to the test case.
3. -Dthreads=#threads: If the traverser is multithreaded, then the arguments limits the number of threads for the traverser.
4. -Denumerators=enumerator1:enumerator2... This argument chains a list of enumerators to be invoked during the enumeration. Every enumerator will be invoked once for each consistent global state. The avaiable enumerators are as follows:
  a. Counter: the enumerator counts the number of consistent glboal states that have been enumerated. If the total number of the consistent glboal states of a poset is more than a long number, then use BigCounter instead. This enumerator is thread-safe.
  b. BigCounter: the enumerator counts the number of consistent glboal states that have been enumerated. This enumerator is thread-safe.
  c. File: the enumerator atomically writes out the numerical representation of consistent global states to the file -- "out.txt". This enumerator is NOT thread-safe.
  d. AtomicFile: the enumerator atomically writes out the numerical representation of consistent global states to the file -- "out.txt". This enumerator is thread-safe.
  e. Stdout: the enumerator atomically writes out the numerical representation of consistent global states to stdout. This enumerator is NOT thread-safe.


=== Reference ===
The multithreaded enumerator -- ConcurrentUnorderedTraverser -- is implemented using the method in the paper: Yen-Jung Chang and Vijay K. Garg: "A Parallel Algorithm for Global States Enumeration in Concurrent Systems", PPoPP 2015.
