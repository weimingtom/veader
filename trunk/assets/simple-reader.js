var SimpleReader = {
  start : function(opt){
    if(typeof opt == "undefined"){
      opt = {};
	//    winW = window.innerWidth;
 // winH = window.innerHeight;
    }
    var defopt = {
      direction: "vertical",
      width: window.innerWidth/1-5,
      height: window.innerHeight/1+20,
      fontSize: 18,
      fontFamily: "IPA明朝, ＭＳ 明朝, Hiragino Mincho Pro",
      charImgRoot:"./img",
	  color:"#000000"
    };
    for(var prop in defopt){
      this[prop] = (opt[prop])? opt[prop] : defopt[prop];
    }
    var self = this;
    var text = document.getElementById("source-text").innerHTML.replace(/<br \/>/gi, "\n").replace(/<br>/gi, "\n");
    this.pageNo = 0;
    this.writing = false;
    this.parser = new Nehan.StreamParser(new Nehan.Layout({
      direction: this.direction,
      width: this.width,
      height: this.height,
      fontSize: this.fontSize,
      fontFamily: this.fontFamily,
      charImgRoot: this.charImgRoot,
	  color:this.color
    }), new Nehan.TextStream(text, text.length, true));

    document.getElementById("pager-next").onclick = function(){ self.next(); return false; };
    document.getElementById("pager-prev").onclick = function(){ self.prev(); return false; };

    this.write(0);
	pageCount=0
	while(this.parser.hasNextPage()){
			var output = this.parser.parsePage(pageCount);
			//var percentage = this.parser.getSeekPercent(pageCount);
			pageCount++;
		}

		this.totalPage = (pageCount==0)?1:pageCount;
	  this.parser.reset();
	//alert(totalPage);
	//alert(this.totalPage);
  },
  totalPage:0,
  write : function(pageNo){
    this.writing = true;
    var output = this.parser.outputPage(pageNo);
    if(output != ""){
      document.getElementById("result").innerHTML = output;
    }
    this.writing = false;
  },
  next : function(step){
    if(!this.writing && this.parser.hasNextPage()){
	
      this.pageNo=this.pageNo+(step/1);
	if(this.pageNo>=this.totalPage)this.pageNo = this.totalPage-1;  
      this.write(this.pageNo);
	this._alert();
    }else
	{
		      _page = this.pageNo/1+1
   alert('pagecount:'+_page+'/'+SimpleReader.totalPage);
   alert('percent:'+ this.parser.getSeekPercent(this.pageNo));
	}
	  
  },
   goto : function(_pageno){
    if(!this.writing && this.parser.hasNextPage()){
	
      //this.pageNo=this.pageNo+(step/1);
	  this.pageNo = _pageno/1;
	if(this.pageNo>=this.totalPage)this.pageNo = this.totalPage-1;  
      this.write(_pageno);
	this._alert();
    }else
	{
		      _page = _pageno/1
   alert('pagecount:'+_page+'/'+SimpleReader.totalPage);
   alert('percent:'+ this.parser.getSeekPercent(this.pageNo));
	}
	  
  },
  prev : function(step){
    if(!this.writing && this.pageNo > 0){
	if((this.pageNo-(step/1))>=0){
     this.pageNo= this.pageNo-(step/1);
	 }
	   else
	  { this.pageNo=0; 
	  }
      this.write(this.pageNo);
	 this._alert();
    }
	 else
	 {
	    alert('pagecount:0');
   alert('percent:'+ this.parser.getSeekPercent(this.pageNo));
	 }
  },
  _alert:function(){
  _page = this.pageNo/1+1
   alert('pagecount:'+_page+'/'+SimpleReader.totalPage);
   alert('percent:'+ this.parser.getSeekPercent(this.pageNo));
  }
}
