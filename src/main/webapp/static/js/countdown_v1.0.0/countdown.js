////////////////////////////////////////////
//                                        //
//                Countdown               //
//                   v1.0                 //
//               Dec 27, 2012             //    
//              by mike gieson            //
//              www.gieson.com            //
//         Copyright 2012 Mike Gieson     //
//                                        //
////////////////////////////////////////////

var CountdownWidth = 400;
var CountdownHeight = 60;
var CountdownImageFolder = "http://dl.dropbox.com/u/156484492/porraeda/js/countdown_v1.0.0/images";
var CountdownImageBasename = "flipper";
var CountdownImageExt = "png";
var CountdownImagePhysicalWidth = 41;
var CountdownImagePhysicalHeight = 60;

// Usage:
// var test = new Countdown({time:15});

////////////////////////////////////////////
//                                        //
//                 jgoop                  //
//         version 0.0.0.1 alpha          //
//            by mike gieson              //
//             www.jgoop.org              //
//         Copyright 2012 Mike Gieson     //
//                                        //
////////////////////////////////////////////
var jgoop = jgoop || {};(function () {var c = function () {};c.getScriptURL = function (a) {for (var b = document.getElementsByTagName("script"), l = b.length; l--;) {var c = b[l].getAttribute("src");if (c && c.indexOf(a) > -1) {a = c;break}}a = a.split("/");a.pop();b = "";a.length > 0 && (b = "/");return a = a.join("/").toString() + b};c.isNumber = function (a) {return !isNaN(parseFloat(a)) && isFinite(a)};c.hexToRgb = function (a) {a = c.fixHexColor(a);return (a = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(a)) ? [parseInt(a[1], 16), parseInt(a[2], 16), parseInt(a[3], 16)] : [0, 0, 0]};c.fixHexColor = function (a) {a = String(a);for (a.substr(0, 1) != "#" && (a = "#" + a); a.length < 7;) a += "0";return a};c.makeColor = function (a, b) {var a = a || "#000000",l = c.hexToRgb(a);return c.isNumber(b) && jgoop.Browser.modern ? (b > 1 && (b /= 100), "rgba(" + l.join(",") + ("," + b) + ")") : jgoop.Browser.modern ? "rgb(" + l.join(",") + ")" : a};c.clone = function (a) {if (a == null || typeof a != "object") return a;var b = new a.constructor,l;for (l in a) b[l] = c.clone(a[l]);return b};jgoop.Utils = c})();(function () {var c = function () {throw "Ticker cannot be instantiated.";};c.useRAF = null;c.ah = null;c.T = null;c.bK = false;c.aN = false;c.aD = 0;c.bf = 0;c.bB = 0;c.aa = 0;c.G = 50;c.ak = 0;c.R = null;c.bV = null;c.aM = false;c.aj = null;c.addListener = function (b, a) {b != null && (c.aN || c.init(), c.removeListener(b), c.T[c.ah.length] = a == null ? true : a, c.ah.push(b))};c.init = function () {c.aN = true;c.R = [];c.bV = [];c.T = [];c.ah = [];c.R.push(c.ak = c.aD = c.H());c.setInterval(c.G)};c.removeListener = function (b) {var a = c.ah;a && (b = a.indexOf(b), b != -1 && (a.splice(b, 1), c.T.splice(b, 1)))};c.removeAllListeners = function () {c.ah = [];c.T = []};c.setInterval = function (b) {c.G = b;c.aN && c.aK()};c.getInterval = function () {return c.G};c.setFPS = function (b) {c.setInterval(1E3 / b)};c.getFPS = function () {return 1E3 / c.G};c.getMeasuredFPS = function (b) {if (c.R.length < 2) return -1;b == null && (b = c.getFPS() | 0);b = Math.min(c.R.length - 1, b);return 1E3 / ((c.R[0] - c.R[b]) / b)};c.setPaused = function (b) {c.bK = b};c.getPaused = function () {return c.bK};c.getTime = function (b) {return c.H() - c.aD - (b ? c.bf : 0)};c.getTicks = function (b) {return c.bB - (b ? c.aa : 0)};c.aI = function () {c.aM = false;c.aK();c.H() - c.ak >= (c.G - 1) * 0.97 && c.ax()};c.bj = function () {c.timeoutID = null;c.aK();c.ax()};c.aK = function () {if (!(c.aM || c.timeoutID != null)) {if (c.useRAF) {var b = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame;if (b) {b(c.aI);c.aM = true;return}}c.timeoutID = setTimeout(c.bj, c.G)}};c.ax = function () {var b = c.H();c.bB++;var a = b - c.ak,k = c.bK;k && (c.aa++, c.bf += a);c.ak = b;for (var d = c.T, e = c.ah.slice(), f = e ? e.length : 0, g = 0; g < f; g++) {var i = e[g];i == null || k && d[g] || (i.tick ? i.tick(a, k) : i instanceof Function && i(a, k))}for (c.bV.unshift(c.H() - b); c.bV.length > 100;) c.bV.pop();for (c.R.unshift(b); c.R.length > 100;) c.R.pop()};var a = window.performance && (performance.now || performance.mozNow || performance.msNow || performance.oNow || performance.webkitNow);c.H = function () {return a && a.call(performance) || (new Date).getTime()};jgoop.Ticker = c})();(function () {var c = {ie: null,ios: null,mac: null,webkit: null,flash: false,touch: false},a = navigator.userAgent.toLowerCase(),b = navigator.platform.toLowerCase();c.platform = b;c.agent = a;c.version = parseInt((a.match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/) || [])[1]);c.ie = /msie/.test(a) && !/opera/.test(a);c.ios = /iphone/.test(a) || /ipod/.test(a) || /ipad/.test(a);c.windows = b ? /win/.test(b) : /win/.test(a);c.mac = b ? /mac/.test(b) : /mac/.test(a);c.android = /android/.test(a);c.opera = /opera/.test(a);c.safari = /webkit/.test(a);c.chrome = /chrome/.test(a) || /chromium/.test(a);c.moz = /mozilla/.test(a) && !/(compatible|webkit)/.test(a);c.webkit = /webkit/.test(a);c.cssPrefix = "";if (c.chrome || c.safari) c.cssPrefix = "webkit";else if (c.opera) c.cssPrefix = "o";else if (c.moz) c.cssPrefix = "moz";else if (c.ie && c.version > 8) c.cssPrefix = "ms";c.flash = c.safari || c.chrome || c.ios || c.android ? false : typeof navigator.plugins != "undefined" && typeof navigator.plugins["Shockwave Flash"] == "object" || window.ActiveXObject && new ActiveXObject("ShockwaveFlash.ShockwaveFlash") != false;c.modern = false;if (c.moz && c.version > 3) c.modern = true;if (c.opera && c.version > 9) c.modern = true;if (c.ie && c.version > 8) c.modern = true;if (c.chrome || c.safari || c.ios || c.android) c.modern = true;c.touch = typeof window.ontouchstart === "undefined" ? false : true;jgoop.Browser = c})();(function () {var c, a, b;function l() {if (!g && (g = true, i)) {for (var b = 0; b < i.length; b++) i[b].call(window, []);i = []}}function k(b) {var a = window.onload;window.onload = typeof window.onload != "function" ? b : function () {a && a();b()}}function d() {if (!f) {f = true;document.addEventListener && !c && document.addEventListener("DOMContentLoaded", l, false);a && window == top &&function () {if (!g) {try {document.documentElement.doScroll("left")} catch (b) {setTimeout(arguments.callee, 0);return}l()}}();c && document.addEventListener("DOMContentLoaded", function () {if (!g) {for (var b = 0; b < document.styleSheets.length; b++) if (document.styleSheets[b].disabled) {setTimeout(arguments.callee, 0);return}l()}}, false);if (b) {var d;(function () {if (!g) if (document.readyState != "loaded" && document.readyState != "complete") setTimeout(arguments.callee, 0);else {if (d === void 0) {for (var b = document.getElementsByTagName("link"), a = 0; a < b.length; a++) b[a].getAttribute("rel") == "stylesheet" && d++;b = document.getElementsByTagName("style");d += b.length}document.styleSheets.length != d ? setTimeout(arguments.callee, 0) : l()}})()}k(l)}};var e = navigator.userAgent.toLowerCase();e.match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/);b = /webkit/.test(e);c = /opera/.test(e);a = /msie/.test(e) && !/opera/.test(e);/mozilla/.test(e) && /(compatible|webkit)/.test(e);var f = false,g = false,i = [];d();jgoop.ready = function (b) {d();g ? b.call(window, []) : i.push(function () {return b.call(window, [])})}})();(function () {var c = function (b) {this.initialize(b)},a = c.prototype;a.element = null;a.J = null;a.alpha = 1;a.id = null;a.name = null;a.parent = null;a.rotation = 0;a.scaleX = 1;a.scaleY = 1;a.skewX = 0;a.skewY = 0;a.shadow = null;a.visible = true;a.overflow = "visible";a.x = 0;a.y = 0;a.width = 0;a.height = 0;a.bQ = 0;a.bi = 0;a.bD = 0;a.bJ = 0;a.ay = 0;a.ba = 1;a.aH = 1;a.aV = null;a.ag = null;a.aL = null;a.z = 0;a.mouseX = 0;a.mouseY = 0;a.bkgdColor = null;a.bkgdAlpha = null;a.initialize = function (b) {if (b) {var a, c, d, e, f, g, i, h, r, p, q;if (b) a = b.x,c = b.y,d = b.w,e = b.h,f = b.rounded,g = b.bkgdColor,i = b.bkgdAlpha,h = b.stroke,r = b.strokeColor,p = b.strokeAlpha,q = b.shadow;if (this.element === null) this.element = document.createElement("div"),this.element.id = jgoop.getUID,this.element.style.position = "absolute",this.element.style.overflow = "visible";b = this.J = this.element.style;b.WebkitBoxSizing = b.MozBoxSizing = b.boxSizing = "border-box";b.userSelect = b.WebkitUserSelect = b.msUserSelect = b.MozUserSelect = b.OUserSelect = "none";f && this.setRounded(f);jgoop.Utils.isNumber(a) && this.setXY(a, c);g && this.fill(g, i);e && this.setHeight(e);d && this.setWidth(d);q && this.setShadow(q);h && this.setStroke(h, r, p);this.setCursor("default")}};a.fill = function (b, a) {typeof b == "object" ? (this.gradient(b), this.bkgdColor = b, this.bkgdAlpha = null) : (this.J.backgroundColor = jgoop.Utils.makeColor(b, a), this.bkgdColor = b, this.bkgdAlpha = a)};a.gradient = function (b) {for (var a = [], c = [], d = 0; d < b.length; d++) {var e = b[d],f = jgoop.Utils.makeColor(e[0], e[1]);a.push(f + " " + e[2] + "%");c.push("color-stop(" + e[2] + "%, " + f + ")")}a = a.join(",");d = this.J;d.background = jgoop.Utils.makeColor(b[0][0]);if (jgoop.Browser.modern) d.background = "-moz-linear-gradient(top, " + a + ")",d.background = "-webkit-linear-gradient(top, " + a + ")",d.background = "-o-linear-gradient(top, " + a + ")",d.background = "-ms-linear-gradient(top, " + a + ")",d.background = "linear-gradient(to bottom, " + a + ")",d.background = "-webkit-gradient(linear, left top, left bottom,  " + c.join("\t,") + ")";d.filter = "progid:DXImageTransform.Microsoft.gradient( startColorstr='" + b[0][0] + "', endColorstr='" + b[b.length - 1][0] + "',GradientType=0 )"};a.setStroke = function (b, a, c) {var d = this.J;d.borderStyle = "solid";d.borderWidth = b + "px";d.borderColor = a == "bevel" ? "#E6E6E6 #666 #000 #CCC" : a == "bevelinv" ? "#000 #CCC #E6E6E6 #666" : jgoop.Utils.makeColor(a, c)};a.setCursor = function (b) {this.J.cursor = b};a.updateDims = function () {this.setSize(this.width, this.height);this.setXY(this.x, this.y)};a.clearWidth = function () {this.width = null;this.J.width = ""};a.clearHeight = function () {this.height = null;this.J.height = ""};a.setWidth = function (b) {this.width = b;this.J.width = b + "px"};a.setHeight = function (b) {this.height = b;this.J.height = b + "px"};a.measure = function () {var b = this.element,a = b.clientWidth,b = b.clientHeight;this.width = a;this.height = b;return [a, b]};a.setSize = function (b, a) {this.width = b;this.height = a;var c = this.J;c.width = b + "px";c.height = a + "px";if (!this.ay) this.bQ = this.x,this.bi = this.y,this.bD = b,this.bJ = a,this.ay = 1};a.setXY = function (b, a) {this.x = b;this.y = a;var c = this.J;c.left = b + "px";c.top = a + "px"};a.setX = function (b) {this.x = b;this.J.left = b + "px"};a.setY = function (b) {this.y = b;this.J.top = b + "px"};a.setTop = function (b) {this.y = b;this.J.top = b + "px"};a.setBottom = function (b) {this.y = b - this.height;this.J.bottom = b + "px"};a.setLeft = function (b) {this.x = b;this.J.left = b + "px"};a.setRight = function (b) {this.x = b - this.width;this.J.right = b + "px"};a.setZ = function (b) {this.z = b;this.J.zIndex = b};a.setScale = function (b, a, c) {this.scaleX = b;this.scaleY = a;this.aB("scale(" + b + "," + a + ")", c || "50% 50%")};a.stretch = function (b, a) {this.ba && this.setWidth(this.bD * b);this.aH && this.setHeight(this.bJ * a);if (this.aV) {if (this.aV == "r" && this.parent) {if (!this.aL) this.aL = this.parent.width - this.x;var c = this.parent.width - this.aL * b;this.setX(c)}} else this.setX(this.bQ * b);if (this.ag) {if (this.ag == "b" && this.parent) {if (!this.aY) this.aY = this.parent.height - this.y;c = this.parent.height - this.aY * a;this.setY(c)}} else this.setY(this.bi * a);return true};a.setRotation = function (b, a) {this.rotation = b;this.aB("rotate(" + b + "deg)", a)};a.setSkew = function (b, a, c) {this.skewX = b;this.skewY = a;this.aB("skew(" + b + "deg," + a + "deg)", c)};a.setOrigin = function (b) {var a = this.J;a.transformOrigin = a.WebkitTransformOrigin = a.msTransformOrigin = a.MozTransformOrigin = a.OTransformOrigin = b};a.aB = function (b, a) {var c = this.J;if (a) c.transformOrigin = c.WebkitTransformOrigin = c.msTransformOrigin = c.MozTransformOrigin = c.OTransformOrigin = a;c.transform = c.transform = c.msTransform = c.WebkitTransform = c.MozTransform = b};a.center = function (b) {if (this.parent) {var a = this.x,c = this.y,d = this.width * 0.5,e = this.height * 0.5,f = this.parent.width * 0.5,g = this.parent.height * 0.5;b == "v" ? c = g - e : b == "h" ? a = f - d : (a = f - d, c = g - e);this.setXY(a, c)}};a.setOverflow = function (b) {this.overflow = b;this.J.overflow = b};a.setVisible = function (b) {this.visible = b;this.J.display = b == true ? "block" : "none"};a.show = function () {this.setVisible(true)};a.hide = function () {this.setVisible(false)};a.setAlpha = function (b) {this.alpha = b;if (b !== null) this.J.opacity = "" + b};a.setRounded = function (b) {var a = this.J;a.borderRadius = a.MozBorderRadius = a.WebkitBorderRadius = a.OBorderRadius = a.msBorderRadius = b + "px"};a.onAdded = function () {};a.setShadow = function (b) {this.shadow = b;for (var a = b.length, c = [], d = [], e = 0, f = 0; f < a; f++) e < 3 ? d.push(b[f] + "px") : (d.push(jgoop.Utils.makeColor(b[f], b[f + 1])), c.push(d.join(" ")), d = [], ++f, e = -1),e++;b = this.J;b.boxShadow = b.MozBoxShadow = b.WebkitBoxShadow = b.OBoxShadow = b.msBoxShadow = c.join(",")};jgoop.Box = c})();(function () {var c = function (b) {this.initialize(b)},a = c.prototype = new jgoop.Box;a.L = [];a.addChild = function (b) {if (b == null) return b;var a = arguments.length;if (a > 0) for (var c = 0; c < a; c++) {var d = arguments[c];d.parent && (d.parent.element.removeChild(d.element), d.parent.removeChild(d));d.parent = this;d.setZ(this.L.length);this.element.appendChild(d.element);this.L.push(d)}};a.removeChild = function (b) {var a = arguments.length;if (a > 1) {for (var c = true, d = 0; d < a; d++) c = c && this.removeChild(arguments[d]);return c}return this.removeChildAt(this.L.indexOf(b))};a.removeChildAt = function (b) {var a = arguments.length;if (a > 1) {for (var c = [], d = 0; d < a; d++) c[d] = arguments[d];c.sort(function (b, a) {return a - b});for (var e = true, d = 0; d < a; d++) e = e && this.removeChildAt(c[d]);return e}if (b < 0 || b > this.L.length - 1) return false;if (a = this.L[b]) a.parent = null;this.element.removeChild(a.element);this.L.splice(b, 1);return true};a.toFront = function (b) {for (var a = b.z, c = this.L.length, d = 0; d < c; d++) {var e = this.L[d];e.z > a && e.setZ(e.z - 1)}b.setZ(c - 1)};a.bz = a.initialize;a.initialize = function (b) {this.bz(b);this.L = []};a.aE = a.stretch;a.stretch = function (b, a) {this.aE(b, a);for (var c = 0; c < this.L.length; c++) this.L[c].stretch(b, a)};a.be = a.onAdded;a.onAdded = function () {this.be();for (var b = 0; b < this.L.length; b++) this.L[b].onAdded()};jgoop.Container = c})();(function () {var c = function (b) {this.af = b;this.configure(b)},a = c.prototype = new jgoop.Container;a.af = null;a.aw = null;a.ay = false;a.bQ = null;a.bi = null;a.bD = null;a.bJ = null;a.configure = function (b) {this.children = [];if (b.width) this.width = b.width;if (b.height) this.height = b.height;if (b.inline) this.aw = b.inline;this.id = jgoop.getUID();b.target && document.getElementById(b.target) ? (this.parent = document.getElementById(b.target), this.element = document.createElement("div"), this.element.id = this.id, this.parent.appendChild(this.element)) : (document.write('<div id="' + this.id + '"> </div>'), this.element = document.getElementById(this.id));jgoop.Base.addStage(this)};a.bP = a.initialize;a.initialize = function () {this.element = document.getElementById(this.id);this.parent = this.element.parentNode;this.J = this.element.style;this.J.position = "relative";this.J.display = this.af.inline === true || this.af.inline == "true" || this.af.inline === 1 ? "inline-block" : "block";this.J.verticalAlign = "top";this.J.zoom = 1;this.width !== null && this.setSize(this.af.width, this.af.height);this.bP(this.af)};jgoop.Stage = c})();(function () {var c = function (b) {this.initialize(b)},a = c.prototype = new jgoop.Box;a.aU = 0;a.by = {font: "Verdana, Geneva, sans-serif",color: "#000",align: "left",lineHeight: 0,size: 8,selectable: false,multiline: false,zoom: 1,alpha: 0.8,weight: null,caps: null,decoration: null,whiteSpace: null,padding: null,foo: null};a.bG = a.initialize;a.initialize = function (b) {this.bG(b);if (b && (b.textFormat && this.setTextFormat(jgoop.Utils.clone(b.textFormat)), b.text && this.setText(b.text), b.textColor && this.setTextColor(b.textColor, b.textAlpha), b.mode)) this.aU = b.mode,this.bp();this.bG(b)};a.aW = function () {var b = this.J,a = this.by,c = !a.multiline;if (a.font) b.fontFamily = a.font;if (a.color) b.color = a.alpha ? jgoop.Utils.makeColor(a.color, a.alpha) : a.color;if (a.align) b.textAlign = a.align;if (c) a.whiteSpace = "nowrap";if (a.lineHeight) b.lineHeight = a.lineHeight + "px";if (a.size) b.fontSize = a.size + "px";if (a.weight) b.fontWeight = a.weight;if (a.caps) b.textTransform = a.caps;if (a.decoration) b.textDecoration = a.decoration;if (a.whiteSpace) b.whiteSpace = a.whiteSpace;if (c) b.padding = "0px";else if (a.padding || a.padding === 0) {for (var c = String(a.padding).split(" "), d = [], e = 0; e < c.length; e++) d.push(c[e] + "px");b.padding = d.join(" ")}if (a.selectable == false) b.WebkitUserSelect = "none",b.msUserSelect = "none",b.MozUserSelect = "none",b.userSelect = "none"};a.setTextFormat = function (b) {if (b) this.by = jgoop.Utils.clone(b),this.aW()};a.setTextColor = function (b, a) {var c = this.by;c.color = b;var d = this.J;a ? (c.alpha = a, d.color = jgoop.Utils.makeColor(b, a)) : d.color = b};a.setText = function (b) {this.text = b = String(b);this.element.innerHTML = b;this.previuosText.length != b.length && (this.measure(), this.bp());this.previuosText = b};a.text = "";a.previuosText = "";a.an = a.setWidth;a.setWidth = function (b) {this.an(b);this.bp()};a.bN = a.setHeight;a.setHeight = function (b) {this.bN(b);this.bp()};a.bn = function () {var b = document.createElement("div");document.body.appendChild(b);var a = this.J,c = b.style;c.fontSize = a.fontSize;c.fontFamily = a.fontFamily;c.fontWeight = a.fontWeight;c.position = "absolute";c.left = 100;c.top = 100;b.innerHTML = this.text;a = parseFloat(a.fontSize) * (this.width / b.clientWidth);if (jgoop.Utils.isNumber(a) && a > 0) this.by.size = a;document.body.removeChild(b)};a.av = a.setSize;a.setSize = function (b, a) {this.av(b, a);this.bp()};a.ab = a.onAdded;a.onAdded = function () {this.ab();this.measure();this.bp()};a.bp = function () {var b = this.by;if (!b.multiline) {var a = this.width,c = this.height;b.lineHeight = c;this.aU == 1 ? b.size = (a < c ? a : c) * 0.7 : this.aU == 2 ? this.bn() : b.size = this.aU == 3 ? c : c * 0.7;this.aW()}};a.setTextStyle = function (b) {b > -1 && this.shadowText(this.aP[b])};a.shadowText = function (b) {if (b) {for (var a = b.length, c = [], d = [], e = 0, f = 0; f < a; f++) e < 3 ? d.push(b[f] + "px") : (d.push(jgoop.Utils.makeColor(args[f], b[f + 1])), c.push(d.join(" ")), d = [], ++f, e = -1),e++;this.J.textShadow = c.join(",")}};a.aP = [[-0.1, -2, 0, "#000000", 1, 1, 2, 0, "#FFFFFF", 0.7]];jgoop.Text = c})();(function () {var c = function (b) {this.initialize(b)},a = c.prototype = new jgoop.Box;a.K = null;a.url = null;a.aW = "actual";a.bo = function () {if (this.K) {var b, a;if (!this.width || !this.height) this.aW = "actual";this.alpha == 0 && this.setAlpha(1);this.aW == "actual" ? (b = this.K.width, a = this.K.height, this.width = this.K.width, this.height = this.K.height) : this.aW == "lock" ? (b = this.width / this.K.width, a = this.height / this.K.height, a = b > a ? a : b, b = this.K.width * a, a *= this.K.height) : this.width && this.height ? (b = this.width, a = this.height) : (b = this.K.width, a = this.K.height);this.J.backgroundSize = b + "px " + a + "px";this.J.backgroundImage = 'url("' + this.url + '")';this.J.backgroundRepeat = "no-repeat";this.J.backgroundPosition = "center center"}};a.setImage = function (b) {this.url = b;this.K = new Image;this.K.src = b;(function (b, a) {a.onload = function () {b.bo()}})(this, this.K)};a.bL = a.initialize;a.initialize = function (b) {this.bL(b);if (b.format) this.aW = b.format;b.url && this.setImage(b.url)};a.al = a.setWidth;a.setWidth = function (b) {this.al(b);this.bo()};a.aG = a.setHeight;a.setHeight = function (b) {this.aG(b);this.bo()};a.bu = a.setSize;a.setSize = function (b, a) {this.bu(b, a);this.bo()};jgoop.Bitmap = c})();(function () {jgoop.amReady = false;jgoop.scriptName = "jgoop.js";jgoop.scriptPath = null;jgoop.readyList = jgoop.readyList || [];jgoop.ticker = null;jgoop.tickerSpeed = 80;jgoop.bq = [];var c = function () {};c.aX = 0;c.bx = function () {jgoop.amReady = true;jgoop.ticker = jgoop.Ticker;jgoop.ticker.init();jgoop.ticker.setInterval(jgoop.tickerSpeed);jgoop.scriptPath = jgoop.Utils.getScriptURL(jgoop.scriptName);if (jgoop.bq) {for (var a = 0; a < jgoop.bq.length; a++) {var b = jgoop.bq[a];b.initialize.call(b)}jgoop.bq = []}if (jgoop.readyList) {for (a = 0; a < jgoop.readyList.length; a++) b = jgoop.readyList[a],b.fn.call(b.home);jgoop.readyList = []}};c.getUID = function () {return "jgoopObject" + c.aX++};c.addStage = function (a) {jgoop.amReady ? a.initialize.call(a) : jgoop.bq.push(a)};c.register = function (a) {jgoop.amReady ? a.initialize.call(a) : (jgoop.readyList = jgoop.readyList || [], jgoop.readyList.push({home: a,fn: a.initialize}))};jgoop.Base = c;jgoop.register = c.register;jgoop.getUID = c.getUID})();(function () {var c = function (b) {this.imageFolder = CountdownImageFolder;this.imageBasename = CountdownImageBasename;this.imageExt = CountdownImageExt;this.imagePhysicalWidth = CountdownImagePhysicalWidth;this.imagePhysicalHeight = CountdownImagePhysicalHeight;this.totalFlipDigits = 2;this.af = b || {};this.bA = new jgoop.Stage({target: b.target,inline: b.inline || false,width: b.width || CountdownWidth,height: b.height || CountdownHeight,rounded: b.bkgd ? b.bkgd.rounded ? b.bkgd.rounded : null : null,bkgdColor: b.bkgd ? b.bkgd.color ? b.bkgd.color : null : null,stroke: b.bkgd ? b.bkgd.stroke ? b.bkgd.stroke : null : null,strokeColor: b.bkgd ? b.bkgd.strokeColor ? b.bkgd.strokeColor : null : null,strokeAlpha: b.bkgd ? b.bkgd.strokeAlpha ? b.bkgd.strokeAlpha : null : null,shadow: b.bkgd ? b.bkgd.shadow ? b.bkgd.shadow : null : null});jgoop.register(this)},a = c.prototype;a.af = null;a.bA = null;a.bb = false;a.bO = null;a.ai = false;a.V = null;a.J = null;a.totalFlipDigits = null;a.imageFolder = null;a.imageBasename = "flipper";a.imageExt = "png";a.O = null;a.aR = null;a.initialize = function () {var b = this.af;this.ai = this.bb = false;this.V = new Date;this.J = b.style || "boring";this.width = b.width || CountdownWidth;this.height = b.height || CountdownHeight;this.bO = b.onComplete;var a = new Date;if (this.J == "flip") {this.imageFolder.substr(-1) != "/" && (this.imageFolder += "/");var c = this.imageFolder + this.imageBasename}this.aR = {second: {use: false,prev: [null, null],ani: [null, null],aniCount: [null, null]},minute: {use: false,prev: [null, null],ani: [null, null],aniCount: [null, null]},hour: {use: false,prev: [null, null],ani: [null, null],aniCount: [null, null]},day: {use: false,prev: [null, null],ani: [null, null],aniCount: [null, null]},month: {use: false,prev: [null, null],ani: [null, null],aniCount: [null, null]},year: {use: false,prev: [null, null],ani: [null, null],aniCount: [null, null]}};if (b.time) this.V.setTime((a.getTime() / 1E3 + parseInt(b.time) + 1) * 1E3);else {var a = b.year ? parseInt(b.year) : a.getFullYear(),d = b.month ? parseInt(b.month) - 1 : 0,e = b.day ? parseInt(b.day) : 0,f = b.hour ? parseInt(b.hour) : 0,g = b.minute ? parseInt(b.minute) : 0,i = b.second ? parseInt(b.second) : 0;/p/.test((b.ampm ? b.ampm : "am").toLowerCase()) && (f += 12);this.V = new Date(a, d, e, f, g, i)}a = "second,minute,hour,day,month,year".split(",");asdf = "segon,minut,hore,die,meso,any".split(",").reverse();f = b.rangeLo ? b.rangeLo : "second";g = b.rangeHi ? b.rangeHi : "year";f = f.substr(-1) == "s" ? f.substr(0, f.length) : f;g = g.substr(-1) == "s" ? g.substr(0, g.length) : g;for (e = 0; e < a.length; e++) d = a[e],d == f && (f = e),d == g && (g = e);for (e = 0; e < a.length; e++) if (e >= f && e <= g) this.aR[a[e]].use = true;e = b.padding === 0 ? 0 : b.padding ? b.padding : this.J == "flip" ? 0 : 0.4;this.J == "flip" && (e /= 2);var d = this.height,f = this.width / (g - f + 1),g = f * 0.25,i = f * 0.1,h = f - i,r = d - g,p = h * e;this.J == "flip" && (p = h * (e / this.totalFlipDigits));var q = h - p,y = this.height - g * 2;if (this.J == "flip") var y = this.height - g,A = d * 0.03;var n = {font: "Arial, _sans",color: "#FFFFFF",weight: "normal",bkgd: this.J == "flip" ? null : [["#2A2A2A", 1, 0],["#3F3F3F", 1, 50],["#000000", 1, 50],["#282828", 1, 80],["#2F2F2F", 1, 100]],rounded: this.J == "flip" ? null : r * 0.12,shadow: this.J == "flip" ? null : [0, 0, h * 0.12, "#000000", 0.5]},e = {font: "Arial, _sans",color: "#303030",weight: "bold"};if (b.numbers) for (var m in n) b.numbers[m] && (n[m] = b.numbers[m]);if (b.labels) for (m in e) b.labels[m] && (e[m] = b.labels[m]);m = {font: n.font,color: n.color,weight: n.weight,align: "center"};var F = {font: e.font,color: e.color,weight: e.weight,align: "center"};a.reverse();this.O = {};for (var z = 0, e = 0; e < a.length; e++) {var j = a[e];if (this.aR[j].use) {this.O[j] = new jgoop.Container({x: z + i / 2,y: 0,w: h,h: r,rounded: n.rounded || null,bkgdColor: n.bkgd || null,shadow: n.shadow || null});j = this.O[j];if (this.J == "flip") {var o = this.imagePhysicalWidth * ((q - A * 2 - p * 2) / this.totalFlipDigits / this.imagePhysicalWidth),t = this.imagePhysicalHeight * (y / this.imagePhysicalHeight);j.time = new jgoop.Container({x: 0,y: 0,w: o * this.totalFlipDigits,h: t});for (var B = [], s = 0; s < this.totalFlipDigits; s++) {for (var u = new jgoop.Container({x: o * s + A * s,y: 0,w: o,h: t}), C = [], v = 0; v < 10; v++) {for (var w = new jgoop.Container({x: 0,y: 0,w: o,h: t}), D = [], x = 0; x < 3; x++) {var E = new jgoop.Bitmap({x: 0,y: 0,w: o,h: t,url: c + ("" + v + "" + x) + "." + this.imageExt,format: "lock"});D[x] = E;w.addChild(E)}w.img = D;C[v] = w;u.addChild(w)}u.num = C;B[s] = u;j.time.addChild(u)}j.time.slot = B;j.addChild(j.time)} else j.time = new jgoop.Text({x: 0,y: g / 3,w: q,h: y,text: "00",textFormat: m,mode: 2}),j.addChild(j.time),j.line = new jgoop.Box({x: 0,y: 0,w: h,h: d * 0.03,bkgdColor: "#000000"}),j.addChild(j.line),j.line.center();o = asdf[e].toUpperCase() + "S";j.labels = new jgoop.Text({x: z,y: d - g,w: f,h: g,textFormat: F,text: o});this.bA.addChild(j);this.bA.addChild(j.labels);j.time.center();b.numberMarginTop && j.time.setY(b.numberMarginTop);z += f}}this.ai = true;jgoop.ticker.addListener(this)};a.aA = function () {this.bO && this.bO()};a.bM = function () {var b, a, c, d, e, f, g = new Date,i = this.V.getTime() - g.getTime(),h = this.O;if (i > 0) b = i / 1E3,a = b / 60,c = a / 60,d = Math.floor(c / 24),e = 0,f = Math.floor(d / 365),b = Math.floor(b % 60),a = Math.floor(a % 60),c = Math.floor(c % 24),d > 27 && (e = new Date(i), e = (e.getUTCMonth() + (e.getUTCFullYear() - 1970) * 12) % 12, d = this.as(g.getMonth(), g.getFullYear()), d = (d - g.getDate() + this.V.getDate()) % d),b < 10 && (b = "0" + b),a < 10 && (a = "0" + a),c < 10 && (c = "0" + c),d < 10 && (d = "0" + d),e < 10 && (e = "0" + e),f < 10 && (f = "0" + f),this.J == "flip" ? (h.year && this.bI("year", f), h.month && this.bI("month", e), h.day && this.bI("day", d), h.hour && this.bI("hour", c), h.minute && this.bI("minute", a), h.second && this.bI("second", b)) : (h.year && h.year.time.setText(f), h.month && h.month.time.setText(e), h.day && h.day.time.setText(d), h.hour && h.hour.time.setText(c), h.minute && h.minute.time.setText(a), h.second && h.second.time.setText(b));else if (!this.bb) f = e = d = c = a = b = "00",this.J == "flip" ? (h.year && this.bI("year", f), h.month && this.bI("month", e), h.day && this.bI("day", d), h.hour && this.bI("hour", c), h.minute && this.bI("minute", a), h.second && this.bI("second", b)) : (h.year && h.year.time.setText(f), h.month && h.month.time.setText(e), h.day && h.day.time.setText(d), h.hour && h.hour.time.setText(c), h.minute && h.minute.time.setText(a), h.second && h.second.time.setText(b)),this.bb = true,this.aA()};a.bI = function (a, c) {for (var k = 0; k < this.totalFlipDigits; k++) {var d = this.O[a].time.slot[k],e = this.aR[a],f = String(c).substr(k, 1),g = d.num[f];if (g) {if (e.prev[k] != f) {for (var i = 0; i < 10; i++) d.num[i].hide();g.show();e.ani[k] = true;e.aniCount[k] = 0}if (e.ani[k]) {for (i = 0; i < 3; i++) g.img[i].hide();this.bb ? g.img[2].show() : (g.img[e.aniCount[k]].show(), e.aniCount[k]++, e.aniCount[k] > 2 && (e.ani[k] = false))}e.prev[k] = f}}};a.tick = function () {this.ai === true && this.bM()};a.as = function (a, c) {if (a < 0 || a > 11) return 0;c || (c = (new Date).getFullYear());return a == 1 && (c % 400 == 0 || c % 4 == 0 && c % 100 != 0) ? 29 : [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][a]};window.Countdown = c})();jgoop.ready(function () {jgoop.Base.bx()});