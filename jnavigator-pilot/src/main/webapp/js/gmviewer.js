
var positionUrl = '/pilot/json/position';
var waypointsUrl = '/pilot/json/waypoints';
var name = "pilot";

var map;
var marker = null;
var position = null;
var zoomLevel = null;

function updateMap() {

	if (marker && !position) {
		map.removeOverlay(marker);
	}

	if (position) {
		var point = new GLatLng(position.latitude, position.longitude);
		if (marker) {
			marker.setLatLng(point);
		} else {
//			var myIcon = new GIcon("http://localhost:8080/pilot/img/markers/red_marker.png");
//			marker = new GMarker(point, {title: name, icon: myIcon});
			marker = new GMarker(point);
//			marker.setImage("http://localhost:8080/pilot/img/markers/white_marker.png");
			map.addOverlay(marker);
			alert("myIcon " + myIcon.image);
		}
		map.setCenter(point, zoomLevel);
	}
}

function resizeWindow() {
	var canvas = document.getElementById("map_canvas");
	var newWidth = window.innerWidth + "px";
	var newHeight = window.innerHeight + "px";
	if (canvas.style.width != newWidth || canvas.style.height != newHeight) {
		canvas.style.width = window.innerWidth + "px";
		canvas.style.height = window.innerHeight + "px";
	}
}

function onLoad() {
	if (!GBrowserIsCompatible())
		return;

	resizeWindow();
	
	map = new GMap2(document.getElementById("map_canvas"));
	map.addControl(new GLargeMapControl());
	
	var center = 0;
	var mapType = '';
	var elems = document.getElementsByTagName("input");
	for (var k=0; k < elems.length; k++) {
		if (elems[k].name.match (/center/))
			center = elems[k].value;
		if (elems[k].name.match (/zoomLevel/))
			zoomLevel = elems[k].value;
		if (elems[k].name.match (/mapType/))
			mapType = elems[k].value;
	}
	
	if (center) {
		var a = center.evalJSON();
		center = new GLatLng(a.y, a.x);
	} else {
		center = new GLatLng(47.821881, 13.040328);
	}
	
	if (zoomLevel)
		zoomLevel = zoomLevel.evalJSON();
	else
		zoomLevel = 17;
	
	var mapTypeName = mapType ? mapType.evalJSON() : '';
	if (mapTypeName == "Hybrid")
		map.setMapType(G_HYBRID_MAP);
	else if (mapTypeName == "Terrain")
		map.setMapType(G_PHYSICAL_MAP);
	else if (mapTypeName == "Satellite")
		map.setMapType(G_SATELLITE_MAP);
	else
		map.setMapType(G_HYBRID_MAP);
//		map.setMapType(G_NORMAL_MAP);
	
	map.setCenter(center, zoomLevel);
	map.setUIToDefault();
	
	new PeriodicalExecuter(
		function() {
			new Ajax.Request(positionUrl,
			  {
			    method:'get',
			    onSuccess: function(transport){
			      position = transport.responseText.evalJSON();
			      resizeWindow();
			      updateMap();
			    },
//			    onFailure: function(){ alert('Something went wrong...') }
			  });
		},
	1);
}







