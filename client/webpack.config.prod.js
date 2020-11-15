const webpack = require('webpack');
const path = require('path');

module.exports = {
    entry: [
        './src/index.tsx',
    ],

    output: {
        filename: 'bundle.prod.js',

        path: path.join(__dirname, 'dist'),

        publicPath: '/static/',
    },

    resolve: {
        extensions: ['.ts', '.tsx', '.js', '.json'],

        alias: {
            // Add an import alias for the project root.
            corla: path.resolve(__dirname, 'src'),
        },
    },

    plugins: [
        new webpack.DefinePlugin({
            DEBUG: false,
        }),

        new webpack.NamedModulesPlugin(),

        new webpack.NoEmitOnErrorsPlugin(),
    ],

    module: {
        rules: [
            {
                test: /\.tsx?$/,
                loaders: [
                    'awesome-typescript-loader'
                ],
                exclude: /node_modules/,
                include: /src/,
            },
            {
                test: /\.js$/,
                loader: 'babel-loader',
                include: /node_modules\/string-similarity/,
            },
        ],
    },

    devtool: 'inline-source-map',
};
