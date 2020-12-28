# Lobster
A CLI program to generate tournament matchups.

## Getting started

- install Java
- download the [release](https://github.com/xerus2000/lobster/releases/latest)
- create a `teams.txt` next to the jar containing the name of a single team per line
- launch from a terminal using `java -jar lobster-x.x.jar`

The program outputs a round of matches into the terminal and appends it to a `games.txt` file in the current directory, which will be read in again to generate further rounds.
Blank lines and lines starting with # will be ignored, all other lines have to contain two team names separated by a "vs".

If teams drop out, simply remove them from the `teams.txt`.
In case there is an odd number of teams one team will skip - signified by a `--` as opponent.
Blank lines in that file will be skipped.