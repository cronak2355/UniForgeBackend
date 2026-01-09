#!/bin/bash

# Find all files in src/main/kotlin, get their base names (without path), sort them, and find duplicates
# ignoring case might be useful but strict filename matching is a good start.

echo "Checking for duplicate class files..."

# Get list of filenames in src/main/kotlin
# specific to Kotlin files to avoid catching resource duplicates if they are intentional (though usually not)
DUPLICATES=$(find src/main/kotlin -name "*.kt" -type f -exec basename {} \; | sort | uniq -d)

if [ -n "$DUPLICATES" ]; then
    echo "Error: Duplicate filenames found in src/main/kotlin:"
    echo "$DUPLICATES"
    echo "This can cause bean definition conflicts in Spring Boot."
    exit 1
else
    echo "No duplicate filenames found."
    exit 0
fi
