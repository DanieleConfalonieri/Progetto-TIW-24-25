/**
 * script JavaScript che gestisce la home page del docente.
 * Metodi utili ai componenti: reset(), show(), update(), autoclick() per generare pseudo-eventi
 */

{
	let pageOrchestrator = new PageOrchestrator(); // main controller
	let courseList, examsList, registeredStudents, reportDetails, reportList, modalForm, gradeUpdateForm;
	
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
			registeredStudents = new RegisteredStudents(document.getElementById("id_registeredcontainer"), document.getElementById("id_registeredalert"), document.getElementById("id_registeredlistcontainer"), document.getElementById("id_registeredlistcontainerbody"), document.getElementById("id_pubblica"), document.getElementById("id_verbalizza"), document.getElementById("id_insmult"), document.getElementById("id_registeredexamcode"));
			reportDetails = new ReportDetails(document.getElementById("id_reportdetailcontainer"), document.getElementById("id_reportdetailcontainerbody"), document.getElementById("id_reportdetailstudentlistcontainerbody"));
			reportList = new ReportList(document.getElementById("id_reportalert"), document.getElementById("id_reportlistcontainer"), document.getElementById("id_reportlistcontainerbody"));
			modalForm = new ModalForm(document.getElementById("id_modal"), document.getElementById("id_invia"), document.getElementById("id_modaltablebody"), document.getElementById("id_modaltext"));
			gradeUpdateForm = new GradeUpdateForm(document.getElementById("id_gradeupdate"));
		}
		
		this.refresh = function(){
			courseList.reset();
			examsList.reset();
			registeredStudents.reset();
			reportDetails.reset();
			reportList.reset();
			gradeUpdateForm.reset();
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
					} else if(req.status === 403 || req.status === 401){
						window.sessionStorage.removeItem('id');
						window.sessionStorage.removeItem('name');
						window.location.href = "index.html";
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
					registeredStudents.reset();
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
							return;
						}
						self.update(exams, courseCode);
					} else if(req.status === 403){
						window.sessionStorage.removeItem('id');
						window.sessionStorage.removeItem('name');
						window.location.href = "index.html";
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
	
	function RegisteredStudents(_container, _alert, _listcontainer, _listcontainerbody, _botpubblica, _botverbalizza, _botinsmult, _examcode){
		this.container = _container;
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;
		this.botpubblica = _botpubblica;
		this.botverbalizza = _botverbalizza;
		this.botinsmult = _botinsmult;
		this.examcode = _examcode;
		this.registrationsList = null;
		
		this.botpubblica.addEventListener("click", (e) =>{
			var self = this;
			makeCall("GET", "PublishGrades?examId="+self.examId, null, function(req){
				if(req.readyState === 4){
					var message = req.responseText;
					if(req.status === 200){ // OK
						self.show(self.examId);
					} else if(req.status === 403){ // FORBIDDEN
						window.sessionStorage.removeItem('id');
						window.sessionStorage.removeItem('name');
						window.location.href = "index.html";
						window.alert(message);
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
						reportList.show();
					} else if(req.status === 403){ // FORBIDDEN
						window.sessionStorage.removeItem('id');
						window.sessionStorage.removeItem('name');
						window.location.href = "index.html";
					} else {
						window.alert(message);
					}
				}
			})		
		});
		this.botinsmult.addEventListener("click", (e) =>{
			modalForm.show(this.registrationsList, this.examcode);
		});
		
		this.reset = function(){
			//console.log("RegisteredStudents reset...")
			this.container.style.visibility = "hidden";
			this.listcontainer.style.visibility = "hidden";
			this.botpubblica.style.visibility = "hidden";
			this.botverbalizza.style.visibility = "hidden";
			this.botinsmult.style.visibility = "hidden";
		}
		
		this.show = function(examCode){
			var self = this; // deve essere accedibile dalla makeCall interna -> chiusura
			makeCall("GET", "GoToRegisteredStudents?examId="+examCode, null, function(req){
				// funzione di callback all'evento di GET della lista degli iscritti - è la funzione onreadystatechange della richiesta per come è definita makeCall
				if(req.readyState === 4){ // richiesta evasa
					var message = req.responseText;
					//console.log("stato GoToRegisteredStudents " + req.status);
					if(req.status === 200){ // OK
						var registrations = JSON.parse(req.responseText);
						self.registrationsList = registrations;
						self.examcode = examCode;
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
						window.sessionStorage.removeItem('id');
						window.sessionStorage.removeItem('name');
						window.location.href = "index.html";
						window.alert(message);
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
			var row, studentCode, surname, name, email, degree, grade, statusGrade, report, updateButton;
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
				if(reg.statusGrade === "NON_INSERITO" || reg.statusGrade === "INSERITO"){
					updateButton = document.createElement("button");
					updateButton.textContent = "MODIFICA";
					updateButton.addEventListener("click", (e) => {
						gradeUpdateForm.show(reg);
					});
					row.appendChild(updateButton);
				}
				self.listcontainerbody.appendChild(row);
				//console.log(reg.statusGrade);
			});
			this.container.style.visibility = "visible";
			this.listcontainer.style.visibility = "visible";
			for(i=0; i<registrations.length; i++){
				if(registrations[i].statusGrade === "INSERITO"){
					this.botpubblica.style.visibility = "visible";
				} else {
					this.botpubblica.style.visibility = "hidden";
				}
				if(registrations[i].statusGrade === "PUBBLICATO" || registrations[i].statusGrade === "RIFIUTATO"){
					this.botverbalizza.style.visibility = "visible";
				} else {
					this.botverbalizza.style.visibility = "hidden";
				}
				if(registrations[i].statusGrade === "NON_INSERITO"){
					this.botinsmult.style.visibility = "visible";
				} else {
					this.botinsmult.style.visibility = "hidden";
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
					} else if(req.status === 403 || req.status === 401){
						window.sessionStorage.removeItem('id');
						window.sessionStorage.removeItem('name');
						window.location.href = "index.html";
						window.alert(message);
					} else {
						window.alert(message);
					}
				}
			});
		}
		
		this.update = function(reportList){
			this.alert.textContent = "";
			var row, code, date, time, examId, courseName, examDate;
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
				courseName = document.createElement("td");
				courseName.textContent = report.courseName;
				row.appendChild(courseName);
				examDate = document.createElement("td");
				examDate.textContent = report.examDate;
				row.appendChild(examDate);
				self.listcontainerbody.appendChild(row);
			});
			self.listcontainer.style.visibility = "visible";
		}
	}
	
	function ModalForm(_container, _sendbutton, _tablebody, _textcontainer){
		this.container = _container;
		this.sendbutton = _sendbutton;
		this.tablebody = _tablebody;
		this.textcontainer = _textcontainer;
		this.examId = 0;
				
		this.sendbutton.addEventListener('click', (e) => {
			e.preventDefault();
			var form = document.getElementById("id_modalform");
			var error = document.getElementById("id_modalerror");
			if(checkEmpty(form)){
				error.textContent = "Dati assenti.";
				error.style.display = "block";
			} else if(checkValidity(form)){
				var self = this;
				makeCall("POST", "UpdateGrades", form, function(req){
					//console.log(req.readyState);
					if(req.readyState == XMLHttpRequest.DONE){
						//console.log("stato UpdateGrades " + req.status);
						var message = req.responseText;
						if(req.status === 200){ // OK
							registeredStudents.reset();
							registeredStudents.show(self.examId);
						} else if(req.status === 403 || req.status === 401){
							window.sessionStorage.removeItem('id');
							window.sessionStorage.removeItem('name');
							window.location.href = "index.html";
							window.alert(message);
						} else {
							window.alert(message);
						}
					}
				});
				form.reset();
				error.style.display = "none";
				this.container.close();
			} else {
				error.textContent = "Dati non validi.";
				error.style.display = "block";
			}
		});
		
		this.reset = function(){
			this.container.close();
		}
		
		this.show = function(registrations, examCode){
			this.examId = examCode;
			this.textcontainer.textContent = "Inserimento multiplo dei voti per l'appello " + examCode;
			var row, studentCode, surname, name, email, degree, grade, statusGrade, report;
			var form = document.getElementById("id_modalform");
			var examCodeInput = form.querySelector("input[name='examCode']");
			this.tablebody.innerHTML = "";
			if(!examCodeInput){
				examCodeInput = document.createElement("input");
				examCodeInput.style.display = "none";
				examCodeInput.setAttribute("name", "examCode");
				document.getElementById("id_modalform").appendChild(examCodeInput); // aggiunge questo campo di input al form
			}
			examCodeInput.setAttribute("value", examCode);
			console.log(examCodeInput.value);
			var self = this;
			registrations.forEach(function(reg){
				if(reg.statusGrade === "NON_INSERITO"){
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
					grade = document.createElement("input");
					grade.setAttribute("name", reg.student.id);
					grade.setAttribute("type", "text");
					grade.setAttribute("placeholder", "Voto");
					row.appendChild(grade);
					statusGrade = document.createElement("td");
					statusGrade.textContent = "NON INSERITO";
					row.appendChild(statusGrade);
					report = document.createElement("td");
					report.textContent = "-";
					row.appendChild(report);
					self.tablebody.appendChild(row);
				}
			});
			
			this.container.showModal();
		}
		
		/*this.checkValidity = function(form){
			const validData = ["ASSENTE", "RIMANDATO", "RIPROVATO", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L"];
			
			var isValid = true;
		    var inputs = form.querySelectorAll("input[name]");

		    inputs.forEach(input => {
				var value = input.value.toLocaleUpperCase();
				if(input.getAttribute("name") !== "examCode"){
			        input.style.border = "";
	
			        if (value!== null && value !== "" && !validData.includes(value)) {
			            input.style.border = "2px solid red";
			            isValid = false;
			        }
				} else {
					if(value===null || isNaN(Number(value))) isValid = false;
				}
		    });

		    return isValid;
		}
		
		this.checkEmpty = function(form){
			var inputs = form.querySelectorAll("input[name]");
			var result = true; 
			
			inputs.forEach(input => {
				if(input.getAttribute("name") !== "examCode"){
					var value = input.value;
					if(value !== null && value !== "") result = false;
				}
			});
			
			return result;
		}*/
	}
	
	function GradeUpdateForm(container){
		this.container = container;
		this.textbanner = this.container.querySelector("h3");
		this.errormessage = this.container.querySelector("h3.error");
		this.form = container.querySelector("form");
		this.examCode = 0;
		/*this.submitbutton = container.querySelector("button");
		this.submitbutton.addEventListener("click", (e) => {
			e.preventDefault();
			// controllo parametri
			if(checkEmpty(this.form)){
				this.errormessage.textContent = "Dati assenti.";
				this.errormessage.style.display = "block";
			} else if(checkValidity(this.form)){
				var self = this;
				makeCall("POST", "UpdateGrades", this.form, function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						var message = req.responseText;
						if(req.status === 200){ // OK
							self.reset();
							registeredStudents.reset();
							registeredStudents.show(self.examCode);
						} else if(req.status === 403 || req.status === 401){
							window.sessionStorage.removeItem('id');
							window.sessionStorage.removeItem('name');
							window.location.href = "index.html";
							window.alert(message);
						} else {
							window.alert(message);
						}
					}
				});
			}
		});*/
		
		this.reset = function(){
			this.container.style.visibility = "hidden";
			this.textbanner.textContent = "";
			this.errormessage.textContent = "";
			this.errormessage.style.display = "none";
			this.form.innerHTML = "";
			this.form.style.visibility = "hidden";
		}
		
		this.show = function(registration){
			this.reset();
			this.examCode = registration.examCode;
			this.textbanner.textContent = "Modifica voto per lo studente " + registration.student.id + " per l'appello " + registration.examCode;
			var studId, name, surname, email, degree, grade, statusGrade, examCodeInput, submitbutton;
			examCodeInput = document.createElement("input");
			examCodeInput.style.display = "none";
			examCodeInput.setAttribute("name", "examCode");
			examCodeInput.setAttribute("value", registration.examCode);
			this.form.appendChild(examCodeInput);
			studId = document.createElement("span");
			studId.textContent = registration.student.id;
			this.form.appendChild(studId);
			this.form.appendChild(document.createElement("br"));
			name = document.createElement("span");
			name.textContent = registration.student.name;
			this.form.appendChild(name);
			this.form.appendChild(document.createElement("br"));
			surname = document.createElement("span");
			surname.textContent = registration.student.surname;
			this.form.appendChild(surname);
			this.form.appendChild(document.createElement("br"));
			email = document.createElement("span");
			email.textContent = registration.student.email;
			this.form.appendChild(email);
			this.form.appendChild(document.createElement("br"));
			degree = document.createElement("span");
			degree.textContent = registration.student.degree;
			this.form.appendChild(degree);
			this.form.appendChild(document.createElement("br"));
			grade = document.createElement("input");
			grade.setAttribute("name", registration.student.id);
			grade.setAttribute("type", "text");
			grade.setAttribute("placeholder", registration.grade);
			this.form.appendChild(grade);
			this.form.appendChild(document.createElement("br"));
			statusGrade = document.createElement("span");
			statusGrade.textContent = statusGrade.textContent = registration.statusGrade === "NON_INSERITO" ? "NON INSERITO" : registration.statusGrade;
			this.form.appendChild(statusGrade);
			this.form.appendChild(document.createElement("br"));
			
			submitbutton = document.createElement("button");
			submitbutton.setAttribute("action", "submit");
			submitbutton.textContent = "MODIFICA";
			submitbutton.addEventListener("click", (e) => {
				e.preventDefault();
				// controllo parametri
				if(checkEmpty(this.form)){
					this.errormessage.textContent = "Dati assenti.";
					this.errormessage.style.display = "block";
				} else if(checkValidity(this.form)){
					var self = this;
					makeCall("POST", "UpdateGrades", this.form, function(req){
						if(req.readyState == XMLHttpRequest.DONE){
							var message = req.responseText;
							if(req.status === 200){ // OK
								self.reset();
								registeredStudents.reset();
								registeredStudents.show(self.examCode);
							} else if(req.status === 403 || req.status === 401){
								window.sessionStorage.removeItem('id');
								window.sessionStorage.removeItem('name');
								window.location.href = "index.html";
								window.alert(message);
							} else {
								window.alert(message);
							}
						}
					});
				} else {
					this.errormessage.textContent = "Dati non validi.";
					this.errormessage.style.display = "block";
				}
			});
			this.form.appendChild(submitbutton);
			
			this.textbanner.style.visibility = "visible";
			this.form.style.visibility = "visible";
			this.container.style.visibility = "visible";
		}
	}
	
	var checkValidity = function(form){
		const validData = ["ASSENTE", "RIMANDATO", "RIPROVATO", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L"];
		
		var isValid = true;
	    var inputs = form.querySelectorAll("input[name]");

	    inputs.forEach(input => {
			var value = input.value.toLocaleUpperCase();
			if(input.getAttribute("name") !== "examCode"){
		        input.style.border = "";

		        if (value!== null && value !== "" && !validData.includes(value)) {
		            input.style.border = "2px solid red";
		            isValid = false;
		        }
			} else {
				if(value===null || isNaN(Number(value))) isValid = false;
			}
	    });

	    return isValid;
	}
	
	var checkEmpty = function(form){
		var inputs = form.querySelectorAll("input[name]");
		var result = true; 
		
		inputs.forEach(input => {
			if(input.getAttribute("name") !== "examCode"){
				var value = input.value;
				if(value !== null && value !== "") result = false;
			}
		});
		
		return result;
	}
}