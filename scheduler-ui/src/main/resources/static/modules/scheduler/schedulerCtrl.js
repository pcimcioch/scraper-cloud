var frontendApp = angular.module('schedulerApp');

frontendApp.controller('schedulerCtrl', ['$scope', 'schedulerSvc', 'notificationSvc', function($scope, schedulerSvc, notificationSvc) {
    "use strict";

    $scope.services = [];
    $scope.serviceInstances = {};

    $scope.refreshServices = function() {
        return notificationSvc.wrap(schedulerSvc.getServices(), function(response) {
            $scope.services = response.data;
        }, 'Error refreshing services list');
    };

    $scope.refreshServiceInstances = function() {
        return notificationSvc.wrap(schedulerSvc.getServiceInstances(), function(response) {
            setServiceInstances(response.data);
        }, 'Error refreshing service instances list');
    };

    var setServiceInstances = function(instances) {
        $scope.serviceInstances = {};
        for(var i = 0; i< instances.length; ++i) {
            var instance = instances[i];
            instance.settings = JSON.parse(instance.settings);

            if($scope.serviceInstances[instance.serviceId]) {
                $scope.serviceInstances[instance.serviceId].push(instance);
            } else {
                $scope.serviceInstances[instance.serviceId] = [instance];
            }
        }
    };

    $scope.runServiceInstance = function(id) {
        notificationSvc.wrap(schedulerSvc.runServiceInstance(id), null, 'Error running service instance');
    };

    $scope.deleteServiceInstance = function(id) {
        notificationSvc.wrap(schedulerSvc.deleteServiceInstance(id), null, 'Error deleting service instance', function() {
            $scope.refreshServiceInstances();
        });
    };

    $scope.createServiceInstance = function(serviceId, instance, settings, schedule) {
        notificationSvc.wrap(schedulerSvc.createServiceInstance(serviceId, instance, JSON.stringify(settings), schedule), null, 'Error creating service instance', function() {
            $scope.refreshServiceInstances();
        });
    };

    $scope.updateServiceInstanceSettings = function(id, settings) {
        notificationSvc.wrap(schedulerSvc.updateServiceInstanceSettings(id, JSON.stringify(settings)), 'Settings updated', 'Error updating service instance settings', function() {
            $scope.refreshServiceInstances();
        });
    };

    $scope.updateServiceInstanceSchedule = function(id, schedule) {
        notificationSvc.wrap(schedulerSvc.updateServiceInstanceSchedule(id, schedule), 'Schedule updated', 'Error updating service instance schedule', function() {
            $scope.refreshServiceInstances();
        });
    };


    var init = function() {
        $scope.refreshServices();
        $scope.refreshServiceInstances();
    };

    init();
}]);