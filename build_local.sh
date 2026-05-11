#!/bin/bash
# build_local.sh

echo "Compiling Mainframe for Apple Silicon..."

# Ensure the directory exists
mkdir -p libs/mac-aarch64

# Compile directly into the resources folder
clang -shared -o libs/mac-aarch64/libmainframe.dylib libs/mainframe.c

echo "Done."