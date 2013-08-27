committersList = (contributors) ->
	committers = ({name: c.name, commits: c.commits} for c in contributors)
	list = '<ul class="list-group">'
	list += """
		<li class="list-group-item">
		<span class="badge">#{c.commits}</span>
		#{c.name}
		</li>
		""" for c in committers
	list += "</ul>"
	$('#committersListContent').html(list)

commitsPerUser = (contributors) ->
	points = ({x: c.name, y: c.commits} for c in contributors)

	data = {
		xScale: "ordinal",
		yScale: "linear",
		type: "bar",
		main: [
				{
				className: ".pizza",
				data: points
				}
		]
	}

	opts = {
		axisPaddingTop: 10,
		axisPaddingLeft: 10
	}

	commitsPerUserChart = new xChart('Contributors Commits', data, '#commitsPerUserF', opts)
	$('#barBtn').click (event) ->
		commitsPerUserChart.setType('bar')
	$('#linesBtn').click (event) ->
		commitsPerUserChart.setType('line')



commitsTimeline = (timeline) ->
	points = ({x: d.date, y: d.commits} for d in timeline)

	data = {
  		xScale: "time",
  		yScale: "linear",
  		type: "line",
		main: [
			{
			className: ".pizza",
			data: points
			}
		]
	}

	opts = {
		dataFormatX: (x) -> d3.time.format('%Y-%m-%d').parse(x),
		tickFormatX: (x) -> d3.time.format('%m/%Y')(x),
		yMin: 0,
		axisPaddingTop: 10,
		axisPaddingLeft: 10
		#TODO fix it
		tickHintY: (p.y for p in points).sort().pop()
	}
	commitsTimelineChart = new xChart('Commits Timeline', data, '#commitsTimelineF', opts)


convertForAutocomplete = (repositories) ->
	$.map repositories, (val,i) ->
		"#{ val.owner }/#{ val.name }"

loadCharts = (owner, repo) ->
	onResponse = (data) ->
		committersList data.contributors
		commitsPerUser data.contributors
		commitsTimeline data.timeline

	$.get "/api/charts/#{ owner }/#{ repo }", onResponse, "json"


$(document).ready ->
	$("#repos").autocomplete({
		source: ( (request, response) ->
			$.get("/api/search/#{ request.term }", ((r) -> response(convertForAutocomplete(r.repositories))), "json")),
		minLength: 4,
		select: ( (event, ui) ->
			s = ui.item.value.split('/')
			loadCharts(s[0], s[1]))
	})

	$('#statsTabs').tabs()
	$('#statsTabs a:first').tab('show')
