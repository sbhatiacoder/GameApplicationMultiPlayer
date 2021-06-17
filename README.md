# Multi Player Game Application
master branch

## Design

This app has 4 endpoints:

1. `GET /player/<nick>` - get player information by nick
2. `GET /player/<nick>/withdraw?points=<points>` - withdraw any number of points
3. `GET /player/<nick>/contribute?points=<points>` - contribute any number of points
4. `GET/player/<nick>/transfer?points=<points>&to=<nick>` - transfer points between players

It is also assumed that:
* each player has an own account.

## Builing & Running

Run the SpringBoot application and app would be listening on port 8080.
