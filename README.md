# Operation Replicator for Deskproto CAM project files

The purpose of this program, is to take a Deskproto project file.
Inside that file it will look for all operations that contain the string "REPLICATE" in their name.
It will then make copies of these operations with the starting point offset by toolpath-distance / 2.
After these it will add copied operations with the starting point offset by toolpath-distance / 4 of the original and the copied operations.
Then it will do the same with toolpath-distance / 8.

# Result

# quality increase
The result is a finishing strategy that gradually increases surface quality and can be paused and aborted at any time.

# no melting
At the same time it does not operate on the same part of the geometry until it has worked on all other parts.
Thus giving the part time to cool down where a simple parallel-strategy with a smaller toolpath-distance would mill over the same place dozens of times, thus heating up the surface and either melting plastic or wax or setting soft wood on fire.


## Installation and usagle

Currently the pathes for input+output file and the number of iterations are still hardcoded.
There is no command line argument parsing or GUI yet.
Sorry.

## License

GPL 3.0

## Author

- Marcus Wolschon (<Marcus@Wolschon.biz>)