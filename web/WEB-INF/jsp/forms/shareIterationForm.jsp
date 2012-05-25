<%@ include file="../inc/_taglibs.jsp"%>
<form>
    <div class="shareIterationForm">
      <div><img src="static/img/agilefant-logo-80px.png" alt="Share iteration" style="float: left;" />
        <div style="margin-left: 90px">
          <p>Send an URL with the token below to grant the recipients read-only access to this iteration:</p>
                   
          <%
          	String url = request.getRequestURL().toString();
          	url = url.substring(0, url.indexOf("WEB-INF"));
          	url = url.concat("token/"); 
          %>
          
          <%-- <p><input type="text" id="tokenUrl" name="tokenUrl" value="<%=url%>${readonlyToken}" readonly="readonly" size="75" />--%>
          <%-- <p>input type="text" id="tokenUrl" name="tokenUrl" value="${readonlyToken}" readonly="readonly" size="40" /> --%>
          <p><b>${readonlyToken}</b></p>
          <p>Usage:<br>[the URL of your Agilefant instance]/ROIteration.action?readonlyToken=[token]</p>
          <p>Example:<br>http://cloud.agilefant.org/community/ROIteration.action?readonlyToken=${readonlyToken}</p>
        
          <%--
          <script>
	          var text_input = document.getElementById ('tokenUrl');
	          text_input.focus ();
	          text_input.select ();
          </script>
          --%>
        </div>
      </div>
    </div>
</form>