#parse( "html/prologue.vm" )

<div id="wrapper">
#parse( "html/header.vm" )

	<div id="page" class="container">
#parse( "html/config/config.vm" )
#parse( "html/config/sidebar.vm" )
	</div>
	<!-- end #page -->
</div>

#include( "html/epilogue.vm" )