/**
 * funzioni necessarie a riordinare la lista degli iscritti a un appello per il docente.
 */

// script eseguito indipendentemente dall'altro, definisce una funzione valutata subito dopo la sua definizione
// che aggiunge un listener a tutte le colonne della tabella con le funzioni necessarie ad ordinarla.

// sort() è un metodo degli oggetti array-like

(function(){
	// mi serve una funzione che, data una colonna e un criterio di ordinamento, mi restituisca un modo per ordinare due righe in base a quelli
	function createComparator(i, asc){ // funzione della colonna i e del criterio asc
		return function(a, b){ // funzione delle due righe a e b
			var v1, v2;
			if(asc){ // recupera i valori da confrontare dalle due righe
				v1 = a.children[i].textContent;
				v2 = b.children[i].textContent;
			} else {
				v1 = b.children[i].textContent;
				v2 = a.children[i].textContent;
			}
			if(v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) return v1.toString().localeCompare(v2);
			return v1 - v2;
		}
	}
	
	// per tutti gli elementi del documento di tipo th e classe sortable
	document.querySelectorAll('th.sortable').forEach(function(th){
		th.addEventListener('click', function(){
			var table = th.closest('table');
			this.asc = !this.asc;
			// devo ordinare un array formato da tutte le righe della tabella (che recupero con querySelectorAll)
			// secondo il comparator dato dall'indice della colonna che mi interessa e il criterio.
			// table.querySelectorAll('tbody > tr') ritorna tutti gli elementi di tag tr figli dell'elemento tbody contenuto in table
			// Array.from(th.parentNode.children).indexOf(th) crea un array dalle colonne della tabella (th.parentNode.children) ed estrae l'indice di quella che mi serve
			Array.from(table.querySelectorAll('tbody > tr')).sort(createComparator(Array.from(th.parentNode.children).indexOf(th), this.asc))
				.forEach(function(tr){
					table.querySelector('tbody').appendChild(tr); // inserisce le righe ordinate nella tabella
				});
		})
	});
})();