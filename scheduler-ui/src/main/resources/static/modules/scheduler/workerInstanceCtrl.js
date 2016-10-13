var frontendApp = angular.module('schedulerApp');

frontendApp.controller('workerInstanceCtrl', ['$scope', function($scope) {
    "use strict";

    $scope.form = {$valid: false};
    $scope.isSchedule = false;
    $scope.schedule = '';
    $scope.settings = {};
}]);