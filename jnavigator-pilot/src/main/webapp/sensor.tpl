#parse( "html/prologue.vm" )

<div id="wrapper">
#parse( "html/header.vm" )

	<div id="page" class="container">
#parse( "html/sensor/sensor.vm" )
#parse( "html/sidebar.vm" )
	</div>
	<!-- end #page -->
</div>

#include( "html/epilogue.vm" )