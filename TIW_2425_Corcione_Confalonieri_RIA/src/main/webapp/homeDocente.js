/**
 * script JavaScript che gestisce la home page del docente.
 * Metodi utili ai componenti: reset(), show(), update(), autoclick() per generare pseudo-eventi
 */

{
	let pageOrchestrator = new PageOrchestrator(); // main controller
	
	window.addEventListener("load", (e) => {
		if (sessionStorage.getItem("id") == null) {
	    	window.location.href = "index.html";
	    } else {
	        pageOrchestrator.start(); // initialize the components
	        //pageOrchestrator.refresh();
	    } // display initial content
	}, false);
	
	function PageOrchestrator(){
		this.start = function(){
			document.getElementById("id_nomeDocente").textContent = sessionStorage.getItem('name');
		}
	}
}