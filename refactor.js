const fs = require('fs');
const path = require('path');

const API_ROOT = path.join(__dirname, 'src/main/kotlin');

function walk(dir) {
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        file = path.join(dir, file);
        const stat = fs.statSync(file);
        if (stat && stat.isDirectory()) {
            walk(file);
        } else {
            if (file.endsWith('.kt')) {
                let content = fs.readFileSync(file, 'utf8');
                let newContent = content
                    .replace(/package com\.unifor\./g, 'package com.uniforge.')
                    .replace(/import com\.unifor\./g, 'import com.uniforge.');

                if (content !== newContent) {
                    fs.writeFileSync(file, newContent, 'utf8');
                    console.log(`Updated: ${file}`);
                }
            }
        }
    });
}

walk(API_ROOT);
console.log('Refactoring complete.');
