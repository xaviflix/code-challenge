# Notes to be rewritten at the end ...
- Use JPA to define table contraints, I like doing it more i SQL scripts
- Allow JPA to create table to be faster, in PRD this must be deactivated and tables should be created by initial migrations
- We assume the sessionId can be repeated among machineId, this is why use both values
- "For how long the machine has worked?" -> This cannot be defined exactly as on power of we do not have an event, we can take last session event as session finish time but this is an approximate way, would be better to have a session-close event on the machine power-off
- "We expect that the aggregation happens quick and the result is as close to real time as possible" -> This leads us to calculate each time an event is received, increasing the CPU use if the events have a high frequency. Another option would be to calculated the aggregation each X seconds (this depends on machine/events domain constraints) and have a timestamp on the aggregated data for the last update. This will make aggregation calculation more predictable.
- Code comments: only when necessary, the code naming for attributes, methods should be as self-explanatory as possible, ideally, comments should not be necessary except for domain/requirements clarifications/constraints. Also an option would be to calculate the aggretions on runtime with a SQL query on the endpoint, but this would imply repeated calculations on the same data.
- Private methods naming convention: I'm aware it is a "Pythionic way", but with our team we found extremely useful to identify private methods at first glance. I have not be able to find any defined policy for this methods in Java, I would guess this should be a team/company policy
- On queries I did extensive use of JPA because they are simple, but we could think on using direct SQL sentences for performance reasons
- Using instanceof is sometimes a code smell, but in this particular use case allows cleaner code
- Wait on SQS queue will determine the frequency on the task execution

# Pending
- Error management policy not defined, we assume all events will be correct
- Concurrency: "select for update" or REDIS (for example)