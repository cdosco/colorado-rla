const webpack = require('webpack');
const path = require('path');

module.exports = {
    entry: [
        // Activate HMR for React.
        'react-hot-loader/patch',

        // Bundle client for dev server, connect to provided endpoint.
        'webpack-dev-server/client?http://localhost:3000',

        // Bundle client for hot reloading. Only hot reload on
        // successful updates.
        'webpack/hot/only-dev-server',

        // Actual app entry point.
        './src/index.tsx',

    ],

    output: {
        filename: 'bundle.js',
        path: path.join(__dirname, 'dist'),
        // Tell HMR where to load hot update chunks.
       publicPath: '/static/',
    },

    resolve: {
        // Add '.ts' and '.tsx' as resolvable extensions.
        extensions: ['.ts', '.tsx', '.js', '.json'],

        alias: {
            // Add an import alias for the project root.
            corla: path.join(__dirname, 'src'),
        },
   },

    plugins: [
        new webpack.DefinePlugin({
            "process.env": {'DEBUG' : true},
            global: {}
          }),   

        // Enable HMR, needed by `react-hot-loader`.
        new webpack.HotModuleReplacementPlugin(),

        // Use readable module names in console.
        //new webpack.NamedModulesPlugin(),

        // Don't emit compiled assets that include errors.
        new webpack.NoEmitOnErrorsPlugin(),
    ],

    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: {loader:  'react-hot-loader/webpack'},
                exclude: path.join(__dirname, 'node_modules'),
                include: path.join(__dirname, 'src'),
            },
            {
                test: /\.tsx?$/,
                use: {loader: 'awesome-typescript-loader'},
                exclude: path.join(__dirname, 'node_modules'),
                include: path.join(__dirname, 'src'),
            },

        ],
    },

    devServer: {
        static: {
            directory:  path.join(__dirname, '/'),
        }, 
        host: 'localhost',
        port: 3000,
        open: true,
        // Serve `index.html` on 404.
        historyApiFallback: true,

        // Support HMR on the dev server.
        hot: true,
    },

    // Enable source maps.
    devtool: 'inline-source-map',
    mode: 'development',
 
};
