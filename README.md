# Lobster
A CLI program to generate tournament matchups.

## Getting started

- install Java
- download the [release](https://github.com/xerus2000/tournament-generator/releases)
- create a `teams.txt` next to the jar with each team in one line
- launch from the terminal with `java -jar tournament.jar`

The program outputs a round of matches into the terminal and appends it to a `games.txt`, which will be read in again to generate further rounds.

If teams drop out, simply remove them from the `teams.txt`. In case there is an odd number of teams one team will skip - signified by a `--` as opponent.