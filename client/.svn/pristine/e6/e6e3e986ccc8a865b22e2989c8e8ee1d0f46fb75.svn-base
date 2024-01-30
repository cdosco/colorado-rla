
#PS4='+[\t] '
#set -eux -o pipefail

# readonly SCRIPT_DIR="$(dirname ${BASH_SOURCE[0]})"
# readonly CLIENT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

SCRIPT_DIR="$(dirname ${BASH_SOURCE[0]})"
CLIENT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${CLIENT_DIR}"
del -rf dist

# Build production JavaScript bundle.
"$(npm bin)/webpack" --config webpack.config.prod.js --output-filename bundle.js

# Copy root HTML document.
copy index.prod.html dist/index.html

# Copy app stylesheet.
copy screen.css dist/

### Copy dependent assets

## Normalize
copy node_modules/normalize.css/normalize.css dist/

## Blueprint - Core
mkdir -p dist/blueprintjs/core/lib/css
copy -a node_modules/blueprintjs/core/lib/css/blueprint.css \
  dist/blueprintjs/core/lib/css/

## Blueprint - Icons
mkdir -p dist/blueprintjs/icons/lib/css
copy -a node_modules/blueprintjs/icons/lib/css/blueprint-icons.css \
  dist/blueprintjs/icons/lib/css/
copy -a node_modules/blueprintjs/icons/resources \
  dist/blueprintjs/icons/

## Blueprint - Date/Time
mkdir -p dist/blueprintjs/datetime/lib/css
copy -a node_modules/blueprintjs/datetime/lib/css
