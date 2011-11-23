#parse( "html/prologue.vm" )

<div id="wrapper">
#parse( "html/header.vm" )

	<div id="page" class="container">
#parse( "html/course/course.vm" )
#parse( "html/course/sidebar.vm" )
	</div>
	<!-- end #page -->
</div>

#include( "html/epilogue.vm" )