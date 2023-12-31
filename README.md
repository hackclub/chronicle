# The Hack Club Dashboard!

## Purpose

Chronicle is the “Hack Club dashboard”, and is meant to:

1. Provide metrics around HQ programs, like “How many people have completed a ‘you ship, we ship’ project like Sprig this month?”
2. Enable Hack Club staff to do direct outreach to Hack Clubbers who are involved in one part of Hack Club, but not others yet (ex. “Who has published a Sprig game, but not yet come to an HQ hackathon?“).

Some examples of questions that Chronicle should be able to answer:

* Who are active Hack Clubbers within driving distance of AngelHacks?
* Who came to Epoch and has contributed to Hack Club repos on GitHub?
* Who is new to Slack, but hasn’t contributed a Sprig game?
* Who has a lot of expertise with Rust, but hasn’t contributed to the Burrow project nor joined its channel?

Key to this project is the fact that Hack Club is volunteer-led, and doesn’t want to mandate usage of universal systems (ex. unified hackathon registration) across the organization.

## Components
Chronicle is split into two major components:
1. A suite of command line tools that load, transform, conflate, and ultimately sync data in a target Elasticsearch cluster
1. An Elasticsearch cluster w/ Kibana used for creating dashboards

## Usage
Access to Chronicle will be limited to a very small set of Hack Club HQ employees with a set of very strict use cases.

## Data
#### Data sources
Chronicle uses data from a variety of sources, including but not limited to:
* Airtable (leaders table, ops address table)
* Scrapbook DB
* Slack APIs
* Google Geocoding APIs
* Github APIs
* Pirateship
* Raw slack data exports (**ONLY** public data)

#### Data freshness
By design, Chronicle will not be designed in a way where new changes in underlying data will be synced to Elasticsearch promptly.  Instead, snapshots will be generated periodically from origin datasources, and then subsequently be consumed when we perform our next sync.

#### Data privacy
Given the sensitive nature of the data Chronicle uses, Chronicle is **not** a tool made for public consumption.  All data posted here in this repo is for purposes of testing and development is mock data not relating to any real person.

Please refer to Hack Club's official data policy guidelines [here](https://github.com/hackclub/chronicle/blob/main/DATA_POLICY.md).

## Local development environment

#### Bring up the local Elasticsearch stack
```
cd docker
docker-compose up
```

#### Package the CLI tool into fat jar (dependencies included) form
```
mvn package
java -jar target/chronicle-1.0-SNAPSHOT-jar-with-dependencies.jar -h
```

## Running tests locally
```
mvn test
```

## Contribution guide
All contributions must...
* ... be sent in pull request form...
* ... have at least one reviewer approving from the 'infra' team...
* ... not cause any test to fail...

...before merging code to the main branch.
