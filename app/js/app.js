import "@babel/polyfill"
import "./stores"
import "./actions"

import {createRuntime} from "./aj"

function main() {
	logger.i("In main function")

	const runtime = createRuntime();
	
	if (platform.device === "browser") {
		const main = require("./web/main");

		main.default();
	}

	return runtime;
}

global.main = main;