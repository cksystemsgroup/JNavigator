<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="keywords" content="" />
<meta name="description" content="" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>ESE CPCC Google Maps Viewer</title>
<link href="$contextPath/css/style.css" rel="stylesheet" type="text/css" media="screen" />
<script src="http://maps.google.com/maps?file=api&amp;amp;v=2&amp;amp;sensor=false&amp;amp;key=ABQIAAAApGUYTzlXwtAjho0_wwtmART2yXp_ZAY8_ufC3CFXhHIE1NvwkxRHls80HZn5Q3wFin31SltZmLZlEQ" type="text/javascript"></script>
<script src="js/gmviewer.js" type="text/javascript"></script>
<!--
<script src="js/raphael-min.js" type="text/javascript"></script>
-->
<script src="js/prototype.js" type="text/javascript"></script>
</head>
<body>

<div id="map_canvas"></div>

<script type="text/javascript">
onLoad(); Event.observe(window, 'unload', GUnload());
</script>

</body>
</html>