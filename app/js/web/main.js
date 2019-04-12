
import $ from "jquery"
import * as aj from"../aj";

var HomeStore = require("../stores").home;
var getMessage = require("../actions").getMessage;

export default function main() {
	$(document).ready(function() {

	    HomeStore.subscribe(this, function(state) {
	        $(".message").text(state.message);
	    });

	    getMessage();

	});
}