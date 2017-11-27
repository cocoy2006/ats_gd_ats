<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<script type="text/javascript">
<!--
	alert("您的账号已经锁定其他设备或者未正常退出! 点击\"确定\"自动返回主页!");
	window.location = '<%=request.getSession().getAttribute("fromUrl").toString() %>';
//-->
</script>