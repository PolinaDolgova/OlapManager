"use strict";
var app = angular.module("myApp", ['ui.router', 'chart.js']);

app.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');
    $stateProvider
        .state('Table Editor', {
            url: '/tableEditor',
            templateUrl: 'tableEdit.html'
        })

        .state('Query Editor', {
            url: '/queryEditor',
            templateUrl: 'queryEdit.html'
        })

        .state('Diagram Editor', {
            url: '/diagramEditor',
            templateUrl: 'diagramEdit.html'
        });
    ;
});

app.controller("AppCtrl", function ($scope, $http) {
    $http.get('getCatalogs').then(function (response) {
        $scope.cataloglist = response.data;
    });
    $scope.infoshow = true;
    $scope.disable = false;
    $scope.tablebtn = "Get data";
    $scope.selected = [];
    $scope.query = "";
    $scope.types = ["line", "pie", "doughnut", "bar", "horizontal-bar"];
    $scope.diagramedit = false;

    $scope.setCatalog = function () {
        $http.get('getCubes', {params: {name: $scope.curcatalog}}).then(function (response) {
            $scope.cubelist = response.data;
        });
    };

    $scope.setDim = function (name, val) {
        $scope.diagramdim = name;
        $scope.dimval = val;
    };

    $scope.hideInfo = function () {
        $scope.infoshow = false;
    };

    $scope.setMes = function (name) {
        $scope.diagrammes = name;
    };

    $scope.setCube = function () {
        $scope.clearTableSpace();
        $scope.clearDiagramSpace();
        $http.get('cube', {params: {name: $scope.curcube}}).then(function (response) {
            $scope.measures = response.data.measure.names;
            $scope.dimensions = response.data.dimensions;
            $scope.inputval = null;
            $scope.selectval = [];
            $scope.selected = [];
        });
    };

    $scope.setChartType = function (item) {
        $scope.chart = item;
    };

    $scope.update = function () {
        $http.get('greeting', {params: {name: $scope.name}}).then(function (response) {
            $scope.greeting = response.data;
        });
    };

    $scope.clearTableSpace = function () {
        $scope.rows = [];
        $scope.query = "";
        $scope.selected = [];
        $scope.tablebtn = "Get data";
    };

    $scope.clearDiagramSpace = function () {
        $scope.labels = [];
        $scope.data = "";
        $scope.diagramedit = false;
    };

    $scope.getData = function () {
        $scope.clearDiagramSpace();
        $http.get('getData', {
            params: {
                name: $scope.curcube,
                check: $scope.selected,
                selectval: $scope.selectval
            }
        }).then(function (response) {
            $scope.query = response.data.query;
            $scope.rows = response.data.result;
            $scope.disable = !$scope.disable;
            if ($scope.tablebtn == "Get data") {
                $scope.tablebtn = "New query";
            }
            else {
                $scope.tablebtn = "Get data";
                $scope.clearTableSpace();
            }
        });
    };

    $scope.createQuery = function (query) {
        $scope.clearDiagramSpace();
        $scope.query = query;
        $http.get('createQuery', {params: {name: $scope.curcube, query: $scope.query}})
            .then(function (response) {
                $scope.query = response.data.query;
                $scope.rows = response.data.result;
            });
    };

    $scope.createDiagram = function (diagramdim, diagrammes) {
        $scope.clearTableSpace();
        $scope.labels = [];
        $scope.data = [];
        $scope.series = [];
        $scope.chart = "line";
        $scope.diagramedit = true;
        $http.get('createDiagram', {
            params: {
                dimension: $scope.diagramdim,
                hierarchy: $scope.dimval,
                measure: $scope.diagrammes
            }
        }).then(function (response) {
            $scope.labels = response.data.xAxes;
            $scope.data = response.data.yAxes;
            $scope.series = response.data.xAxes;
        });
    };

    $scope.exist = function (item) {
        return $scope.selected.indexOf(item) > -1;
    };

    $scope.toggleSelection = function (item, val) {
        var idx = $scope.selected.indexOf(item);
        if (idx > -1) {
            $scope.selected.splice(idx, 1);
            $scope.selectval.splice(idx, 1);
        }
        else {
            $scope.selected.push(item);
            $scope.selectval.push(val);
        }
    };

    $scope.colors = ['#45b7cd', '#ff6384', '#ff8e72'];

    $scope.datasetOverride = [
        {
            label: "Bar chart",
            borderWidth: 1,
            type: 'bar'
        },
        {
            label: "Line chart",
            borderWidth: 3,
            hoverBackgroundColor: "rgba(255,99,132,0.4)",
            hoverBorderColor: "rgba(255,99,132,1)",
            type: 'line'
        }
    ];

    $scope.options = {
        legend: {
            display: true,
            labels: {
                fontColor: 'rgb(255, 99, 132)'
            }
        },
        title: {
            display: true,
            text: 'Custom Chart Title'
        }
    };
});