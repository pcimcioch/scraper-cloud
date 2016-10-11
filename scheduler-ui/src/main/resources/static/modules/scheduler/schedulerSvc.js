var frontendApp = angular.module('schedulerApp');

frontendApp.service('schedulerSvc', ['$http', function($http) {
    "use strict";

    var self = this;

    self.getServices = function() {
        return $http.get('/scheduler-service/service');
    };

    self.getServiceInstances = function() {
        return $http.get('/scheduler-service/service/instance');
    };

    self.runServiceInstance = function(id) {
        return $http.get('/scheduler-service/service/instance/' + id + '/run');
    };

    self.deleteServiceInstance = function(id) {
        return $http.delete('/scheduler-service/service/instance/' + id);
    };

    self.createServiceInstance = function(serviceId, instanceName, settings, schedule) {
        return $http.post('/scheduler-service/service/instance/', {
            serviceId: serviceId,
            instanceName: instanceName,
            settings: settings,
            schedule: schedule
        }, {headers: {'Content-Type': 'application/json'}});
    };

    self.updateServiceInstanceSettings = function(id, settings) {
        return $http.put('/scheduler-service/service/instance/' + id + '/settings', settings, {headers: {'Content-Type': 'text/plain'}});
    };

    self.updateServiceInstanceSchedule = function(id, schedule) {
        return $http.put('/scheduler-service/service/instance/' + id + '/schedule', schedule, {headers: {'Content-Type': 'text/plain'}});
    };
}]);
