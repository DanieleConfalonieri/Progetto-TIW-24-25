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
	        pageOrchestrator.refresh();
	    } // display initial content
	}, false);
	
	function PageOrchestrator(){
		
		this.start = function(){
			document.getElementById("id_nomeDocente").textContent = sessionStorage.getItem('name');
			courseList = new CourseList(document.getElementById("id_coursealert"), document.getElementById("id_courselistcontainer"), document.getElementById("id_courselistcontainerbody"));
			examList = new ExamList(document.getElementById("id_examalert")), document.getElementById("id_examlistcontainer"), document.getElementById("id_examlistcontainerbody");
		}
		
		this.refresh = function(){
			courseList.reset();
			examList.reset();
			courseList.show();
		}
	}
	
	function CourseList(alert, listcontainer, listcontainerbody){ // costruttore della lista dei corsi
		this.alert = alert;
		this.listcontainer = listcontainer;
		this.listcontainerbody = listcontainerbody;
		
		this.reset = function(){
			this.listcontainer.style.visibility = "hidden";
		}
		
		this.show = function(){ // deve effettuare la chiamata al server e chiamare update()
			var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
			makeCall("GET", "GoToHomeTeacher", null, function(req){
				// funzione di callback all'evento di GET della lista dei corsi - è la funzione onreadystatechange della richiesta per come è definita makeCall
				if(req.readyState === 4){ // richiesta evasa
					var message = req.responseText;
					if(req.status === 200){ // OK
						var courses = JSON.parse(req.responseText);
						if(courses.length === 0){
							self.alert.textContent = "Nessun corso da vedere.";
							return;
						}
						self.update(courses);
					} else if(req.status === 403){
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
					} else {
						self.alert.textContent = message;
					}
				}
			});
		}
		
		this.update = function(coursesList){
			var row, code, name, link, anchor;
			this.listcontainerbody.innerHTML = "";
			var self = this;
			coursesList.forEach(function(course){ // costruisce la tabella in base alla lista dei corsi
				row = document.createElement("tr");
				code = document.createElement("td");
				code.textContent = course.courseCode;
				row.appendChild(code);
				name = document.createElement("td");
				name.textContent = course.name;
				row.appendChild(name);
				link = document.createElement("td");
				anchor = document.createElement("a");
				anchor.href = "#";
				linkText = document.createTextNode("Vedi corso");
				anchor.appendChild(linkText);
				link.appendChild(anchor);
				row.appendChild(link);
				anchor.setAttribute('courseCode', course.courseCode);
				anchor.addEventListener("click", (e) => {
					examsList.show(e.target.getAttribute("courseCode"));
				}, false);
				self.listcontainerbody.appendChild(row);
			});
			self.listcontainer.style.visibility = "visible";
		}
	}
	
	function ExamList(alert, listcontainer, listcontainerbody){ // costruttore della lista degli appelli
		this.alert = alert;
		this.listcontainer = listcontainer;
		this.listcontainerbody = listcontainerbody;
				
		this.reset = function(){
			this.listcontainer.style.visibility = "hidden";
		}
		
		this.show = function(courseCode){ // deve effettuare la chiamata al server e chiamare update()
			var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
			makeCall("GET", "GoToExamsTeacher", null, function(req){
				// funzione di callback all'evento di GET della lista dei corsi - è la funzione onreadystatechange della richiesta per come è definita makeCall
				if(req.readyState === 4){ // richiesta evasa
					var message = req.responseText;
					if(req.status === 200){ // OK
						var exams = JSON.parse(req.responseText);
						if(courses.length === 0){
							self.alert.textContent = "Nessun appello da vedere.";
							return;
						}
						self.update(exams);
					} else if(req.status === 403){
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
					} else {
						self.alert.textContent = message;
					}
				}
			});
		}
	}
}