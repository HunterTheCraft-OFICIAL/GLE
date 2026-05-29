#!/usr/bin/env python3
"""
Script to translate C++ files to Kotlin.
This is a basic refactoring translation and may not produce fully functional code.
"""

import os
import re
import sys

def cpp_to_kotlin_type(cpp_type):
    """Convert C++ type to Kotlin type."""
    cpp_type = cpp_type.strip()
    
    # Remove common C++ modifiers
    cpp_type = re.sub(r'\b(const|static|inline|virtual|explicit|extern|volatile)\b', '', cpp_type).strip()
    cpp_type = re.sub(r'&|\*', '', cpp_type).strip()
    
    # Handle common types
    type_mapping = {
        'void': 'Unit',
        'bool': 'Boolean',
        'int8_t': 'Byte',
        'int16_t': 'Short',
        'int32_t': 'Int',
        'int64_t': 'Long',
        'uint8_t': 'UByte',
        'uint16_t': 'UShort',
        'uint32_t': 'UInt',
        'uint64_t': 'ULong',
        'int': 'Int',
        'long': 'Long',
        'short': 'Short',
        'char': 'Char',
        'float': 'Float',
        'double': 'Double',
        'string': 'String',
        'std::string': 'String',
        'std::vector': 'List',
        'std::map': 'Map',
        'std::set': 'Set',
        'std::pair': 'Pair',
        'std::tuple': 'Triple',
        'size_t': 'Int',
        'ptrdiff_t': 'Int',
        'nullptr': 'null',
    }
    
    # Check for template types
    template_match = re.match(r'(std::)?(\w+)<(.+)>', cpp_type)
    if template_match:
        base_type = template_match.group(2)
        if base_type in type_mapping:
            return type_mapping[base_type]
        return base_type
    
    if cpp_type in type_mapping:
        return type_mapping[cpp_type]
    
    # Keep unknown types as-is (likely custom classes)
    return cpp_type if cpp_type else 'Any'

def convert_function_params(params_str):
    """Convert C++ function parameters to Kotlin."""
    if not params_str or params_str.strip() == 'void':
        return ''
    
    params = []
    depth = 0
    current = ''
    
    for char in params_str:
        if char == '<':
            depth += 1
            current += char
        elif char == '>':
            depth -= 1
            current += char
        elif char == ',' and depth == 0:
            params.append(current.strip())
            current = ''
        else:
            current += char
    
    if current.strip():
        params.append(current.strip())
    
    kotlin_params = []
    for param in params:
        param = param.strip()
        if not param:
            continue
        
        # Handle default values
        default_value = None
        if '=' in param:
            parts = param.split('=', 1)
            param = parts[0].strip()
            default_value = parts[1].strip()
        
        # Extract type and name
        match = re.match(r'^(.+?)\s+(\w+)$', param)
        if match:
            cpp_type = match.group(1).strip()
            name = match.group(2).strip()
            kotlin_type = cpp_to_kotlin_type(cpp_type)
            
            if default_value:
                kotlin_params.append(f'{name}: {kotlin_type} = {default_value}')
            else:
                kotlin_params.append(f'{name}: {kotlin_type}')
        else:
            kotlin_params.append(param)
    
    return ', '.join(kotlin_params)

def translate_cpp_to_kotlin(content, filename):
    """Translate C++ code to Kotlin."""
    lines = content.split('\n')
    result_lines = []
    
    in_class = False
    class_name = ''
    brace_depth = 0
    in_function = False
    current_function = []
    includes = set()
    
    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()
        
        # Skip preprocessor directives (convert some to comments)
        if stripped.startswith('#'):
            if stripped.startswith('#include'):
                match = re.search(r'#include\s*[<"]([^>"]+)[>"]', stripped)
                if match:
                    includes.add(match.group(1))
            result_lines.append(f'// {stripped}')
            i += 1
            continue
        
        # Skip empty lines but preserve them
        if not stripped:
            result_lines.append('')
            i += 1
            continue
        
        # Handle single-line comments
        if stripped.startswith('//'):
            result_lines.append(stripped)
            i += 1
            continue
        
        # Handle multi-line comments
        if stripped.startswith('/*'):
            result_lines.append(stripped)
            i += 1
            while i < len(lines) and '*/' not in lines[i]:
                result_lines.append(lines[i])
                i += 1
            if i < len(lines):
                result_lines.append(lines[i])
            i += 1
            continue
        
        # Detect class/struct definition
        class_match = re.match(r'^(?:template\s*<[^>]+>\s*)?(class|struct)\s+(\w+)(?:\s*:\s*(public|private|protected)\s+\w+(?:\s*<[^>]+>)?)?\s*\{?', stripped)
        if class_match and brace_depth == 0:
            keyword = class_match.group(1)
            class_name = class_match.group(2)
            in_class = True
            result_lines.append(f'// Original: {keyword} {class_name}')
            result_lines.append(f'class {class_name} {{')
            if '{' in stripped:
                brace_depth = 1
            i += 1
            continue
        
        # Track braces
        brace_depth += stripped.count('{') - stripped.count('}')
        
        if brace_depth == 0 and in_class:
            result_lines.append('}')
            result_lines.append('')
            in_class = False
            class_name = ''
            i += 1
            continue
        
        # Detect function definitions
        func_match = re.match(r'^(?:(?:static|inline|virtual|constexpr|extern)\s+)*(?:[\w:*~&<>,\s]+?)\s+(\w+)\s*\(([^)]*)\)\s*(?:const)?\s*(?:override)?\s*(?:final)?\s*\{?', stripped)
        if func_match and not in_function and brace_depth <= 1:
            func_name = func_match.group(1)
            params = func_match.group(2)
            
            # Skip constructors/destructors in translation
            if func_name == class_name or func_name == f'~{class_name}':
                result_lines.append(f'// Constructor/Destructor: {func_name}')
                i += 1
                continue
            
            kotlin_params = convert_function_params(params)
            
            # Try to detect return type
            return_type = 'Unit'
            return_match = re.match(r'^([\w:*&<>,\s]+?)\s+\w+\s*\(', stripped)
            if return_match:
                return_type = cpp_to_kotlin_type(return_match.group(1))
            
            result_lines.append(f'fun {func_name}({kotlin_params}): {return_type} {{')
            result_lines.append(f'    // TODO: Implement {func_name}')
            result_lines.append('}')
            result_lines.append('')
            i += 1
            continue
        
        # Handle member variables
        var_match = re.match(r'^(?:static\s+)?(?:const\s+)?(?:[\w:*&<>,\s]+?)\s+(\w+)\s*(?:=\s*[^;]+)?;', stripped)
        if var_match and in_class:
            var_name = var_match.group(1)
            var_type_match = re.match(r'^(?:static\s+)?(?:const\s+)?([\w:*&<>,\s]+?)\s+\w+', stripped)
            var_type = 'Any'
            if var_type_match:
                var_type = cpp_to_kotlin_type(var_type_match.group(1))
            
            is_static = 'static' in stripped
            modifier = 'companion object ' if is_static else ''
            result_lines.append(f'    {modifier}var {var_name}: {var_type}? = null')
            i += 1
            continue
        
        # Add other lines as comments
        if stripped and brace_depth > 0:
            result_lines.append(f'// {stripped}')
        
        i += 1
    
    # Add header comment
    header = [
        '// Auto-translated from C++ to Kotlin',
        f'// Original file: {filename}',
        '// Note: This is a basic translation and may require manual adjustments',
        ''
    ]
    
    if includes:
        header.append('// Original includes:')
        for inc in sorted(includes):
            header.append(f'//   #include <{inc}>')
        header.append('')
    
    return '\n'.join(header + result_lines)

def main():
    input_file = sys.argv[1] if len(sys.argv) > 1 else None
    
    if not input_file:
        print("Usage: python3 cpp_to_kotlin.py <input_file>")
        sys.exit(1)
    
    if not os.path.exists(input_file):
        print(f"File not found: {input_file}")
        sys.exit(1)
    
    with open(input_file, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()
    
    kotlin_code = translate_cpp_to_kotlin(content, input_file)
    print(kotlin_code)

if __name__ == '__main__':
    main()
