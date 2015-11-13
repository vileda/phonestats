$(function () {
	var wsPort = window.location.port === '' ? '' : ':' + window.location.port;
	var wsUrl = window.location.protocol + '//' + window.location.hostname + wsPort;
	var sock = new SockJS(wsUrl+'/socket');

	var displayedAggregate = {incomingTotal: 0};

	sock.onopen = function() {
		console.log('open');
	};

	sock.onmessage = function(e) {
		var aggregate = JSON.parse(e.data);
		if(aggregate.incomingTotal > displayedAggregate.incomingTotal) {
			$('#incomingTotal').text(aggregate.incomingTotal);
			displayedAggregate = aggregate;
		}
	};

	sock.onclose = function() {
		console.log('close');
	};

	var id = window.location.pathname.split('/');
	$.getJSON('/api/aggregate/dashboard/' + id[id.length - 1])
		.done(function(aggregate) {
			$('#incomingTotal').text(aggregate.incomingTotal);
		});
});