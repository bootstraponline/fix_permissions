# fix permissions

Simple Java file visitor to fix dir and file posix permissions.
Symbolic links are skipped.
By default directories are set to 755 and files are set to 644.
Files already set to 744 will not be changed to 644 (execute bit preserved for owner).

#### build

> gradle clean build

#### usage

> java -jar ./build/libs/fix_permissions.jar some_path

Note that you may need to use sudo if set permission fails.
