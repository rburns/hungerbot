try {
    require("source-map-support").install();
} catch(err) {
}
require("./test_out/goog/bootstrap/nodejs.js");
require("./test_out/test.js");
goog.require("test_runner");
