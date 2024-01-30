
ECHO "DELETING DIST FOLDER"
REM DEL /Q dist

REM # Build production JavaScript bundle.
REM npx webpack -p --config webpack.config.prod.js --output-filename bundle.js 
npx webpack --config webpack.config.prod.js --output-filename bundle.js 


REM # Copy root HTML document.
copy index.prod.html dist\index.html

REM # Copy app stylesheet.
copy screen.css dist\


REM ### Copy dependent assets

REM ## Normalize
copy node_modules\normalize.css\normalize.css dist\

REM ## Blueprint - Core
mkdir -p dist\blueprintjs\core\lib\css
copy node_modules\blueprintjs\core\lib\css\blueprint.css dist\blueprintjs\core\lib\css\

REM ## Blueprint - Icons
mkdir -p dist\blueprintjs\icons\lib\css

copy node_modules\blueprintjs\icons\lib\css\blueprint-icons.css dist\blueprintjs\icons\lib\css\

copy node_modules\blueprintjs\icons\resources\icons dist\blueprintjs\icons\

REM ## Blueprint - Date/Time
mkdir -p dist\blueprintjs\datetime\lib\css
copy node_modules\blueprintjs\datetime\lib\css dist\blueprintjs\datetime\lib\css


\script\7z.exe a -tzip colorado-rla-release-2.3.28.zip dist
