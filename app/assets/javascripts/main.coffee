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

	commitsPerUserChart = new xChart('Contributors Commits', data, '#commitsPerUser', opts)
	# TODO maybe some JQuery UI ?
	# TODO https://github.com/nostalgiaz/bootstrap-switch
	# TODO BOOTSTRAP
	$('.buttons button').click (event) ->
		commitsPerUserChart.setType($(this).attr('data-type'))
		$('.buttons button').removeClass('toggled')
		$(this).attr('class', 'toggled')



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
	commitsTimelineChart = new xChart('Commits Timeline', data, '#commitsTimeline', opts)



$ ->
	uri = $(location).attr('href')
	matches = uri.split('/')
	[owner, repo] = [matches[matches.length-2], matches[matches.length-1]]

	# Put it directly into .get call ?
	onResponse = (data) ->
		commitsPerUser data.contributors
		commitsTimeline data.timeline
	
	$.get "/api/stats/#{ owner }/#{ repo }", onResponse, "json"
