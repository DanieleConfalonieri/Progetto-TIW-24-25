/**
 * AJAX call management
 */

	function makeCall(method, url, formElement, cback) {
	    var req = new XMLHttpRequest(); // visible by closure
	    req.onreadystatechange = function() {
	      cback(req);
	    }; // closure
	    req.open(method, url, true);
	    if (formElement == null) {
	      req.send();
	    } else {
			const formData = new FormData(formElement);
			const urlParams = new URLSearchParams();
			for (const [key,value] of formData.entries()) {
		      urlParams.append(key, value);
		    }
			const body = urlParams.toString(); // "username=...&pwd=...&role=..."
			req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			req.send(body);
	    }
	  }
