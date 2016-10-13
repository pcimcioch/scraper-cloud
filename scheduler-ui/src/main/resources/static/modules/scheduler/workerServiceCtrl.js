var frontendApp = angular.module('schedulerApp');

frontendApp.controller('workerServiceCtrl', ['$scope', function($scope) {
    "use strict";

    $scope.settings = {};
    $scope.form = {$valid: false};
    $scope.instance = "";
    $scope.isSchedule = false;
    $scope.schedule = "";
}]);