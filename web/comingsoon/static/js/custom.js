$(window).load(function() {
   //$('#body-container2').vAlign(); 
});

$(window).resize(function() {
    //$('#body-container2').vAlign();
});

$(document).ready(function() {
    Cufon.replace('#footer h3');
});

(function ($) {
	$.fn.vAlign = function() {
		return this.each(function(i){
			var ah = $(this).height();
			var ph = $(this).parent().height()-500;
			var ph = $(this).parent().height()-75;
			var mh = Math.ceil((ph-ah) / 2);
			$(this).css('margin-top', mh);
		});
	};
})(jQuery);