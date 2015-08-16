var seasons = ['spring', 'summer', 'autumn', 'winter'];
var season = seasons[Math.floor(Math.random() * seasons.length)];

$(document).ready(function() {
	snowStorm.stop();
	snowStorm.autoStart = false;
	snowStorm.snowStick = false;
	snowStorm.snowCharacter = '▪';
	snowStorm.useTwinkleEffect = true;
	$('#background img').attr('src', 'assets/img/' + season + '/background.png');
	$('#banner img').attr('src', 'assets/img/' + season + '/banner.png');
	$('#season img').attr('src', 'assets/img/' + season + '/season.png');
	if(season == 'winter') {
		snowStorm.flakesMaxActive = 96;
		snowStorm.snowColor = '#FFFFFF';
		snowStorm.resume();
	}
	else if(season == 'spring') {
		snowStorm.flakesMaxActive = 20;
		snowStorm.snowColor = '#FF85F2';
		snowStorm.resume();
	}
	/*var body = $('body');
	body.css('background', 'url(\'assets/img/' + season + '/background.png\') no-repeat center center fixed');
	body.css('-webkit-background-size', 'cover');
	body.css('-moz-background-size', 'cover');
	body.css('-o-background-size', 'cover');
	body.css('background-size', 'cover');*/
	$('img').tooltip();
});