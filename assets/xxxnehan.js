/*
 source : nehan.js
 version : 1.0
 site : http://tategakibunko.mydns.jp/
 blog : http://tategakibunko.blog83.fc2.com/

 Copyright (c) 2010, Watanabe Masaki <lambda.watanabe@gmail.com>
 licenced under MIT licence.

 Permission is hereby granted, free of charge, to any person
 obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without
 restriction, including without limitation the rights to use,
 copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the
 Software is furnished to do so, subject to the following
 conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.
 */

var Nehan;

if (!Nehan) {
	Nehan = {};
}

if (!Nehan.Layout) {
	Nehan.Layout = {};
}

if (!Nehan.TextStream) {
	Nehan.TextStream = {};
}

if (!Nehan.StreamParser) {
	Nehan.StreamParser = {};
}

if (!Nehan.LayoutMapper) {
	Nehan.LayoutMapper = {};
}

if (!Nehan.Env) {
	Nehan.Env = {};
}

(function() {

	var Filename = {
		concat : function(p1, p2) {
			p1 = (p1 == "") ? "" : (p1.slice(-1) == "/") ? p1 : p1 + "/";
			p2 = (p2 == "") ? "" : (p2[0] == "/") ? p2.substring(1, p2.length)
					: p2;
			return p1 + p2;
		}
	};

	var Env = {
		isIE : (document.all && window.ActiveXObject
				&& navigator.userAgent.toLowerCase().indexOf("msie") > -1 && navigator.userAgent
				.toLowerCase().indexOf("opera") == -1)
	};

	function Layout(option) {
		var stdFontSize = 16;
		var defopt = {
			width : 700,
			height : 480,
			fontSize : stdFontSize,
			lineHeightRate : 1.45,
			letterSpacingRate : 0.1,
			direction : "vertical",
			textLayerClassName : "text-layer",
			charImgRoot : "/img/char",
			charImgMap : [],
			charImgColor : "black",
			kinsokuCharCount : 2
		};
		for ( var p in defopt) {
			this[p] = (typeof option[p] == "undefined") ? defopt[p] : option[p];
		}
		if (typeof option.fontFamily != "undefined") {
			this.fontFamily = option.fontFamily;
		}

		this.initialize();
	}
	;

	Layout.prototype.initialize = function() {
		var isV = (this.direction == "vertical");
		this.baseLineHeight = Math.floor(this.lineHeightRate * this.fontSize);
		this.baseLetterSpacing = Math.floor(this.letterSpacingRate
				* this.fontSize);
		var minW = this.baseLineHeight;
		var minH = this.fontSize + this.baseLetterSpacing;
		this.width = Math.max(minW, this.width);
		this.height = Math.max(minH, this.height);
		this.yohakuHeight = 0//this.baseLineHeight - this.fontSize;
		this.letterHeight = (isV) ? this.fontSize + this.baseLetterSpacing : 1;
		this.wrapTag = (isV) ? "table" : "div";
		this.rubyFontSize = Math.floor(this.fontSize / 2); // half size of main
															// text font size.

		if (isV) {
			this.lineCount = Math.floor(this.height / this.letterHeight)
					- this.kinsokuCharCount;
		} else {
			this.lineCount = Math.floor(this.width / this.fontSize)
					- this.kinsokuCharCount;
		}

		this.wrapCss = "";
		this.wrapCss += "text-align:left;";
		this.wrapCss += "padding:0;";
		this.wrapCss += "font-size:" + this.fontSize + "px;";
		this.wrapCss += "width:" + this.width + "px;";
		this.wrapCss += "height:" + this.height + "px;";
		this.wrapCss += "overflow:hidden;";
		this.wrapCss += "white-space:nowrap;"; // when tail NG happend, disable
												// auto newline even if over
												// flow layout size.

		if (isV) {
			this.wrapCss += "border-collapse:collapse;";
		} else {
			this.wrapCss += "line-height:1.8em;";
			this.wrapCss += "letter-spacing:0;";
		}
		if (typeof this.fontFamily != "undefined") {
			if (this.fontFamily != "") {
				this.wrapCss += "font-family:" + this.fontFamily
						+ ", monospace;";
			} else {
				this.wrapCss += "font-family:monospace;";
			}
		}
	};

	Layout.prototype.setHeight = function(height) {
		this.height = height;
	};

	Layout.prototype.setWidth = function(width) {
		this.width = width;
	};

	Layout.prototype.setCharImgRoot = function(root) {
		this.charImgRoot = root;
	};

	Layout.prototype.setCharImgColor = function(color) {
		var c = color.toLowerCase().replace(/#/g, "");
		if (c == "white" || c == "fff" || c == "ffffff") {
			this.charImgColor = "white";
		} else {
			this.charImgColor = "black";
		}
	};

	Layout.prototype.setDirection = function(direction) {
		this.direction = direction;
	};

	Layout.prototype.getDirection = function() {
		return this.direction;
	};

	Layout.prototype.setFontFamily = function(family) {
		this.fontFamily = family;
	};

	Layout.prototype.setFontSize = function(size) {
		this.fontSize = size;
	};

	Layout.prototype.setLineHeightRate = function(rate) {
		this.lineHeightRate = rate;
	};

	Layout.prototype.setLetterSpacingRate = function(rate) {
		this.letterSpacingRate = rate;
	};

	function TextStream(buffer, length, isEOF) {
		this.buffer = buffer;
		this.length = length;
		this.isEOF = isEOF;
		this.seekPos = 0;
	}
	;

	TextStream.prototype.getchar = function() {
		if (this.seekPos < this.buffer.length) {
			var s = this.buffer.substring(this.seekPos, this.seekPos + 1);
			this.seekPos++;
			return s;
		} else {
			throw "BufferEnd";
		}
	};

	TextStream.prototype.checkNextChar = function(checker) {
		if (this.seekPos < this.buffer.length) {
			var s = this.buffer.substring(this.seekPos, this.seekPos + 1);
			if (checker(s)) {
				this.seekPos++;
				return s;
			}
		}
		return "";
	};

	TextStream.prototype.getTag = function() {
		var ret = "<";
		while (true) {
			var s = this.getchar();
			ret += s;
			if (s == ">") {
				return ret;
			}
		}
		return "";
	};

	TextStream.prototype.skipCRLF = function() {
		var mark = this.seekPos;
		var nc1 = this.checkNextChar(function(s) {
			return (s == "\r" || s == "\n");
		});
		if (nc1 == "\n") {
			return this.seekPos;
		} else if (nc1 == "\r") {
			var nc2 = this.checkNextChar(function(s) {
				return (s == "\n");
			});
			if (nc2 == "\n") {
				return this.seekPos;
			}
		}
		return mark;
	};

	TextStream.prototype.setBuffer = function(buff, length) {
		this.buffer = buff;
		this.length = (typeof length != "undefined") ? length : buff.length;
		this.isEOF = true;
	};

	TextStream.prototype.addBuffer = function(buff) {
		if (!this.isEOF) {
			this.buffer += buff;
			if (this.buffer.length >= this.length) {
				this.isEOF = true;
			}
		}
	};

	TextStream.prototype.setEOF = function(isEOF) {
		this.isEOF = isEOF;
	};

	TextStream.prototype.getEOF = function() {
		return this.isEOF;
	};

	function RubyStream(kanji) {
		this.kanji = kanji;
		this.seekPos = 0;
	}
	;

	RubyStream.prototype.getchar = function() {
		if (this.seekPos < this.kanji.length) {
			var s = this.kanji.substring(this.seekPos, this.seekPos + 1);
			this.seekPos++;
			return s;
		} else {
			throw "RubyBufferEnd";
		}
	};

	function StreamParser(layout, textStream) {
		this.layout = layout;
		this.seekCharCount = 0; // real charactor count except ruby, tag,
								// newline(\n)
		this.seekTable = [ {
			spos : 0,
			cpos : 0
		} ];
		this.seekWidth = 0;
		this.seekHeight = 0;
		this.seekLineCharCount = 0;
		this.lineBuff = "";
		this.customwidth = 23;
		this.lineSave = ""; // stock value of lineBuff before parsing tag.
		this.resumePos = -1; // stock value of textStream.seekPos before
								// parsing tag.
		this.blockBuff = "";
		this.tagStack = [];
		this.rubyStack = [];
		this.pageCache = [];
		this.boutenStack = [];
		this.boutenCount = 0;
		this.bouten = false;
		this.indentCount = 0;
		this.rubyYomi = "";
		this.rubyKanji = "";
		this.rubyStream = null;
		this.packStr = "";
		this.textStream = textStream;
		this.isResuming = false;
		this.cacheAble = true;
		this.fontScale = 1;
		this.lineScale = 1;
		this.bgColor = "";
		this.fontStyle = ""; // for IE or horizontal
		this.isImgChar = false;
		this.isHankaku = false;
		this.imgBuff = [];
		this.curImgWidth = 0;
		this.imgIndentCount = 0;
		this.blockIndentCount = 0;
		this.tagFlag = [];
	}
	;

	StreamParser.prototype.activateTag = function(tag, enable) {
		var watchFlags = [ "ruby", "rp", "rb", "rt", "pack", "script" ];
		for ( var i = 0; i < watchFlags.length; i++) {
			if (tag == watchFlags[i]) {
				this[tag] = enable;
				return;
			}
		}
	};

	StreamParser.prototype.isActiveTag = function(tag) {
		if (typeof this[tag] == "undefined") {
			return false;
		}
		return this[tag];
	};

	StreamParser.prototype.getTextStream = function() {
		return this.textStream;
	};

	StreamParser.prototype.tagAttr = function(attr) {
		var ret = "";
		for (prop in attr) {
			ret += prop + "='" + attr[prop] + "' ";
		}
		return ret;
	};

	StreamParser.prototype.inlineCss = function(attr) {
		var ret = "";
		for (prop in attr) {
			ret += prop + ":" + attr[prop] + ";";
		}
		return ret;
	};

	StreamParser.prototype.tagStart = function(tag, attr, isSingle) {
		return "<" + tag + " " + this.tagAttr(attr) + (isSingle ? " />" : " >");
	};

	StreamParser.prototype.charToImg = function(c) {
		switch (c) {
		case "「":
		case "｢":
			c = "kakko1.gif";
			break;
		case "」":
		case "｣":
			c = "kakko2.gif";
			break;
		case "『":
			c = "kakko3.gif";
			break;
		case "』":
			c = "kakko4.gif";
			break;
		case "（":
		case "(":
		case "{":
			c = "kakko5.gif";
			break;
		case "）":
		case "}":
		case ")":
			c = "kakko6.gif";
			break;
		case "＜":
		case "<":
		case "〈":
			c = "kakko7.gif";
			break;
		case "＞":
		case ">":
		case "〉":
			c = "kakko8.gif";
			break;
		case "《":
		case "≪":
			c = "kakko9.gif";
			break;
		case "》":
		case "≫":
			c = "kakko10.gif";
			break;
		case "［":
		case "[":
		case "〔":
			c = "kakko13.gif";
			break;
		case "］":
		case "]":
		case "〕":
			c = "kakko14.gif";
			break;
		case "【":
			c = "kakko17.gif";
			break;
		case "】":
			c = "kakko18.gif";
			break;
		case "｡":
		case "。":
			c="。";//c = "kuten.gif";
			break;
		case "．":
		case ".":
			c = "kuten2.gif";
			break;
		case "､":
		case "、":
		
			c = "、";
			break;
		case ",":
		
			c = "touten.gif";
			break;
		case "，":
		case ",":
		c = "，";
		break;
		case "～":
		case "〜":
			c = "kara.gif";
			break;
		case "…":
			c = "mmm.gif";
			break;
		case "：":
		case ":":
			c = "tenten.gif";
			break;
		case "‥":
			c = "mm.gif";
			break;
		case "＝":
		case "=":
			c = "equal.gif";
			break;
		case "―":
			c = "dash.gif";
			break;
		case "ー":
		case "－":
		case "━":
			c = "｜";
			break;
		case "—":
		case "-":
		case "‐":
		case "─":
		case "−":
		case "_":
		case "ｰ":
			c = "｜";
			break;
		case "→":
		case "⇒":
			c = "↓";
			break;
		case "←":
			c = "↑";
			break;
		case "!":
			c = "！";
			break;
		case "?":
			c = "？";
			break;
		case "･":
			c = "・";
			break;
		case "+":
			c = "＋";
			break;
		case "@":
			c = "＠";
			break;
		case "#":
			c = "＃";
			break;
		case "\\":
			c = "￥";
			break;
		default:
			break;
		}
		return c;
	};

	StreamParser.prototype.makeCharInner = function(c1) {
		var c2 = this.charToImg(c1);

		if (c2.match(/\.gif/)) {
			this.isImgChar = true;
			var imgKey = (this.layout.charImgColor == "white") ? "w@" + c2 : c2;
			if (typeof this.layout.charImgMap[imgKey] != "undefined") {
				return this.makeCharImgTag(this.layout.charImgMap[imgKey]);
			}
		}
		if (c2 == c1 || c2.length == 1) {
			this.isImgChar = false;
			return c2;
		}
		
		this.isImgChar = true;
		
		if (this.layout.charImgColor == "white") {
			c2 = "w_" + c2;
		}
		return this
				.makeCharImgTag(Filename.concat(this.layout.charImgRoot, c2));
	};

	StreamParser.prototype.makeCharImgTag = function(imgPath) {
		var width = Math.floor(this.layout.fontSize * this.fontScale);
		var height = width;

		var css = {
			"vertical-align" : "top",
			"width" : width + "px",
			"height" : height + "px",
			"line-height" : height + "px",
			"margin" : "0",
			"padding" : "0",
			"border-width" : "0"
		};
		var attr = {
			"src" : imgPath,
			"style" : this.inlineCss(css)
		};
		return this.tagStart("img", attr, true);
	};

	StreamParser.prototype.getBoutenStr = function(tagName) {
		switch (tagName) {
		case "bt-disc":
			return "・";
		case "bt-accent":
			return "ヽ";
		case "bt-circle":
			return "。";
		case "bt-dot":
			return "・";
		}
		return "・";
	};

	StreamParser.prototype.parseAttr = function(html) {
		var tmp = html.split(/[\s\t　]+/);
		var ret = {};
		for ( var i = 0; i < tmp.length; i++)
			(function(v, i) {
				if (v.match(/([^=]+)=(.+)/)) {
					var name = RegExp.$1;
					var value = RegExp.$2;
					ret[name] = value;
				}
			})(tmp[i], i);
		return ret;
	};

	StreamParser.prototype.cutQuote = function(src) {
		return src.replace(/\"/g, "").replace(/\'/g, "");
	};

	StreamParser.prototype.unscript = function(src) {
		return src.replace(/script\:/gi, "");
	};

	StreamParser.prototype.startBgColor = function() {
		if (this.layout.direction == "vertical") {
			var yohaku = Math.floor(this.layout.yohakuHeight * this.lineScale);
			var pTB = Math.floor(yohaku / 3);
			var width = Math.floor(this.layout.baseLineHeight * this.lineScale);
			var css = {
				"text-align" : "center",
				"padding" : pTB + "px 0",
				"width" : width + "px",
				"background-color" : this.bgColor
			};
			return this.tagStart("div", {
				"style" : this.inlineCss(css)
			}, false);
		} else {
			var css = {
				"padding-top" : "0.3em",
				"padding-left" : "0.3em",
				"vertical-align" : "middle",
				"background-color" : this.bgColor
			};
			return this.tagStart("span", {
				"style" : this.inlineCss(css)
			}, false);
		}
	};

	StreamParser.prototype.endBgColor = function() {
		if (this.layout.direction == "vertical") {
			return "</div>";
		}
		return "</span>";
	};

	StreamParser.prototype.makeLineTd = function() {
		if (this.boutenCount > 0) {
			this.boutenStack.push( {
				startPos : this.boutenStartPos,
				count : this.boutenCount,
				str : this.boutenStr
			});
			this.boutenCount = 0;
			this.boutenStartPos = 0;
		}
		var bodyHeight = Math.floor(this.layout.fontSize * this.lineScale);
		var yohakuHeight = Math.floor(this.layout.yohakuHeight * this.lineScale);
		var lineHeight = bodyHeight + yohakuHeight;
		var cssBody = {
			"font-size" : this.layout.fontSize + "px",
			"margin" : "0",
			"padding" : "0",
			"text-align" : (this.lineScale > 1) ? "center" : "left",
			"line-height" : this.layout.letterHeight + "px",
			"vertical-align" : "top",
			"width" : this.customwidth + "px"
		};
		var cssRuby = {
			"margin" : "0",
			"padding" : "0",
			"text-align" : "left",
			"width" : yohakuHeight + "px",
			"vertical-align" : "top"
		};
		return (this.tagStart("td", {
			"style" : this.inlineCss(cssBody)
		}, false) + this.lineBuff + "</td>" + this.tagStart("td", {
			"style" : this.inlineCss(cssRuby)
		}, false) + this.makeRubyLine() + this.makeBoutenLine() + "</td>");
	};

	StreamParser.prototype.makeRubyLine = function() {
		var ret = "";
		var self = this;
		var css = {
			"position" : "absolute",
			"font-size" : Math.floor(this.layout.rubyFontSize * this.lineScale)
					+ "px",
			"line-height" : "1.14em"
		};
		var baseStyle = this.inlineCss(css);
		var indentOffset = this.indentCount * this.layout.letterHeight;

		for ( var i = 0; i < this.rubyStack.length; i++)
			(function(i, ruby) {
				var style = baseStyle + "margin-top:"
						+ Math.floor(indentOffset + ruby.startPos) + "px;";
				ret += self.tagStart("div", {
					"style" : style
				}, false);
				for ( var k = 0; k < ruby.yomi.length; k++)
					(function(k, y) {
						ret += self.makeCharInner(y) + "<br />";
					})(k, ruby.yomi.substring(k, k + 1));
				ret += "</div>";
			})(i, this.rubyStack[i]);

		this.rubyStack = [];
		return ret;
	};

	StreamParser.prototype.makeLineH = function() {
		var ret = "";
		if (this.rubyStack.length > 0) {
			ret += this.makeRubyLineH();
		}
		ret += "<div>";
		ret += this.lineBuff + "<br />";
		if (this.fontStyle != "") {
			ret += "</span>";
		}
		if (this.bgColor != "") {
			ret += this.endBgColor();
		}
		ret += "</div>";
		return ret;
	};

	StreamParser.prototype.makeRubyLineH = function() {
		var ret = "";
		var self = this;
		var indentOffset = this.indentCount * this.layout.letterHeight;
		var rfs = Math.floor(this.layout.rubyFontSize * this.lineScale);
		var css = {
			"font-size" : rfs + "px",
			"margin" : "0",
			"padding" : "0",
			"line-height" : rfs + "px"
		};
		ret += this.tagStart("div", {
			style : this.inlineCss(css)
		}, false);

		for ( var i = 0; i < this.rubyStack.length; i++)
			(function(i, ruby) {
				var style = "position:absolute; margin-top:-0.3em; margin-left:"
						+ Math.floor(indentOffset + ruby.startPos) + "px;";
				ret += self.tagStart("span", {
					"style" : style
				}, false);
				for ( var k = 0; k < ruby.yomi.length; k++)
					(function(k, y) {
						ret += y;
					})(k, ruby.yomi.substring(k, k + 1));
				ret += "</span>";
			})(i, this.rubyStack[i]);

		this.rubyStack = [];
		ret += "</div>";
		return ret;
	};

	StreamParser.prototype.makeBoutenLine = function() {
		var ret = "";
		var self = this;
		var css = {
			"position" : "absolute",
			"margin-left" : "-0.35em"
		};
		var baseStyle = this.inlineCss(css);

		for ( var i = 0; i < this.boutenStack.length; i++)
			(function(bouten) {
				while (bouten.count > 0) {
					if (bouten.str == "・") {
						var boutenFontSize = self.layout.fontSize;
					} else if (bouten.str == "ヽ") {
						var boutenFontSize = Math
								.floor(self.layout.fontSize * 70 / 100);
					}
					var style = baseStyle + "; font-size:" + boutenFontSize
							+ "px; margin-top:" + bouten.startPos + "px;";
					ret += self.tagStart("div", {
						"style" : style
					}, false);
					ret += bouten.str;
					ret += "</div>";
					bouten.startPos += self.layout.letterHeight;
					bouten.count--;
				}
			})(this.boutenStack[i]);

		this.boutenStack = [];
		return ret;
	};

	StreamParser.prototype.normalIndent = function(str) {
		this.isHankaku = false;
		if (this.lineScale <= 1) {
			if (str.match(/[a-z0-9]/i)) {
				this.isHankaku = true;
				var css = {
					"margin-left" : "0.2em",
					"line-height" : "1em"
				};
				var style = this.inlineCss(css);
				return (this.tagStart("span", {
					"style" : style
				}, false) + str + "</span><br />");
			}
		}
		return this.makeCharInner(str) + "<br />";
	};

	StreamParser.prototype.toBold = function(str) {
		return ("<b>" + str + "</b>");
	};

	StreamParser.prototype.toStyle = function(css) {
		return (function(str) {
			return ("<span style='" + css + "'>" + str + "</span>");
		});
	};

	StreamParser.prototype.toLink = function(href) {
		return (function(str) {
			return ("<a href='" + href + "'>" + str + "</a>");
		});
	};

	StreamParser.prototype.toLink2 = function(href) {
		return (function(str) {
			return ("<a target='_blank' href='" + href + "'>" + str + "</a>");
		});
	};

	StreamParser.prototype.isHeadNg = function(s) {
		var ngChars = [ "？", "】", "，", ",", "》", "。", "、", "・", "｣", "」", "』",
				"）", "＞", "〉", "≫", "]", "〕", "]", "］", "！", "!", ") ", "々",
				"ゝ", "ー", "－" ];
		for (i = 0; i < ngChars.length; i++) {
			if (s == ngChars[i]) {
				//return true;
				return false;
			}
		}
		return false;
	};

	StreamParser.prototype.isTailNg = function(s) {
		var ngChars = [ "【", "《", "「", "『", "（", "［", "[", "〔", "＜", "≪", "(",
				"〈" ];
		for (i = 0; i < ngChars.length; i++) {
			if (s == ngChars[i]) {
				return true;
			}
		}
		return false;
	};

	StreamParser.prototype.applyTagStack = function(str) {
		var ret = this.normalIndent(str);
		for (i = this.tagStack.length - 1; i >= 0; i--) {
			var f = this.tagStack[i];
			ret = f(ret);
		}
		return ret;
	};

	StreamParser.prototype.isValidPageRange = function(page) {
		return (0 <= page && page < this.seekTable.length);
	};

	StreamParser.prototype.setSeekPage = function(page) {
		if (this.isValidPageRange(page)) {
			this.textStream.seekPos = this.seekTable[page].spos;
			this.seekWidth = 0;
			this.seekHeight = 0;
			this.seekLineCharCount = 0;
			if (page == 0) {
				this.tagStack = [];
			}
		}
	};

	StreamParser.prototype.getPageSeekPos = function(page) {
		if (this.isValidPageRange(page)) {
			return this.seekTable[page];
		}
		return 0;
	};

	StreamParser.prototype.getSeekPercent = function(page) {
		if (page < this.seekTable.length - 1) {
			return Math.floor(100 * this.seekTable[page + 1].spos
					/ this.textStream.length);
		}
		return 100;
	};

	StreamParser.prototype.getPageSourceText = function(page) {
		if (page < this.seekTable.length) {
			var from = this.seekTable[page].spos;
			if (page + 1 < this.seekTable.length) {
				var to = this.seekTable[page + 1].spos;
				return this.textStream.buffer.substring(from, to);
			}
		}
		return "";
	};

	StreamParser.prototype.makeRestSpaceTd = function() {
		var restWidth = this.layout.width - this.seekWidth;
		var restTd = "";
		if (restWidth > 0) {
			restTd = "<td style='display:block; width:" + restWidth
					+ "px; height:" + this.layout.height + "'></td>\n";
		}
		return restTd;
	};

	StreamParser.prototype.getPageSeekPos = function(page) {
		if (this.isValidPageRange(page)) {
			return this.seekTable[page];
		}
		return {
			spos : 0,
			cpos : 0
		};
	};

	StreamParser.prototype.saveSeekState = function(page, seekData) {
		if (this.isValidPageRange(page)) {
			this.seekTable[page] = seekData;
		} else {
			this.seekTable.push(seekData);
		}
	};

	StreamParser.prototype.fixH = function(c) {
		if (c == "―") {
			return "<span style='margin-top:-0.2em; float:right'>" + c
					+ "</span>";
		}
		return c;
	};

	StreamParser.prototype.fixW = function(c) {
		if (c == "―" || c == "…") {
			return "<span style='margin-left:-0.24em'>" + c + "</span>";
		}
		return c;
	};

	StreamParser.prototype.hasNextPage = function() {
		return (this.textStream.seekPos < this.textStream.length);
	};

	StreamParser.prototype.addCache = function(page, pageHtml) {
		this.pageCache[page] = pageHtml;
	};

	StreamParser.prototype.clearCache = function() {
		this.pageCache = [];
		this.tagStack = [];
	};

	StreamParser.prototype.reset = function() {
		this.clearCache();
		this.textStream.seekPos = 0;
	};

	StreamParser.prototype.getLetterCount = function(c) {
		if (this.layout.direction == "vertical") {
			return 1;
		} else if (escape(c).charAt(1) == "u") {
			return 1;
		} else {
			return 0.5;
		}
	};

	StreamParser.prototype.addIndent = function(count) {
		var space = (this.layout.direction == "vertical") ? "　<br />" : "　";
		for ( var i = 0; i < count; i++) {
			this.lineBuff += space;
		}
	};

	StreamParser.prototype.getLayout = function() {
		return this.layout;
	};

	StreamParser.prototype.setLayout = function(layout) {
		this.layout = layout;
		this.layout.initialize();
	};

	StreamParser.prototype.getPageLayout = function(pageNo, body) {
		var t1 = "<" + this.layout.wrapTag + " class='"
				+ this.layout.textLayerClassName + "' style='"
				+ this.layout.wrapCss + "'>";
		var t2 = "</" + this.layout.wrapTag + ">";
		var pageHtml = (body != "") ? t1 + body + t2 : "";

		this.addCache(pageNo, pageHtml);

		return pageHtml;
	};

	StreamParser.prototype.outputPage = function(pageNo) {
		if (this.isResuming) {
			this.isResuming = false;
			return this.parsePage(pageNo);
		} else if (typeof this.pageCache[pageNo] != "undefined") {
			this.setSeekPage(pageNo + 1);// move to head of next page.
			return this.pageCache[pageNo];
		} else {
			this.setSeekPage(pageNo);
			return this.parsePage(pageNo);
		}
	};

	StreamParser.prototype.onOverFlowPage = function(pageNo, isV) {
		if (isV) {
			this.blockBuff = this.makeRestSpaceTd() + this.blockBuff;
		}
		var page = (isV) ? "<tr>" + this.blockBuff + "</tr>" : this.blockBuff;
		this.saveSeekState(pageNo + 1, {
			spos : this.textStream.seekPos,
			cpos : this.seekCharCount
		});
		this.blockBuff = "";
		this.lineBuff = "";

		// if bg color defined, end it.
		if (this.bgColor != "") {
			this.lineBuff += this.startBgColor();
		}
		this.lineScale = this.fontScale;

		if (isV) {
			this.seekWidth = 0;
		} else {
			this.seekHeight = 0;
		}
		this.seekLineCharCount = 0;

		return this.getPageLayout(pageNo, page);
	}; // onOverFlowPage

	StreamParser.prototype.onBufferEnd = function(pageNo, isV) {

		// stream text already end.
		if (this.textStream.isEOF) {
			if (this.lineBuff != "") {
				this.pushLine(pageNo, isV);
			}
			//return "";
			return this.onOverFlowPage(pageNo, isV); // final page.

		} else { // stream has more text data, and it's not still received.

			this.isResuming = true;

			// buffer end while parsing tag.
			if (this.resumePos >= 0) {

				// resume parsing from position of tag start.(seek position of
				// "<")
				this.textStream.seekPos = this.resumePos;
				this.lineBuff = this.lineSave;
			}

			// request next buffer to caller.
			throw "BufferEnd";
		}
	}; // onBufferEnd

	StreamParser.prototype.onRubyBufferEnd = function(pageNo, isV) {
		delete this.rubyStream;
		this.rubyStream = null;
		if (!isV) {
			this.lineBuff += "</a>";
		}
	};

	StreamParser.prototype.pushLine = function(pageNo, isV) {
		if (this.blockBuff != "" || this.lineBuff != "") {
			if (isV) {
				this.blockBuff = this.makeLineTd() + this.blockBuff;
			} else { // horizontal
				this.blockBuff += this.makeLineH();
			}
			this.lineBuff = "";
			if (this.fontStyle != "") {
				this.lineBuff += fontStyle;
			}
			if (this.bgColor != "") {
				this.lineBuff = this.startBgColor();
			}
			if (isV) {
				this.seekWidth += Math.floor(this.layout.baseLineHeight
						* this.lineScale);
				this.seekHeight = 0;
			} else {
				this.seekHeight += Math.floor(this.layout.baseLineHeight
						* this.lineScale);
				this.seekWidth = 0;
			}
			this.seekLineCharCount = 0;
			this.lineScale = this.fontScale;
		}
	}; // pushLine

	StreamParser.prototype.checkOverflow = function(isV) {
		if (isV) {
			return (this.seekWidth
					+ Math.floor(this.layout.fontSize * this.lineScale) > this.layout.width);
		}
		return (this.seekHeight
				+ Math.floor((this.layout.fontSize + this.layout.rubyFontSize)
						* this.lineScale) > this.layout.height);
	};

	StreamParser.prototype.parseEndPage = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		if (this.lineBuff != "") {
			this.pushLine(pageNo, isV);
		}
		throw "OverflowPage";
	};

	StreamParser.prototype.parseImg = function(pageNo, isV, tagStr, tagAttr,
			tagName) {

		// if current line is not empty, push as new line before image.
		if (this.lineBuff != "") {
			this.pushLine(pageNo, isV);
			if (this.checkOverflow(isV)) {
				this.textStream.seekPos = this.resumePos;
				throw "OverflowPage";
			}
		}
		var src = this.unscript(this.cutQuote(tagAttr.src));
		var imgW = (typeof tagAttr.width != "undefined") ? parseInt(this
				.cutQuote(tagAttr.width)) : 200;
		var imgH = (typeof tagAttr.height != "undefined") ? parseInt(this
				.cutQuote(tagAttr.height)) : 300;
		var imgAlign = (typeof tagAttr.align != "undefined") ? this
				.cutQuote(tagAttr.align) : "none";
		var imgW2 = imgW;
		var imgH2 = imgH;

		// adjust width.
		if (this.seekWidth + imgW > this.layout.width) {
			imgW2 = this.layout.width - this.seekWidth;
			imgH2 -= Math.floor((imgH / imgW) * (imgW - imgW2));
		}
		// adjust height.
		if (this.seekHeight + imgH > this.layout.height) {
			imgH2 = this.layout.height - this.seekHeight;
			imgW2 -= Math.floor((imgW / imgH) * (imgH - imgH2));
		}

		// if image size is half of screen size, we write it in next page.
		if (isV) {
			if (this.layout.width > imgW && this.seekWidth > 0
					&& (imgW2 * 2 < imgW || imgH2 * 2 < imgH)) {
				this.textStream.seekPos = this.resumePos;
				throw "OverflowPage";
			}
		} else {
			if (this.layout.height > imgH && this.seekHeight > 0
					&& (imgH2 * 2 < imgH || imgW2 * 2 < imgW)) {
				this.textStream.seekPos = this.resumePos;
				throw "OverflowPage";
			}
		}

		if (isV) {

			// white space height.
			var restH = this.layout.height - imgH2 - this.layout.fontSize;
			var inlinePage = "";

			// conditions
			// 1: streaming text already end.
			// 2: image aling is defined.
			// 3: white space size is over half of layout height.
			if (this.textStream.isEOF && imgAlign != "none" && restH > 0
					&& restH * 2 >= this.layout.height) {
				if (imgAlign == "top" || imgAlign == "left") {
					var imgStyle = "margin-bottom:" + this.layout.fontSize
							+ "px;";
				} else {
					var imgStyle = "margin-top:0;";
				}
				var imgTag = this.tagStart("img", {
					"src" : src,
					"width" : imgW2,
					"height" : imgH2,
					"style" : imgStyle
				}, true);

				// recursive output for white space(textStream is shared).
				var parserTmp = new StreamParser(new Layout( {
					width : imgW2,
					height : restH,
					fontSize : this.layout.fontSize,
					direction : this.layout.direction,
					charImgRoot : this.layout.charImgRoot,
					charImgMap : this.layout.charImgMap,
					charImgColor : this.layout.charImgColor
				}), this.textStream);

				if (this.layout.fontFamily) {
					parserTmp.layout.fontFamily = this.layout.fontFamily;
					parserTmp.layout.initialize();
				}

				var inlinePage = parserTmp.parsePage(0);
				delete parserTmp;
			} else {
				var imgTag = this.tagStart("img", {
					"src" : src,
					"width" : imgW2,
					"height" : imgH2
				}, true);
			}
			var tdCss = {
				"vertical-align" : "top",
				"padding-right" : this.layout.yohakuHeight + "px"
			};
			var tdBody = (imgAlign == "top" || imgAlign == "left") ? imgTag
					+ "<br />" + inlinePage : inlinePage + imgTag;
			this.blockBuff = this.tagStart("td", {
				"style" : this.inlineCss(tdCss)
			}, false) + tdBody + "</td>" + this.blockBuff;
			this.seekWidth += imgW2 + this.layout.yohakuHeight;

		} else { // horizontal

			// white space width.
			var restW = this.layout.width - imgW2 - this.layout.fontSize;
			var inlinePage = "";

			// conditions
			// 1: streaming text already end.
			// 2: image aling is defined.
			// 3: white space size is over half of layout width.
			if (this.textStream.isEOF && imgAlign != "none" && restW > 0
					&& restW * 2 >= this.layout.width) {
				var imgTag = this.tagStart("img", {
					"src" : src,
					"width" : imgW2,
					"height" : imgH2
				}, true);

				// recursive output for white space(textStream is shared).
				var parserTmp = new StreamParser(new Layout( {
					width : restW,
					height : imgH2,
					fontSize : this.layout.fontSize,
					direction : "horizontal",
					charImgRoot : this.layout.charImgRoot,
					charImgMap : this.layout.charImgMap,
					charImgColor : this.layout.charImgColor
				}), this.textStream);

				if (this.layout.fontFamily) {
					parserTmp.layout.fontFamily = this.layout.fontFamily;
					parserTmp.layout.initialize();
				}

				var inlinePage = parserTmp.parsePage(0);
				delete parserTmp;

				if (imgAlign == "top" || imgAlign == "left") {
					var leftBlock = "<div style='float:left; width:"
							+ (imgW2 + this.layout.fontSize) + "px;'>" + imgTag
							+ "</div>";
					var rightBlock = "<div style='float:left; width:" + restW
							+ "px;'>" + inlinePage + "</div>";
				} else {
					var leftBlock = "<div style='float:left; width:" + restW
							+ "px;'>" + inlinePage + "</div>";
					var rightBlock = "<div style='float:left; width:"
							+ (imgW2 + this.layout.fontSize) + "px;'>" + imgTag
							+ "</div>";
				}
				this.blockBuff += ("<div style='width:"
						+ this.layout.width
						+ "px;'>"
						+ leftBlock
						+ rightBlock
						+ "<div style='clear:left;line-height:0px;font-size:0px;'></div>" + "</div>");
				this.seekHeight += imgH2 + this.layout.yohakuHeight;

			} else {
				var imgTag = this.tagStart("img", {
					"src" : src,
					"width" : imgW2,
					"height" : imgH2
				}, true);
				this.blockBuff += imgTag + "<br />";
				this.seekHeight += imgH2 + this.layout.yohakuHeight;
			}
		}
	}; // parseImg

	StreamParser.prototype.parseLinkStart = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		var href = this.unscript(this.cutQuote(tagAttr.href));

		if (typeof tagAttr.target != "undefined") {
			var blank = (this.cutQuote(tagAttr.target) == "_blank");
		} else if (tagName == "a2") {
			var blank = true;
		} else {
			var blank = false;
		}

		if (isV) {
			if (blank) {
				this.tagStack.push(this.toLink2(href));
			} else {
				this.tagStack.push(this.toLink(href));
			}
		} else {
			if (tagName == "a") {
				this.lineBuff += tagStr;
			} else {
				this.lineBuff += "<a target='_blank' href='" + href + "'>";
			}
		}
	};

	StreamParser.prototype.parseLinkEnd = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		if (isV) {
			this.tagStack.pop();
		} else if (tagName == "/a2") {
			this.lineBuff += "</a>";
		} else {
			this.lineBuff += tagStr;
		}
	};

	StreamParser.prototype.parseBoldStart = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		if (isV) {
			this.tagStack.push(this.toBold);
		} else {
			this.lineBuff += tagStr;
		}
	};

	StreamParser.prototype.parseBoldEnd = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		if (isV) {
			this.tagStack.pop();
		} else {
			this.lineBuff += tagStr;
		}
	};

	StreamParser.prototype.parseFontStart = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		var css = {};

		this.fontScale = (typeof tagAttr.scale != "undefined") ? parseFloat(this
				.cutQuote(tagAttr.scale))
				: 1;
		if (this.fontScale < 1 || this.fontScale > 1) {
			css["font-size"] = this.fontScale + "em";
			if (isV) {
				css["line-height"] = "1.1em";
			}
		}

		// line scale follows max font scale.
		if (this.fontScale > this.lineScale) {
			this.lineScale = this.fontScale;
		}

		if (typeof tagAttr.color != "undefined") {
			css["color"] = this.cutQuote(tagAttr.color);
		}

		if (typeof tagAttr.fontfamily != "undefined") {
			css["font-family"] = this.cutQuote(tagAttr.fontfamily);
		}

		this.bgColor = (typeof tagAttr.bgcolor != "undefined") ? this
				.cutQuote(tagAttr.bgcolor) : "";
		if (this.bgColor != "") {
			this.lineBuff += this.startBgColor();
		}

		var style = this.inlineCss(css);
		if (style != "") {
			style += "vertical-align:baseline;";
			if (isV) {
				this.tagStack.push(this.toStyle(style));
			} else {
				this.fontStyle = this.tagStart("span", {
					"style" : style
				}, false);
				this.lineBuff += this.fontStyle;
			}
		}
	}; // parseFontStart

	StreamParser.prototype.parseFontEnd = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		this.fontScale = 1;

		// if parser mets "</font>" on head of line, we can't reset previous
		// lineScale forever.
		if (this.seekLineCharCount == 0) {
			this.lineScale = 1;
		}

		if (isV) {
			if (this.bgColor != "") {
				this.lineBuff += this.endBgColor(); // end bg color.
				this.bgColor = "";
			}
			this.tagStack.pop();
		} else {
			this.fontStyle = "";
			this.lineBuff += "</span>"; // end font style.
			if (this.bgColor != "") {
				this.lineBuff += this.endBgColor(); // end bg color.
				this.bgColor = "";
			}
		}
	}; // parseFontEnd

	StreamParser.prototype.parseBoutenStart = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		this.bouten = true;
		this.boutenStartPos = this.seekHeight;
		this.boutenStr = this.getBoutenStr(tagName);
	};

	StreamParser.prototype.parseBoutenEnd = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		this.bouten = false;
		this.boutenStack.push( {
			startPos : this.boutenStartPos,
			count : this.boutenCount,
			str : this.boutenStr
		});
		this.boutenCount = 0;
	};

	StreamParser.prototype.parsePackStart = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		if (isV) {
			this.packStr = "";
		}
	};

	StreamParser.prototype.parseRubyStart = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		if (isV) {
			this.rubyStartPos = this.seekHeight;
		} else {
			this.rubyStartPos = this.seekWidth;
		}
	};

	StreamParser.prototype.parseRubyEnd = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		this.rubyStream = new RubyStream(this.rubyKanji);
		this.rubyStack.push( {
			yomi : this.rubyYomi,
			startPos : this.rubyStartPos
		});
		this.rubyYomi = "";
		this.rubyKanji = "";
	};

	StreamParser.prototype.parseBlockquoteStart = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		this.blockIndentCount = (typeof tagAttr.indent != "undefined") ? parseInt(this
				.cutQuote(tagAttr.indent))
				: 2;
		this.indentCount += this.blockIndentCount;
		if (this.lineCount <= this.indentCount * 2) {
			this.activateTag("blockquote", false);
		}
		this.layout.lineCount -= this.blockIndentCount * 2;
	};

	StreamParser.prototype.parseBlockquoteEnd = function(pageNo, isV, tagStr,
			tagAttr, tagName) {
		this.indentCount -= this.blockIndentCount;
		this.layout.lineCount += this.blockIndentCount * 2;
		this.blockIndentCount = 0;
	};

	StreamParser.prototype.parseTag = function(pageNo, isV) {

		var tagStr = this.textStream.getTag();
		var tagInner = tagStr.replace("<", "").replace(">", "").replace("/>",
				"");
		var tagAttr = this.parseAttr(tagInner);
		var tagName = tagInner.split(/[\s\t]+/)[0].toLowerCase();

		this.activateTag(tagName.replace("/", ""),
				tagName.substring(0, 1) != "/");

		switch (tagName) {
		case "end-page":
			this.parseEndPage(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "img":
			this.parseImg(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "a":
		case "a2":
			this.parseLinkStart(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "/a":
		case "/a2":
			this.parseLinkEnd(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "b":
		case "strong":
			this.parseBoldStart(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "/b":
		case "/strong":
			this.parseBoldEnd(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "font":
			this.parseFontStart(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "/font":
			this.parseFontEnd(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "bt-disc":
		case "bt-accent":
		case "bt-circle":
		case "bt-dot":
			this.parseBoutenStart(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "/bt-disc":
		case "/bt-accent":
		case "/bt-circle":
		case "/bt-dot":
			this.parseBoutenEnd(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "pack":
			this.parsePackStart(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "ruby":
			this.parseRubyStart(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "/ruby":
			this.parseRubyEnd(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "blockquote":
			this.parseBlockquoteStart(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		case "/blockquote":
			this.parseBlockquoteEnd(pageNo, isV, tagStr, tagAttr, tagName);
			break;

		default:
			break;
		}
	}; // parseTag

	StreamParser.prototype.pushSemiLastChar = function(pageNo, isV) {

		// nomally, s2 is tail charactor of curent line.
		var s2 = this.textStream.checkNextChar(this.isTailNg);

		// TODO
		// when s2 is "<", we can't check tail NG.

		// if s2 is tail NG.
		if (s2 != "") {
			this.seekCharCount++;

			// end bg color.
			if (this.bgColor != "") {
				this.lineBuff += this.endBgColor();
			}

			// add new line. and push s2 to next line head.
			if (isV) {
				this.blockBuff = this.makeLineTd() + this.blockBuff;
				this.lineBuff = "";

				// now next line head, so if indent is defined, add indent.
				if (this.indentCount > 0) {
					this.addIndent(this.indentCount);
				}
				if (this.bgColor != "") {
					this.lineBuff += this.startBgColor(); // continue bg color
															// also in next
															// line.
				}
				var dw = Math
						.floor(this.layout.baseLineHeight * this.lineScale);
				this.lineBuff += this.applyTagStack(s2);
				this.seekWidth += dw;
				this.seekWidth2 += dw;
				this.seekHeight = 0;
				this.seekLineCharCount = this.getLetterCount(s2);
				this.textStream.skipCRLF();
				this.lineScale = this.fontScale;
			} else { // hotizontal
				this.blockBuff += this.makeLineH();
				this.lineBuff = "";

				// now next line head, so if indent is defined, add indent.
				if (this.indentCount > 0) {
					this.addIndent(this.indentCount);
				}
				if (this.fontStyle != "") {
					this.lineBuff += this.fontStyle;
				}
				if (this.bgColor != "") {
					this.lineBuff += this.startBgColor(); // continue bg color
															// also in next
															// line.
				}
				this.lineBuff += this.fixW(s2.str);
				this.seekHeight += Math.floor(this.layout.baseLineHeight
						* this.lineScale);
				this.seekWidth = 0;
				this.seekLineCharCount = this.getLetterCount(s2.str);
				this.textStream.skipCRLF();
				this.lineScale = this.fontScale;
			}
		}
	}; // pushSemiLastChar

	StreamParser.prototype.pushLastChar = function(pageNo, isV) {

		// nomally, s2 is head charactor of next line.
		var s2 = this.textStream.checkNextChar(this.isHeadNg);

		// if s2 is head NG...
		if (s2 != "") {
			this.seekCharCount++;
			if (this.bouten) {
				this.boutenCount++;
			}

			// add s2 to current line.
			if (isV) {
				this.lineBuff += this.applyTagStack(s2);
			} else {
				this.lineBuff += this.fixW(s2);
			}

			// check one more charactor for double tail NG. ex: 。」 or ？」
			var s3 = this.textStream.checkNextChar(this.isHeadNg);

			// double NG
			if (s3 != "") {
				this.seekCharCount++;
				if (this.bouten) {
					this.boutenCount++;
				}
				this.lineBuff += this.applyTagStack(s3);
			}
		}

		if (this.fontStyle != "") {
			this.lineBuff += "</span>";
		}
		// if bgcolor defined, end it before adding new line.
		if (this.bgColor != "") {
			this.lineBuff += this.endBgColor();
		}

		// push as new line.
		if (isV) {
			this.blockBuff = this.makeLineTd() + this.blockBuff;
		} else {
			this.blockBuff += this.makeLineH();
		}

		this.lineBuff = "";

		if (this.fontStyle != "") {
			this.lineBuff += this.fontStyle;
		}
		// if bgcolor defined, continue it also in next line.
		if (this.bgColor != "") {
			this.lineBuff += this.startBgColor();
		}

		if (isV) {
			var dw = Math.floor(this.layout.baseLineHeight * this.lineScale);
			this.seekWidth += dw;
			this.seekWidth2 += dw;
			this.seekHeight = 0;
		} else {
			this.seekHeight += Math.floor(this.layout.baseLineHeight
					* this.lineScale);
			this.seekWidth = 0;
		}

		this.seekLineCharCount = 0;
		this.lineScale = this.fontScale;
		this.textStream.skipCRLF();
	}; // pushLastChar

	StreamParser.prototype.pushChar = function(pageNo, isV, s1) {
		var letterCount = this.getLetterCount(s1); // 0.5 or 1.0
		var scaleWeight = letterCount * this.fontScale;

		// head of line, and defined indent count.
		if (this.seekLineCharCount == 0 && this.indentCount > 0) {
			this.addIndent(this.indentCount);
		}

		// add one char to current line.
		if (isV) {
			this.lineBuff += this.applyTagStack(s1);

			if (this.isImgChar) {
				this.seekHeight += Math.floor(scaleWeight
						* this.layout.fontSize);
			} else if (this.isHankaku) {
				this.seekHeight += Math.floor(scaleWeight
						* this.layout.fontSize);
			} else {
				this.seekHeight += Math.floor(scaleWeight
						* this.layout.letterHeight);
			}
		} else {
			this.lineBuff += this.fixW(s1);
			this.seekWidth += Math.floor(scaleWeight * this.layout.fontSize);
		}

		this.seekLineCharCount += scaleWeight;
		this.seekCharCount++;

		if (this.bouten) {
			this.boutenCount++;
		}

		var restCharCount = this.layout.lineCount - this.seekLineCharCount;

		if (1 <= restCharCount && restCharCount <= 1.5) {
			this.pushSemiLastChar(pageNo, isV);
		} else if (restCharCount < 1) {
			this.pushLastChar(pageNo, isV);
		}
	}; // pushChar

	StreamParser.prototype.parsePage = function(pageNo) {

		var isV = (this.layout.direction == "vertical");
		this.lineSave = "";

		while (true) {
			try {
				this.resumePos = -1;
				var prevSeekPos = this.textStream.seekPos;

				if (!this.pack && this.packStr != "") {
					var s1 = this.packStr;
					this.packStr = "";
				} else if (this.rubyStream) {
					var s1 = this.rubyStream.getchar();
				} else {
					var s1 = this.textStream.getchar();
				}

				if (s1 == "\r") { // CR
				} else if (s1 == "\n") { // LF
					this.pushLine(pageNo, isV);
				} else if (s1 == "<") { // start tag

					// stock before parsing tag
					this.lineSave = this.lineBuff;
					this.resumePos = prevSeekPos;

					// and parse tag
					this.parseTag(pageNo, isV);
				} else if (this.isActiveTag("pack")) { // now packing more than
														// 1charactor for space
														// only one charactor.
					this.packStr += s1;
				} else if (this.isActiveTag("ruby")) {
					if (this.isActiveTag("rt")) { // yomi
						this.rubyYomi += s1;
					} else if (this.isActiveTag("rb")) { // kanji
						this.rubyKanji += s1;
					}
				} else if (this.isActiveTag("script")) { // script tag is
															// ignored.
				} else {
					this.pushChar(pageNo, isV, s1);
				}
				if (this.checkOverflow(isV)) {
					throw "OverflowPage";
				}
			} catch (e) {
				if (e == "RubyBufferEnd") { // ruby kanji stream ends.
					this.onRubyBufferEnd(pageNo, isV);
				} else if (e == "OverflowPage") { // page is filled.
					return this.onOverFlowPage(pageNo, isV);
				} else if (e == "BufferEnd") { // text stream ends.
					return this.onBufferEnd(pageNo, isV);
				}
			}
		}
	}; // parsePage

	var LayoutParamParser = {
		parse : function(param) {
			var list = param.split(/[\s\t]/);
			var ret = {
				direction : "vertical",
				fontSize : 16,
				width : 400,
				height : 300,
				order : 0,
				charImgColor : "black",
				isSinglePaging : false
			};

			for ( var i = 0; i < list.length; i++) {
				var klass = list[i];
				if (klass == "lp-vertical") {
					ret.direction = "vertical";
				} else if (klass == "lp-horizontal") {
					ret.direction = "horizontal";
				} else if (klass.match(/span-([0-9]+)/)) { // blueprint.css
					ret.width = parseInt(RegExp.$1) * 40 - 10;
				} else if (klass.match(/lp-width-([0-9]+)/)) {
					ret.width = parseInt(RegExp.$1);
				} else if (klass.match(/lp-height-([0-9]+)/)) {
					ret.height = parseInt(RegExp.$1);
				} else if (klass.match(/lp-font-size-([0-9]+)/)) {
					ret.fontSize = parseInt(RegExp.$1);
				} else if (klass.match(/lp-order-([0-9]+)/)) {
					ret.order = parseInt(RegExp.$1);
				} else if (klass.match(/lp-char-img-white/)) {
					ret.charImgColor = "white";
				} else if (klass.match(/lp-single-paging/)) {
					ret.isSinglePaging = true;
				}
			}
			return ret;
		}
	};

	function LayoutGroup(groupName, grids, option) {
		this.grids = grids.sort(function(grid1, grid2) {
			return (grid1.order - grid2.order);
		});
		this.head = this.grids[0];
		var text = this.head.node.innerHTML;
		if (Env.isIE || !option.noBR) {
			text = text.replace(/<br \/>/gi, "\n").replace(/<br>/gi, "\n");
		}
		this.fontFamily = option.fontFamily;
		this.onSeek = option.onSeek;
		this.onComplete = option.onComplete;
		this.charImgRoot = option.charImgRoot;
		this.groupName = groupName;
		this.gridIndex = 0;
		this.parser = new StreamParser(new Layout( {
			direction : this.head.direction,
			width : this.head.width,
			height : this.head.height,
			fontSize : this.head.fontSize,
			fontFamily : this.fontFamily,
			kinsokuCharCount : 1,
			letterSpacingRate : 0.1,
			charImgRoot : this.charImgRoot,
			charImgColor : this.head.charImgColor
		}), new TextStream(text, text.length, true));

		if (this.head.isSinglePaging) {
			if (document.getElementById(groupName + "-pager")) {
				var target = document.getElementById(groupName + "-pager");
				var links = target.getElementsByTagName("a");
				var self = this;
				for ( var i = 0; i < links.length; i++)
					(function(a) {
						if (a.className.match(/next/)) {
							a.onclick = function() {
								if (self.parser.hasNextPage()) {
									self.gridIndex++;
									self.render(self.gridIndex);
								}
							}
						} else if (a.className.match(/prev/)) {
							a.onclick = function() {
								if (self.gridIndex > 0) {
									self.gridIndex--;
									self.render(self.gridIndex);
								}
							}
						}
					})(links[i]);
			}
		}
	}
	;

	LayoutGroup.prototype.setGridLayout = function(grid) {
		var lay = this.parser.layout;
		lay.setDirection(grid.direction);
		lay.setWidth(grid.width);
		lay.setHeight(grid.height);
		lay.setFontSize(grid.fontSize);
		lay.setFontFamily(this.fontFamily);
		lay.setCharImgColor(grid.charImgColor);
		lay.setCharImgRoot(this.charImgRoot);
		lay.initialize();
	};

	LayoutGroup.prototype.getGrid = function(gridIndex) {
		return (gridIndex < this.grids.length) ? this.grids[gridIndex]
				: this.grids[this.grids.length - 1];
	};

	LayoutGroup.prototype.render = function(gridIndex) {
		this.gridIndex = gridIndex;
		var grid = (this.head.isSinglePaging) ? this.head : this
				.getGrid(gridIndex);

		this.setGridLayout(grid);

		var output = this.parser.outputPage(gridIndex);

		this.onSeek(this.groupName, this.parser.getSeekPercent(gridIndex));

		if (output != "") {
			if (gridIndex < this.grids.length || this.head.isSinglePaging) {
				grid.node.innerHTML = output;
			} else {
				var node = document.createElement("div");
				node.innerHTML = output;
				grid.node.appendChild(node);
			}
		}

		if (!this.parser.hasNextPage()) {
			this.onComplete(this.groupName);
			LayoutMapper.setFinish(this.groupName);
		} else if (!this.head.isSinglePaging) {
			var self = this;
			setTimeout(function() {
				self.render(gridIndex + 1);
			}, 0);
		}
	};

	var LayoutMapper = {
		setFinish : function(groupName) {
			this.finishFlag[groupName] = true;

			if (this.checkCompleteAll()) {
				this.onCompleteAll();
			}
		},
		checkCompleteAll : function() {
			for ( var prop in this.finishFlag) {
				if (!this.finishFlag[prop]) {
					return false;
				}
			}
			return true;
		},
		start : function(tagGroup, option) {
			var defopt = {
				filter : "direction",
				noBR : false, // nomally, new line is <br>, but sometimes it's
								// \n(when <pre> is used).
				charImgRoot : "/img/char",
				fontFamily : "IPA明朝, ＭＳ 明朝, Hiragino Mincho Pro",
				onSeek : function(groupName, percent) {
				}, // seek each group
				onComplete : function(groupName) {
				}, // complete each group
				onCompleteAll : function() {
				} // complete all group
			};
			if (typeof option == "undefined") {
				option = {};
			}
			for ( var prop in defopt) {
				if (prop == "onCompleteAll") {
					this[prop] = (typeof option[prop] != "undefined") ? option[prop]
							: defopt[prop];
				} else {
					option[prop] = (typeof option[prop] != "undefined") ? option[prop]
							: defopt[prop];
				}
			}
			var nodes = document.getElementsByTagName(tagGroup);
			var createGridInfo = function(node, groupName, gridParam) {
				var gridInfo = LayoutParamParser.parse(gridParam);
				gridInfo.node = node;
				gridInfo.tagGroup = tagGroup;
				return gridInfo;
			};

			var grids = {};
			this.finishFlag = {};

			// gather layout by filter type
			for ( var i = 0; i < nodes.length; i++) {
				var node = nodes[i];
				var matched = false;

				if (option.filter == "group") { // filter by group
					if (node.className.match(/lp-group-([a-zA-Z0-9\-_]+)/)) {
						var groupName = RegExp.$1;
						matched = true;
					}
				} else if (option.filter == "direction") { // filter by writing
															// direction
					if (node.className.match(/lp-vertical/)) {
						var groupName = "group-v" + i;
						matched = true;
					} else if (node.className.match(/lp-horizontal/)) {
						var groupName = "group-h" + i;
						matched = true;
					}
				}

				if (matched) {
					var gridParam = node.className;
					var gridInfo = createGridInfo(node, groupName, gridParam);

					if (typeof grids[groupName] == "undefined") {
						this.finishFlag[groupName] = false;
						grids[groupName] = [ gridInfo ];
					} else {
						grids[groupName].push(gridInfo);
					}
				}
			}

			// render all layout group
			for ( var groupName in grids) {
				(new LayoutGroup(groupName, grids[groupName], option))
						.render(0);
			}
		}
	};

	// namespace
	Nehan.Env = Env;
	Nehan.Layout = Layout;
	Nehan.TextStream = TextStream;
	Nehan.StreamParser = StreamParser;
	Nehan.LayoutMapper = LayoutMapper;

})();
