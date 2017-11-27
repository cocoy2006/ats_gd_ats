<!DOCTYPE html>
<%@ page language="java" pageEncoding="UTF-8"%>

<%@ page import="android.testing.system.util.Adb"%>
<%@ page import="android.testing.system.log.LogHandler"%>
<%@ page import="com.android.ddmlib.IDevice"%>

<html>
	<head>
		<META http-equiv=”pragma” content=”no-cache”>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		
		<link rel="stylesheet" href="../css/device.css" />
		<link rel="stylesheet" href="../css/style.css">
		<link rel="stylesheet" href="../css/facebox.css">
		<link rel="stylesheet" href="../js/jquery-ui/css/smoothness/jquery-ui-1.8.21.custom.css">
		<link rel="stylesheet" href="../image/x/main.css">
		<title>设备操作界面</title>
		<style type="text/css">
			<!--
			.STYLE1 {
				font-size: 28px
			}
			
			.STYLE2 {
				color: #0000FF
			}
			-->
		</style>
		<%
			String username = session.getAttribute("username").toString();
			String server = session.getAttribute("server").toString();
			String serialNumber = session.getAttribute("serialNumber").toString();
			String fromUrl = session.getAttribute("fromUrl").toString();
			Adb adb = Adb.getInstance();
			int width = 0, height = 0, h = 0;
			IDevice idevice = adb.getIDevice(serialNumber);
			try {
				//out.print("<link rel='stylesheet' type='text/css' href='".concat(idevice.getProperty("self.css.url")).concat("'/>"));
				width = Integer.parseInt(idevice.getProperty("self.display.width"));
				height = Integer.parseInt(idevice.getProperty("self.display.height"));
				//h = Integer.parseInt(idevice.getProperty("self.skin.height"));
				//if (h == 0) h += height;
			} catch(Exception e) {
				new LogHandler().newLog(username, server, serialNumber, "EXCEPTION:NULLPOINTER", System.currentTimeMillis());
				response.sendRedirect(fromUrl);
			}
		%>	
		<script type="text/javascript" src="../js/jquery.js"></script>			
		<script type="text/javascript" src="../js/facebox.js"></script>	
		<script type="text/javascript" src="../js/jquery.progressbar.min.js"></script>
		<script type="text/javascript" src="../js/jquery.hovercard.js"></script>
		<script type="text/javascript" src="../js/jquery-ui/js/jquery-ui-1.8.21.custom.min.js"></script>
		<script type="text/javascript" src="../js/Groject.ImageSwitch.js"></script>
		<script type="text/javascript" src="../js/device.js"></script>
		<script type="text/javascript">
			var g_width = <%=width%>;
		</script>	
	</head>

	<body onbeforeunload="return '您的数据将被清除!'">
		<div class="tophead">
			<div class="logo">				
			</div>
		</div>
		
		<div class="newslist">
			<div class="w_tit">
				&nbsp;演示试用&nbsp;&nbsp;
				<span><a id="operationTips" style="color: #D54300" href="javascript:;">操作提示</a></span>
				<span class="more"><a style="color: #D54300" href="../servlet/Unlock">退 回 首 页</a></span>
			</div>
			<div>
				<!-- WRAP CONTENT AND SIDEBAR -->
				<div class="container-content-sidebar" style="position: relative;">
				
					<!-- SIDEBAR -->
					<div class="sidebar sidebar-font">						
						<!-- 我的机时 -->
						<script type="text/javascript">
							var username = "<%=session.getAttribute("username").toString() %>";
							var server = "<%=session.getAttribute("server").toString() %>";
							var serialNumber = "<%=session.getAttribute("serialNumber").toString() %>";
							var fromUrl = "<%=session.getAttribute("fromUrl").toString() %>";
							var lefttime = <%=session.getAttribute("lefttime").toString() %> - 0;
							startupCountdownTimer(username, fromUrl, server, serialNumber, lefttime, 1, 10);/*单位：秒*/
						</script>
						<div class="newslist-sidebar">
							<div class="w_tit-sidebar">
								我的机时
							</div>
							<div id="resttime" style="position: relative; width:220px; height: 26px;">
								<div id="hour" class="no" style="left: 0px; top: 0px;"></div><div class="units" style="left: 35px; top: 0px;">小时</div>
								<div id="minute" class="no" style="left: 70px; top: 0px;"></div><div class="units" style="left: 105px; top: 0px;">分钟</div>
								<div id="second" class="no" style="left: 140px; top: 0px;"></div><div class="units" style="left: 175px; top: 0px;">秒</div>
							</div>						
						</div>
						
						<!-- 标准操作 -->
						<div class="newslist-sidebar">
							<div class="w_tit-sidebar">
								标准操作
							</div>
							<div>
								<!-- 刷新 -->
								<p class="news">
									<img src="../image/a.gif"/>&nbsp;<a title="刷新当前屏幕" onClick="javascript:;">刷新</a>
									<input type="radio" id="automatic" name="refreshWay" checked="checked" onChange="startupTimer(1000)">自动
									<input type="radio" id="manual" name="refreshWay" onChange="shutdownTimer()">手动
								</p>
								
								<!-- 图像质量 -->
								<!--  
								<p class="news">
									<img src="../image/a.gif"/>&nbsp;当前图像质量:
									<span id="screenQAmount" style="display:inline; border:0; color:#f6931f; font-weight:bold; width: 70px;">最佳</span>									
								</p>
								<div id="screenQSlider"></div>
								-->
								<!-- 自动测试 -->
								<p class="news">
									<img src="../image/a.gif" />&nbsp;<a id="autotestBtn">自动测试</a>
								</p>
								<div id="autotestDiv" class="bg-grey003">
									<div class="closeDiv">
										<a onClick="javascript:closeDiv('autotest');$('#autotestBtn').click();">×关闭</a>
									</div>
									<!--  
									<form name="autotestForm" method="POST" enctype="multipart/form-data" target="hiddenAutotestFrame">
										<p class="browse"><input id="xml" name="xml" type="file" ></p>								
										<input type="button" class="btn" value=" 自 动 测 试 " onClick="autotest()">
										<input type="hidden" name="serialNumber" value="<%=serialNumber%>"/>
									</form>
									-->
									<form name="autotestForm" method="post">
										<p>请选择测试案例</p>
										工程:<select><option value="">请选择</option></select><br/>
										案例:<select><option value="">请选择</option></select><br/>
										<input type="button" class="submit" value=" 运 行 " onClick="runTestcase('<%=serialNumber %>')"/>
									</form>
									<div id="autotestTips"></div>
								</div>
								<!-- <iframe name="hiddenAutotestFrame" id="hiddenAutotestFrame" style="display: none;"></iframe> -->
								
								<!-- 键盘输入 -->
								<p class="news">
									<img src="../image/a.gif"/>&nbsp;<font color="#D54300">键盘输入</font>
									<span id="searchDiv">
										<input type="text" size="8" id="t" onkeyup="this.value=this.value.replace(/[\u4E00-\u9FA5|^!#$%^&]/g,'')" 
											onafterpaste="this.value=this.value.replace(/[\u4E00-\u9FA5|^!#$%^&]/g,'')">
										<input type="button" value="确定" onClick="typeAction()" />
									</span>
								</p>
								<!-- 截取屏幕 -->
								<p class="news">
									<img src="../image/a.gif"/>
									<a onClick="capture()">截取屏幕</a>
								</p>
								<div id="currentScreen" style="display: none;">
									<fieldset>
										<legend>&nbsp;当前屏幕&nbsp;</legend>
										<div>
											<br/>
											<img style="width: 200px;"><br/><hr/>
											下载地址：<font color="#D54300">（右键另存为...）</font><br/><a></a>
										</div>
									</fieldset>
								</div>
							</div>
						</div>
	
						<div class="newslist-sidebar">
							<div class="w_tit-sidebar">
								我的应用<span style="float: right;"><a href="#upfileDiv" rel="facebox">上传文件</a></span>
							</div>
							<div>
								<div id="apks" style="overflow-x:hidden; overflow-y: auto; height: 160px">
									<!-- applications uploaded -->
								</div>
								<iframe name="hiddenApkFrame" id="hiddenApkFrame" style="display: none;"></iframe>
								<div style="height: 16px;">
									<span id="apkTips"></span><!-- show tips when synchronization & installation finish -->
								</div>
							</div>
						</div>
						<div id="upfileDiv" style="display: none;">
							<fieldset>
								<legend>&nbsp;上传文件&nbsp;</legend>
								<div>
									<br/>
									<form name="upfileForm" method="POST" enctype="multipart/form-data" target="hiddenFileFrame">
										<p class="browse"><input type="file" id="upfile" name="upfile"></p>
										<input type="button" class="btn1" value=" 开 始 上 传 文 件 " onClick="uploadfile()"/>
										<input type="hidden" name="serialNumber" value="<%=serialNumber%>" />
									</form><hr/>
									<div id="upfileTips">提示：请不要在文件名中包含非英文字符，否则可能出现异常.</div>
								</div>
							</fieldset>
						</div>
						<iframe name="hiddenFileFrame" id="hiddenFileFrame" style="display: none;"></iframe>
						
						<div class="newslist-sidebar">
							<div class="w_tit-sidebar">
								提示区域
							</div>
							<div>
								<div style="height: 50px;">
									<div>
										<p class="news"><img src="../image/a.gif"/>&nbsp;屏幕大小&nbsp;<%=width%>×<%=height%></p>
										<p class="news">
											<img src="../image/a.gif"/>&nbsp;<a href="javascript:;">用户手册</a>
										</p>
									</div>
								</div>
							</div>
						</div>
						<!--END_EDITABLE-->
					</div>
				
					<!-- CONTENT -->
					<div class="content content-font" style="min-height: <%=330*height/width+10 %>px;">
						<!--BEGIN_EDITABLE-->
						<!-- Text container -->
						<div class="contentbox-container">
							<div class="contentbox-full">
								<div class="contentbox-noshading">
									<div id="skin">
										<div id="screenBox" onMouseDown="start(event)" onMouseUp="finish(event)">
											<img id="screen" alt="" src="" style="width: 330px;"
												onMouseDown="if(event.preventDefault) event.preventDefault()" onDrag="return false"/>
											<div id="screenLoading" style="font-size: 24px;">
												<img src="../image/loading.gif" style="width: 24px;"/>
												正在准备桌面，请等待...
											</div>
										</div>
										<div id="keys">
											<div class="key" id="search" onClick="press('SEARCH')"></div>
											<div class="key" id="home" onMouseDown="pressDown()" onMouseUp="pressUp(this)"></div>
											<div class="key" id="menu" onMouseDown="pressDown()" onMouseUp="pressUp(this)"></div>
											<div class="key" id="back" onClick="press('BACK')"></div>
											<div class="key" id="call" onMouseDown="pressDown()" onMouseUp="pressUp(this)"></div>
											<div class="key" id="endcall" onMouseDown="pressDown()" onMouseUp="pressUp(this)"></div>
											<div class="key" id="up" onClick="press('UP')"></div>
											<div class="key" id="down" onClick="press('DOWN')"></div>
											<div class="key" id="left" onClick="press('LEFT')"></div>
											<div class="key" id="right" onClick="press('RIGHT')"></div>
											<div class="key" id="enter" onClick="press('ENTER')"></div>
										</div>
									</div>
									
									<div class="newslist-sidebar" style="position: absolute; top: 150px; right: 10px;">
										<div class="w_tit-sidebar">
											历史记录
										</div>
										<div>
											<div id="historys" style="overflow: auto; height: 310px;">
												<!-- 历史记录内容，同时也是测试案例内容 -->
											</div>
											<span>
												<input id="startRecordTestcaseBtn" type="button" value="开始记录" onClick="startRecordTestcase(this)">
												<input id="finishRecordTestcaseBtn" type="button" value="结束记录" onClick="finishRecordTestcase(this)" disabled>
											</span>
										</div>
									</div>
								</div>
							</div>
						</div>
						<!--END_EDITABLE-->
					</div>
				<!-- END WRAP CONTENT AND SIDEBAR -->
				</div>
			</div>
		</div>
		<!-- 记录测试用例 -->
		<div id="saveRecordTestcaseDiv" style="display: none;">
			<fieldset>
				<legend>&nbsp;记录测试用例&nbsp;</legend>
				<div>
					<br/>
					<form name="saveRecordTestcaseForm" method="POST">
						<label>测试案例名称</label>&nbsp;
						<input type="text" name="testcase" class="inputtext" 
							value="<%=new java.text.SimpleDateFormat("yyyyMMddhhmmss").format(new java.util.Date()) + ".xml" %>"/><br/>
						<label>请选择所属的测试项目</label>
						<select></select>
						<input type="button" class="submit" value=" 保 存 " onClick="saveRecordTestcase('<%=serialNumber %>')"/>
					</form>
					<hr/>
					<div id="saveRecordTestcaseTips">
						<p class="tips red">提示:相同测试项目下的同名测试案例将直接覆盖!</p>
						<p class="tips"></p>
					</div>
				</div>
			</fieldset>
		</div>
		<!-- 自动化测试案例 -->
		<div id="runTestcaseDiv" style="display: none;">
			<fieldset>
				<legend>&nbsp;自动化测试案例&nbsp;</legend>
				<div>
					<br/>
					<form name="runTestcaseForm" method="POST">
						<p>请选择测试案例</p>
						<select></select>
						<select></select>
						<input type="button" class="submit" value=" 运 行 " onClick="runTestcase('<%=serialNumber %>')"/>
					</form>
					<hr/>
					<div id="runTestcaseTips">
						
					</div>
				</div>
			</fieldset>
		</div>
	</body>
</html>