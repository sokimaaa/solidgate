# Solidgate Test Task

## Objective
Develop a Kotlin application with Spring Boot that has a REST endpoint "set-users-balance" that
accepts a map (int,int). The key of this map is the user ID, and the value is the balance of his
wallet. The Postgres database has a user table with fields: ID, name, balance. When receiving a
request to the "set-users-balance" endpoint, the application must update the balances of all
users sent to it.

Additional goals: 
- It is also necessary to take into account that there may be up to a million records.
- Write functional tests for this endpoint.

> set-users-balance functionality was considered as "overwriting" existing balance, not as "appending".

## Decisions

1. Use Reactive approach to avoid "Thread Blocking" issue. It would be extremely useful for dealing with numerous batch requests.
   It has backpressure mechanism that can be used for dealing with high load.
2. Reactive approach allows utilise thread capabilities 100% effective.
3. Saving records one-by-one is time-consuming + makes additional load to database. 
So, using saveAll for batch update is preferred. Could be optimized with DatabaseClient + Select For Update Query.
4. Using chunking for speed up. Saving millions records at one big batch is long-running task + database high loads. 
We dont want to lose the balance updates, so transactionality is important for us, so rollbacking+retrying the whole batch is extremely expensive operation.
5. 2-Tier Architecture to decrease number of map operation as much as possible.

## Observations

| optimization          | insert records per 5 sec |
|-----------------------|--------------------------|
| no optimization       | 1140 - 1230              |
| one big batch save    | 1340 - 1380              |
| only chunking         | 1590 - 1700              |
| batch save + chunking | 2440 - 2500              |

Positive dynamic after optimizations ;)

> chunking.size parameter may impact additional optimization. 
> In the real case is good to have performance metrics + A/B testing to determine the optimal chunk.size for your kind of load.

> Was decreased number of `map` operations as much as possible for optimization perspective.


## Additional Tuning
1. Append Transactions\Locks + Retrying mechanism for chunks. 
I'd prefer Optimistic lock because it's rare case to have conflict in single record.

2. Consider using another protocol for data transferring, e.g. gRPC.
   1. It compresses data into proto that significantly lightweight the trasporting.
   2. It supports streaming, Server-Side or Bi-directional could be useful for performance perspective.

3. Configuring backpressure mechanism

4. Benchmarking for locating bottle necks

5. Horizontal scaling

6. Considering Apache Spark for batch processing

7. Complication of application & database design