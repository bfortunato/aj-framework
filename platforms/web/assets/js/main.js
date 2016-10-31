var aj = require("aj");
var HomeStore = require("stores").home;
var getMessage = require("actions").getMessage;

$(document).ready(function() {

    HomeStore.subscribe(this, function(state) {
        $(".message").text(state.message);
    });

    getMessage();

});