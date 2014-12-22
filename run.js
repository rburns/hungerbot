try {
    require("source-map-support").install();
} catch(err) {
}
require("./out/goog/bootstrap/nodejs.js");
require("./out/caterbot.js");
goog.require("caterbot.core");
goog.require("cljs.nodejscli");
