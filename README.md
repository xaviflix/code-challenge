## Comments on requirements
- About the requirements "For how long the machine has worked?", it seems that This cannot be defined exactly as on power 
of we do not have an event, we can take last session event as session finish time but this is an approximate way, would 
be better to have a session-close event on the machine power-off.
- The machineId is defined as a global ID unique for each machine, we will assume the same for the sessionId, if the 
sessionId could be repeated across different machines, we will need the machineId in each event to be able to map it 
correctly to the tuple sessionId/machineId.

## Notes on implementation
- I took advantage of JPA to define table constraints (and create table on runtime) to go faster, but for a production 
application, I'm more in favour of the idea of having SQL scripts to initialize of change the DB schema. 
- On DB queries I also did extensive use of JPA because they are simple, but we could think on using direct SQL sentences 
for performance reasons. Anyway we should have into account that doing certain operations with DB queries will limit our
capacity to do unit testing without the database.
- All tables have an Integer ID as PK because this is very useful for data replication, DMS for example, without it the 
data replication gets more complicated. It is not mandatory in this case, but I like to work in this way because it does
not do any harm and avoid potential problems in the future.
- I'm in favour of trying to do the code as self-explanatory as possible, the comments on the code should be referring
for domain/requirements clarifications/constraints.
- Private methods naming convention: I'm aware it is a "Pythionic way", but with our team we found extremely useful to 
identify private methods at first glance. I have not be able to find any defined policy for this methods in Java, I would 
guess this should be a team/company policy.
- In "ReadMessagesTask" I take advantage of the "instanceof" method. I know using this method can be qualified as a code
smell, but in this specific case I think it is worth as it allows a cleaner code.

## Pending items to be production ready
- We should not allow to create new tables with code, but using migrations.
- Get machines endpoint should have pagination.
- More error control, we are assuming all the SQS will be received with the correct format/data, if this is not the case
we should define how the system should behave.
- Ensure the scheduled task finish gracefully to avoid problems in deployments, we need to avoid shutdown the service
while processing a SQS queue message in order to avoid processing the same message twice. We also could think on using
the event message sessionId+eventType+timestamp to ensure we are not storing the same data twice.
- Concurrency: we cna have several services reading events of the same type for the same sessionId+machineId, we need to
ensure the aggregation data is calculated in the correct way. Concurrency management could be done using a shared semaphore
in REDIS for example, or using more low level policies, like using "select for update" in the events aggregation table if
the database allows it.
- The DB (Hibernate) defaults to 2 decimals resolution, depending on the necessary resolution for the received data we
must ensure the correct decimal definition at DB level (now despite using a BigDecimal our resolution is trunked at DB
level, you will be able to see it in the "SessionControllerTests").
- Deployment pipeline: One pretty easy option (the one we are using right now in my current company) taking advantage of 
the current docket containers will be to link GitHub hooks with CircleCI in order to ensure all test all run before each 
merge in master. For sure there are other options like GitLab, all of them well documented and not so hard to configure.

## Notes on performance / bottlenecks
- According to the requirement "We expect that the aggregation happens quick and the result is as close to real time as 
possible", the application is refreshing the event aggregation data each time an event is received, increasing the CPU 
use if the events have a high frequency. Another option would be to calculate the aggregation each X seconds (this would
depend on machine/events domain constraints) and have a timestamp on the aggregated data for the last update. This will 
make aggregation calculation more predictable in terms of CPU usage.
- The DB will be a bottleneck, before moving to cache policies, I would try to think in a good partition policy and DB
data historification. It seems that doing DB partition based on machineId and timestamp could be a solution, so we only
have data from the last year available, for example. Also, and according to the API definition, we are interested in the
"machines", "session" and "eventaggregation" tables, the "events" table, which should be the one with more registers is
not necessary, making it a good candidate for early historification (maybe data from last 3 months?).
- The configuration on the SQS queues reading policy (time to wait and maximum number of messages to be read) is something
that can be measured and tuned to obtain better results.

## Notes on testing
- I'm considering the tests on controllers as the integration tests. In general the IT are slower as they involve the setup
of the entire application, real DB connections, API calls ... this is why I try to define the IT as a flow-test doing more
than one thing to leverage this costly setup. Also, there is a trade of here, if the flows are so complicated understanding
the IT could be a nightmare.
- For the UT I tried to not have the DB involved to make them faster, this could be object of discussion depending on how 
we deal with the DB (only using the ORM, implementing our own SQL for doing quite complex operations at once ...).

## How to?

### Work with the project
- I added in GitHub the ".idea" folder for opening the project with IntelliJ. Just keep in mind the project is set for
JDK 1.17.
- For setting up the DB and SQS infrastructure you can execute `./deploy.sh infra` inside the `deployment` folder.

### Do API calls
- When hitting the application running on your localhost in development:
  - curl -vS -X GET "http://localhost:8080/machines/list"
  - curl -vS -X GET "http://localhost:8080/sessions/summary?sessionId=session-1&machineId=machine-1"
  - curl -vS -X GET "http://localhost:8080/sessions/last?machineId=machine-1"
- When hitting the application deployed in its docker container (just change the port):
  - curl -vS -X GET "http://localhost:8000/machines/list"
  - curl -vS -X GET "http://localhost:8000/sessions/summary?sessionId=session-1&machineId=machine-1"
  - curl -vS -X GET "http://localhost:8000/sessions/last?machineId=machine-1"

### Deployment
Just use `./deploy.sh` inside the `deployment` folder. Examples of use would be:
- `./deploy.sh app` for deploying only a docker container with the application.
- `./deploy.sh infra` for deploying 2 running containers with the proper infrastructure: DB MySQL and Localstack with SQS.
- `./deploy.sh all` for deploying both, application and infrastructure.
