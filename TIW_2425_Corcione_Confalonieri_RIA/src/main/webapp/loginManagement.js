/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("loginbutton").addEventListener('click', (e) => {
	e.preventDefault();
    const form = document.getElementById("login-form");
    if (form.checkValidity()) { // parametri ok
	    makeCall("POST", 'CheckLogin', form, // chiamata asincrona al server, ha metodo, URL, event target e funzione di callback
        function(x) {
		  if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
				var partsArray = message.split(' ');
            	sessionStorage.setItem('id', partsArray[1]);
				sessionStorage.setItem('name', partsArray[2]);
				if(partsArray[0] === "docente"){
                	window.location.href = "HomeDocente.html";
				} else if(partsArray[0] === "studente"){
					window.location.href = "HomeStudente.html";
				}
                break;
              case 400: // bad request
                document.getElementById("errormessage").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("errormessage").textContent = message;
                  break;
              case 502: // bad gateway
            	document.getElementById("errormessage").textContent = message;
                break;
            }
			form.reset();
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();