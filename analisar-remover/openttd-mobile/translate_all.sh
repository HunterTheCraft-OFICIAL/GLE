#!/bin/bash

# Script to translate all C++ files to Kotlin

COUNTER=0
TOTAL=$(wc -l < /tmp/cpp_files.txt)

echo "Starting translation of $TOTAL C++ files to Kotlin..."

while IFS= read -r file; do
    # Get relative path from /workspace/src
    rel_path=${file#/workspace/src/}
    
    # Create output directory structure
    output_dir="/workspace/kotlin_output/$(dirname "$rel_path")"
    mkdir -p "$output_dir"
    
    # Generate output filename with .kt extension
    base_name=$(basename "$file")
    base_name_no_ext="${base_name%.*}"
    output_file="$output_dir/${base_name_no_ext}.kt"
    
    # Translate the file
    python3 /workspace/cpp_to_kotlin.py "$file" > "$output_file" 2>/dev/null
    
    COUNTER=$((COUNTER + 1))
    
    if [ $((COUNTER % 50)) -eq 0 ]; then
        echo "Progress: $COUNTER / $TOTAL files translated"
    fi
done < /tmp/cpp_files.txt

echo "Translation complete! $COUNTER files translated."
echo "Output directory: /workspace/kotlin_output"
