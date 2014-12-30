try {
    require("source-map-support").install();
} catch(err) {
}
require("./out/goog/bootstrap/nodejs.js");
require("./out/hungerbot.js");
goog.require("hungerbot.core");
goog.require("cljs.nodejscli");
