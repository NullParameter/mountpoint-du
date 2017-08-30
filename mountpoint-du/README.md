# Overview

Goal of the program is to provide the ability to retrieve disk usage details about the files within a directory.
The program accepts a single parameter of a directory, and prints a JSON blob to sysout, containing all of the files 
within the directory tree, paired with the disk usage for each.

# Dependencies

Dependency management is handled through [Maven](https://maven.apache.org/).

Dependencies include:
  * [GSON](https://github.com/google/gson) for JSON output.
  * [Guava](https://github.com/google/guava) for general utilities.
  * [AssertJ](http://joel-costigliola.github.io/assertj/) for fluent assertions in tests.
  
# Notes

* Made some assumptions about the requirements...
	* A full-fledged program was wanted, not just a simple script.  I could have definitely done it a little more "quick and dirty", but decided to throw a little more design into it.
	* If the requested path doesn't exist, or isn't a directory, then we exit with an error code and a message to syserr.
	* Only the sizes of files are output, not the aggregate sizes of directories.
	* Symlinks are not followed, as they are not _technically_ within the directory tree.  This is easily changed if we desire different behaviour.
	* If an I/O error occurs at any point when processing the directory tree, likely due to a permissions issue, the program is aborted. (We could make the program a bit more involved and return partial results, or a null/sentinel value in the JSON for any file that had an issue.)
	* The requested JSON formatting was a bit odd, having an object for each path/usage pair, but the values within not being a key/value pair itself.  I took the liberty of correcting this to be valid JSON, though we can technically retain the old formatting if that is a requirement.