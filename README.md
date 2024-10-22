## Code Review

Your task is to review a [pull request](https://github.com/avdavies/code-review/pull/1) for the following made-up ticket:

```
Write an AWS Lambda function that:
1. Reads Geofence data from a relational database table (Postgres)
2. Saves the data to a cache (Redis)
```

The developer whose code you are reviewing is an inexperienced junior developer, and you should tailor your review with that in mind.

As such, you don't need to concern yourself too much with the low-level details - this is more about getting the basics right.

Some things to know:

- You don't need to know about the specifics of AWS Lambda. Just know that it is AWS's "code as a service".  The 'entrypoint' to the application is via `App::handleRequest`, which you can think of like the `main` function in a Java application.
- The code uses a library named JDBI to interact with the database. You don't need to know the low-level details of using this library.
- The code uses a library named Jedis to interact with the cache (Redis/Elasticache). Again you don't need to worry about the nuts and bolts of using that library.

The main purpose of this review is to give the developer some feedback about the _basics_, for example the structure of the code, rather than the line-by-line correctness of the code.

You can put comments on the PR, or you can make written notes, or you can just go through it verbally - whichever suits.