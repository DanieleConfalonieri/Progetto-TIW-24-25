/**
 * script JavaScript che gestisce la home page dello studente.
 * Metodi utili ai componenti: reset(), show(), update(), autoclick() per generare pseudo-eventi
 */

{
	let pageOrchestrator = new PageOrchestrator(); // main controller
	let courseList, examList, examResult, popUp, bin, logout;
	
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
			document.getElementById("id_nomeStudente").textContent = sessionStorage.getItem('name');
			courseList = new Courses(document.getElementById("id_coursealert"), document.getElementById("id_courselistcontainer"), document.getElementById("id_courselistcontainerbody"));
			examList = new Exams(document.getElementById("id_examalert"), document.getElementById("id_examlistcontainer"), document.getElementById("id_examlistcontainerbody"));
			examResult = new ExamResult(document.getElementById("id_examresultalert"), document.getElementById("id_examresultcontainer"), document.getElementById("id_examresultcontainerbody"));
			bin = new Bin(document.getElementById("bin"));   
			popUp = new PopUp(document.getElementById("id_modal"), document.getElementById("id_conferma"), document.getElementById("id_annulla"));
		}
		this.refresh = function(){
			courseList.reset();
			examList.reset();
			examResult.reset();
			popUp.reset();
			courseList.show();
		}
	}
	function Courses(alert, listcontainer, listcontainerbody){ // costruttore della lista dei corsi
			this.alert = alert;
			this.listcontainer = listcontainer;
			this.listcontainerbody = listcontainerbody;
			
			this.reset = function(){
				this.listcontainer.style.visibility = "hidden";
			}
			
			this.show = function(){ // deve effettuare la chiamata al server e chiamare update()
				var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
				makeCall("GET", "GoToHomeStudent", null, function(req){
					// funzione di callback all'evento di GET della lista dei corsi - è la funzione onreadystatechange della richiesta per come è definita makeCall
					if(req.readyState === 4){ // richiesta evasa
						var message = req.responseText;
						if(req.status === 200){ // OK
							var courses = JSON.parse(req.responseText);
							if(courses.length === 0){
								self.listcontainer.style.visibility = "hidden";
								self.alert.textContent = "Nessun corso da vedere.";
								return;
							}
							self.update(courses);
						} else if(req.status === 403 || req.status === 401){
							window.location.href = "index.html"
							window.sessionStorage.removeItem('id');
							window.sessionStorage.removeItem('name');
							window.alert(message);
						} else {
							window.alert(message);
						}
					}
				});
			}
			
			this.update = function(coursesList){
				this.alert.textContent = "";
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
						examResult.reset();
						examList.show(e.target.getAttribute("courseCode"));
					}, false);
					self.listcontainerbody.appendChild(row);
				});
				self.listcontainer.style.visibility = "visible";
			}
		}
	function Exams(alert, listcontainer, listcontainerbody){ // costruttore della lista degli appelli
		this.alert = alert;
		this.listcontainer = listcontainer;
		this.listcontainerbody = listcontainerbody;
											
		this.reset = function(){
		this.listcontainer.style.visibility = "hidden";
		}
				
		this.show = function(courseCode){
			var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
			makeCall("GET", "GoToExamsStudent?courseId="+courseCode, null, function(req){
			// funzione di callback all'evento di GET della lista degli appelli - è la funzione onreadystatechange della richiesta per come è definita makeCall
			if(req.readyState === 4){ // richiesta evasa
				var message = req.responseText;
				if(req.status === 200){ // OK
					var exams = JSON.parse(req.responseText);
					if(exams.length === 0){
						self.listcontainer.style.visibility = "hidden";
						self.alert.textContent = "Nessun appello da vedere.";
						return;
					}
					self.update(exams, courseCode);
				} else if(req.status === 403 || req.status === 401){
					window.location.href = "index.html"
					window.sessionStorage.removeItem('id');
					window.sessionStorage.removeItem('name');
					window.alert(message);
				} else {
					window.alert(message);
				}
			}
			});
		}
									
		this.update = function(examList, courseCode){
			this.alert.textContent = "";
			var row, code, date, course, link, anchor;
			this.listcontainerbody.innerHTML = "";
			var self = this;
			examList.forEach(function(exam){
				row = document.createElement("tr");
				code = document.createElement("td");
				code.textContent = exam.examCode;
				row.appendChild(code);
				date = document.createElement("td");
				date.textContent = exam.date;
				row.appendChild(date);
				course = document.createElement("td");
				course.textContent = courseCode;
				row.appendChild(course);
				link = document.createElement("td");
				anchor = document.createElement("a");
				anchor.href = "#";
				linkText = document.createTextNode("Vedi Esito");
				anchor.appendChild(linkText);
				link.appendChild(anchor);
				row.appendChild(link);
				anchor.setAttribute('examCode', exam.examCode);
				anchor.setAttribute('courseCode', courseCode);
				anchor.addEventListener("click", (e) => {
					examResult.show(e.target.getAttribute("examCode"), e.target.getAttribute("courseCode"));
				}, false);	
			self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";
		}	
	}
	function ExamResult(alert, listcontainer, listcontainerbody){
		this.alert = alert;
		this.listcontainer = listcontainer;
		this.listcontainerbody = listcontainerbody;
											
		this.reset = function(){
			this.listcontainer.style.visibility = "hidden";
			this.listcontainerbody.innerHTML = "";
			this.alert.textContent = "";
		}
				
		this.show = function(examCode,courseCode){
			var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
			makeCall("GET", "GoToDetailExam?courseId=" + courseCode + "&examId=" + examCode, null, function(req){
			// funzione di callback all'evento di GET della lista degli appelli - è la funzione onreadystatechange della richiesta per come è definita makeCall
			if(req.readyState === 4){ // richiesta evasa
				var message = req.responseText;
				if(req.status === 200){ // OK
					var examResult = JSON.parse(req.responseText);
					if(examResult.length === 0 || (examResult.statusGrade != "PUBBLICATO" && examResult.statusGrade != "RIFIUTATO")){
						self.reset();
						self.listcontainer.style.visibility = "hidden";
						self.alert.textContent = "Voto non disponibile";
					}
					else
						self.update(examResult, courseCode, examCode);
				} else if(req.status === 403 || req.status === 401){
					window.location.href = "index.html"
					window.sessionStorage.removeItem('id');
					window.sessionStorage.removeItem('name');
					window.alert(message);
				} else {
				window.alert(message);
				}
			}
			});
		}
									
		this.update = function(reg, courseCode, examCode){
			this.alert.textContent = "";
			var row, studentCode, surname, name, email, degree, grade, statusGrade, report;
			this.listcontainerbody.innerHTML = "";
			var self = this;
			row = document.createElement("tr");
			row.setAttribute("draggable", "true");
			studentCode = document.createElement("td");
			studentCode.textContent = reg.student.id;
			row.appendChild(studentCode);
			surname = document.createElement("td");
			surname.textContent = reg.student.surname;
			row.appendChild(surname);
			name = document.createElement("td");
			name.textContent = reg.student.name;
			row.appendChild(name);
			email = document.createElement("td");
			email.textContent = reg.student.email;
			row.appendChild(email);
			degree = document.createElement("td");
			degree.textContent = reg.student.degree;
			row.appendChild(degree);
			grade = document.createElement("td");
			grade.textContent = reg.grade ? reg.grade : "-";
			row.appendChild(grade);
			statusGrade = document.createElement("td");
			statusGrade.textContent = reg.statusGrade === "NON_INSERITO" ? "NON INSERITO" : reg.statusGrade;
			row.appendChild(statusGrade);
			report = document.createElement("td");
			report.textContent = reg.reportCode ? reg.reportCode : "-";
			row.appendChild(report);
			self.listcontainerbody.appendChild(row);
			this.listcontainer.style.visibility = "visible";
			this.listcontainerbody.style.visibility = "visible";
			row.addEventListener("dragstart",(e) => {
	            const dataToTransfer = {
	                courseCode: courseCode,
	                examCode: examCode,
	                reg: reg
	           	};
	            e.dataTransfer.setData('application/json', JSON.stringify(dataToTransfer));
		            
		        setTimeout(() => {
		           e.target.style.visibility = "hidden";
		        }, 0);
	        }, false );

	        row.addEventListener("dragend", (e) => {
	            if (e.dataTransfer.dropEffect === 'none') {
	                e.target.style.visibility = "visible";
	            }
	        });
		}	
	}
	function Bin(id_bin){
		this.bin = id_bin
		this.bin.addEventListener("dragover", (e) => {
		        e.preventDefault(); 
		    }, false);

	    this.bin.addEventListener("drop", (e) => {
	        e.preventDefault();
	        const dataString = e.dataTransfer.getData("application/json");
	        if (dataString) {
	            popUp.show(dataString);
	        }
	    }, false);
	}
	
	function PopUp(window, confirm, close){
		this.window = window;
		this.confirm = confirm;
		this.close = close;
		this.currentExamData = null;
		
		this.confirm.addEventListener("click", (e) => {
			if (!this.currentExamData) return;
			var self = this;
			makeCall("GET", "RefuseGrade?courseId=" + this.currentExamData.courseCode + "&examId=" + this.currentExamData.examCode, null, function(req){
				if(req.readyState === 4){ // richiesta evasa
					var message = req.responseText;
					if(req.status === 200){ // OK
						var exRes = JSON.parse(req.responseText);
						if(exRes.length === 0 || (exRes.statusGrade != "PUBBLICATO" && exRes.statusGrade != "RIFIUTATO")){
							examResult.listcontainer.style.visibility = "hidden";
							examResult.alert.textContent = "Voto non disponibile";
							return;
						}
						examResult.show(self.currentExamData.examCode, self.currentExamData.courseCode);
					} else if(req.status === 403 || req.status === 401){
						window.location.href = "index.html"
						window.sessionStorage.removeItem('id');
						window.sessionStorage.removeItem('name');
						window.alert(message);
					} else {
						window.alert(message);
					}
				}
				});
			this.reset();
		});
		this.close.addEventListener("click", (e) => {
			this.reset();
			pageOrchestrator.refresh();
		});
		
		this.reset = function(){
			this.window.close();
		}
		this.show = function(dataString){
			this.currentExamData = JSON.parse(dataString);
			if (!this.currentExamData) return;
			if(isRefusable(this.currentExamData.reg)){
				this.confirm.style.visibility = "visible";
				this.close.style.visibility = "visible";
			}
			else{
				this.confirm.style.visibility = "hidden";
				this.close.style.visibility = "visible";
			}
			this.window.showModal();
		}
	}
	
	function isRefusable(reg) {
  		lowCaseGrade = reg.grade.toLowerCase();
		lowCaseStatusGrade = reg.statusGrade.toLowerCase();
  		return !(lowCaseGrade === "assente" || lowCaseGrade === "rimandato" || lowCaseGrade === "riprovato" || lowCaseGrade === "") && lowCaseStatusGrade === "pubblicato";
	}
	
	function Logout(id_logout){
		this.logoutbutton = id_logout;
		this.logoutbutton.addEventListener("click", (e) => {
			makeCall("GET", "Logout", null, function(req){
				// funzione di callback all'evento di GET della lista dei verbali - è la funzione onreadystatechange della richiesta per come è definita makeCall
				if(req.readyState === 4){ // richiesta evasa
					window.sessionStorage.removeItem('id');
					window.sessionStorage.removeItem('name');
					window.location.href = "index.html";
				}
			});
		});
		}
}