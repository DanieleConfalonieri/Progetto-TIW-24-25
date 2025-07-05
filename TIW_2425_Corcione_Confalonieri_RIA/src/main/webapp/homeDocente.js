
/**
 * script JavaScript che gestisce la home page del docente.
 * Metodi utili ai componenti: reset(), show(), update(), autoclick() per generare pseudo-eventi
 */

{
	let pageOrchestrator = new PageOrchestrator(); // main controller
	let courseList, examsList, registeredStudents, reportDetails, reportList;
	
	window.addEventListener("load", (e) => {
		if (sessionStorage.getItem("id") == null) {
	    	window.location.href = "index.html";
	    } else {
	        pageOrchestrator.start(); // inizializza i componenti
	        pageOrchestrator.refresh(); // mostra il contenuto iniziale
	    }
	}, false);
	
	function PageOrchestrator(){
		
		this.start = function(){
			document.getElementById("id_nomeDocente").textContent = sessionStorage.getItem('name');
			courseList = new CourseList(document.getElementById("id_coursealert"), document.getElementById("id_courselistcontainer"), document.getElementById("id_courselistcontainerbody"));
			examsList = new ExamList(document.getElementById("id_examalert"), document.getElementById("id_examlistcontainer"), document.getElementById("id_examlistcontainerbody"));
			registeredStudents = new RegisteredStudents(document.getElementById("id_registeredcontainer"), document.getElementById("id_registeredalert"), document.getElementById("id_registeredlistcontainer"), document.getElementById("id_registeredlistcontainerbody"), document.getElementById("id_pubblica"), document.getElementById("id_verbalizza"), document.getElementById("id_registeredexamcode"));
			reportDetails = new ReportDetails(document.getElementById("id_reportdetailcontainer"), document.getElementById("id_reportdetailcontainerbody"), document.getElementById("id_reportdetailstudentlistcontainerbody"));
			reportList = new ReportList(document.getElementById("id_reportalert"), document.getElementById("id_reportlistcontainer"), document.getElementById("id_reportlistcontainerbody"));
		}
		
		this.refresh = function(){
			courseList.reset();
			examsList.reset();
			registeredStudents.reset();
			reportDetails.reset();
			reportList.reset();
			courseList.show();
			reportList.show();
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
							self.listcontainer.style.visibility = "hidden";
							self.alert.textContent = "Nessun corso da vedere.";
							return;
						}
						self.update(courses);
					} else if(req.status === 403){
						window.alert(message);
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
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
		
		this.show = function(courseCode){
			var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
			makeCall("GET", "GoToExamsTeacher?courseId="+courseCode, null, function(req){
				// funzione di callback all'evento di GET della lista degli appelli - è la funzione onreadystatechange della richiesta per come è definita makeCall
				if(req.readyState === 4){ // richiesta evasa
					var message = req.responseText;
					if(req.status === 200){ // OK
						var exams = JSON.parse(req.responseText);
						if(exams.length === 0){
							self.listcontainer.style.visibility = "hidden";
							self.alert.textContent = "Nessun appello da vedere.";
							registeredStudents.reset();
							return;
						}
						self.update(exams, courseCode);
					} else if(req.status === 403){
						window.alert(message);
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
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
				linkText = document.createTextNode("Vedi iscritti");
				anchor.appendChild(linkText);
				link.appendChild(anchor);
				row.appendChild(link);
				anchor.setAttribute('examCode', exam.examCode);
				anchor.addEventListener("click", (e) => {
					registeredStudents.show(e.target.getAttribute("examCode"));
				}, false);
				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";
		}
	}
	
	function RegisteredStudents(_container, _alert, _listcontainer, _listcontainerbody, _botpubblica, _botverbalizza, _examcode){
		this.container = _container;
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;
		this.botpubblica = _botpubblica;
		this.botverbalizza = _botverbalizza;
		this.examcode = _examcode;
		this.examId = 0;
		
		this.botpubblica.addEventListener("click", (e) =>{
			var self = this;
			makeCall("GET", "PublishGrades?examId="+self.examId, null, function(req){
				if(req.readyState === 4){
					var message = req.responseText;
					if(req.status === 200){ // OK
						self.show(self.examId);
					} else if(req.status === 403){ // FORBIDDEN
						window.alert(message);
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
					} else {
						window.alert(message);
					}
				}
			})		
		});
		this.botverbalizza.addEventListener("click", (e) =>{
			var self = this;
			makeCall("GET", "RegisterGrades?examId="+self.examId, null, function(req){
				if(req.readyState === 4){
					var message = req.responseText;
					if(req.status === 200){ // OK
						self.show(self.examId);
						reportDetails.show(message);
					} else if(req.status === 403){ // FORBIDDEN
						window.alert(message);
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
					} else {
						window.alert(message);
					}
				}
			})		
		});
		
		this.reset = function(){
			this.container.style.visibility = "hidden";
			this.listcontainer.style.visibility = "hidden";
		}
		
		this.show = function(examCode){
			this.examId = examCode;
			var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
			makeCall("GET", "GoToRegisteredStudents?examId="+examCode, null, function(req){
				// funzione di callback all'evento di GET della lista degli iscritti - è la funzione onreadystatechange della richiesta per come è definita makeCall
				if(req.readyState === 4){ // richiesta evasa
					var message = req.responseText;
					if(req.status === 200){ // OK
						var registrations = JSON.parse(req.responseText);
						if(registrations.length === 0){
							self.listcontainer.style.visibility = "hidden";
							self.botpubblica.style.visibility = "hidden";
							self.botverbalizza.style.visibility = "hidden";
							self.examcode.textContent = "Iscritti all'appello " + examCode;
							self.alert.textContent = "Nessun iscritto per questo appello.";
							self.container.style.visibility = "visible";
							return;
						}
						self.update(registrations, examCode);
					} else if(req.status === 403){
						window.alert(message);
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
					} else {
						window.alert(message);
					}
				}
			});
		}
		
		this.update = function(registrations, examCode){
			this.alert.textContent = "";
			this.botpubblica.style.visibility = "hidden";
			this.botverbalizza.style.visibility = "hidden";
			this.examcode.textContent = "Iscritti all'appello " + examCode;
			var row, studentCode, surname, name, email, degree, grade, statusGrade, report, publishable, verbalizable;
			this.listcontainerbody.innerHTML = "";
			var self = this;
			registrations.forEach(function(reg){
				row = document.createElement("tr");
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
			});
			this.container.style.visibility = "visible";
			this.listcontainer.style.visibility = "visible";
			for(i=0; i<registrations.length; i++){
				if(registrations[i].statusGrade === "INSERITO"){
					this.botpubblica.style.visibility = "visible";
				}
				if(registrations[i].statusGrade === "PUBBLICATO" || registrations[i].statusGrade === "RIFIUTATO"){
					this.botverbalizza.style.visibility = "visible";
				}
			}
		}
	}
	
	function ReportDetails(_reportDetailContainer, _reportDetailContainerBody, _reportDetailStudentListContainerBody){
		this.reportDetailContainer = _reportDetailContainer;
		this.reportDetailContainerBody = _reportDetailContainerBody;
		this.reportDetailStudentListContainerBody = _reportDetailStudentListContainerBody;
		
		this.reset = function(){
			this.reportDetailContainer.style.visibility = "hidden";
		}
		
		this.show = function(message){
			var parameters = JSON.parse(message);
			var reportCode = parameters.report.reportCode, date = parameters.report.date,
				time = parameters.report.time, teacher = parameters.report.teacher, exam = parameters.report.exam;
			var students = parameters.students;
			this.update(reportCode, date, time, teacher, exam, students);
		}
			
		this.update = function(reportCode, date, time, teacher, exam, students){
			var row, reportCodeCell, dateCell, timeCell, teacherCell, examCell, name, surname, studId, grade;
			this.reportDetailContainerBody.innerHTML = "";
			this.reportDetailStudentListContainerBody.innerHTML = "";
			row = document.createElement("tr");
			reportCodeCell = document.createElement("td");
			reportCodeCell.textContent = reportCode;
			row.appendChild(reportCodeCell);
			dateCell = document.createElement("td");
			dateCell.textContent = date;
			row.appendChild(dateCell);
			timeCell = document.createElement("td");
			timeCell.textContent = time;
			row.appendChild(timeCell);
			teacherCell = document.createElement("td");
			teacherCell.textContent = teacher;
			row.appendChild(teacherCell);
			examCell = document.createElement("td");
			examCell.textContent = exam;
			row.appendChild(examCell);
			this.reportDetailContainerBody.appendChild(row);
			
			var self = this;
			students.forEach(function(s){
				row = document.createElement("tr");
				name = document.createElement("td");
				name.textContent = s.student.name;
				row.appendChild(name);
				surname = document.createElement("td");
				surname.textContent = s.student.surname;
				row.appendChild(surname);
				studId = document.createElement("td");
				studId.textContent = s.student.id;
				row.appendChild(studId);
				grade = document.createElement("td");
				grade.textContent = s.grade;
				row.appendChild(grade);
				self.reportDetailStudentListContainerBody.appendChild(row);
			});
			this.reportDetailContainer.style.visibility = "visible";
		}
	}
	
	function ReportList(_alert, _listcontainer, _listcontainerbody){
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;
		
		this.reset = function(){
			this.listcontainer.style.visibility = "hidden";
		}
		
		this.show = function(){ // deve effettuare la chiamata al server e chiamare update()
			var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
			makeCall("GET", "GoToReports?idDocente=" + sessionStorage.getItem("id"), null, function(req){
				// funzione di callback all'evento di GET della lista dei verbali - è la funzione onreadystatechange della richiesta per come è definita makeCall
				if(req.readyState === 4){ // richiesta evasa
					var message = req.responseText;
					if(req.status === 200){ // OK
						var reports = JSON.parse(req.responseText);
						if(reports.length === 0){
							self.listcontainer.style.visibility = "hidden";
							self.alert.textContent = "Nessun verbale da vedere.";
							return;
						}
						self.update(reports);
					} else if(req.status === 403){
						window.alert(message);
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
					} else {
						window.alert(message);
					}
				}
			});
		}
		
		this.update = function(reportList){
			this.alert.textContent = "";
			var row, code, date, time, examId;
			this.listcontainerbody.innerHTML = "";
			var self = this;
			reportList.forEach(function(report){ // costruisce la tabella in base alla lista dei corsi
				row = document.createElement("tr");
				code = document.createElement("td");
				code.textContent = report.reportCode;
				row.appendChild(code);
				date = document.createElement("td");
				date.textContent = report.date;
				row.appendChild(date);
				time = document.createElement("td");
				time.textContent = report.time;
				row.appendChild(time);
				examId = document.createElement("td");
				examId.textContent = report.exam;
				row.appendChild(examId);
				self.listcontainerbody.appendChild(row);
			});
			self.listcontainer.style.visibility = "visible";
		}
	}
}