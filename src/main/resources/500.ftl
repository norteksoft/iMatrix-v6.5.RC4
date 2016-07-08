<html>

<head>
<title>500 - ${excepitionHandle500title}</title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta http-equiv="Cache-Control" content="no-store"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
        
         <script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
         <link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/colorbox/colorbox.css" />
         <script type="text/javascript" src="${resourceCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<STYLE type=text/css>
A:link {
COLOR: green; TEXT-DECORATION: none;
}
A:visited {
	COLOR: green; TEXT-DECORATION: none
}
A:active {
	COLOR: green; TEXT-DECORATION: none
}
A:hover {
	COLOR: #6f9822; TEXT-DECORATION: none
}
.text {
	FONT-FAMILY: ""; COLOR: #555555; FONT-SIZE: 14px; TEXT-DECORATION: none
}
.STYLE1 {
	FONT-SIZE: 13px
}
.STYLE2 {
	FONT-SIZE: 12px
}
.STYLE3 {
	FONT-SIZE: 11px
}
</STYLE>
<script type="text/javascript">
	$().ready(function(){
		var iframes=window.parent.$('iframe');
		if(iframes.length>0){
			var isIE = $.browser.msie && !$.support.opacity;
			if (isIE) {
				var loc=window.parent.location.href;
				if(loc.indexOf("exception-handle")<0){
					window.parent.location.reload();
				}
			}
		}
	});
	function __iMatrixShowErrorDetails(){
		var pageWidth = $(window).width()-200;
		var pageHeight = $(window).height()-100;
		$.colorbox({href:"#testPage",inline:true, width:pageWidth, height:pageHeight,overlayClose:false,title:"错误详细信息"});
	}
</script>
</head>

<BODY>
<TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%"align=center border=0>
<TBODY>
	<TR>
		<TD vAlign="center" align="middle">
		<TABLE cellSpacing=0 cellPadding=0 width=500 align=center border=0>
			<TR>
				<TD width=17 height=17><IMG height=17
					src="${resourceCtx}/images/co_01.gif" width=17></TD>
				<TD width=466 background="${resourceCtx}/images/bg01.gif"></TD>
				<TD width=17 height=17><IMG height=17
					src="${resourceCtx}/images/co_02.gif" width=17></TD>
			</TR>
			<TR>
				<TD background=${resourceCtx}/images/bg02.gif></TD>
				<TD>
				<TABLE class=text cellSpacing=0 cellPadding=10 width="100%"
					align=center border=0>
					<TR>
						<TD>
					<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
						<TR>
							<TD ><IMG src="${resourceCtx}/images/500.jpg" width=128 height=128 style="float: right;"></TD>
							<TD >
								<P  style="FONT-SIZE: 24px;font-weight: bold;margin:8px 0;font-family:黑体;">
									${excepitionHandle500inerror}
								</P>
								<p style="FONT-SIZE: 12px;COLOR: #777;margin:8px 0;"> ${excepitionHandle500}</p>
								<p style="FONT-SIZE: 12px;COLOR: #777;margin:8px 0;"><a href="#" onclick="__iMatrixShowErrorDetails();">${excepitionHandle500error}</a> </p>
							</TD>
						</TR>
					</TABLE>
					</TD>
					</TR>
					<TR>
						<TD style="border-top: 1px solid #ddd;">
						<BR>
						<TABLE class=text cellSpacing=0 cellPadding=0 width="100%"
							border=0>
								<TR>
									<TD align="center">
										<BR><p style="margin:8px 0;">
										<a href="${ctx}/j_spring_security_logout"">${loginAgain}</a>
										</p>
									</TD>
								</TR>
							</TABLE>
							</TD>
						</TR></TABLE></TD>
						<TD background="${resourceCtx}/images/bg03.gif"></TD>
					</TR>
					<TR>
						<TD width=17 height=17><IMG height=17 src="${resourceCtx}/images/co_03.gif" width=17></TD>
						<TD background="${resourceCtx}/images/bg04.gif" height=17></TD>
						<TD width=17 height=17><IMG height=17 src="${resourceCtx}/images/co_04.gif" width=17></TD>
					</TR>
					</TABLE>
					<TABLE class=text cellSpacing=0 cellPadding=0 width=500 align=center border=0>
					<TR>
					<TD></TD></TR>
					<TR>
					<TD align="middle"></TD></TR></TABLE></TD></TR></TBODY>
					</TABLE>
					<#if errorDetails?if_exists !="">
					<div style="display: none;">
						<div id="testPage" align="left" style="margin: 10px;">
							<table class="form-table-without-border">
								<tr>
									<td >${errorDetails}</td>
								</tr>	
							</table>
						</div>
					</div>
					</#if>
	</BODY>
</html>
