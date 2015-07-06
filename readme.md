# fix permissions

Simple Java file visitor to fix dir and file posix permissions.
Symbolic links are skipped.
By default directories are set to 755 and files are set to 644.
If a file has execute set then the file will be set to 755.

#### build

> gradle clean build

#### usage

> java -jar ./build/libs/fix_permissions.jar some_path

Note that you may need to use sudo if set permission fails.
