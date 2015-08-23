var seasons = ['spring', 'summer', 'autumn', 'winter'];
var season = seasons[Math.floor(Math.random() * seasons.length)];

var images = $('img');
var imageCount = images.length;

$(document).ready(function() {
	snowStorm.stop();
	$('#background img').attr('src', 'assets/img/' + season + '/background.png');
	$('#banner img').attr('src', 'assets/img/' + season + '/banner.png');
	$('#season img').attr('src', 'assets/img/' + season + '/season.png');
	$('.link img').miniTip({
		anchor: $(this).attr('data-placement')
	});
	if(season == 'winter') {
		initSnowStorm(96, '#FFFFFF', 2, 1);
	}
	else if(season == 'spring') {
		initSnowStorm(17, '#FF85F2', 0.5, 0.5);
	}
	images.each(function() {
		if(this.complete) {
			loadPage();
		}
		else {
            $(this).one('load', loadPage);
        }
	});
	/*var body = $('body');
	body.css('background', 'url(\'assets/img/' + season + '/background.png\') no-repeat center center fixed');
	body.css('-webkit-background-size', 'cover');
	body.css('-moz-background-size', 'cover');
	body.css('-o-background-size', 'cover');
	body.css('background-size', 'cover');*/
});

function loadPage() {
	if(--imageCount == 0) {
		var loader = $('#loader');
		loader.fadeOut(1000, function() {
			loader.remove();
		});
	}
}

function initSnowStorm(flakesMaxActive, snowColor, vMaxX, vMaxY) {
	snowStorm.autoStart = false;
	snowStorm.snowStick = false;
	snowStorm.snowCharacter = '■';
	snowStorm.useTwinkleEffect = true;
	snowStorm.flakesMaxActive = flakesMaxActive;
	snowStorm.snowColor = snowColor;
	snowStorm.vMaxX = vMaxX;
	snowStorm.vMaxY = vMaxY;
	snowStorm.resume();
}