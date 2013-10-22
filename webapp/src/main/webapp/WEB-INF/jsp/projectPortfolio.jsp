<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="portfolio">

<jsp:attribute name="includeInHeader">
<aef:css path="simile" minify="true" />

<script type="text/javascript" src="static/js/excanvas.js"></script>
<%@include file="inc/includeSimile.jsp" %>
<script type="text/javascript" src="static/js/simile/extensions/portfolio-eventsource.js"></script>

<script type="text/javascript">
window.Timeline.DateTime = window.SimileAjax.DateTime;
Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
$(document).ready(function() {
  var controller = new PortfolioController({
    timelineElement: $("#timeline"),
    rankedProjectsElement: $("#rankedProjects"),
    unrankedProjectsElement: $("#unrankedProjects")
  });

  /*
   * Change to -dropdown
   */
  $('#changeToSelection').change(function() {
    var value = $(this).val();
    if (value === "portfolio") {
      window.location.href = "projectPortfolio.action"
    }
    else if (value === "createNew") {
      window.location.href = "createPortfolio.action"
    }
    else {
      window.location.href = "portlets.action?collectionId=" + value
    }
  });
});
</script>
</jsp:attribute>

<jsp:body>
<h2>Ongoing projects</h2>

<p>

Change to
<select id="changeToSelection">
  <option selected="selected" style="color: #666;">Select a dashboard...</option>

  <optgroup label="General">
    <option value="portfolio">Ongoing projects</option>
  </optgroup>
  
  <optgroup label="Dashboards">
    <c:forEach items="${collections}" var="collection">
      <option value="${collection.id}">${collection.name}</option>
    </c:forEach>
  </optgroup>
  
  <optgroup label="Other">
    <option value="createNew" style="font-style: italic; color: #666;">Create new...</option>
  </optgroup>
</select>

</p>


	<div class="structure-main-block">	
		<div class="dynamictable ui-widget ui-widget-content ui-corner-all">
			<div class="dynamictable-caption dynamictable-caption-block ui-widget-header ui-corner-all">Timeline</div>
			<div id="timeline">
			</div>
		</div>
	</div>
	<div id="rankedProjects" class="structure-main-block">
	</div>
	<div id="unrankedProjects" class="structure-main-block">
	</div>
</jsp:body>
</struct:htmlWrapper>
