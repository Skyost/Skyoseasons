var seasons = ['spring', 'summer', 'autumn', 'winter'];
var season = seasons[Math.floor(Math.random() * seasons.length)];

$(document).ready(function() {
	$('#banner img').attr('src', 'assets/img/' + season + '/banner.png');
	$('#season img').attr('src', 'assets/img/' + season + '/season.png');
	var body = $('body');
	body.css('background', 'url(\'assets/img/' + season + '/background.png\') no-repeat center center fixed');
	body.css('-webkit-background-size', 'cover');
	body.css('-moz-background-size', 'cover');
	body.css('-o-background-size', 'cover');
	body.css('background-size', 'cover');
	$('img').tooltip();
});