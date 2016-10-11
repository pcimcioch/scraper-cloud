var frontendApp = angular.module('commonApp');

frontendApp.directive('dynamicForm', function() {
    "use strict";
    
    return {
        restrict: 'E',
        scope: {
            metamodel: '=',
            settings: '=',
            form: '='
        },
        templateUrl: './modules/metamodel/metamodel.html',
        link: function(scope) {
            scope.$watch("metamodelForm", function(newVal, oldVal) {
                scope.form = newVal;
            });
        }
    };
});